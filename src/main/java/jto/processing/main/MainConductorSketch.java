package jto.processing.main;


import jto.processing.sketch.ConductableSketch;
import jto.processing.surface.mapper.SurfaceMapperGui;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.net.URL;

public class MainConductorSketch extends PApplet {
    private SurfaceMapperGui surfaceMapperGui;

    @Override
    public void draw() {
        surfaceMapperGui.draw();
    }

    @Override
    public void setup() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("IMG_1028.JPG");
        PImage image = loadImage(url.getFile());

        image.resize(image.width / 2, image.height / 2);

        size(image.width, image.height, PConstants.OPENGL);

        Conductor conductor = new Conductor(this);
        conductor.setup();

        surfaceMapperGui = new SurfaceMapperGui(this);
        for (ConductableSketch conductableSketch : conductor.getSketchList()) {
            surfaceMapperGui.addSketch(conductableSketch);
        }
        surfaceMapperGui.setBackgroundImage(image);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{ MainConductorSketch.class.getName() });
    }

}
