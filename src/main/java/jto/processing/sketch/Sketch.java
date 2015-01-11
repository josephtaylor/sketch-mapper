package jto.processing.sketch;


import processing.core.PGraphics;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public interface Sketch {
    public void destroy();

    public void draw();

    public String getName();

    public void setup();

    public void update();

    public PGraphics getPGraphics();

    public void keyEvent(KeyEvent event);

    public void mouseEvent(MouseEvent event);
}
