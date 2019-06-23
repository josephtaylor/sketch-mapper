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

package ixagon.surface.mapper;

import jto.processing.sketch.mapper.Sketch;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.XML;

import java.awt.*;
import java.io.File;

//Parts derived from MappingTools library

public class BezierSurface implements SuperSurface {

    static private int GRID_LINE_COLOR;
    static private int GRID_LINE_SELECTED_COLOR;
    static private int SELECTED_OUTLINE_OUTER_COLOR;
    static private int CORNER_MARKER_COLOR;
    static private int SELECTED_OUTLINE_INNER_COLOR;
    static private int SELECTED_CORNER_MARKER_COLOR;
    final private int MODE_RENDER = 0;
    final private int MODE_CALIBRATE = 1;
    private PApplet parent;
    private SurfaceMapper sm;
    private int MODE = MODE_RENDER;
    private int activePoint = -1; // Which corner point is selected?
    // Corners of the Surface
    private Point3D[] cornerPoints;

    // Contains all coordinates
    private Point3D[][] vertexPoints;

    // Coordinates of the bezier vectors
    private Point3D[] bezierPoints;

    private PVector[] textureWindow = new PVector[2];

    // Displacement forces

    private int horizontalForce = 0;
    private int verticalForce = 0;

    private int GRID_RESOLUTION;
    private int surfaceId;

    private boolean isSelected;
    private boolean isLocked;
    private int selectedCorner;
    private int selectedBezierControl;

    private int ccolor = 0xFF3c3c3c;

    private String surfaceName;

    private Polygon poly = new Polygon();

    private float currentZ;
    private boolean shaking;
    private float shakeStrength;
    private int shakeSpeed;
    private int fallOfSpeed;
    private float shakeAngle;

    private boolean hidden = false;

    private PImage surfaceMask;
    private PImage maskedTex;
    private File maskFile;
    private PImage maskFilter;
    private boolean usingEdgeBlend = false;
    private PImage edgeBlendTex;
    private boolean blendRight = false, blendLeft = false;
    private float blendRightSize = 0, blendLeftSize = 0;
    private PGraphics blendScreen;
    private PGraphics bufferScreen;
    private int bufferScreenWidth = 0;

    private Sketch sketch;
    private int sketchIndex;

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
    BezierSurface(PApplet parent, SurfaceMapper ks, float x, float y, int res, int id) {
        if (res % 2 != 0) res++;
        init(parent, ks, res, id, null);

        this.cornerPoints[0].x = (float) (x - (SuperSurface.DEFAULT_SIZE * 0.5));
        this.cornerPoints[0].y = (float) (y - (SuperSurface.DEFAULT_SIZE * 0.5));

        this.cornerPoints[1].x = (float) (x + (SuperSurface.DEFAULT_SIZE * 0.5));
        this.cornerPoints[1].y = (float) (y - (SuperSurface.DEFAULT_SIZE * 0.5));

        this.cornerPoints[2].x = (float) (x + (SuperSurface.DEFAULT_SIZE * 0.5));
        this.cornerPoints[2].y = (float) (y + (SuperSurface.DEFAULT_SIZE * 0.5));

        this.cornerPoints[3].x = (float) (x - (SuperSurface.DEFAULT_SIZE * 0.5));
        this.cornerPoints[3].y = (float) (y + (SuperSurface.DEFAULT_SIZE * 0.5));

        //bezier points init

        this.bezierPoints[0].x = (float) (this.cornerPoints[0].x + (SuperSurface.DEFAULT_SIZE * 0.0));
        this.bezierPoints[0].y = (float) (this.cornerPoints[0].y + (SuperSurface.DEFAULT_SIZE * 0.3));

        this.bezierPoints[1].x = (float) (this.cornerPoints[0].x + (SuperSurface.DEFAULT_SIZE * 0.3));
        this.bezierPoints[1].y = (float) (this.cornerPoints[0].y + (SuperSurface.DEFAULT_SIZE * 0.0));

        this.bezierPoints[2].x = (float) (this.cornerPoints[1].x - (SuperSurface.DEFAULT_SIZE * 0.3));
        this.bezierPoints[2].y = (float) (this.cornerPoints[1].y + (SuperSurface.DEFAULT_SIZE * 0.0));

        this.bezierPoints[3].x = (float) (this.cornerPoints[1].x - (SuperSurface.DEFAULT_SIZE * 0.0));
        this.bezierPoints[3].y = (float) (this.cornerPoints[1].y + (SuperSurface.DEFAULT_SIZE * 0.3));

        this.bezierPoints[4].x = (float) (this.cornerPoints[2].x - (SuperSurface.DEFAULT_SIZE * 0.0));
        this.bezierPoints[4].y = (float) (this.cornerPoints[2].y - (SuperSurface.DEFAULT_SIZE * 0.3));

        this.bezierPoints[5].x = (float) (this.cornerPoints[2].x - (SuperSurface.DEFAULT_SIZE * 0.3));
        this.bezierPoints[5].y = (float) (this.cornerPoints[2].y - (SuperSurface.DEFAULT_SIZE * 0.0));

        this.bezierPoints[6].x = (float) (this.cornerPoints[3].x + (SuperSurface.DEFAULT_SIZE * 0.3));
        this.bezierPoints[6].y = (float) (this.cornerPoints[3].y + (SuperSurface.DEFAULT_SIZE * 0.0));

        this.bezierPoints[7].x = (float) (this.cornerPoints[3].x - (SuperSurface.DEFAULT_SIZE * 0.0));
        this.bezierPoints[7].y = (float) (this.cornerPoints[3].y - (SuperSurface.DEFAULT_SIZE * 0.3));

        this.setTextureWindow(0, 0, 1, 1);

        this.updateTransform();
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
    BezierSurface(PApplet parent, SurfaceMapper ks, XML xml, int id, String name) {

        init(parent, ks, (xml.getInt("res")), id, name);

        if (Boolean.TRUE.equals(Boolean.valueOf(xml.getString("lock")))) {
            this.toggleLocked();
        }

        for (Sketch sketch : ks.getSketchList()) {
            if (sketch.getName().equals(xml.getString("sketch"))) {
                setSketch(sketch);
                break;
            }
        }

        // reload the Corners
        for (int i = 0; i < xml.getChildCount(); i++) {
            XML point = xml.getChild(i);
            if (point.getName().equals("cornerpoint"))
                setCornerPoint(point.getInt("i"), point.getFloat("x"), point.getFloat("y"));
            if (point.getName().equals("bezierpoint"))
                this.setBezierPoint(point.getInt("i"), point.getFloat("x"), point.getFloat("y"));
        }

        horizontalForce = xml.getInt("horizontalForce");
        verticalForce = xml.getInt("verticalForce");

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
                //this.setSurfaceMask(new PImage(parent, xo.getString("path")+"/"+xo.getString("filename")));
                this.setMaskFile(new File(xo.getString("path") + "/" + xo.getString("filename")));
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


        this.updateTransform();
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

    /**
     * Decrease the amount of horizontal displacement force used for spherical mapping for bezier surfaces. (using orthographic projection)
     */
    public void decreaseHorizontalForce() {
        this.horizontalForce -= 2;
        this.updateTransform();
    }

    /**
     * Decrease the subdivision
     */
    public void decreaseResolution() {
        if ((this.GRID_RESOLUTION - 1) > 2) {
            this.GRID_RESOLUTION -= 2;
            this.vertexPoints = new Point3D[this.GRID_RESOLUTION + 1][this.GRID_RESOLUTION + 1];
            this.updateTransform();
        }
    }

    /**
     * Decrease the amount of vertical displacement force used for spherical mapping for bezier surfaces. (using orthographic projection)
     */
    public void decreaseVerticalForce() {
        this.verticalForce -= 2;
        this.updateTransform();
    }

    /**
     * Returns index 0-7 if coordinates are on a bezier control
     *
     * @param mX
     * @param mY
     * @return
     */
    public int getActiveBezierPointIndex(int mX, int mY) {
        for (int i = 0; i < this.bezierPoints.length; i++) {
            if (PApplet.dist(mX, mY, this.bezierPoints[i].x, this.bezierPoints[i].y) < sm.getSelectionDistance()) {
                this.setSelectedBezierControl(i);
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns index 0-3 if coordinates are near a corner or index 4 if on a surface
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
     * Get the index of active corner (or surface)
     *
     * @return
     */
    public int getActivePoint() {
        return this.activePoint;
    }

    public double getArea() {
        return 0;
    }

    /**
     * Get the target bezier point
     *
     * @param index
     * @return
     */
    public Point3D getBezierPoint(int index) {
        return this.bezierPoints[index];
    }

    /**
     * Get all bezier points
     *
     * @return
     */
    public Point3D[] getBezierPoints() {
        return this.bezierPoints;
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
     * Get the average center point of the surface
     *
     * @return
     */
    public Point3D getCenter() {
        // Find the average position of all the control points, use that as the
        // center point.
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
     * Get the target corner point
     *
     * @param index
     * @return
     */
    public Point3D getCornerPoint(int index) {
        return this.cornerPoints[index];
    }

    /**
     * Get all corner points
     *
     * @return
     */
    public Point3D[] getCornerPoints() {
        return this.cornerPoints;
    }

    /**
     * Get the amount of horizontal displacement force used for spherical mapping for bezier surfaces.
     *
     * @return
     */
    public int getHorizontalForce() {
        return horizontalForce;
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
     * Get the surfaces polygon
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
        return GRID_RESOLUTION;
    }

    /**
     * Get the currently selected bezier control
     *
     * @return
     */
    public int getSelectedBezierControl() {
        return selectedBezierControl;
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
        if (surfaceName == null) return String.valueOf(this.getId());
        return surfaceName;
    }

    @Override
    public int getSurfaceType() {
        return SuperSurface.BEZIER;
    }

    public PVector[] getTextureWindow() {
        return this.textureWindow;
    }

    /**
     * Get the amount of vertical displacement force used for spherical mapping for bezier surfaces.
     *
     * @return
     */
    public int getVerticalForce() {
        return verticalForce;
    }

    /**
     * Increase the amount of horizontal displacement force used for spherical mapping for bezier surfaces. (using orthographic projection)
     */
    public void increaseHorizontalForce() {
        this.horizontalForce += 2;
        this.updateTransform();
    }

    /**
     * Increase the subdivision
     */
    public void increaseResolution() {
        this.GRID_RESOLUTION += 2;
        this.vertexPoints = new Point3D[this.GRID_RESOLUTION + 1][this.GRID_RESOLUTION + 1];
        this.updateTransform();
    }

    /**
     * Increase the amount of vertical displacement force used for spherical mapping for bezier surfaces. (using orthographic projection)
     */
    public void increaseVerticalForce() {
        this.verticalForce += 2;
        this.updateTransform();

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
        this.surfaceName = name;
        this.surfaceId = id;
        this.GRID_RESOLUTION = res;
        this.horizontalForce = 0;
        this.verticalForce = 0;
        this.selectedBezierControl = -1;

        this.cornerPoints = new Point3D[4];
        this.bezierPoints = new Point3D[8];
        this.vertexPoints = new Point3D[this.GRID_RESOLUTION + 1][this.GRID_RESOLUTION + 1];

        for (int i = 0; i < this.cornerPoints.length; i++) {
            this.cornerPoints[i] = new Point3D();
        }

        for (int i = 0; i < this.bezierPoints.length; i++) {
            this.bezierPoints[i] = new Point3D();
        }

        GRID_LINE_COLOR = parent.color(160, 160, 160, 50);
        GRID_LINE_SELECTED_COLOR = parent.color(255, 128, 0);
        SELECTED_OUTLINE_OUTER_COLOR = parent.color(255, 128, 0, 128);
        SELECTED_OUTLINE_INNER_COLOR = parent.color(255, 128, 50);
        CORNER_MARKER_COLOR = parent.color(255, 255, 255, 100);
        SELECTED_CORNER_MARKER_COLOR = parent.color(0, 255, 255);

        this.updateTransform();

        //maskFilter = new PImage(parent, "Mask.xml");
    }

    public boolean isBlendLeft() {
        return blendLeft;
    }

    public boolean isBlendRight() {
        return blendRight;
    }

    public boolean isCornerMovementAllowed() {
        return true;
    }

    /**
     * See if surface is hidden
     *
     * @return
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Returns true if coordinates are inside a surface
     *
     * @param mX
     * @param mY
     * @return
     */
    public boolean isInside(float mX, float mY) {
        if (poly.contains(mX, mY)) return true;
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
     * Render method for rendering in RENDER mode.
     * Takes one GLGraphicsOffScreen and one GLTexture. The GLTexture is the texture used for the surface, and is drawn to the offscreen buffer.
     *
     * @param g
     * @param tex
     */
    public void render(PGraphics g, PImage tex) {
        if (this.isHidden()) return;
        this.renderSurface(g, tex);
    }

    /**
     * Draws the bezier points
     *
     * @param g
     * @param x
     * @param y
     * @param selected
     * @param cornerIndex
     */
    private void renderBezierPoint(PGraphics g, float x, float y, boolean selected, int cornerIndex) {
        g.noFill();
        g.strokeWeight(1);
        if (selected) {
            g.stroke(BezierSurface.SELECTED_CORNER_MARKER_COLOR);
        } else {
            g.stroke(BezierSurface.CORNER_MARKER_COLOR);
        }
        if (cornerIndex == getSelectedBezierControl() && isSelected()) {
            g.fill(BezierSurface.SELECTED_CORNER_MARKER_COLOR, 100);
            g.stroke(BezierSurface.SELECTED_CORNER_MARKER_COLOR);
        }
        g.ellipse(x, y, 10, 10);
        g.line(x, y - 5, x, y + 5);
        g.line(x - 5, y, x + 5, y);
    }

    /**
     * Draws the Corner points
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
            g.stroke(BezierSurface.SELECTED_CORNER_MARKER_COLOR);
        } else {
            g.stroke(BezierSurface.CORNER_MARKER_COLOR);
        }
        if (cornerIndex == getSelectedCorner() && isSelected()) {
            g.fill(BezierSurface.SELECTED_CORNER_MARKER_COLOR, 100);
            g.stroke(BezierSurface.SELECTED_CORNER_MARKER_COLOR);
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


        g.fill(ccolor);

        g.noStroke();
        for (int i = 0; i < GRID_RESOLUTION; i++) {
            for (int j = 0; j < GRID_RESOLUTION; j++) {

                g.beginShape();
                g.vertex(vertexPoints[i][j].x, vertexPoints[i][j].y);
                g.vertex(vertexPoints[i + 1][j].x, vertexPoints[i + 1][j].y);
                g.vertex(vertexPoints[i + 1][j + 1].x, vertexPoints[i + 1][j + 1].y);
                g.vertex(vertexPoints[i][j + 1].x, vertexPoints[i][j + 1].y);
                g.endShape();

            }
        }

        g.textFont(sm.getIdFont());
        g.fill(255);
        g.textAlign(PApplet.CENTER, PApplet.CENTER);
        g.textSize(20);
        g.text("" + this.getSurfaceName(), (float) (this.getCenter().x), (float) this.getCenter().y);
        if (isLocked) {
            g.textSize(12);
            g.text("Surface locked", (float) this.getCenter().x, (float) this.getCenter().y + 26);
        }
        if (sketch != null) {
            g.textSize(10);
            g.text(sketch.getName(), (float) this.getCenter().x, (float) this.getCenter().y + 40);
        }


        g.noFill();
        g.stroke(BezierSurface.GRID_LINE_COLOR);
        g.strokeWeight(2);
        if (isSelected)
            g.stroke(BezierSurface.GRID_LINE_SELECTED_COLOR);

        if (!isLocked) {
            for (int i = 0; i <= GRID_RESOLUTION; i++) {
                for (int j = 0; j <= GRID_RESOLUTION; j++) {
                    g.point(vertexPoints[i][j].x, vertexPoints[i][j].y, vertexPoints[i][j].z);
                }
            }
        }

        if (isSelected) {
            g.strokeWeight(4);
            g.stroke(BezierSurface.GRID_LINE_SELECTED_COLOR);

            //draw the outline here
            for (int i = 0; i < poly.npoints - 1; i++) {
                g.line(poly.xpoints[i], poly.ypoints[i], poly.xpoints[i + 1], poly.ypoints[i + 1]);
                if (i == poly.npoints - 2)
                    g.line(poly.xpoints[i + 1], poly.ypoints[i + 1], poly.xpoints[0], poly.ypoints[0]);
            }
        }

        g.strokeWeight(1);
        g.stroke(SELECTED_OUTLINE_INNER_COLOR);
        //draw the outline here
        for (int i = 0; i < poly.npoints - 1; i++) {
            g.line(poly.xpoints[i], poly.ypoints[i], poly.xpoints[i + 1], poly.ypoints[i + 1]);
            if (i == poly.npoints - 2)
                g.line(poly.xpoints[i + 1], poly.ypoints[i + 1], poly.xpoints[0], poly.ypoints[0]);
        }


        if (!isLocked) {
            // Draw the control points.
            for (int i = 0; i < this.cornerPoints.length; i++) {
                this.renderCornerPoint(g, this.cornerPoints[i].x, this.cornerPoints[i].y, (this.activePoint == i), i);

            }

            for (int i = 0; i < this.bezierPoints.length; i++) {
                this.renderBezierPoint(g, this.bezierPoints[i].x, this.bezierPoints[i].y, (this.selectedBezierControl == i), i);
                g.strokeWeight(1);
                g.stroke(255);
                g.line(this.bezierPoints[i].x, this.bezierPoints[i].y, this.cornerPoints[(int) (i / 2)].x, this.cornerPoints[(int) (i / 2)].y);
            }

        }

        g.endDraw();
    }

    /**
     * Actual rendering of the surface. Is called from the render method.
     * Should normally not be accessed directly.
     *
     * @param g
     * @param tex
     */
    private void renderSurface(PGraphics g, PImage tex) {
        float tWidth = 1;
        float tHeight = 1;
        float tOffX = 0;
        float tOffY = 0;

        tWidth = tex.width * (textureWindow[1].x);
        tHeight = tex.height * (textureWindow[1].y);
        tOffX = tex.width * textureWindow[0].x;
        tOffY = tex.height * textureWindow[0].y;

        if (this.isUsingEdgeBlend() || this.isUsingSurfaceMask()) {

            if (bufferScreen == null || bufferScreen.width != this.getBufferScreenWidth()) {
                bufferScreen = parent.createGraphics(this.getBufferScreenWidth(), this.getBufferScreenWidth());
            }
            bufferScreen.beginDraw();
            bufferScreen.beginShape(PApplet.QUADS);
            bufferScreen.texture(tex);
            bufferScreen.vertex(0, 0, tOffX, tOffY);
            bufferScreen.vertex(bufferScreen.width, 0, tWidth + tOffX, tOffY);
            bufferScreen.vertex(bufferScreen.width, bufferScreen.height, tWidth + tOffX, tHeight + tOffY);
            bufferScreen.vertex(0, bufferScreen.height, tOffX, tHeight + tOffY);
            bufferScreen.endShape(PApplet.CLOSE);
            bufferScreen.endDraw();


            if (this.isUsingSurfaceMask()) {
//				maskFilter.setParameterValue("mask_factor", 0.0f);
//				maskFilter.apply(new GLTexture[]{bufferScreen.getTexture(), surfaceMask}, maskedTex);
//				applyEdgeBlendToTexture(maskedTex);
            } else {
                applyEdgeBlendToTexture(bufferScreen.get());
            }
        }
        g.beginDraw();
        g.noStroke();
        g.beginShape(PApplet.QUADS);

        if (this.isUsingSurfaceMask() || this.isUsingEdgeBlend()) {
            g.texture(maskedTex);
            tOffX = 0;
            tOffY = 0;
            tWidth = maskedTex.width;
            tHeight = maskedTex.height;
        } else {
            g.texture(tex);
            if (bufferScreen != null)
                bufferScreen = null;
        }


        for (int i = 0; i < GRID_RESOLUTION; i++) {
            for (int j = 0; j < GRID_RESOLUTION; j++) {


                g.vertex(vertexPoints[i][j].x,
                        vertexPoints[i][j].y,
                        vertexPoints[i][j].z + currentZ,
                        ((float) i / GRID_RESOLUTION) * tWidth + tOffX,
                        ((float) j / GRID_RESOLUTION) * tHeight + tOffY);

                g.vertex(vertexPoints[i + 1][j].x,
                        vertexPoints[i + 1][j].y,
                        vertexPoints[i + 1][j].z + currentZ,
                        (((float) i + 1) / GRID_RESOLUTION) * tWidth + tOffX,
                        ((float) j / GRID_RESOLUTION) * tHeight + tOffY);

                g.vertex(vertexPoints[i + 1][j + 1].x,
                        vertexPoints[i + 1][j + 1].y,
                        vertexPoints[i + 1][j + 1].z + currentZ,
                        (((float) i + 1) / GRID_RESOLUTION) * tWidth + tOffX,
                        (((float) j + 1) / GRID_RESOLUTION) * tHeight + tOffY);

                g.vertex(vertexPoints[i][j + 1].x,
                        vertexPoints[i][j + 1].y,
                        vertexPoints[i][j + 1].z + currentZ,
                        ((float) i / GRID_RESOLUTION) * tWidth + tOffX,
                        (((float) j + 1) / GRID_RESOLUTION) * tHeight + tOffY);


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
//		Point3D[] sourcePoints = cornerPoints.clone();
//		switch(direction){
//		case 0:
//			cornerPoints[0] = sourcePoints[1];
//			cornerPoints[1] = sourcePoints[2];
//			cornerPoints[2] = sourcePoints[3];
//			cornerPoints[3] = sourcePoints[0];
//			this.updateTransform();
//			break;
//		case 1:
//			cornerPoints[0] = sourcePoints[3];
//			cornerPoints[1] = sourcePoints[0];
//			cornerPoints[2] = sourcePoints[1];
//			cornerPoints[3] = sourcePoints[2];
//			this.updateTransform();
//			break;
//		}
    }

    /**
     * Translates a point on the screen into a point in the surface. (not implemented in Bezier Surfaces yet)
     *
     * @param x
     * @param y
     * @return
     */
    public Point3D screenCoordinatesToQuad(float x, float y) {
        //TODO :: maybe add this code
        return null;
    }

    /**
     * Set index of which corner is active
     *
     * @param activePoint
     */
    public void setActivePoint(int activePoint) {
        this.activePoint = activePoint;
    }

    /**
     * Set target bezier control point to coordinates
     *
     * @param pointIndex
     * @param x
     * @param y
     */
    public void setBezierPoint(int pointIndex, float x, float y) {
        this.bezierPoints[pointIndex].x = x;
        this.bezierPoints[pointIndex].y = y;
        this.updateTransform();
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
     * Set target corner point to coordinates
     *
     * @param pointIndex
     * @param x
     * @param y
     */
    public void setCornerPoint(int pointIndex, float x, float y) {
        this.cornerPoints[pointIndex].x = x;
        this.cornerPoints[pointIndex].y = y;
        this.updateTransform();
    }

    /**
     * Manually set coordinates for all corners of the surface
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
    }

    public void setMaskFile(File maskFile) {
        this.maskFile = maskFile;
    }

    /**
     * Set surface to calibration mode
     */
    public void setModeCalibrate() {
        this.MODE = this.MODE_CALIBRATE;
    }

    /**
     * Set surface to render mode
     */
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

    /**
     * Set target bezier control to selected
     *
     * @param selectedBezierControl
     */
    public void setSelectedBezierControl(int selectedBezierControl) {
        this.selectedBezierControl = selectedBezierControl;
    }

    /**
     * Set target corner to selected
     *
     * @param selectedCorner
     */
    public void setSelectedCorner(int selectedCorner) {
        this.selectedCorner = selectedCorner;
    }

    /**
     * Set parameters for shaking the surface. Strength == max Z-displacement, Speed == vibration speed, FallOfSpeed 1-1000 == how fast strength is diminished
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
    
    public int getSketchIndex() {
		return sketchIndex;
	}

	public void setSketchIndex(int sketchIndex) {
		this.sketchIndex = sketchIndex;
	}    

    /**
     * Tells surface to shake (will only do something if setShake has been called quite recently)
     */
    public void shake() {
        if (shaking) {
            shakeAngle += (float) (shakeSpeed / 1000);
            shakeStrength *= ((float) this.fallOfSpeed / 1000);
            float shakeZ = (float) (Math.sin(shakeAngle) * shakeStrength);
            this.setZ(shakeZ);
            if (shakeStrength < 1) {
                shaking = false;
            }
        }
    }

    /**
     * Toggle if surface is locked
     */
    public void toggleLocked() {
        this.isLocked = !this.isLocked;
    }

    /**
     * Toggle surface mode
     */
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
     * Recalculates all coordinates of the surface.
     * Must be called whenever any change has been done to the surface.
     */
    public void updateTransform() {

        for (int i = 0; i <= GRID_RESOLUTION; i++) {
            for (int j = 0; j <= GRID_RESOLUTION; j++) {

                float start_x = parent.bezierPoint(cornerPoints[0].x, bezierPoints[0].x, bezierPoints[7].x, cornerPoints[3].x, (float) j / GRID_RESOLUTION);
                float end_x = parent.bezierPoint(cornerPoints[1].x, bezierPoints[3].x, bezierPoints[4].x, cornerPoints[2].x, (float) j / GRID_RESOLUTION);

                float start_y = parent.bezierPoint(cornerPoints[0].y, bezierPoints[0].y, bezierPoints[7].y, cornerPoints[3].y, (float) j / GRID_RESOLUTION);
                float end_y = parent.bezierPoint(cornerPoints[1].y, bezierPoints[3].y, bezierPoints[4].y, cornerPoints[2].y, (float) j / GRID_RESOLUTION);

                float x = parent.bezierPoint(start_x, ((bezierPoints[1].x - bezierPoints[6].x) * (1.0f - (float) j / GRID_RESOLUTION)) + bezierPoints[6].x, ((bezierPoints[2].x - bezierPoints[5].x) * (1.0f - (float) j / GRID_RESOLUTION)) + bezierPoints[5].x, end_x, (float) i / GRID_RESOLUTION);
                float y = parent.bezierPoint(start_y, ((bezierPoints[1].y - bezierPoints[6].y) * (1.0f - (float) j / GRID_RESOLUTION)) + bezierPoints[6].y, ((bezierPoints[2].y - bezierPoints[5].y) * (1.0f - (float) j / GRID_RESOLUTION)) + bezierPoints[5].y, end_y, (float) i / GRID_RESOLUTION);

                //the formula for Orthographic Projection
                //x = cos(latitude) * sin(longitude-referenceLongitude);
                //y = cos(referenceLatitude)*sin(latitude)-sin(referenceLatitude)*cos(latitude)*cos(longitude-referenceLongitude);
                //http://mathworld.wolfram.com/OrthographicProjection.html

                float pi1 = (float) ((Math.PI) / GRID_RESOLUTION);

                float xfix = (float) (Math.cos((j - (GRID_RESOLUTION / 2)) * pi1) * Math.sin((i * pi1) - ((float) (GRID_RESOLUTION / 2) * pi1))) * horizontalForce;
                float yfix = (float) (Math.cos((float) (GRID_RESOLUTION / 2) * pi1) * Math.sin(j * pi1) - Math.sin((float) (GRID_RESOLUTION / 2) * pi1) * Math.cos(j * pi1) * Math.cos((i * pi1) - ((float) (GRID_RESOLUTION / 2) * pi1))) * verticalForce;

                vertexPoints[i][j] = new Point3D(x + xfix, y + yfix, 0);
            }
        }

        poly = new Polygon();
        for (int w = 0; w < 4; w++) {
            for (int i = 0; i < GRID_RESOLUTION; i++) {
                switch (w) {
                    case 0:
                        poly.addPoint((int) vertexPoints[i][0].x, (int) vertexPoints[i][0].y);
                        break;

                    case 1:
                        poly.addPoint((int) vertexPoints[GRID_RESOLUTION][i].x, (int) vertexPoints[GRID_RESOLUTION][i].y);
                        break;

                    case 2:
                        poly.addPoint((int) vertexPoints[GRID_RESOLUTION - i][GRID_RESOLUTION].x, (int) vertexPoints[GRID_RESOLUTION - i][GRID_RESOLUTION].y);
                        break;

                    case 3:
                        poly.addPoint((int) vertexPoints[0][GRID_RESOLUTION - i].x, (int) vertexPoints[0][GRID_RESOLUTION - i].y);
                        break;
                }
            }
        }
    }
}
