package jto.processing.main;


import jto.processing.sketch.AnotherSketch;
import jto.processing.sketch.Outline;
import jto.processing.sketch.TestSketch;
import jto.processing.surface.mapper.SurfaceMapperGui;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import java.net.URL;

public class MainSketch extends PApplet {

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

        surfaceMapperGui = new SurfaceMapperGui(this);
        surfaceMapperGui.addSketch(new TestSketch(this, width / 2, height / 2));
        surfaceMapperGui.addSketch(new AnotherSketch(this, width / 2, height / 2));
        surfaceMapperGui.addSketch(new Outline(this, width / 2, height / 2));
        surfaceMapperGui.setBackgroundImage(image);
    }

    public static void main(String[] args) {
        PApplet.main(new String[]{ MainSketch.class.getName() });
    }
}
