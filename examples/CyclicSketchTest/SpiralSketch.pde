import jto.processing.sketch.mapper.AbstractCyclicSketch;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import static processing.core.PApplet.radians;
import static processing.core.PConstants.CENTER;

public class SpiralSketch extends AbstractCyclicSketch {

  private static final float TWO = 2;
  private static final int WHITE = 255;
  private static final int ROTATE_OFFSET = 5;
  private static final int RECTANGLE_X = 50;
  private static final int RECTANGLE_Y = 50;

  private float halfWidth;
  private float halfHeight;

  public void setup() {
    halfWidth = graphics.width / TWO;
    halfHeight = graphics.height / TWO;
    graphics.rectMode(CENTER);
    graphics.stroke(WHITE);
  }

  public void draw() {
    graphics.translate(halfWidth, halfHeight);
    graphics.rotate(radians(parent.frameCount) * ROTATE_OFFSET);
    generateRectangle(255, 25, 100, 2225);
    generateRectangle(20, 10, 80, 2125);
    generateRectangle(10, 90, 50, 2025);
    generateRectangle(50, 20, 255, 1925);
    generateRectangle(100, 110, 70, 1825);
    generateRectangle(150, 90, 50, 1725);
    generateRectangle(200, 30, 10, 1625);
    generateRectangle(40, 70, 40, 1525);
    generateRectangle(70, 55, 120, 1425);
    generateRectangle(90, 125, 170, 1325);
    generateRectangle(240, 180, 190, 1225);
    generateRectangle(250, 150, 225, 1125);
    generateRectangle(190, 250, 210, 1025);
    generateRectangle(60, 200, 145, 925);
    generateRectangle(80, 180, 55, 825);
    generateRectangle(50, 160, 90, 725);
    generateRectangle(130, 20, 190, 625);
    generateRectangle(110, 150, 200, 525);
    generateRectangle(210, 100, 75, 425);
    generateRectangle(220, 40, 40, 325);
    generateRectangle(235, 30, 75, 225);
    generateRectangle(167, 20, 180, 125);
    generateRectangle(65, 60, 175, 25);
  }

  private void generateRectangle(int red, int green, int blue, int rectangleSize) {
    graphics.fill(parent.random(red), parent.random(green), parent.random(blue));
    graphics.rect(RECTANGLE_X, RECTANGLE_Y, rectangleSize, rectangleSize);
  }

  @Override
    public void keyEvent(KeyEvent keyEvent) {
  }

  @Override
    public void mouseEvent(MouseEvent mouseEvent) {
  }
}
