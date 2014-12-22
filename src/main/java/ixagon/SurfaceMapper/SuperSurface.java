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

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.io.File;

public interface SuperSurface {
    public final static int QUAD = 0;
    public final static int BEZIER = 1;

    public static int DEFAULT_SIZE = 100;

    public void clearSurfaceMask();

    /**
     * Decrease the amount of horizontal displacement force used for spherical mapping for bezier surfaces. (using orthographic projection)
     */
    public void decreaseHorizontalForce();

    /**
     * Decrease the amount of subdivision
     */
    public void decreaseResolution();

    /**
     * Decrease the amount of horizontal displacement force used for spherical mapping for bezier surfaces. (using orthographic projection)
     */
    public void decreaseVerticalForce();

    /**
     * Returns index 0-7 if coordinates are on a bezier control
     *
     * @param mX
     * @param mY
     * @return
     */
    public int getActiveBezierPointIndex(int mX, int mY);

    /**
     * Returns index 0-3 if coordinates are near a corner or index -2 if on a surface
     *
     * @param mX
     * @param mY
     * @return
     */
    public int getActiveCornerPointIndex(int mX, int mY);

    /**
     * Get the index of active corner (or surface)
     *
     * @return
     */
    public int getActivePoint();

    /**
     * Calculates and returns the surfaces area in squarepixels.
     *
     * @return
     */
    public double getArea();

    /**
     * Get target Bezier control point
     *
     * @param index
     * @return
     */
    public Point3D getBezierPoint(int index);

    /**
     * Get the width of the left edge blend
     *
     * @return
     */
    public float getBlendLeftSize();

    /**
     * Get the width of the right edge blend
     *
     * @return
     */
    public float getBlendRightSize();

    /**
     * Get the average center point of the surface
     *
     * @return
     */
    public Point3D getCenter();

    /**
     * Get the surfaces current fill color in calibration mode
     *
     * @return
     */
    public int getColor();

    /**
     * Get the target corner point
     *
     * @param index
     * @return
     */
    public Point3D getCornerPoint(int index);

    /**
     * Get all corner points
     *
     * @return
     */
    public Point3D[] getCornerPoints();

    /**
     * Get the amount of horizontal displacement force used for spherical mapping for bezier surfaces.
     */
    public int getHorizontalForce();

    /**
     * Get the surfaces ID
     *
     * @return
     */
    public int getId();

    public double getLongestSide();

    public File getMaskFile();

    /**
     * Get the surfaces polygon
     *
     * @return
     */
    public Polygon getPolygon();

    /**
     * The the amount of subdivision currently used
     *
     * @return
     */
    public int getRes();

    /**
     * Get the currently selected bezier control
     *
     * @return
     */
    public int getSelectedBezierControl();

    /**
     * Get the currently selected corner
     *
     * @return
     */
    public int getSelectedCorner();

    public PImage getSurfaceMask();

    public String getSurfaceName();

    /**
     * See which type this surface is
     *
     * @return
     */
    public int getSurfaceType();

    public PVector[] getTextureWindow();

    /**
     * Get the amount of vertical displacement force used for spherical mapping for bezier surfaces.
     */
    public int getVerticalForce();

    /**
     * Increase the amount of horizontal displacement force used for spherical mapping for bezier surfaces. (using orthographic projection)
     */
    public void increaseHorizontalForce();

    /**
     * Increase the amount of subdivision
     */
    public void increaseResolution();

    /**
     * Increase the amount of vertical displacement force used for spherical mapping for bezier surfaces. (using orthographic projection)
     */
    public void increaseVerticalForce();

    /**
     * See if the surface has been set to blend on the left
     *
     * @return
     */
    public boolean isBlendLeft();

    /**
     * See if the surface has been set to blend on the right
     *
     * @return
     */
    public boolean isBlendRight();

    /**
     * See if we can move the cornerpoint of the surface
     *
     * @return
     */
    public boolean isCornerMovementAllowed();

    /**
     * See if surface is hidden
     *
     * @return
     */
    public boolean isHidden();

    /**
     * Returns true if coordinates are inside a surface
     *
     * @param mX
     * @param mY
     * @return
     */
    public boolean isInside(float mX, float mY);

    /**
     * See if the surface is locked
     *
     * @return
     */
    public boolean isLocked();

    /**
     * See if the surface is selected
     *
     * @return
     */
    public boolean isSelected();

    /**
     * See if the surface is using edge blend
     *
     * @return
     */

    public boolean isUsingEdgeBlend();

    public boolean isUsingSurfaceMask();

    /**
     * Renders the surface in calibration mode
     *
     * @param g
     */
    public void render(PGraphics g);

    /**
     * Render the surface with texture
     *
     * @param g
     * @param tex
     */
    public void render(PGraphics g, PImage tex);

    /**
     * Rotate the corners of surface (0=ClockWise, 1=CounterClockWise)
     * TODO Broken for Bezier Surfaces
     *
     * @param direction
     */
    public void rotateCornerPoints(int direction);

    /**
     * Translates a point on the screen into a point in the surface. (not implemented in Bezier Surfaces)
     *
     * @param x
     * @param y
     * @return
     */
    public Point3D screenCoordinatesToQuad(float x, float y);

    /**
     * Set index of which corner is active
     *
     * @param activePoint
     */
    public void setActivePoint(int activePoint);

    /**
     * Set target bezier control point to coordinates
     *
     * @param pointIndex
     * @param x
     * @param y
     */
    public void setBezierPoint(int pointIndex, float x, float y);

    /**
     * Set if the left side should be blended
     *
     * @param blendLeft
     */
    public void setBlendLeft(boolean blendLeft);

    /**
     * Set the width of the left edge blend
     *
     * @return
     */
    public void setBlendLeftSize(float blendLeftSize);

    /**
     * Set if the right side should be blended
     *
     * @param blendRight
     */
    public void setBlendRight(boolean blendRight);

    /**
     * Set the width of the right edge blend
     *
     * @return
     */
    public void setBlendRightSize(float blendRightSize);

    /**
     * Set the width of the buffer offscreen
     */
    public void setBufferScreenWidth(int width);

    /**
     * Set the fill color of the surface in calibration mode
     *
     * @param ccolor
     */
    public void setColor(int ccolor);

    /**
     * Set target corner point to coordinates
     *
     * @param pointIndex
     * @param x
     * @param y
     */
    public void setCornerPoint(int pointIndex, float x, float y);

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
    public void setCornerPoints(float x0, float y0, float x1, float y1, float x2, float y2, float x3, float y3);

    /**
     * Set if surface is hidden
     *
     * @param hide
     */
    public void setHide(boolean hide);

    /**
     * Set the ID of the surface
     *
     * @param id
     */
    public void setId(int id);

    /**
     * Set if the surface is locked
     *
     * @param isLocked
     */
    public void setLocked(boolean isLocked);

    public void setMaskFile(File maskFile);

    /**
     * Set surface to calibration mode
     */
    public void setModeCalibrate();

    /**
     * Set surface to render mode
     */
    public void setModeRender();

    /**
     * Set if the surface is selected
     *
     * @param selected
     */
    public void setSelected(boolean selected);

    /**
     * Set target bezier control to selected
     *
     * @param selectedBezierControl
     */
    public void setSelectedBezierControl(int selectedBezierControl);

    /**
     * Set target corner to selected
     *
     * @param selectedCorner
     */
    public void setSelectedCorner(int selectedCorner);

    /**
     * Set parameters for shaking the surface. Strength == max Z-displacement, Speed == vibration speed, FallOfSpeed 1-1000 == how fast strength is diminished
     *
     * @param strength
     * @param speed
     * @param fallOfSpeed
     */
    public void setShake(int strength, int speed, int fallOfSpeed);

    public void setSurfaceMask(PImage mask);

    /**
     * Sets the name of the surface
     *
     * @param name
     */
    public void setSurfaceName(String name);

    public void setTextureWindow(float x, float y, float width, float height);

    /**
     * Set the Z displacement for all coordinates of the surface
     *
     * @param z
     */
    public void setZ(float z);

    /**
     * Tells surface to shake (will only do something if setShake has been called quite recently)
     */
    public void shake();

    /**
     * Toggle if surface is locked
     */
    public void toggleLocked();

    /**
     * Toggle surface mode
     */
    public void toggleMode();


}
