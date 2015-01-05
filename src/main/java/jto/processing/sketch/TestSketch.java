package jto.processing.sketch;


import processing.core.PApplet;

public class TestSketch extends AbstractSketch {

    public TestSketch(final PApplet parent, final int width, final int height) {
        super(parent, width, height);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void draw() {
        graphics.beginDraw();
        graphics.background(255);
        graphics.fill(0);
        for(int i = 0; i < 100; i++) {
            graphics.ellipse(parent.random(graphics.width), parent.random(graphics.height), 25, 25);
        }
        graphics.endDraw();\
    }

    @Override
    public String getName() {
        return "TestSketch";
    }

    @Override
    public void setup() {

    }

    @Override
    public void update() {

    }
}
