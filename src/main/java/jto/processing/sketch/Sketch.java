package jto.processing.sketch;


import processing.core.PGraphics;

public interface Sketch {
    public void destroy();

    public void draw();

    public String getName();

    public void setup();

    public void update();

    public PGraphics getGraphics();
}
