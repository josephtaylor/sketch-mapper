package jto.processing.sketch;

import processing.core.PApplet;

public class AnotherSketch extends AbstractSketch {

    public AnotherSketch(final PApplet parent, final int width, final int height) {
        super(parent, width, height);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void draw() {
        graphics.beginDraw();
        graphics.background(parent.random(255));
        graphics.endDraw();
    }

    @Override
    public String getName() {
        return "AnotherSketch";
    }

    @Override
    public void setup() {

    }

    @Override
    public void update() {

    }
}
