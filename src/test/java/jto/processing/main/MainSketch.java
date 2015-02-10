package jto.processing.main;


import jto.processing.surface.mapper.SurfaceMapperGui;
import processing.core.PApplet;
import processing.core.PConstants;

public class MainSketch extends PApplet {

    private SurfaceMapperGui surfaceMapperGui;

    @Override
    public void draw() {
        surfaceMapperGui.draw();
    }

    @Override
    public void setup() {
        size(800, 600, PConstants.OPENGL);
        surfaceMapperGui = new SurfaceMapperGui(this);
        surfaceMapperGui.addSketch(new TestSketch(this, width / 2, height / 2));
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{ MainSketch.class.getName() });
    }
}
