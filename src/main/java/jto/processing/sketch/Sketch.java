package jto.processing.sketch;


import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

/**
 * This represents a processing sketch.
 */
public interface Sketch {

    /**
     * The process draw method.
     * This is invoked when draw is called on
     * {@link jto.processing.surface.mapper.SurfaceMapperGui}
     *
     */
    public void draw();

    /**
     * Returns the name of the sketch.
     * This is what will show up in the UI dropdown.
     *
     * Implement this to return custom labels if desired.
     *
     * @return the name of the class of the sketch
     */
    public String getName();

    /**
     * The processing setup method.
     * This is called when the surfaceMapperGui is initialized.
     */
    public void setup();

    /**
     * Return the PGraphics associated with this sketch.
     * @return the graphics object associated with this sketch.
     */
    public PGraphics getPGraphics();

    /**
     * This is the keyEvent callback method that will be propagated through
     * the {@link jto.processing.surface.mapper.SurfaceMapperGui} object
     * and called on this sketch.
     * @param event the key event.
     */
    public void keyEvent(KeyEvent event);

    /**
     * This is the mouseEvent callback method that will be propagated through
     * the {@link jto.processing.surface.mapper.SurfaceMapperGui} object and
     * called on this sketch.
     * @param event the mouse event.
     */
    public void mouseEvent(MouseEvent event);
}
