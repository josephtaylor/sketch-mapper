package jto.processing.main;

import jto.processing.sketch.mapper.AbstractSketch;
import jto.processing.sketch.mapper.SketchMapper;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class MainSketch extends PApplet {

    private SketchMapper sketchMapper;

    @Override
    public void draw() {
        sketchMapper.draw();
    }

    public void settings() {
        size(800, 600, PConstants.OPENGL);
    }

    @Override
    public void setup() {
        sketchMapper = new SketchMapper(this);
//    	sketchMapper.addSketch(new TestSketchInvert(this, width / 2, height / 2));
//    	sketchMapper.addSketch(new TestSketch(this, width / 2, height / 2));
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{ MainSketch.class.getName() });
    }

    /**
     * When a class is created in a "tab" in the processing IDE,
     * it's actually an inner class of the main sketch class.
     */
    public class InnerSketch extends AbstractSketch {

        public InnerSketch(final PApplet parent, final int width, final int height) {
            super(parent, width, height);
        }

        @Override
        public void draw() {
            graphics.beginDraw();
            graphics.background(255);
            graphics.fill(random(255), random(255), random(255));
            for (int i = 0; i < 100; i++) {
                graphics.ellipse(parent.random(graphics.width), parent.random(graphics.height), 25, 25);
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
