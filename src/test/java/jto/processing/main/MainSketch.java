package jto.processing.main;

import jto.processing.sketch.mapper.AbstractSketch;
import jto.processing.sketch.mapper.CyclicSketch;
import jto.processing.sketch.mapper.SketchMapper;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class MainSketch extends PApplet {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int TWO = 2;
    private static final int TIME_INTERVAL = 10000;
    private static final int FRAME_RATE = 30;

    private SketchMapper sketchMapper;

    public static void main(String[] args) {
        PApplet.main(MainSketch.class.getName());
    }

    public void settings() {
        size(WIDTH, HEIGHT, P3D);
    }

    @Override
    public void setup() {
        sketchMapper = new SketchMapper(this);
        int sketchWidth = width / TWO;
        int sketchHeight = height / TWO;
        sketchMapper.addSketch(new TestSketch(this, sketchWidth, sketchHeight));
        sketchMapper.addSketch(new TestSketchInvert(this, sketchWidth, sketchHeight));
        TestSketchCyclic testSketchCyclic = new TestSketchCyclic();
        ShapesSketch shapesSketch = new ShapesSketch();
        SpiralSketch spiralSketch = new SpiralSketch();
        sketchMapper.addSketch(new CyclicSketch(this, sketchWidth, sketchHeight, TIME_INTERVAL, testSketchCyclic, shapesSketch, spiralSketch));
        frameRate(FRAME_RATE);
    }

    @Override
    public void draw() {
        sketchMapper.draw();
    }

    /**
     * When a class is created in a "tab" in the processing IDE,
     * it's actually an inner class of the main sketch class.
     */
    public class InnerSketch extends AbstractSketch {

        private static final int BLACK = 255;
        private static final int UPPER_BOUND = 255;
        private static final int LOWER_INDEX = 0;
        private static final int UPPER_INDEX = 100;
        private static final int ELLIPSE_SIZE = 25;

        public InnerSketch(final PApplet parent, final int width, final int height) {
            super(parent, width, height);
        }

        @Override
        public void draw() {
            graphics.beginDraw();
            graphics.background(BLACK);
            graphics.fill(random(UPPER_BOUND), random(UPPER_BOUND), random(UPPER_BOUND));
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

}
