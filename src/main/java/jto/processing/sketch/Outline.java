package jto.processing.sketch;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Created by joconnor on 1/4/2015.
 */
public class Outline extends AbstractSketch {

    public Outline(final PApplet parent, final int width, final int height) {
        super(parent, width, height);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void draw() {
        graphics.beginDraw();
        graphics.background(0);
        graphics.rectMode(PConstants.CORNERS);
        graphics.fill(255);
        graphics.rect(0, 0, graphics.width, graphics.height);
        graphics.fill(0);
        graphics.rect(20, 20, graphics.width - 20, graphics.height - 20);
        graphics.endDraw();
    }

    @Override
    public String getName() {
        return "Outline";
    }

    @Override
    public void setup() {

    }

    @Override
    public void update() {

    }
}
