package jto.processing.main;

import jto.processing.sketch.mapper.AbstractCyclicSketch;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class TestSketchCyclic extends AbstractCyclicSketch {

    private static final int BLACK = 255;
    private static final int WHITE = 0;
    private static final int LOWER_INDEX = 0;
    private static final int UPPER_INDEX = 100;
    private static final int ELLIPSE_SIZE = 25;

    @Override
    public void draw() {
        graphics.beginDraw();
        graphics.background(BLACK);
        graphics.fill(WHITE);
        for (int index = LOWER_INDEX; index < UPPER_INDEX; index++) {
            float ellipseXCoordinate = parent.random(graphics.width);
            float ellipseYCoordinate = parent.random(graphics.height);
            graphics.ellipse(ellipseXCoordinate, ellipseYCoordinate, ELLIPSE_SIZE, ELLIPSE_SIZE);
        }
        graphics.endDraw();
    }

    @Override
    public void keyEvent(KeyEvent event) {

    }

    @Override
    public void mouseEvent(MouseEvent event) {

    }

    @Override
    public void setup() {

    }

}
