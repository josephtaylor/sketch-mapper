package jto.processing.sketch.bouncy.ball;

import jto.processing.event.EdgeEnum;
import jto.processing.event.EdgeEvent;
import jto.processing.model.Ball;
import jto.processing.sketch.BounyBallSketch;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.Iterator;

public class BigBoxTop extends BounyBallSketch {
    public BigBoxTop(final PApplet parent, final int width, final int height) {
        super(parent, width, height);
    }

    @Override
    public void edgeEvent(EdgeEvent edgeEvent) {
        Ball ball = new Ball();
        switch (edgeEvent.getReceivingEdge()) {
            case BOTTOM: {
                ball.setVelocity(edgeEvent.getVelocity());
                ball.setLocation(new PVector((float) ((float) graphics.width * edgeEvent.getLocation()), graphics.height));
                break;
            }
            case LEFT: {
                ball.setVelocity(edgeEvent.getVelocity());
                ball.getVelocity().set(ball.getVelocity().y * -1, ball.getVelocity().x);
                ball.setLocation(new PVector(0.0f, (float) ((float) graphics.height * edgeEvent.getLocation())));
                break;
            }
            default: {
                return;
            }
        }
        balls.add(ball);
    }

    @Override
    public String getName() {
        return "BigBoxTop";
    }

    @Override
    public void update() {
        Iterator<Ball> it = balls.iterator();
        while (it.hasNext()) {
            Ball ball = it.next();
            ball.getLocation().set(ball.getLocation().x + ball.getVelocity().x, ball.getLocation().y + ball.getVelocity().y);

            if (ball.getLocation().x < 0) {
                double ratio = ball.getLocation().y / (double) graphics.height;
                EdgeEvent edgeEvent = new EdgeEvent(getName(), EdgeEnum.LEFT, EdgeEnum.TOP, ratio, ball.getVelocity());
                publishEvent(edgeEvent);
                it.remove();
                continue;
            }
            if (ball.getLocation().y > graphics.height) {
                double ratio = ball.getLocation().x / (double) graphics.width;
                EdgeEvent edgeEvent = new EdgeEvent(getName(), EdgeEnum.BOTTOM, EdgeEnum.TOP, ratio, ball.getVelocity());
                publishEvent(edgeEvent);
                it.remove();
                continue;
            }
            if (ball.getLocation().x > graphics.width) {
                ball.getVelocity().set(ball.getVelocity().x * -1, ball.getVelocity().y);
            }
            if (ball.getLocation().y < 0) {
                ball.getVelocity().set(ball.getVelocity().x, ball.getVelocity().y * -1);
            }
        }
    }
}
