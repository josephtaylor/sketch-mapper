package jto.processing.sketch;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

/**
 * This is an abstract implementation of
 * {@link jto.processing.sketch.Sketch}.
 *
 * This contains protected fields for the graphics and
 * parent PApplet as well some basic implementations of other methods.
 */
public abstract class AbstractSketch implements Sketch {
    protected final PGraphics graphics;

    protected final PApplet parent;

    /**
     * Constructor
     *
     * this creates a PGraphics object with the size provided in the parameters
     * and the renderer specified by {@link processing.core.PConstants#OPENGL}.
     * @param parent the parent {@link processing.core.PApplet}.
     * @param width the width of the sketch in pixels.
     * @param height the height of the sketch in pixels.
     */
    public AbstractSketch(final PApplet parent, final int width, final int height) {
        this.parent = parent;
        this.graphics = parent.createGraphics(width, height, PConstants.OPENGL);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Sketch)) {
            return false;
        }
        Sketch s = (Sketch) o;
        return this.getName().equals(s.getName());
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public PGraphics getPGraphics() {
        return graphics;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
