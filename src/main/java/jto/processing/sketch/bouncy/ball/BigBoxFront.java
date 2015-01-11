package jto.processing.sketch.bouncy.ball;

import jto.processing.event.EdgeEnum;
import jto.processing.event.EdgeEvent;
import jto.processing.model.Ball;
import jto.processing.sketch.BounyBallSketch;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.Iterator;

public class BigBoxFront extends BounyBallSketch {
    public BigBoxFront(final PApplet parent, final int width, final int height) {
        super(parent, width, height);
    }

    @Override
    public void edgeEvent(EdgeEvent edgeEvent) {
        Ball ball = new Ball();
        ball.setVelocity(edgeEvent.getVelocity());
        switch (edgeEvent.getReceivingEdge()) {
            case BOTTOM: {
                ball.setLocation(new PVector((float) (graphics.width * 2 / 3 + (graphics.width / 3 * edgeEvent.getLocation())), graphics.height * 2 / 3));
                break;
            }
            case RIGHT: {
                ball.setLocation(new PVector(graphics.width * 2 / 3, (float) (graphics.height * 2 / 3 + (graphics.height / 3 * edgeEvent.getLocation()))));
                break;
            }
            case TOP: {
                ball.setLocation(new PVector((float) (graphics.width * edgeEvent.getLocation()), 0.0f));
                break;
            }
            case LEFT: {
                ball.setLocation(new PVector(0.0f, (float) (graphics.height * edgeEvent.getLocation())));
            }
        }
        balls.add(ball);
    }

    @Override
    public String getName() {
        return "BigBoxFront";
    }

    @Override
    public void update() {
        Iterator<Ball> it = balls.iterator();
        while (it.hasNext()) {
            Ball ball = it.next();
            ball.getLocation().set(ball.getLocation().x + ball.getVelocity().x, ball.getLocation().y + ball.getVelocity().y);
            if (ball.getLocation().x > (graphics.width * 2.0d / 3.0d + 2.0d) && ball.getLocation().y > (graphics.height * 2.0d / 3.0d)) {
                double location = (ball.getLocation().x - (2.0d * (double) graphics.width / 3.0d)) / ((double) graphics.width / 3.0d);
                EdgeEvent edgeEvent = new EdgeEvent(getName(), EdgeEnum.BOTTOM, EdgeEnum.TOP, location, ball.getVelocity());
                publishEvent(edgeEvent);
                it.remove();
                continue;
            }
            if (ball.getLocation().x > (graphics.width * 2.0d / 3.0d) && ball.getLocation().y > (graphics.height * 2.0d / 3.0d + 2.0d)) {
                double location = (ball.getLocation().y - (2.0d * (double) graphics.height / 3.0d)) / ((double) graphics.height / 3.0d);
                EdgeEvent edgeEvent = new EdgeEvent(getName(), EdgeEnum.RIGHT, EdgeEnum.LEFT, location, ball.getVelocity());
                publishEvent(edgeEvent);
                it.remove();
                continue;
            }
            if (ball.getLocation().x < 0) {
                double location = (ball.getLocation().y) / (double) graphics.height;
                EdgeEvent edgeEvent = new EdgeEvent(getName(), EdgeEnum.LEFT, EdgeEnum.RIGHT, location, ball.getVelocity());
                publishEvent(edgeEvent);
                it.remove();
                continue;
            }
            if (ball.getLocation().y < 0) {
                double location = ball.getLocation().x / (double) graphics.width;
                EdgeEvent edgeEvent = new EdgeEvent(getName(), EdgeEnum.TOP, EdgeEnum.BOTTOM, location, ball.getVelocity());
                publishEvent(edgeEvent);
                it.remove();
                continue;
            }
            if (ball.getLocation().x > graphics.width) {
                ball.getVelocity().set(ball.getVelocity().x * -1, ball.getVelocity().y);
            }
            if (ball.getLocation().y > graphics.height) {
                ball.getVelocity().set(ball.getVelocity().x, ball.getVelocity().y * -1);
            }
        }
    }
}
