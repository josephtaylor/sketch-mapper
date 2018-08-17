package jto.processing.main;

import jto.processing.sketch.mapper.AbstractSketch;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class TestSketchInvert extends AbstractSketch {
	public TestSketchInvert(PApplet parent, int width, int height) {
		super(parent, width, height);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void draw() {
        graphics.beginDraw();
        graphics.background(0);
        graphics.fill(255);
        for (int i = 0; i < 100; i++) {
            graphics.ellipse(parent.random(graphics.width), parent.random(graphics.height), 25, 25);
        }
        graphics.endDraw();
	}

	@Override
	public void setup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyEvent(KeyEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEvent(MouseEvent event) {
		// TODO Auto-generated method stub

	}
}
