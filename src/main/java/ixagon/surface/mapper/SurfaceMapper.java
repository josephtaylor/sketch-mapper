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

import java.awt.Event;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jto.processing.sketch.mapper.Sketch;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.XML;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class SurfaceMapper {
    final static public int MODE_RENDER = 0;
    final static public int MODE_CALIBRATE = 1;
    final static public int CMD = 157;
    public final String VERSION = "1";
    public int MODE = MODE_CALIBRATE;
    protected ArrayList<SuperSurface> surfaces;
    protected ArrayList<SuperSurface> selectedSurfaces;
    protected boolean snap = true;
    protected PVector prevMouse = new PVector();
    protected boolean ctrlDown;
    protected boolean altDown;
    protected boolean grouping;
    protected Rectangle selectionTool;
    protected PVector startPos;
    protected boolean isDragging;
    protected boolean disableSelectionTool;
    private PApplet parent;
    private boolean allowUserInput;
    private int snapDistance = 30;
    private int selectionDistance = 15;
    private int selectionMouseColor;
    private int numAddedSurfaces = 0;
    private PImage backgroundTexture;
    private boolean usingBackground = false;
    private int[] ccolor;
    private int width;
    private int height;

    private PFont idFont;

    private boolean debug = true;

    private boolean shaking;
    private int shakeStrength;
    private int shakeSpeed;
    private float shakeAngle;
    private float shakeZ;

    private boolean enableSelectionMouse;

    private Object eventHandlerObject;
    private Method eventHandlerMethod;
    private String eventHandlerMethodName;
    private List<Sketch> sketchList = new ArrayList<Sketch>();

    /**
     * Create instance of IxKeystone
     *
     * @param parent
     * @param width
     * @param height
     */

    public SurfaceMapper(PApplet parent, int width, int height) {
        this.parent = parent;
        this.enableMouseEvents();
        this.parent.registerMethod("keyEvent", this);
        this.width = width;
        this.height = height;
        this.ccolor = new int[0];
        this.idFont = parent.createFont("Verdana", 80);
        this.setSelectionMouseColor(0xFFCCCCCC);
        surfaces = new ArrayList<SuperSurface>();
        selectedSurfaces = new ArrayList<SuperSurface>();
        allowUserInput = true;
        enableSelectionMouse = true;
    }

    public static <T> void removeDuplicates(ArrayList<T> list) {
        int size = list.size();
        int out = 0;
        {
            final Set<T> encountered = new HashSet<T>();
            for (int in = 0; in < size; in++) {
                final T t = list.get(in);
                final boolean first = encountered.add(t);
                if (first) {
                    list.set(out++, t);
                }
            }
        }
        while (out < size) {
            list.remove(--size);
        }
    }

    public void addEventHandler(Object object, String methodName) {
        eventHandlerObject = object;
        eventHandlerMethodName = methodName;
        try {
            eventHandlerMethod = object.getClass().getMethod(methodName, new Class[] { int.class });
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add a surface to selected surfaces
     *
     * @param cps
     */
    public void addSelectedSurface(SuperSurface cps) {
        selectedSurfaces.add(cps);
    }

    /**
     * Add a surface to surfaceMapper
     *
     * @param superSurface
     * @return
     */
    public SuperSurface addSurface(SuperSurface superSurface) {
        if (ccolor.length > 0)
            superSurface.setColor(ccolor[superSurface.getId() % ccolor.length]);
        superSurface.setModeCalibrate();
        surfaces.add(superSurface);

        if (superSurface.getId() >= numAddedSurfaces)
            numAddedSurfaces = superSurface.getId() + 1;

        return surfaces.get(surfaces.size() - 1);
    }

    /**
     * Places the surface last in the surfaces array, i.e. on top.
     *
     * @param index
     */
    public void bringSurfaceToFront(int index) {
        SuperSurface s = surfaces.get(index);
        surfaces.remove(index);
        surfaces.add(s);
    }

    /**
     * Clears the arraylist of selected surfaces.
     */
    public void clearSelectedSurfaces() {
        selectedSurfaces.clear();
    }

    /**
     * Remove all surfaces
     */
    public void clearSurfaces() {
        selectedSurfaces.clear();
        surfaces.clear();
    }
    
    public void bringFrontSurface() {
    	
    }
    
    public void bringBackSurface() {
    	
    }

    /**
     * Creates a Bezier surface with perspective transform. Res is the amount of subdivisioning. Returns the surface after it has been created.
     *
     * @param res
     * @return
     */
    public SuperSurface createBezierSurface(int res) {
        SuperSurface s = new BezierSurface(parent, this, parent.mouseX, parent.mouseY, res, getNextIndex());
        if (ccolor.length > 0)
            s.setColor(ccolor[numAddedSurfaces % ccolor.length]);
        s.setModeCalibrate();
        surfaces.add(s);
        numAddedSurfaces++;
        return s;
    }

    /**
     * Creates a Bezier surface at X/Y with perspective transform. Res is the amount of subdivisioning. Returns the surface after it has been created.
     *
     * @param res
     * @param x
     * @param y
     * @return
     */
    public SuperSurface createBezierSurface(int res, int x, int y) {

        SuperSurface s = new BezierSurface(parent, this, x, y, res, getNextIndex());
        if (ccolor.length > 0)
            s.setColor(ccolor[numAddedSurfaces % ccolor.length]);
        s.setModeCalibrate();
        surfaces.add(s);
        numAddedSurfaces++;
        return s;
    }

    /**
     * Creates a Quad surface with perspective transform. Res is the amount of subdivisioning. Returns the surface after it has been created.
     *
     * @param res
     * @return
     */
    public SuperSurface createQuadSurface(int res) {
        SuperSurface s = new QuadSurface(parent, this, parent.mouseX, parent.mouseY, res, getNextIndex());
        if (ccolor.length > 0)
            s.setColor(ccolor[numAddedSurfaces % ccolor.length]);
        s.setModeCalibrate();
        surfaces.add(s);
        numAddedSurfaces++;
        return s;
    }

    /**
     * Creates a Quad surface at X/Y with perspective transform. Res is the amount of subdivisioning. Returns the surface after it has been created.
     *
     * @param res
     * @param x
     * @param y
     * @return
     */
    public SuperSurface createQuadSurface(int res, int x, int y) {
        SuperSurface s = new QuadSurface(parent, this, x, y, res, getNextIndex());
        if (ccolor.length > 0)
            s.setColor(ccolor[numAddedSurfaces % ccolor.length]);
        s.setModeCalibrate();
        surfaces.add(s);
        numAddedSurfaces++;
        return s;
    }

    /**
     * Unregisters Mouse Event listener for the SurfaceMapper
     */
    public void disableMouseEvents() {
        this.parent.unregisterMethod("mouseEvent", this);
    }

    /**
     * Registers Mouse Event listener for the SurfaceMapper
     */
    public void enableMouseEvents() {
        this.parent.registerMethod("mouseEvent", this);
    }

    /**
     * Check if coordinates is inside any of the bezier surfaces.
     *
     * @param mX
     * @param mY
     * @return
     */
    public boolean findActiveBezierSurface(float mX, float mY) {
        for (SuperSurface surface : surfaces) {
            if (surface.isInside(mX, mY) && surface.getSurfaceType() == SuperSurface.BEZIER) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if coordinates is inside any of the quad surfaces.
     *
     * @param mX
     * @param mY
     * @return
     */
    public boolean findActiveQuadSurface(float mX, float mY) {
        for (SuperSurface surface : surfaces) {
            if (surface.isInside(mX, mY) && surface.getSurfaceType() == SuperSurface.QUAD) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if coordinates is inside any of the surfaces.
     *
     * @param mX
     * @param mY
     * @return
     */
    public boolean findActiveSurface(float mX, float mY) {
        for (SuperSurface surface : surfaces) {
            if (surface.isInside(mX, mY)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Fire event to object
     */
    public void fireEvent(int id) {
        if (eventHandlerMethod != null) {
            try {
                eventHandlerMethod.invoke(eventHandlerObject, id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the array of colors used in calibration mode for coloring the surfaces.
     *
     * @return
     */
    public int[] getColor() {
        return ccolor;
    }

    /**
     * See if debug mode is on.
     *
     * @return
     */
    public boolean getDebug() {
        return debug;
    }

    /**
     * Is the selection tool disabled?
     *
     * @return
     */
    public boolean getDisableSelectionTool() {
        return disableSelectionTool;
    }

    /**
     * Get font for drawing text
     *
     * @return
     */
    public PFont getIdFont() {
        return idFont;
    }

    /**
     * Check which mode is enabled (render or calibrate)
     *
     * @return
     */
    public int getMode() {
        return this.MODE;
    }

    private int getNextIndex() {
        if (surfaces.isEmpty()) {
            return 0;
        }

        int index = 0;
        for (SuperSurface superSurface : surfaces) {
            if (superSurface.getId() > index) {
                index = superSurface.getId();
            }
        }
        return index + 1;
    }

    public int getNumAddedSurfaces() {
        return numAddedSurfaces;
    }

    /**
     * Get previous mouse position
     *
     * @return
     */
    public PVector getPrevMouse() {
        return prevMouse;
    }

    /**
     * Get the selected surfaces
     *
     * @return
     */
    public ArrayList<SuperSurface> getSelectedSurfaces() {
        return selectedSurfaces;
    }

    /**
     * Get current max distance for an object to be selected
     *
     * @return
     */
    public int getSelectionDistance() {
        return selectionDistance;
    }

    public int getSelectionMouseColor() {
        return selectionMouseColor;
    }

    /**
     * Returns the rectangle used for selecting surfaces
     *
     * @return
     */
    public Rectangle getSelectionTool() {
        return selectionTool;
    }

    public List<Sketch> getSketchList() {
        return sketchList;
    }

    /**
     * See if snap mode is used
     *
     * @return
     */
    public boolean getSnap() {
        return snap;
    }

    /**
     * See the snap distance
     *
     * @return
     */
    public int getSnapDistance() {
        return snapDistance;
    }

    /**
     * Get surface by Id.
     *
     * @param id
     * @return
     */
    public SuperSurface getSurfaceById(int id) {
        for (SuperSurface superSurface : surfaces) {
            if (superSurface.getId() == id) {
                return superSurface;
            }
        }
        return null;
    }

    /**
     * Get all surfaces
     *
     * @return surfaces
     */
    public ArrayList<SuperSurface> getSurfaces() {
        return surfaces;
    }

    /**
     * Check if any user event is allowed
     *
     * @return
     */
    public boolean isAllowUserInput() {
        return allowUserInput;
    }

    /**
     * Is ALT pressed?
     *
     * @return
     */
    public boolean isAltDown() {
        return altDown;
    }

    /**
     * Is CTRL pressed?
     *
     * @return
     */
    public boolean isCtrlDown() {
        return ctrlDown;
    }

    /**
     * @return
     */
    public boolean isDragging() {
        return isDragging;
    }

    public boolean isEnableSelectionMouse() {
        return enableSelectionMouse;
    }

    /**
     * Check if multiple surfaces are being manipulated
     *
     * @return
     */
    public boolean isGrouping() {
        return grouping;
    }

    /**
     * Boolean used to know if the background image should be rendered in calibration mode.
     *
     * @return
     */
    public boolean isUsingBackground() {
        return usingBackground;
    }

    /**
     * KeyEvent method
     *
     * @param k
     */
    public void keyEvent(KeyEvent k) {
        if (MODE == MODE_RENDER)
            return; // ignore everything unless we're in calibration mode

        switch (k.getAction()) {
            case KeyEvent.RELEASE:

                switch (k.getKeyCode()) {

                    case KeyEvent.CTRL:
                    case CMD:
                        ctrlDown = false;
                        break;

                    case KeyEvent.ALT:
                        altDown = false;
                        break;
                }
                break;

            case KeyEvent.PRESS:

                switch (k.getKeyCode()) {
                    case '1':
                        if (selectedSurfaces.size() == 1)
                            selectedSurfaces.get(0).setSelectedCorner(0);
                        break;

                    case '2':
                        if (selectedSurfaces.size() == 1)
                            selectedSurfaces.get(0).setSelectedCorner(1);
                        break;

                    case '3':
                        if (selectedSurfaces.size() == 1)
                            selectedSurfaces.get(0).setSelectedCorner(2);
                        break;

                    case '4':
                        if (selectedSurfaces.size() == 1)
                            selectedSurfaces.get(0).setSelectedCorner(3);

                    case '9':
                        if (selectedSurfaces.size() == 1)
                            selectedSurfaces.get(0).setId(0);
                        break;

                    case 38:
                        if (!altDown && !ctrlDown) {
                            for (SuperSurface ss : selectedSurfaces) {
                                movePoint(ss, 0, -1);
                            }
                        }
                        // ALT is Offset!
                        if (altDown && !ctrlDown) {
                            for (SuperSurface ss : selectedSurfaces) {
                                ss.setTextureWindow(ss.getTextureWindow()[0].x,
                                        ss.getTextureWindow()[0].y + 0.05f, ss.getTextureWindow()[1].x, ss.getTextureWindow()[1].y);
                            }
                        }

                        // CTRL is Size!
                        if (!altDown && ctrlDown) {
                            for (SuperSurface ss : selectedSurfaces) {
                                ss.setTextureWindow(ss.getTextureWindow()[0].x, ss.getTextureWindow()[0].y, ss.getTextureWindow()[1].x,
                                        ss.getTextureWindow()[1].y + 0.05f);
                            }
                        }

                        break;

                    case 40:
                        if (!altDown && !ctrlDown) {
                            for (SuperSurface ss : selectedSurfaces) {
                                movePoint(ss, 0, 1);
                            }
                        }
                        // ALT is Offset!
                        if (altDown && !ctrlDown) {
                            for (SuperSurface ss : selectedSurfaces) {
                                ss.setTextureWindow(ss.getTextureWindow()[0].x,
                                        ss.getTextureWindow()[0].y - 0.05f, ss.getTextureWindow()[1].x, ss.getTextureWindow()[1].y);
                            }
                        }

                        // CTRL is Size!
                        if (!altDown && ctrlDown) {
                            for (SuperSurface ss : selectedSurfaces) {
                                ss.setTextureWindow(ss.getTextureWindow()[0].x, ss.getTextureWindow()[0].y, ss.getTextureWindow()[1].x,
                                        ss.getTextureWindow()[1].y - 0.05f);
                            }
                        }

                        break;

                    case 37:
                        if (!altDown && !ctrlDown) {
                            for (SuperSurface ss : selectedSurfaces) {
                                movePoint(ss, -1, 0);
                            }
                        }

                        // ALT is Offset!
                        if (altDown && !ctrlDown) {
                            for (SuperSurface ss : selectedSurfaces) {
                                ss.setTextureWindow(ss.getTextureWindow()[0].x
                                                    + 0.05f, ss.getTextureWindow()[0].y, ss.getTextureWindow()[1].x, ss.getTextureWindow()[1].y);
                            }
                        }

                        // CTRL is Size!
                        if (!altDown && ctrlDown) {
                            for (SuperSurface ss : selectedSurfaces) {
                                ss.setTextureWindow(ss.getTextureWindow()[0].x, ss.getTextureWindow()[0].y,
                                        ss.getTextureWindow()[1].x + 0.05f, ss.getTextureWindow()[1].y);
                            }
                        }

                        break;

                    case 39:
                        if (!altDown && !ctrlDown) {
                            for (SuperSurface ss : selectedSurfaces) {
                                movePoint(ss, 1, 0);
                            }
                        }

                        // ALT is Offset!
                        if (altDown && !ctrlDown) {
                            for (SuperSurface ss : selectedSurfaces) {
                                ss.setTextureWindow(ss.getTextureWindow()[0].x
                                                    - 0.05f, ss.getTextureWindow()[0].y, ss.getTextureWindow()[1].x, ss.getTextureWindow()[1].y);
                            }
                        }

                        // CTRL is Size!
                        if (!altDown && ctrlDown) {
                            for (SuperSurface ss : selectedSurfaces) {
                                ss.setTextureWindow(ss.getTextureWindow()[0].x, ss.getTextureWindow()[0].y,
                                        ss.getTextureWindow()[1].x - 0.05f, ss.getTextureWindow()[1].y);
                            }
                        }
                        break;
            /*
             * case KeyEvent.VK_O: for (SuperSurface ss : selectedSurfaces) { ss.increaseResolution(); } break;
			 *
			 * case KeyEvent.VK_P: for (SuperSurface ss : selectedSurfaces) { ss.decreaseResolution(); } break;
			 *
			 * case KeyEvent.VK_U: for (SuperSurface ss : selectedSurfaces) { ss.increaseHorizontalForce(); } break;
			 *
			 * case KeyEvent.VK_I: for (SuperSurface ss : selectedSurfaces) { ss.decreaseHorizontalForce(); } break;
			 *
			 * case KeyEvent.VK_J: for (SuperSurface ss : selectedSurfaces) { ss.increaseVerticalForce(); } break;
			 *
			 * case KeyEvent.VK_K: for (SuperSurface ss : selectedSurfaces) { ss.decreaseVerticalForce(); } break;
			 *
			 * case KeyEvent.VK_T: for (SuperSurface ss : selectedSurfaces) { ss.toggleLocked(); } break;
			 *
			 */
                    //SUPR key. Todo
                    //case 147:
                    //	removeSelectedSurfaces(); 
                    //	break;
                    case KeyEvent.CTRL:
                    case CMD:
                        ctrlDown = true;
                        grouping = true;
                        break;

                    case KeyEvent.ALT:
                        altDown = true;
                        break;
                    default:
                    	break;
                    	
                }
        }
    }

    /**
     * MouseEvent method.
     *
     * @param e
     */
    public void ksMouseEvent(MouseEvent e) {
        if (this.MODE == SurfaceMapper.MODE_RENDER)
            return;

        int mX = e.getX();
        int mY = e.getY();

        switch (e.getAction()) {
            case MouseEvent.PRESS:
                selectSurfaces(mX, mY);
                break;

            case MouseEvent.DRAG:
                if (this.MODE == SurfaceMapper.MODE_CALIBRATE) {

                    float deltaX = mX - prevMouse.x;
                    float deltaY = mY - prevMouse.y;

                    // Right mouse button drags very slowly.
                    if (e.getButton() == PApplet.RIGHT) {
                        deltaX *= 0.1;
                        deltaY *= 0.1;
                    }

                    boolean[] movingPolys = new boolean[surfaces.size()];
                    int iteration = 0;
                    for (SuperSurface ss : selectedSurfaces) {

                        movingPolys[iteration] = false;
                        // Don't allow editing of surface if it's locked!
                        if (!ss.isLocked()) {
                            if (ss.getSelectedBezierControl() != -1) {
                                ss.setBezierPoint(ss.getSelectedBezierControl(),
                                        ss.getBezierPoint(ss.getSelectedBezierControl()).x + deltaX,
                                        ss.getBezierPoint(ss.getSelectedBezierControl()).y + deltaY);
                            } else if (ss.getActivePoint() != -1) {
                                // special case.
                                // index 2000 is the center point so move all four
                                // corners.
                                if (ss.getActivePoint() == 2000) {
                                    // If multiple surfaces are selected, ALT need
                                    // to be pressed in order to move them.
                                    if ((grouping && altDown) || selectedSurfaces.size() == 1) {

                                        boolean cornerMovementAllowed = true;
                                        for (int i = 0; i < 4; i++) {
                                            Point3D[] cTemp = new Point3D[4];
                                            for (int j = 0; j < 4; j++) {
                                                cTemp[j] = new Point3D(ss.getCornerPoint(j).x, ss.getCornerPoint(j).y);
                                            }

                                            cTemp[i].x = ss.getCornerPoint(i).x + deltaX;
                                            cTemp[i].y = ss.getCornerPoint(i).y + deltaY;

                                            for (int j = 0; j < cTemp.length; j++) {
                                                if (QuadSurface.CCW(cTemp[(j + 2) % cTemp.length], cTemp[(j + 1)
                                                                                                         % cTemp.length], cTemp[j
                                                                                                                                % cTemp.length])
                                                    >= 0) {
                                                    cornerMovementAllowed = false;
                                                }
                                            }
                                        }
                                        if (cornerMovementAllowed) {
                                            for (int i = 0; i < 4; i++) {

                                                ss.setCornerPoint(i,
                                                        ss.getCornerPoint(i).x + deltaX, ss.getCornerPoint(i).y + deltaY);
                                                if (SuperSurface.BEZIER == ss.getSurfaceType()) {
                                                    ss.setBezierPoint(i,
                                                            ss.getBezierPoint(i).x + deltaX, ss.getBezierPoint(i).y + deltaY);
                                                    ss.setBezierPoint(
                                                            i + 4,
                                                            ss.getBezierPoint(i + 4).x + deltaX,
                                                            ss.getBezierPoint(i + 4).y + deltaY);
                                                }
                                            }
                                        }
                                        movingPolys[iteration] = true;
                                    }
                                } else {
                                    // Move a corner point.
                                    int index = ss.getActivePoint();
                                    ss.setCornerPoint(index,
                                            ss.getCornerPoint(ss.getActivePoint()).x + deltaX,
                                            ss.getCornerPoint(ss.getActivePoint()).y + deltaY);
                                    if (SuperSurface.BEZIER == ss.getSurfaceType()) {
                                        index = index * 2;
                                        ss.setBezierPoint(index,
                                                ss.getBezierPoint(index).x + deltaX, ss.getBezierPoint(index).y + deltaY);
                                        index = index + 1;
                                        ss.setBezierPoint(index,
                                                ss.getBezierPoint(index).x + deltaX, ss.getBezierPoint(index).y + deltaY);
                                    }
                                    movingPolys[iteration] = true;

                                }
                            }

                        }
                        iteration++;
                    }

                    for (int i = 0; i < movingPolys.length; i++) {
                        if (movingPolys[i]) {
                            disableSelectionTool = true;
                            break;
                        }
                    }

                    if (altDown)
                        disableSelectionTool = true;

                    if (!disableSelectionTool && startPos != null) {
                        selectionTool = new Rectangle((int) startPos.x, (int) startPos.y, (int) (mX - startPos.x), (int) (mY
                                                                                                                          - startPos.y));

                        PVector sToolPos = new PVector(selectionTool.x, selectionTool.y);

                        if (selectionTool.x < selectionTool.x - selectionTool.width) {
                            sToolPos.set(sToolPos.x + selectionTool.width, sToolPos.y, 0);
                        }
                        if (selectionTool.y < selectionTool.y - selectionTool.height) {
                            sToolPos.set(sToolPos.x, sToolPos.y + selectionTool.height, 0);
                        }

                        for (SuperSurface cps : surfaces) {
                            java.awt.Polygon p = cps.getPolygon();

                            if (p.intersects(sToolPos.x, sToolPos.y, Math.abs(selectionTool.width), Math.abs(selectionTool.height))) {
                                cps.setSelected(true);
                                selectedSurfaces.add(cps);
                                removeDuplicates(selectedSurfaces);
                                grouping = true;
                            } else {
                                if (!ctrlDown) {
                                    cps.setSelected(false);
                                    selectedSurfaces.remove(cps);
                                }
                            }
                        }
                    }
                    isDragging = true;
                }

                break;

            case MouseEvent.RELEASE:
                if (this.MODE == SurfaceMapper.MODE_CALIBRATE) {
                    if (snap) {
                        for (SuperSurface ss : selectedSurfaces) {
                            if (ss.getActivePoint() != 2000 && ss.getActivePoint() != -1) {
                                int closestIndex = -1;
                                int cornerIndex = -1;
                                float closestDist = this.getSnapDistance() + 1;
                                for (int j = 0; j < surfaces.size(); j++) {
                                    if (surfaces.get(j).getId() != ss.getId()) {
                                        for (int i = 0; i < surfaces.get(j).getCornerPoints().length; i++) {
                                            float dist = PApplet.dist(ss.getCornerPoint(ss.getActivePoint()).x, ss.getCornerPoint(ss.getActivePoint()).y, surfaces.get(j).getCornerPoint(i).x, surfaces.get(j).getCornerPoint(i).y);
                                            if (dist < this.getSnapDistance()) {
                                                if (dist < closestDist) {
                                                    closestDist = dist;
                                                    closestIndex = j;
                                                    cornerIndex = i;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (closestDist > -1 && closestDist < this.getSnapDistance()) {
                                    ss.setCornerPoint(ss.getActivePoint(), surfaces.get(closestIndex).getCornerPoint(cornerIndex).x, surfaces.get(closestIndex).getCornerPoint(cornerIndex).y);
                                }
                            }
                        }
                        int selection = 0;
                        for (SuperSurface cps : surfaces) {
                            cps.setActivePoint(-1);
                            if (cps.getActiveCornerPointIndex(mX, mY) != -1)
                                selection++;
                        }

                        if (isDragging)
                            selection++;

                        if (selection == 0) {
                            for (SuperSurface ss : selectedSurfaces) {
                                ss.setSelected(false);
                            }
                            grouping = false;
                            selectedSurfaces.clear();
                        }
                    }

                }
                for (SuperSurface ss : selectedSurfaces) {
                    fireEvent(ss.getId());
                }
                startPos = new PVector(0, 0);
                selectionTool = null;
                disableSelectionTool = false;
                isDragging = false;
                break;

        }
        prevMouse = new PVector(mX, mY, 0);
    }

    /**
     * Load projection map from file
     *
     * @param file
     */
    public void load(File file) {
        if (this.MODE == SurfaceMapper.MODE_CALIBRATE) {
            if (file.exists()) {
                this.setGrouping(false);
                selectedSurfaces.clear();
                surfaces.clear();
                try {
                    XML root = new XML(file);
                    for (int i = 0; i < root.getChildCount(); i++) {
                        if (root.getChild(i).getName().equals("surface")) {
                            SuperSurface loaded = null;
                            if (SuperSurface.BEZIER == root.getChild(i).getInt("type")) {
                                loaded = new BezierSurface(parent, this, root.getChild(i), root.getChild(i).getInt("id"), root.getChild(i).getString("name"));
                            } else {
                                loaded = new QuadSurface(parent, this, root.getChild(i), root.getChild(i).getInt("id"), root.getChild(i).getString("name"));
                            }
                            if (ccolor.length > 0)
                                loaded.setColor(ccolor[numAddedSurfaces % ccolor.length]);
                            loaded.setModeCalibrate();
                            final String sketch = root.getChild(i).getString("sketch");
                            if (null != sketch && sketch.trim().length() > 0) {
                                loadSketch(loaded, sketch);
                            }
                            surfaces.add(loaded);
                            numAddedSurfaces++;
                        }
                    }
                    if (this.getDebug())
                        PApplet.println("Projection layout loaded from " + file.getName() + ". " + surfaces.size()
                                        + " surfaces were loaded!");
                } catch (Exception e) {
                    PApplet.println("Error loading configuration!!!");
                    e.printStackTrace();
                }
            } else {
                if (this.getDebug())
                    PApplet.println("ERROR loading XML! No projection layout exists!");
            }
        }

    }

    private void loadSketch(final SuperSurface loaded, final String sketchName) {
        Optional<Sketch> sketch = sketchList.stream().filter(s -> sketchName.equals(s.getName())).findFirst();
        if (sketch.isPresent()) {
            loaded.setSketch(sketch.get());
        }
    }

    /**
     * MouseEvent method. Forwards the MouseEvent to ksMouseEvent if user input is allowed
     *
     * @param e
     */
    public void mouseEvent(MouseEvent e) {
        if (allowUserInput) {
            ksMouseEvent(e);
        }
    }

    /**
     * Move a point of a surface
     *
     * @param ss
     * @param x
     * @param y
     */
    public void movePoint(SuperSurface ss, int x, int y) {
        int index = ss.getSelectedCorner();

        ss.setCornerPoint(index, ss.getCornerPoint(index).x + x, ss.getCornerPoint(index).y + y);
        if (SuperSurface.BEZIER == ss.getSurfaceType()) {
            index = index * 2;
            ss.setBezierPoint(index, ss.getBezierPoint(index).x + x, ss.getBezierPoint(index).y + y);
            index = index + 1;
            ss.setBezierPoint(index, ss.getBezierPoint(index).x + x, ss.getBezierPoint(index).y + y);
        }
    }

    /**
     * Delete the selected surfaces
     */
    public void removeSelectedSurfaces() {
        for (SuperSurface ss : selectedSurfaces) {
            for (int i = surfaces.size() - 1; i >= 0; i--) {
                if (ss.getId() == surfaces.get(i).getId()) {
                    if (ss.isLocked())
                        return;
                    if (this.getDebug())
                        PApplet.println("SurfaceMapper --> DELETED SURFACE with ID: #" + ss.getId());
                    surfaces.remove(i);
                }
            }
        }
        this.setGrouping(false);
        selectedSurfaces.clear();
        if (surfaces.size() == 0)
            numAddedSurfaces = 0;
    }

    /**
     * Render method used when calibrating. Shouldn't be used for final rendering.
     *
     * @param glos
     */
    public void render(PGraphics glos) {
        //        glos.beginDraw();
        //        glos.endDraw();
        if (MODE == MODE_CALIBRATE) {
            parent.cursor();
            glos.beginDraw();

            if (this.isUsingBackground()) {
                glos.image(backgroundTexture, 0, 0, width, height);
            }

            glos.fill(0, 40);
            glos.noStroke();
            glos.rect(-2, -2, width + 4, height + 4);
            glos.stroke(255, 255, 255, 40);
            glos.strokeWeight(1);
            /*
             * float gridRes = 32.0f;
			 *
			 * float step = (float) (width / gridRes);
			 *
			 * for (float i = 1; i < width; i += step) { glos.line(i, 0, i, parent.height); }
			 *
			 * step = (float) (height / gridRes);
			 *
			 * for (float i = 1; i < width; i += step) { glos.line(0, i, parent.width, i); }
			 */
            glos.stroke(255);
            glos.strokeWeight(2);
            glos.line(1, 1, width - 1, 1);
            glos.line(width - 1, 1, width - 1, height - 1);
            glos.line(1, height - 1, width - 1, height - 1);
            glos.line(1, 1, 1, height - 1);
            glos.endDraw();

            for (int i = 0; i < surfaces.size(); i++) {
                surfaces.get(i).render(glos);
            }

            // Draw circles for SelectionDistance or SnapDistance (snap if CMD
            // is down)
            glos.beginDraw();
            if (enableSelectionMouse) {
                if (!ctrlDown) {
                    glos.ellipseMode(PApplet.CENTER);
                    glos.fill(this.getSelectionMouseColor(), 100);
                    glos.noStroke();
                    glos.ellipse(parent.mouseX, parent.mouseY, this.getSelectionDistance() * 2, this.getSelectionDistance() * 2);
                } else {
                    glos.ellipseMode(PApplet.CENTER);
                    glos.fill(255, 0, 0, 100);
                    glos.noStroke();
                    glos.ellipse(parent.mouseX, parent.mouseY, this.getSnapDistance() * 2, this.getSnapDistance() * 2);
                }
            }

            if (selectionTool != null && !disableSelectionTool) {
                glos.stroke(255, 100);
                glos.strokeWeight(1);
                glos.fill(0, 200, 255, 50);
                glos.rect(selectionTool.x, selectionTool.y, selectionTool.width, selectionTool.height);
                glos.noStroke();
            }

            glos.endDraw();

        } else {
            parent.noCursor();
        }
    }

    /**
     * Puts all projection mapping data in the XMLElement
     *
     * @param root
     */
    public void save(XML root) {
        root.setName("ProjectionMap");
        // create XML elements for each surface containing the resolution
        // and control point data
        for (SuperSurface s : surfaces) {
            XML surf = new XML("surface");
            surf.setInt("type", s.getSurfaceType());
            surf.setInt("id", s.getId());
            surf.setString("name", s.getSurfaceName());
            surf.setInt("res", s.getRes());
            surf.setString("lock", String.valueOf(s.isLocked()));
            surf.setInt("horizontalForce", s.getHorizontalForce());
            surf.setInt("verticalForce", s.getVerticalForce());
            surf.setString("sketch", s.getSketch().getName());

            for (int i = 0; i < s.getCornerPoints().length; i++) {
                XML cp = new XML("cornerpoint");
                cp.setInt("i", i);
                cp.setFloat("x", s.getCornerPoint(i).x);
                cp.setFloat("y", s.getCornerPoint(i).y);
                surf.addChild(cp);

            }

            if (s.getSurfaceType() == SuperSurface.BEZIER) {
                for (int i = 0; i < 8; i++) {
                    XML bp = new XML("bezierpoint");
                    bp.setInt("i", i);
                    bp.setFloat("x", s.getBezierPoint(i).x);
                    bp.setFloat("y", s.getBezierPoint(i).y);
                    surf.addChild(bp);
                }
            }
            root.addChild(surf);
        }
    }

    /**
     * Save all projection mapping data to file
     *
     * @param file
     */
    public void save(File file) {
        if (this.MODE == SurfaceMapper.MODE_CALIBRATE) {
            XML root = new XML("root");
            this.save(root);
            try {
                root.save(file);
            } catch (Exception e) {
                PApplet.println(e.getStackTrace());
            }
        }
    }

    public void selectSurfaces(int mX, int mY) {
        if (this.MODE == SurfaceMapper.MODE_CALIBRATE) {
            startPos = new PVector(mX, mY);
            for (int i = surfaces.size() - 1; i >= 0; i--) {
                SuperSurface cps = surfaces.get(i);

                cps.setActivePoint(cps.getActiveCornerPointIndex(mX, mY));
                cps.setSelectedBezierControl(cps.getActiveBezierPointIndex(mX, mY));

                if (cps.getActivePoint() >= 0 || cps.getSelectedBezierControl() >= 0) {
                    if (grouping && !ctrlDown) {
                        if (!cps.isSelected()) {
                            for (SuperSurface ss : selectedSurfaces) {
                                ss.setSelected(false);
                            }
                            grouping = false;
                            selectedSurfaces.clear();
                        }
                    }

                    disableSelectionTool = true;
                    if (ctrlDown && grouping) {
                        boolean actionTaken = false;
                        if (cps.isSelected()) {
                            cps.setSelected(false);
                            for (int j = selectedSurfaces.size() - 1; j >= 0; j--) {
                                if (cps.getId() == selectedSurfaces.get(j).getId())
                                    selectedSurfaces.remove(j);
                            }
                            actionTaken = true;
                        }
                        if (!cps.isSelected() && !actionTaken) {
                            cps.setSelected(true);
                            selectedSurfaces.add(cps);
                            removeDuplicates(selectedSurfaces);
                        }
                    } else {
                        if (grouping == false) {
                            for (SuperSurface ss : selectedSurfaces) {
                                ss.setSelected(false);
                            }
                            selectedSurfaces.clear();
                            cps.setSelected(true);
                            selectedSurfaces.add(cps);
                        }
                    }

                    // no need to loop through all surfaces unless multiple
                    // surfaces has been selected
                    if (!grouping)
                        break;
                }
            }
            if (grouping) {
                int moveClick = 0;
                for (SuperSurface ss : selectedSurfaces) {
                    if (ss.getActivePoint() == 2000)
                        moveClick++;
                }
                // PApplet.println(moveClick);
                if (moveClick > 0) {
                    for (SuperSurface ss : selectedSurfaces) {
                        ss.setActivePoint(2000);
                        // PApplet.println(ss.getActivePoint());
                    }
                }
            }

        }
    }

    /**
     * Set if any user event is allowed
     *
     * @param allowUserInput
     */
    public void setAllowUserInput(boolean allowUserInput) {
        this.allowUserInput = allowUserInput;
    }

    /**
     * Optionally set a background image in calibration mode.
     *
     * @param tex
     */
    public void setBackground(PImage tex) {
        this.backgroundTexture = tex;
        this.setUsingBackground(true);
    }

    /**
     * Set the array of colors used in calibration mode for coloring the surfaces.
     *
     * @param ccolor
     */
    public void setColor(int[] ccolor) {
        this.ccolor = ccolor;
    }

    /**
     * Set CTRL pressed
     *
     * @param pressed
     */
    public void setCtrlDown(boolean pressed) {
        ctrlDown = pressed;
    }

    /**
     * Manually set debug mode. (debug mode will print more to console)
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Enable/disable selection tool
     *
     * @param disableSelectionTool
     */
    public void setDisableSelectionTool(boolean disableSelectionTool) {
        this.disableSelectionTool = disableSelectionTool;
    }

    public void setEnableSelectionMouse(boolean enableSelectionMouse) {
        this.enableSelectionMouse = enableSelectionMouse;
    }

    /**
     * Set if multiple surfaces are being manipulated
     *
     * @param grouping
     */
    public void setGrouping(boolean grouping) {
        this.grouping = grouping;
    }

    /**
     * @param isDragging
     */
    public void setIsDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }

    /**
     * Set mode to calibrate
     */
    public void setModeCalibrate() {
        this.MODE = SurfaceMapper.MODE_CALIBRATE;
        for (SuperSurface s : surfaces) {
            s.setModeCalibrate();
        }
    }

    /**
     * Set mode to render
     */
    public void setModeRender() {
        this.MODE = SurfaceMapper.MODE_RENDER;
        for (SuperSurface s : surfaces) {
            s.setModeRender();
        }
    }

    public void setNumAddedSurfaces(int numAddedSurfaces) {
        this.numAddedSurfaces = numAddedSurfaces;
    }

    /**
     * Set previous mouse position
     *
     * @param x
     * @param y
     */
    public void setPrevMouse(float x, float y) {
        prevMouse = new PVector(x, y);
    }

    /**
     * Select the surface. Deselects all previously selected surfaces.
     *
     * @param cps
     */
    public void setSelectedSurface(SuperSurface cps) {
        for (SuperSurface ss : selectedSurfaces) {
            ss.setSelected(false);
        }
        selectedSurfaces.clear();
        cps.setSelected(true);
        selectedSurfaces.add(cps);
    }

    /**
     * Set the max distance for an object to be selected
     *
     * @param selectionDistance
     */
    public void setSelectionDistance(int selectionDistance) {
        this.selectionDistance = selectionDistance;
    }

    public void setSelectionMouseColor(int selectionMouseColor) {
        this.selectionMouseColor = selectionMouseColor;
    }

    /**
     * Set the selection tool
     *
     * @param r
     */
    public void setSelectionTool(Rectangle r) {
        selectionTool = r;
    }

    /**
     * Set the selection tool
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void setSelectionTool(int x, int y, int width, int height) {
        selectionTool = new Rectangle(x, y, width, height);
    }

    /**
     * Shake all surfaces with max Z-displacement strength, vibration-speed speed, and shake decline fallOfSpeed. (min 0, max 1000 (1000 = un-ending shaking))
     *
     * @param strength
     * @param speed
     * @param fallOfSpeed
     */
    public void setShakeAll(int strength, int speed, int fallOfSpeed) {
        for (SuperSurface ss : surfaces) {
            ss.setShake(strength, speed, fallOfSpeed);
        }
    }

    public void setSketchList(List<Sketch> sketchList) {
        this.sketchList = sketchList;
    }

    /**
     * Manually set corner snap mode
     *
     * @param snap
     */
    public void setSnap(boolean snap) {
        this.snap = snap;
    }

    /**
     * Set corner snap distance
     *
     * @param snapDistance
     */
    public void setSnapDistance(int snapDistance) {
        this.snapDistance = snapDistance;
    }

    /**
     * Set if background image should rendered in calibration mode
     *
     * @param val
     */
    public void setUsingBackground(boolean val) {
        usingBackground = val;
    }

    /**
     * Update shaking for all surfaces
     */
    public void shake() {
        for (SuperSurface ss : surfaces) {
            ss.shake();
        }
    }

    /**
     * Toggle the mode
     */
    public void toggleCalibration() {
        if (MODE == MODE_RENDER)
            MODE = MODE_CALIBRATE;
        else
            MODE = MODE_RENDER;

        for (SuperSurface s : surfaces) {
            if (MODE == MODE_CALIBRATE)
                s.setModeCalibrate();
            else
                s.setModeRender();
        }
    }

    /**
     * Toggle if corner snapping is used
     */
    public void toggleSnap() {
        snap = !snap;
    }

    public String version() {
        return VERSION;
    }

}
