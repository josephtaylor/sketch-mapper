package jto.processing.sketch;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;

public abstract class AbstractSketch implements Sketch {
    protected final PGraphics graphics;

    protected final PApplet parent;

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
    public PGraphics getPGraphics() {
        return graphics;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
