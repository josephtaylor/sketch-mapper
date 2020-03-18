import jto.processing.sketch.mapper.AbstractCyclicSketch;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import static processing.core.PConstants.CENTER;
import static processing.core.PConstants.HSB;

public class ShapesSketch extends AbstractCyclicSketch {

  private static final float TWO = 2f;
  private static final int INITIAL_SCALE = 3;
  private static final float SCALE_SPEED = 0.02f;
  private static final int INITIAL_TIME = 5;
  private static final float INITIAL_HUE = 0.30f;
  private static final int INITIAL_OFFSET = 50;
  private static final int RUNNING_TIME_LIMIT = 10000;
  private static final float TIME_INCREMENT = 0.0167f;
  private static final int HUE = 400;
  private static final int MAX_SATURATION = 255;
  private static final int BRIGHTNESS = 100;
  private static final int MAX_BRIGHTNESS = 255;
  private static final float ANGLE_INCREMENT_ONE = 1.5f;
  private static final int MAX_SCALE = 5;
  private static final int MIN_SCALE = 0;
  private static final int MAX_HUE = 255;
  private static final int MIN_HUE = 0;
  private static final int SATURATION = 100;
  private static final int TIME_TWENTY_FIVE = 25;
  private static final int TIME_FIFTY = 50;
  private static final int ELLIPSE_X = 56;
  private static final int ELLIPSE_Y = 46;
  private static final int ELLIPSE_SIZE = 55;
  private static final int TIME_ZERO = 0;
  private static final float ANGLE_INCREMENT_TWO = -0.4f;
  private static final int TIME_EIGHTEEN = 18;
  private static final int ANGLE_INCREMENT_THREE = 3;
  private static final int TRIANGLE_X1 = 30;
  private static final int TRIANGLE_Y1 = 75;
  private static final int TRIANGLE_X2 = 58;
  private static final int TRIANGLE_Y2 = 20;
  private static final int TRIANGLE_X3 = 86;

  private float halfWidth;
  private float halfHeight;
  private float angle;
  private float scale;
  private float scaleSpeed;
  private float time;
  private float hue;
  private float offset;
  private int startTime;

  @Override
    public void setup() {
    halfWidth = graphics.width / TWO;
    halfHeight = graphics.height / TWO;
    scale = INITIAL_SCALE;
    scaleSpeed = SCALE_SPEED;
    time = INITIAL_TIME;
    hue = INITIAL_HUE;
    offset = INITIAL_OFFSET;
  }

  @Override
    public void draw() {
    graphics.beginDraw();
    resetPicture();
    time += TIME_INCREMENT;
    graphics.colorMode(HSB, HUE, MAX_SATURATION, BRIGHTNESS);
    angle += ANGLE_INCREMENT_ONE;
    scale += scaleSpeed;
    if (scale > MAX_SCALE || scale < MIN_SCALE) {
      scaleSpeed = -scaleSpeed;
    }
    graphics.ellipseMode(CENTER);
    hue += offset;
    if (hue >= MAX_HUE) {
      offset = -offset;
      hue = MAX_HUE;
    } else if (hue < MIN_HUE) {
      offset = -offset;
      hue = MIN_HUE;
    }
    graphics.fill(hue, SATURATION, BRIGHTNESS);
    graphics.stroke(hue, SATURATION, BRIGHTNESS);
    if (time > TIME_TWENTY_FIVE && time < TIME_FIFTY) {
      graphics.fill(hue, MAX_SATURATION, MAX_BRIGHTNESS);
      graphics.stroke(hue, MAX_SATURATION, MAX_BRIGHTNESS);
      graphics.pushMatrix();
      graphics.translate(halfWidth, halfHeight);
      graphics.rotate(angle);
      graphics.scale(scale);
      graphics.ellipse(ELLIPSE_X, ELLIPSE_Y, ELLIPSE_SIZE, ELLIPSE_SIZE);
      graphics.popMatrix();
    }
    if (time > TIME_ZERO && time < TIME_TWENTY_FIVE) {
      angle += ANGLE_INCREMENT_TWO;
      graphicsSetup();
      graphics.ellipse(ELLIPSE_X, ELLIPSE_Y, ELLIPSE_SIZE, ELLIPSE_SIZE);
      graphics.popMatrix();
    }
    if (time > TIME_FIFTY && time >= TIME_EIGHTEEN) {
      angle += ANGLE_INCREMENT_THREE;
      graphicsSetup();
      graphics.triangle(TRIANGLE_X1, TRIANGLE_Y1, TRIANGLE_X2, TRIANGLE_Y2, TRIANGLE_X3, TRIANGLE_Y1);
      graphics.popMatrix();
    }
    graphics.endDraw();
  }

  private void resetPicture() {
    int runningTime = parent.millis() - startTime;
    if (runningTime > RUNNING_TIME_LIMIT) {
      graphics.clear();
      startTime = parent.millis();
    }
  }

  private void graphicsSetup() {
    graphics.fill(hue, SATURATION, BRIGHTNESS);
    graphics.stroke(hue, SATURATION, BRIGHTNESS);
    graphics.pushMatrix();
    graphics.translate(halfWidth, halfHeight);
    graphics.rotate(angle);
    graphics.scale(scale);
  }

  @Override
    public void keyEvent(KeyEvent keyEvent) {
  }

  @Override
    public void mouseEvent(MouseEvent mouseEvent) {
  }
}
