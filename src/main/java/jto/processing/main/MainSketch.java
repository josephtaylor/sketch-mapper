package jto.processing.main;


import jto.processing.sketch.TestSketch;
import jto.processing.surface.mapper.SurfaceMapperGui;
import processing.core.PApplet;

public class MainSketch extends PApplet {

    private SurfaceMapperGui surfaceMapperGui;

    @Override
    public void draw() {
        surfaceMapperGui.draw();
    }

    @Override
    public void setup() {
        size(1024, 768, PApplet.OPENGL);

        surfaceMapperGui = new SurfaceMapperGui(this);
        surfaceMapperGui.addSketch(new TestSketch(createGraphics(width / 2, height / 2, OPENGL)));
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{ MainSketch.class.getName() });
    }
}
