package jto.processing.main;

import jto.processing.sketch.mapper.AbstractSketch;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class TestSketchInvert extends AbstractSketch {

    private static final int WHITE = 0;
    private static final int BLACK = 255;
    private static final int LOWER_INDEX = 0;
    private static final int UPPER_INDEX = 100;
    private static final int ELLIPSE_SIZE = 25;

    public TestSketchInvert(PApplet parent, int width, int height) {
        super(parent, width, height);
    }

    @Override
    public void draw() {
        graphics.beginDraw();
        graphics.background(WHITE);
        graphics.fill(BLACK);
        for (int index = LOWER_INDEX; index < UPPER_INDEX; index++) {
            float ellipseXCoordinate = parent.random(graphics.width);
            float ellipseYCoordinate = parent.random(graphics.height);
            graphics.ellipse(ellipseXCoordinate, ellipseYCoordinate, ELLIPSE_SIZE, ELLIPSE_SIZE);
        }
        graphics.endDraw();
    }

    @Override
    public void setup() {

    }

    @Override
    public void keyEvent(KeyEvent event) {

    }

    @Override
    public void mouseEvent(MouseEvent event) {

    }

}
