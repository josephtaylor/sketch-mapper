/**
 * Part of the SurfaceMapper library: http://surfacemapper.sourceforge.net/
 * Copyright (c) 2011-12 Ixagon AB 
 *
 * This source is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License is available on the World
 * Wide Web at <http://www.gnu.org/copyleft/gpl.html>. You can also
 * obtain it by writing to the Free Software Foundation,
 * Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package ixagon.SurfaceMapper;

//Derived from KeystoneP5 library
//and code from rrrufusss
//https://forum.processing.org/topic/compensating-for-keystone-distortion-or-creating-some-kind-of-homography-routine

import jto.processing.sketch.Sketch;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.XML;

import javax.media.jai.PerspectiveTransform;
import java.awt.*;
import java.io.File;

public class QuadSurface implements SuperSurface {

    static private int GRID_LINE_COLOR;
    static private int GRID_LINE_SELECTED_COLOR;
    static private int SELECTED_OUTLINE_OUTER_COLOR;
    static private int CORNER_MARKER_COLOR;
    static private int SELECTED_OUTLINE_INNER_COLOR;
    static private int SELECTED_CORNER_MARKER_COLOR;
    final private int MODE_RENDER = 0;
    final private int MODE_CALIBRATE = 1;
    float tWidth = 1;
    float tHeight = 1;
    float tOffX = 0;
    float tOffY = 0;
    private PApplet parent;
    private SurfaceMapper sm;
    private int MODE = MODE_RENDER;
    private int activePoint = -1; // Which corner point is selected?
    // The four corners of the transformed quad (in 2d screen space)
    private Point3D[] cornerPoints;
    // The entire list of transformed grid points are stored in this array (from
    // left to right, top to bottom, like pixels..).
    // This list is updated whenever the updateTransform() method is invoked.
    private Point3D[] gridPoints;
    // The raw list of verticies to be pumped out each frame. This array
    // holds the pre-computed list, including duplicates, to save on computation
    // during rendering.
    private Point3D[][] vertexPoints;
    // The transform! Thank you Java Advanced Imaging, now I don't have to learn
    // a bunch of math..
    // Docs:
    // http://download.oracle.com/docs/cd/E17802_01/products/products/java-media/jai/forDevelopers/jai-apidocs/javax/media/jai/PerspectiveTransform.html
    private PerspectiveTransform transform;
    private PVector[] textureWindow = new PVector[2];
    private int GRID_RESOLUTION;
    private int surfaceId;
    private String surfaceName;
    // Metrics for the projected texture..
    private float textureX = 0;
    private float textureY = 0;
    private boolean isSelected;
    private boolean isLocked;
    private int selectedCorner;
    private boolean cornerMovementAllowed = true;
    private int ccolor = 0xFF3c3c3c;
    private Polygon poly = new Polygon();
    private float currentZ;
    private boolean shaking;
    private float shakeStrength;
    private int shakeSpeed;
    private float shakeAngle;
    private int fallOfSpeed;
    private boolean hidden = false;
    private PImage surfaceMask;
    private PImage maskedTex;
    private File maskFile;
    private PImage maskFilter;
    private PImage edgeBlendTex;
    private boolean blendRight = false, blendLeft = false;
    private float blendRightSize = 0.1f, blendLeftSize = 0.1f;
    private PGraphics blendScreen;
    private PGraphics bufferScreen;
    private int bufferScreenWidth = 0;

    private Sketch sketch;

    /**
     * Constructor for creating a new surface at X,Y with RES subdivision.
     *
     * @param parent
     * @param ks
     * @param x
     * @param y
     * @param res
     * @param id
     */
    QuadSurface(PApplet parent, SurfaceMapper ks, float x, float y, int res, int id) {
        init(parent, ks, res, id, null);

        this.setCornerPoints((float) (x - (SuperSurface.DEFAULT_SIZE * 0.5)), (float) (y - (SuperSurface.DEFAULT_SIZE * 0.5)), (float) (x + (SuperSurface.DEFAULT_SIZE * 0.5)), (float) (y - (SuperSurface.DEFAULT_SIZE * 0.5)), (float) (x + (SuperSurface.DEFAULT_SIZE * 0.5)), (float) (y + (SuperSurface.DEFAULT_SIZE * 0.5)), (float) (x - (SuperSurface.DEFAULT_SIZE * 0.5)), (float) (y + (SuperSurface.DEFAULT_SIZE * 0.5)));
        this.setTextureWindow(0, 0, 1, 1);
    }

    /**
     * Constructor used when loading a surface from file
     *
     * @param parent
     * @param ks
     * @param xml
     * @param name
     * @param id
     */
    QuadSurface(PApplet parent, SurfaceMapper ks, XML xml, int id, String name) {

        init(parent, ks, xml.getInt("res"), id, name);

        for (Sketch sketch : ks.getSketchList()) {
            if (sketch.getName().equals(xml.getString("sketch"))) {
                setSketch(sketch);
                break;
            }
        }

        if (Boolean.TRUE.equals(Boolean.valueOf(xml.getString("lock")))) {
            this.setLocked(Boolean.valueOf(xml.getString("lock")));
        }

        setupCornerPoints(xml);
        PVector offset = new PVector(0, 0);
        PVector size = new PVector(1, 1);
        for (XML xo : xml.getChildren()) {
            if (xo.getName().equalsIgnoreCase("texturewindowoffset")) {
                offset = new PVector(xo.getFloat("x"), xo.getFloat("y"));
            }
            if (xo.getName().equalsIgnoreCase("texturewindowsize")) {
                size = new PVector(xo.getFloat("x"), xo.getFloat("y"));
            }
            if (xo.getName().equalsIgnoreCase("surfacemask")) {
//				this.setSurfaceMask(new GLTexture(parent, xo.getString("path")+"/"+xo.getString("filename")));
//				this.setMaskFile(new File(xo.getString("path")+"/"+xo.getString("filename")));
            }
            if (xo.getName().equalsIgnoreCase("blendleft")) {
                this.setBlendLeft(true);
                this.setBlendLeftSize(xo.getFloat("blendsize"));
            }
            if (xo.getName().equalsIgnoreCase("blendright")) {
                this.setBlendRight(true);
                this.setBlendRightSize(xo.getFloat("blendsize"));
            }
        }
        this.setTextureWindow(offset.x, offset.y, size.x, size.y);

    }

    private void setupCornerPoints(XML xml) {
        PVector[] points = new PVector[4];
        int index = 0;
        while (index < 4) {
            for (XML child : xml.getChildren()) {
                if (!"cornerpoint".equals(child.getName())) {
                    continue;
                }
                float x = child.getFloat("x");
                float y = child.getFloat("y");
                points[index] = new PVector(x, y);
                index++;
            }
        }
        setCornerPoints(
                points[0].x, points[0].y,
                points[1].x, points[1].y,
                points[2].x, points[2].y,
                points[3].x, points[3].y
        );
    }

    /**
     * Three points are a counter-clockwise turn if ccw > 0, clockwise if ccw <
     * 0, and collinear if ccw = 0 because ccw is a determinant that gives the
     * signed area of the triangle formed by p1, p2 and p3.
     */
    public static float CCW(Point3D p1, Point3D p2, Point3D p3) {
        return (p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x);
    }

    private void applyEdgeBlendToTexture(PImage tex) {
        if (this.isUsingEdgeBlend()) {

            if (maskedTex == null) {
                maskedTex = new PImage(parent.width, parent.height);
            }
//			maskFilter.setParameterValue("mask_factor", 0.0f);
//			maskFilter.apply(new GLTexture[]{tex, blendScreen.getTexture()}, maskedTex);
        }
    }

    public void clearSurfaceMask() {
        surfaceMask = null;
        maskFile = null;
    }

    @Override
    public void decreaseHorizontalForce() {
        //not needed
    }

    /**
     * Decrease the subdivision
     */
    public void decreaseResolution() {
        if ((this.GRID_RESOLUTION - 1) > 1) {
            this.GRID_RESOLUTION -= 1;
            this.initTransform();
            this.updateTransform();
        }
    }

    @Override
    public void decreaseVerticalForce() {
        //not needed
    }

    public int getActiveBezierPointIndex(int mX, int mY) {
        return -1;
    }

    /**
     * Checks if the coordinates is close to any of the corners, and if not,
     * checks if the coordinates are inside the surface. Returns the index of
     * the corner (0,1,2,3) or (4) if coordinates was inside the surface
     *
     * @param mX
     * @param mY
     * @return
     */
    public int getActiveCornerPointIndex(int mX, int mY) {
        for (int i = 0; i < this.cornerPoints.length; i++) {
            if (PApplet.dist(mX, mY, this.cornerPoints[i].x, this.cornerPoints[i].y) < sm.getSelectionDistance()) {
                setSelectedCorner(i);
                return i;
            }
        }
        if (this.isInside(mX, mY))
            return 2000;
        return -1;
    }

    /**
     * Get the index of the active corner
     *
     * @return
     */
    public int getActivePoint() {
        return this.activePoint;
    }

    /**
     * Calculate the area in squarepixels
     *
     * @return returns the area as N squarepixels
     */
    public double getArea() {
        double area = 0;

        for (int i = 0; i < cornerPoints.length; i++) {
            int j = (i + 1) % cornerPoints.length;
            area += cornerPoints[i].x * cornerPoints[j].y;
            area -= cornerPoints[j].x * cornerPoints[i].y;
        }
        area = Math.abs(area) / 2.0;
        return area;

    }

    @Override
    public Point3D getBezierPoint(int index) {
        return null;
    }

    public float getBlendLeftSize() {
        return blendLeftSize;
    }

    public float getBlendRightSize() {
        return blendRightSize;
    }

    public int getBufferScreenWidth() {
        return bufferScreenWidth;
    }

    /**
     * Find the average position of all the control points, use that as the
     * center point.
     *
     * @return
     */
    public Point3D getCenter() {

        float avgX = 0;
        float avgY = 0;
        for (int c = 0; c < 4; c++) {
            avgX += this.cornerPoints[c].x;
            avgY += this.cornerPoints[c].y;
        }
        avgX /= 4;
        avgY /= 4;

        return new Point3D(avgX, avgY);
    }

    /**
     * Get the fill color of the surface in calibration mode
     *
     * @return
     */
    public int getColor() {
        return ccolor;
    }

    /**
     * Get a specific corner
     *
     * @param index
     * @return
     */
    public Point3D getCornerPoint(int index) {
        return this.cornerPoints[index];
    }

    /**
     * Get all corners
     *
     * @return
     */
    public Point3D[] getCornerPoints() {
        return this.cornerPoints;
    }

    @Override
    public int getHorizontalForce() {
        return 0;
    }

    /**
     * Get the surfaces ID
     *
     * @return
     */
    public int getId() {
        return this.surfaceId;
    }

    /**
     * See if the surface is locked
     *
     * @return
     */
    public boolean getLocked() {
        return this.isLocked;
    }

    /**
     * Get the longest side as double
     *
     * @return
     */
    public double getLongestSide() {
        double[] longest = new double[4];
        longest[0] = PVector.dist(new PVector(cornerPoints[0].x, cornerPoints[0].y), new PVector(cornerPoints[1].x, cornerPoints[1].y));
        longest[1] = PVector.dist(new PVector(cornerPoints[2].x, cornerPoints[2].y), new PVector(cornerPoints[3].x, cornerPoints[3].y));
        longest[2] = PVector.dist(new PVector(cornerPoints[0].x, cornerPoints[0].y), new PVector(cornerPoints[3].x, cornerPoints[3].y));
        longest[3] = PVector.dist(new PVector(cornerPoints[1].x, cornerPoints[1].y), new PVector(cornerPoints[2].x, cornerPoints[2].y));

        double longer = 0;
        for (int i = 0; i < longest.length; i++) {
            if (longest[i] > longer) longer = longest[i];
        }
        return longer;
    }

    public File getMaskFile() {
        return maskFile;
    }

    /**
     * Get the surface as a polygon
     *
     * @return
     */
    public Polygon getPolygon() {
        return poly;
    }

    /**
     * Get the amount of subdivision used in the surface
     *
     * @return
     */
    public int getRes() {
        // The actual resolution is the number of tiles, not the number of mesh
        // points
        return GRID_RESOLUTION - 1;
    }

    /**
     * Get the currently selected bezier control
     *
     * @return
     */
    public int getSelectedBezierControl() {
        return -1;
    }

    /**
     * Get the currently selected corner
     *
     * @return
     */
    public int getSelectedCorner() {
        return this.selectedCorner;
    }

    public Sketch getSketch() {
        return sketch;
    }

    public PImage getSurfaceMask() {
        return surfaceMask;
    }

    public String getSurfaceName() {
        if (surfaceName == null)
            return String.valueOf(this.getId());
        return surfaceName;
    }

    @Override
    public int getSurfaceType() {
        return SuperSurface.QUAD;
    }

    public PVector[] getTextureWindow() {
        return this.textureWindow;
    }

    @Override
    public int getVerticalForce() {
        return 0;
    }

    @Override
    public void increaseHorizontalForce() {
        //not needed
    }

    /**
     * Increase the subdivision
     */
    public void increaseResolution() {
        this.GRID_RESOLUTION += 1;
        this.initTransform();
        this.updateTransform();
    }

    @Override
    public void increaseVerticalForce() {
        //not needed
    }

    /**
     * Convenience method used by the constructors.
     *
     * @param parent
     * @param ks
     * @param res
     * @param id
     */
    private void init(PApplet parent, SurfaceMapper ks, int res, int id, String name) {
        this.parent = parent;
        this.sm = ks;
        this.surfaceId = id;
        this.surfaceName = name;
        this.GRID_RESOLUTION = res + 1;
        this.updateCalibrateTexture();

        this.cornerPoints = new Point3D[4];

        for (int i = 0; i < this.cornerPoints.length; i++) {
            this.cornerPoints[i] = new Point3D();
        }

        GRID_LINE_COLOR = parent.color(160, 160, 160, 50);
        GRID_LINE_SELECTED_COLOR = parent.color(0, 255, 255);
        SELECTED_OUTLINE_OUTER_COLOR = parent.color(0, 255, 255, 128);
        SELECTED_OUTLINE_INNER_COLOR = parent.color(50, 255, 255);
        CORNER_MARKER_COLOR = parent.color(255, 255, 255, 100);
        SELECTED_CORNER_MARKER_COLOR = parent.color(255, 0, 255);

        this.initTransform();

        //maskFilter = new PImage(parent, "Mask.xml");
        //edgeBlendTex = new GLTexture(parent, "edgeblendmask.png");
    }

    /**
     * Initializes the arrays used for transformation
     */
    private void initTransform() {
        this.gridPoints = new Point3D[this.GRID_RESOLUTION * this.GRID_RESOLUTION];
        this.vertexPoints = new Point3D[this.GRID_RESOLUTION][this.GRID_RESOLUTION];

        for (int i = 0; i < this.gridPoints.length; i++) {
            this.gridPoints[i] = new Point3D();
        }

        for (int i = 0; i < this.GRID_RESOLUTION; i++) {
            for (int j = 0; j < this.GRID_RESOLUTION; j++)
                this.vertexPoints[i][j] = new Point3D();
        }
    }

    public boolean isBlendLeft() {
        return blendLeft;
    }

    public boolean isBlendRight() {
        return blendRight;
    }

    public boolean isCornerMovementAllowed() {
        return cornerMovementAllowed;
    }

    /**
     * See if the surface is hidden
     *
     * @return
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Check if coordinates are inside the surface
     *
     * @param mX
     * @param mY
     * @return
     */
    public boolean isInside(float mX, float mY) {
        if (poly.contains(mX, mY))
            return true;
        return false;
    }

    @Override
    public boolean isLocked() {
        return this.isLocked;
    }

    /**
     * See if the surface is selected
     *
     * @return
     */
    public boolean isSelected() {
        return this.isSelected;
    }

    public boolean isUsingEdgeBlend() {
        if (this.isBlendLeft() || this.isBlendRight()) return true;
        return false;
    }

    public boolean isUsingSurfaceMask() {
        if (surfaceMask != null) return true;
        return false;
    }

    private boolean lineIntersects(int pointIndex, Point3D[] corners) {
        float aX = 0, aY = 0, bX = 0, bY = 0;

        aX = corners[(pointIndex - 1) % corners.length].x;
        aY = corners[(pointIndex - 1) % corners.length].y;
        bX = corners[(pointIndex + 1) % corners.length].x;
        bY = corners[(pointIndex - +1) % corners.length].y;

        PVector d = new PVector(bX, bY);
        d.sub(aX, aY, 0);
        PVector f = new PVector(aX, aY);
        f.sub(corners[pointIndex].x, corners[pointIndex].y, 0);

        float a = d.dot(d);
        float b = 2 * f.dot(d);
        float c = f.dot(f) - (10 * 10);

        float discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return false;
        }
        return true;
    }

    /**
     * Render method for rendering while in calibration mode
     *
     * @param g
     */
    public void render(PGraphics g) {
        if (this.MODE == this.MODE_CALIBRATE && !this.isHidden()) {
            this.renderGrid(g);
        }
    }

    /**
     * Render method for rendering in RENDER mode. Takes one GLGraphicsOffScreen
     * and one GLTexture. The GLTexture is the texture used for the surface, and
     * is drawn to the offscreen buffer.
     *
     * @param g
     * @param tex
     */
    public void render(PGraphics g, PImage tex) {
        if (this.isHidden())
            return;

        this.renderQuad(g, tex);

    }

    /**
     * Draws the Cornerpoints
     *
     * @param g
     * @param x
     * @param y
     * @param selected
     * @param cornerIndex
     */
    private void renderCornerPoint(PGraphics g, float x, float y, boolean selected, int cornerIndex) {
        g.noFill();
        g.strokeWeight(2);
        if (selected) {
            g.stroke(QuadSurface.SELECTED_CORNER_MARKER_COLOR);
        } else {
            g.stroke(QuadSurface.CORNER_MARKER_COLOR);
        }
        if (cornerIndex == getSelectedCorner() && isSelected()) {
            g.fill(QuadSurface.SELECTED_CORNER_MARKER_COLOR, 100);
            g.stroke(QuadSurface.SELECTED_CORNER_MARKER_COLOR);
        }
        g.ellipse(x, y, 10, 10);
        g.line(x, y - 5, x, y + 5);
        g.line(x - 5, y, x + 5, y);
    }

    /**
     * Renders the grid in the surface. (useful in calibration mode)
     *
     * @param g
     */
    private void renderGrid(PGraphics g) {
        g.beginDraw();
        g.fill(ccolor, 100);
        g.noStroke();

        g.beginShape(PApplet.QUADS);
        for (int i = 0; i < this.getCornerPoints().length; i++) {
            g.vertex(this.getCornerPoint(i).x, this.getCornerPoint(i).y);
        }
        g.endShape();

        g.textFont(sm.getIdFont());
        g.fill(255);

        g.textAlign(PApplet.CENTER, PApplet.CENTER);
        g.textSize(20);
        g.text("" + this.getSurfaceName(), (float) (this.getCenter().x), (float) this.getCenter().y);
        if (isLocked) {
            g.textSize(12);
            g.text("Surface locked", (float) this.getCenter().x, (float) this.getCenter().y + 26);
        }

        g.noFill();
        g.stroke(QuadSurface.GRID_LINE_COLOR);
        g.strokeWeight(1);
        if (isSelected)
            g.stroke(QuadSurface.GRID_LINE_SELECTED_COLOR);

        if (!isLocked) {
            // Draw vertial grid lines
            int rowOffset = (this.GRID_RESOLUTION * this.GRID_RESOLUTION) - this.GRID_RESOLUTION;
            for (int i = 0; i < this.GRID_RESOLUTION; i++) {
                g.line(this.gridPoints[i].x, this.gridPoints[i].y, this.gridPoints[i + rowOffset].x, this.gridPoints[i + rowOffset].y);
            }

            // Draw horezontal grid lines
            for (int y = 0; y < this.GRID_RESOLUTION; y++) {
                int row = this.GRID_RESOLUTION * y;
                g.line(this.gridPoints[row].x, this.gridPoints[row].y, this.gridPoints[row + this.GRID_RESOLUTION - 1].x, this.gridPoints[row + this.GRID_RESOLUTION - 1].y);
            }
        }

        if (isSelected) {
            g.stroke(SELECTED_OUTLINE_OUTER_COLOR);
            g.strokeWeight(3);
            g.line(cornerPoints[0].x, cornerPoints[0].y, cornerPoints[1].x, cornerPoints[1].y);
            g.line(cornerPoints[1].x, cornerPoints[1].y, cornerPoints[2].x, cornerPoints[2].y);
            g.line(cornerPoints[2].x, cornerPoints[2].y, cornerPoints[3].x, cornerPoints[3].y);
            g.line(cornerPoints[3].x, cornerPoints[3].y, cornerPoints[0].x, cornerPoints[0].y);
        }
        g.stroke(SELECTED_OUTLINE_INNER_COLOR);
        g.strokeWeight(1);
        g.line(cornerPoints[0].x, cornerPoints[0].y, cornerPoints[1].x, cornerPoints[1].y);
        g.line(cornerPoints[1].x, cornerPoints[1].y, cornerPoints[2].x, cornerPoints[2].y);
        g.line(cornerPoints[2].x, cornerPoints[2].y, cornerPoints[3].x, cornerPoints[3].y);
        g.line(cornerPoints[3].x, cornerPoints[3].y, cornerPoints[0].x, cornerPoints[0].y);

        if (!isLocked) {
            // Draw the control points.
            for (int i = 0; i < this.cornerPoints.length; i++) {
                this.renderCornerPoint(g, this.cornerPoints[i].x, this.cornerPoints[i].y, (this.activePoint == i), i);
            }

        }

        if (this.isUsingSurfaceMask()) {
            g.beginShape(PApplet.QUADS);
            g.texture(surfaceMask);
            g.noStroke();
            g.tint(255, 150);

            float tWidth = surfaceMask.width;
            float tHeight = surfaceMask.height;
            float tOffX = 0;
            float tOffY = 0;

            for (int i = 0; i < GRID_RESOLUTION - 1; i++) {
                for (int j = 0; j < GRID_RESOLUTION - 1; j++) {

                    g.vertex(vertexPoints[i][j].x,
                            vertexPoints[i][j].y,
                            vertexPoints[i][j].z + currentZ,
                            ((float) i / (GRID_RESOLUTION - 1)) * tWidth + tOffX,
                            ((float) j / (GRID_RESOLUTION - 1)) * tHeight + tOffY);

                    g.vertex(vertexPoints[i + 1][j].x,
                            vertexPoints[i + 1][j].y,
                            vertexPoints[i + 1][j].z + currentZ,
                            (((float) i + 1) / (GRID_RESOLUTION - 1)) * tWidth + tOffX,
                            ((float) j / (GRID_RESOLUTION - 1)) * tHeight + tOffY);

                    g.vertex(vertexPoints[i + 1][j + 1].x,
                            vertexPoints[i + 1][j + 1].y,
                            vertexPoints[i + 1][j + 1].z + currentZ,
                            (((float) i + 1) / (GRID_RESOLUTION - 1)) * tWidth + tOffX,
                            (((float) j + 1) / (GRID_RESOLUTION - 1)) * tHeight + tOffY);

                    g.vertex(vertexPoints[i][j + 1].x,
                            vertexPoints[i][j + 1].y,
                            vertexPoints[i][j + 1].z + currentZ,
                            ((float) i / (GRID_RESOLUTION - 1)) * tWidth + tOffX,
                            (((float) j + 1) / (GRID_RESOLUTION - 1)) * tHeight + tOffY);

                }
            }

            g.endShape(PApplet.CLOSE);
            g.noTint();
        }

        g.endDraw();
    }

    /**
     * Actual rendering of the QUAD. Is called from the render method. Should
     * normally not be accessed directly.
     *
     * @param g
     * @param tex
     */
    private void renderQuad(PGraphics g, PImage tex) {

        tOffX = tex.width * textureWindow[0].x;
        tOffY = tex.height * textureWindow[0].y;
        tWidth = tex.width * (textureWindow[1].x);
        tHeight = tex.height * (textureWindow[1].y);


        if (this.isUsingEdgeBlend() || this.isUsingSurfaceMask()) {
//
//            if (bufferScreen == null || bufferScreen.width != this.getBufferScreenWidth()) {
//                bufferScreen = parent.createGraphics(this.getBufferScreenWidth(), this.getBufferScreenWidth());
//            }
//            bufferScreen.beginDraw();
//            bufferScreen.beginShape(PApplet.QUADS);
//            bufferScreen.texture(tex);
//            bufferScreen.vertex(0, 0, tOffX, tOffY);
//            bufferScreen.vertex(bufferScreen.width, 0, tWidth + tOffX, tOffY);
//            bufferScreen.vertex(bufferScreen.width, bufferScreen.height, tWidth + tOffX, tHeight + tOffY);
//            bufferScreen.vertex(0, bufferScreen.height, tOffX, tHeight + tOffY);
//            bufferScreen.endShape(PApplet.CLOSE);
//            bufferScreen.endDraw();
//
//
//            if (this.isUsingSurfaceMask()) {
////				maskFilter.setParameterValue("mask_factor", 0.0f);
////				maskFilter.apply(new GLTexture[]{bufferScreen.getTexture(), surfaceMask}, maskedTex);
////				applyEdgeBlendToTexture(maskedTex);
//            } else {
//                applyEdgeBlendToTexture(bufferScreen.get());
//            }
        }

        g.beginDraw();
        g.noStroke();
        g.beginShape(PApplet.QUADS);
        g.texture(tex);

        if (this.isUsingSurfaceMask() || this.isUsingEdgeBlend()) {
            g.texture(maskedTex);
            tOffX = 0;
            tOffY = 0;
            tWidth = maskedTex.width;
            tHeight = maskedTex.height;
        } else {
            g.texture(tex);
            if (bufferScreen != null) {
                bufferScreen = null;
            }
            if (blendScreen != null) {
                blendScreen = null;
            }
        }

        for (int i = 0; i < GRID_RESOLUTION - 1; i++) {
            for (int j = 0; j < GRID_RESOLUTION - 1; j++) {

                g.vertex(vertexPoints[i][j].x,
                        vertexPoints[i][j].y,
                        vertexPoints[i][j].z + currentZ,
                        ((float) i / (GRID_RESOLUTION - 1)) * tWidth + tOffX,
                        ((float) j / (GRID_RESOLUTION - 1)) * tHeight + tOffY);

                g.vertex(vertexPoints[i + 1][j].x,
                        vertexPoints[i + 1][j].y,
                        vertexPoints[i + 1][j].z + currentZ,
                        (((float) i + 1) / (GRID_RESOLUTION - 1)) * tWidth + tOffX,
                        ((float) j / (GRID_RESOLUTION - 1)) * tHeight + tOffY);

                g.vertex(vertexPoints[i + 1][j + 1].x,
                        vertexPoints[i + 1][j + 1].y,
                        vertexPoints[i + 1][j + 1].z + currentZ,
                        (((float) i + 1) / (GRID_RESOLUTION - 1)) * tWidth + tOffX,
                        (((float) j + 1) / (GRID_RESOLUTION - 1)) * tHeight + tOffY);

                g.vertex(vertexPoints[i][j + 1].x,
                        vertexPoints[i][j + 1].y,
                        vertexPoints[i][j + 1].z + currentZ,
                        ((float) i / (GRID_RESOLUTION - 1)) * tWidth + tOffX,
                        (((float) j + 1) / (GRID_RESOLUTION - 1)) * tHeight + tOffY);

            }
        }
        g.endShape(PApplet.CLOSE);
        g.endDraw();
    }

    /**
     * Rotate the cornerpoints in direction (0=ClockWise 1=CounterClockWise)
     *
     * @param direction
     */
    public void rotateCornerPoints(int direction) {
        Point3D[] sourcePoints = cornerPoints.clone();
        switch (direction) {
            case 0:
                cornerPoints[0] = sourcePoints[1];
                cornerPoints[1] = sourcePoints[2];
                cornerPoints[2] = sourcePoints[3];
                cornerPoints[3] = sourcePoints[0];
                this.updateTransform();
                break;
            case 1:
                cornerPoints[0] = sourcePoints[3];
                cornerPoints[1] = sourcePoints[0];
                cornerPoints[2] = sourcePoints[1];
                cornerPoints[3] = sourcePoints[2];
                this.updateTransform();
                break;
        }
    }

    /**
     * Translate a point on the screen into a point in the surface.
     *
     * @param x
     * @param y
     * @return
     */
    public Point3D screenCoordinatesToQuad(float x, float y) {
        double[] srcPts = new double[2];
        srcPts[0] = x;
        srcPts[1] = y;

        double[] dstPts = new double[2];

        try {
            this.transform.inverseTransform(srcPts, 0, dstPts, 0, 1);
        } catch (Exception e) {
            return new Point3D(0, 0);
        }

        return new Point3D((float) dstPts[0], (float) dstPts[1]);
    }

    /**
     * Set index of which corner is active
     *
     * @param activePoint
     */
    public void setActivePoint(int activePoint) {
        this.activePoint = activePoint;
    }

    @Override
    public void setBezierPoint(int pointIndex, float x, float y) {
        //
    }

    public void setBlendLeft(boolean blendLeft) {
        this.blendLeft = blendLeft;
        updateBlendScreen();
    }

    public void setBlendLeftSize(float blendLeftSize) {
        this.blendLeftSize = blendLeftSize;
        updateBlendScreen();
    }

    public void setBlendRight(boolean blendRight) {
        this.blendRight = blendRight;
        updateBlendScreen();
    }

    public void setBlendRightSize(float blendRightSize) {
        this.blendRightSize = blendRightSize;
        updateBlendScreen();
    }

    public void setBufferScreenWidth(int bufferScreenWidth) {
        this.bufferScreenWidth = bufferScreenWidth;
    }

    /**
     * Sets the fill color of the surface in calibration mode
     *
     * @param ccolor
     */
    public void setColor(int ccolor) {
        this.ccolor = ccolor;
    }

    /**
     * Set the coordinates of one of the target corner points. But first check
     * that all angles are clockwise, to avoid concave shapes (collinearity is
     * not allowed either as it freaks out the matrix calculation)
     *
     * @param pointIndex
     */
    public void setCornerPoint(int pointIndex, float inX, float inY) {
        Point3D[] cTemp = new Point3D[4];
        for (int i = 0; i < 4; i++) {
            cTemp[i] = new Point3D(cornerPoints[i].x, cornerPoints[i].y);
        }

        cTemp[pointIndex].x = inX;
        cTemp[pointIndex].y = inY;

        cornerMovementAllowed = true;
        for (int i = 0; i < cTemp.length; i++) {
            if (CCW(cTemp[(i + 2) % cTemp.length], cTemp[(i + 1) % cTemp.length], cTemp[i % cTemp.length]) >= 0) {
                cornerMovementAllowed = false;
            }
        }

        // no intersection
        if (cornerMovementAllowed) {
            this.cornerPoints[pointIndex].x = inX;
            this.cornerPoints[pointIndex].y = inY;
            this.updateTransform();
        }
    }

    /**
     * Set all four corners of the surface
     *
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void setCornerPoints(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3) {
        this.cornerPoints[0].x = x0;
        this.cornerPoints[0].y = y0;

        this.cornerPoints[1].x = x1;
        this.cornerPoints[1].y = y1;

        this.cornerPoints[2].x = x2;
        this.cornerPoints[2].y = y2;

        this.cornerPoints[3].x = x3;
        this.cornerPoints[3].y = y3;

        this.updateTransform();
    }

    /**
     * Set if the surface should be hidden
     *
     * @param hidden
     */
    public void setHide(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Set the surfaces ID
     *
     * @param id
     */
    public void setId(int id) {
        this.surfaceId = id;
    }

    /**
     * Set if the surface is locked
     *
     * @param isLocked
     */
    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
        this.updateCalibrateTexture();
    }

    public void setMaskFile(File maskFile) {
        this.maskFile = maskFile;
    }

    public void setModeCalibrate() {
        this.MODE = this.MODE_CALIBRATE;
    }

    public void setModeRender() {
        this.MODE = this.MODE_RENDER;
    }

    /**
     * Set if the surface is selected
     *
     * @param selected
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public void setSelectedBezierControl(int selectedBezierControl) {
        //
    }

    /**
     * Set the currently selected corner
     *
     * @param selectedCorner
     */
    public void setSelectedCorner(int selectedCorner) {
        this.selectedCorner = selectedCorner;
    }

    /**
     * Set parameters for shaking the surface. Strength == max Z-displacement,
     * Speed == vibration speed, FallOfSpeed 1-1000 == how fast strength is
     * diminished
     *
     * @param strength
     * @param speed
     * @param fallOfSpeed
     */
    public void setShake(int strength, int speed, int fallOfSpeed) {
        if (fallOfSpeed < 1) fallOfSpeed = 1;
        if (fallOfSpeed > 1000) fallOfSpeed = 1000;
        shaking = true;
        this.shakeStrength = strength;
        this.shakeSpeed = speed;
        this.fallOfSpeed = 1000 - fallOfSpeed;
        shakeAngle = 0;
    }

    public void setSketch(Sketch sketch) {
        this.sketch = sketch;
    }

    public void setSurfaceMask(PImage mask) {
        surfaceMask = mask;
        maskedTex = new PImage(parent.width, parent.height);
    }

    public void setSurfaceName(String surfaceName) {
        this.surfaceName = surfaceName;
    }

    /**
     * Manually set coordinates for mapping the texture. This allows for easy
     * cropping and enables a single texture to span more than one surface.
     * Use normalized values for the values! (i.e 0-1)
     */
    public void setTextureWindow(float x, float y, float width, float height) {
        x = (x > 0) ? x : 0;
        x = (x < 1) ? x : 1;
        y = (y > 0) ? y : 0;
        y = (y < 1) ? y : 1;

        width = (width > 0) ? width : 0;
        width = (width < 1) ? width : 1;
        height = (height > 0) ? height : 0;
        height = (height < 1) ? height : 1;

        textureWindow[0] = new PVector(x, y);
        textureWindow[1] = new PVector(width, height);
    }

    /**
     * Set Z-displacement for all coordinates of surface
     *
     * @param currentZ
     */
    public void setZ(float currentZ) {
        this.currentZ = currentZ;
    }

    /**
     * Tells surface to shake (will only do something if setShake has been
     * called quite recently)
     */
    public void shake() {
        if (shaking) {
            shakeAngle += (float) shakeSpeed / 1000;
            shakeStrength *= ((float) this.fallOfSpeed / 1000);
            float shakeZ = (float) (Math.sin(shakeAngle) * shakeStrength);
            this.setZ(shakeZ);
            if (shakeStrength < 1) {
                shaking = false;
            }
        }
    }

    /**
     * Toggle if surface is locked (a locked surface cannot be moved or
     * manipulated in calibration mode, but other surfaces still snap to it)
     */
    public void toggleLocked() {
        this.isLocked = !this.isLocked;
        this.updateCalibrateTexture();
    }

    public void toggleMode() {
        if (this.MODE == this.MODE_RENDER) {
            this.MODE = this.MODE_CALIBRATE;
        } else {
            this.MODE = this.MODE_RENDER;
        }
    }

    private void updateBlendScreen() {
        if (blendScreen == null) {
            blendScreen = parent.createGraphics(512, 512);
        }

        blendScreen.beginDraw();

        if (this.isBlendLeft()) {
            blendScreen.noStroke();
            blendScreen.beginShape(PApplet.QUADS);
            blendScreen.texture(edgeBlendTex);
            blendScreen.vertex(-2, 0, 0, 0);
            blendScreen.vertex(blendScreen.width * this.getBlendLeftSize(), 0, edgeBlendTex.width, 0);
            blendScreen.vertex(blendScreen.width * this.getBlendLeftSize(), blendScreen.height, edgeBlendTex.width, edgeBlendTex.height);
            blendScreen.vertex(-2, blendScreen.height, 0, edgeBlendTex.height);
            blendScreen.endShape(PApplet.CLOSE);
        }
        if (this.isBlendRight()) {
            blendScreen.noStroke();
            blendScreen.beginShape(PApplet.QUADS);
            blendScreen.texture(edgeBlendTex);
            blendScreen.vertex(blendScreen.width - (blendScreen.width * this.getBlendRightSize()), 0, edgeBlendTex.width, 0);
            blendScreen.vertex(blendScreen.width + 2, 0, 0, 0);
            blendScreen.vertex(blendScreen.width + 2, blendScreen.height, 0, edgeBlendTex.height);
            blendScreen.vertex(blendScreen.width - (blendScreen.width * this.getBlendRightSize()), blendScreen.height, edgeBlendTex.width, edgeBlendTex.height);
            blendScreen.endShape(PApplet.CLOSE);
        }

        blendScreen.endDraw();
    }

    /**
     * Used to update the calibration texture when a surface's settings have
     * changed.
     */
    private void updateCalibrateTexture() {
        /*
         * if (calibrateTex == null) this.calibrateTex = new
		 * GLGraphicsOffScreen(parent, 600, 600); calibrateTex.beginDraw(); if
		 * (ccolor == 0) { calibrateTex.clear(50, 80, 150); } else {
		 * calibrateTex.clear(ccolor); } calibrateTex.textFont(idFont); if
		 * (ccolor == 0) { calibrateTex.fill(255); } else {
		 * calibrateTex.fill(0); }
		 *
		 * calibrateTex.textAlign(PApplet.CENTER, PApplet.CENTER);
		 * calibrateTex.textSize(80); calibrateTex.text("" + surfaceId, (float)
		 * (calibrateTex.width * 0.5), (float) (calibrateTex.height * 0.5)); if
		 * (isLocked) { calibrateTex.textSize(40);
		 * calibrateTex.text("Surface locked", (float) (calibrateTex.width *
		 * 0.5), (float) (calibrateTex.height * 0.7)); } calibrateTex.endDraw();
		 */
    }

    /**
     * Recalculates all coordinates using the perspective transform. Must be
     * called whenever any change has been done to the surface.
     */
    private void updateTransform() {
        // Update the PerspectiveTransform with the current width, height, and
        // destination coordinates.
        this.transform = PerspectiveTransform.getQuadToQuad(0, 0, SuperSurface.DEFAULT_SIZE, 0, SuperSurface.DEFAULT_SIZE, SuperSurface.DEFAULT_SIZE, 0, SuperSurface.DEFAULT_SIZE, this.cornerPoints[0].x, this.cornerPoints[0].y, this.cornerPoints[1].x, this.cornerPoints[1].y, this.cornerPoints[2].x, this.cornerPoints[2].y, this.cornerPoints[3].x, this.cornerPoints[3].y);

        // calculate the x and y interval to subdivide the source rectangle into
        // the desired resolution.
        float stepX = SuperSurface.DEFAULT_SIZE / (float) (this.GRID_RESOLUTION - 1);
        float stepY = SuperSurface.DEFAULT_SIZE / (float) (this.GRID_RESOLUTION - 1);

        // figure out the number of points in the whole grid.
        int numPoints = this.GRID_RESOLUTION * this.GRID_RESOLUTION;

        // create the array of floats (used for input into the transform method,
        // it requires a single array of floats)
        float[] srcPoints = new float[numPoints * 2];

        // calculate the source coordinates of the grid points, as well as the
        // texture coordinates of the destination points.
        int i = 0;
        for (int y = 0; y < this.GRID_RESOLUTION; y++) {
            for (int x = 0; x < this.GRID_RESOLUTION; x++) {
                float percentX = (x * stepX) / SuperSurface.DEFAULT_SIZE;
                float percentY = (y * stepY) / SuperSurface.DEFAULT_SIZE;

                this.gridPoints[x + y * this.GRID_RESOLUTION].u = SuperSurface.DEFAULT_SIZE * percentX + this.textureX;
                this.gridPoints[x + y * this.GRID_RESOLUTION].v = SuperSurface.DEFAULT_SIZE * percentY + this.textureY; // y
                // *
                // stepY;

                srcPoints[i++] = x * stepX;
                srcPoints[i++] = y * stepY;
            }
        }

        // create an array for the transformed points (populated by
        // PerspectiveTransform.transform())
        float[] transformed = new float[srcPoints.length];

        // perform the transformation.
        this.transform.transform(srcPoints, 0, transformed, 0, numPoints);

        // convert the array of float values back into x/y pairs in the Point3D
        // class for ease of use later.
        for (int p = 0; p < numPoints; p++) {
            this.gridPoints[p].x = transformed[p * 2];
            this.gridPoints[p].y = transformed[p * 2 + 1];
        }

        // Precompute the verticies for use in rendering.
        int offset = 0;
        int vertextIndex = 0;
        for (int x = 0; x < this.GRID_RESOLUTION - 1; x++) {
            for (int y = 0; y < this.GRID_RESOLUTION - 1; y++) {
                offset = x + y * this.GRID_RESOLUTION;

                this.vertexPoints[x][y].copyPoint(this.gridPoints[offset]);
                this.vertexPoints[x + 1][y].copyPoint(this.gridPoints[offset + 1]);

                offset = x + (y + 1) * this.GRID_RESOLUTION;

                this.vertexPoints[x + 1][y + 1].copyPoint(this.gridPoints[offset + 1]);
                this.vertexPoints[x][y + 1].copyPoint(this.gridPoints[offset]);
            }
        }

        // keep track of the four transformed corner points for use in
        // calibration mode.
        this.cornerPoints[0].x = this.gridPoints[0].x;
        this.cornerPoints[0].y = this.gridPoints[0].y;

        this.cornerPoints[1].x = this.gridPoints[this.GRID_RESOLUTION - 1].x;
        this.cornerPoints[1].y = this.gridPoints[this.GRID_RESOLUTION - 1].y;

        this.cornerPoints[2].x = this.gridPoints[this.gridPoints.length - 1].x;
        this.cornerPoints[2].y = this.gridPoints[this.gridPoints.length - 1].y;

        this.cornerPoints[3].x = this.gridPoints[this.gridPoints.length - this.GRID_RESOLUTION].x;
        this.cornerPoints[3].y = this.gridPoints[this.gridPoints.length - this.GRID_RESOLUTION].y;

        poly = new Polygon();

        poly.addPoint((int) cornerPoints[0].x, (int) cornerPoints[0].y);
        poly.addPoint((int) cornerPoints[1].x, (int) cornerPoints[1].y);
        poly.addPoint((int) cornerPoints[2].x, (int) cornerPoints[2].y);
        poly.addPoint((int) cornerPoints[3].x, (int) cornerPoints[3].y);
    }

}