package jto.processing.main;


import jto.processing.sketch.mapper.SketchMapper;
import processing.core.PApplet;
import processing.core.PConstants;

public class MainSketch extends PApplet {

    private SketchMapper sketchMapper;

    @Override
    public void draw() {
        sketchMapper.draw();
    }

    @Override
    public void setup() {
        size(800, 600, PConstants.OPENGL);
        sketchMapper = new SketchMapper(this);
        sketchMapper.addSketch(new TestSketch(this, width / 2, height / 2));
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{ MainSketch.class.getName() });
    }
}
