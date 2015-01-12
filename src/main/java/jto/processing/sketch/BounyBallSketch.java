package jto.processing.sketch;

import java.util.ArrayList;
import java.util.List;

import jto.processing.model.Ball;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public abstract class BounyBallSketch extends ConductableSketch {

    private static final float INITIAL_VELOCITY = 10.0f;

    protected List<Ball> balls;

    public BounyBallSketch(final PApplet parent, final int width, final int height) {
        super(parent, width, height);
    }

    @Override
    public void destroy() {
        balls = null;
    }

    @Override
    public void draw() {
        graphics.beginDraw();
        graphics.fill(0, 0, 0, 20);
        graphics.rect(-10, -10, graphics.width + 10, graphics.height + 10);
        //graphics.background(0,0,0,10);
        graphics.fill(255);
        graphics.noStroke();
        for (Ball ball : balls) {
            //System.out.println(getName() + ": Ball at " + ball.getLocation().x + ", " + ball.getLocation().y);
            graphics.ellipse(ball.getLocation().x, ball.getLocation().y, 15, 15);
        }
        graphics.endDraw();
    }

    public List<Ball> getBalls() {
        return balls;
    }

    @Override
    public void keyEvent(KeyEvent event) {

    }

    @Override
    public void mouseEvent(MouseEvent event) {

    }

    public void setBalls(List<Ball> balls) {
        this.balls = balls;
    }

    @Override
    public void setup() {
        balls = new ArrayList<Ball>();
        //if (this instanceof BigBoxFront) {
            for (int i =0; i < 20; i++) {
                Ball ball = new Ball();
                ball.setLocation(new PVector(parent.random(10, graphics.width / 4), parent.random(10, graphics.height / 4)));
                float angle = parent.random(PConstants.TWO_PI);
                ball.setVelocity(new PVector(INITIAL_VELOCITY * parent.cos(angle), INITIAL_VELOCITY * parent.sin(angle)));
                //ball.setVelocity(new PVector(1, -4));
                balls.add(ball);
                System.out.println("First ball initialized for " + this.getName() + ": " + ball.getLocation().x + ", " + ball.getLocation().y);
            }
        //}
    }
}
