package jto.processing.sketch;

import processing.core.PGraphics;

public abstract class AbstractSketch implements Sketch {
    protected final PGraphics graphics;

    public AbstractSketch(final PGraphics graphics) {
        this.graphics = graphics;
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
    public PGraphics getGraphics() {
        return graphics;
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
