package jto.processing.sketch.bouncy.ball;

import jto.processing.event.EdgeEnum;
import jto.processing.event.EdgeEvent;
import jto.processing.model.Ball;
import jto.processing.sketch.BounyBallSketch;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.Iterator;

public class BigBoxLeft extends BounyBallSketch {
    public BigBoxLeft(final PApplet parent, final int width, final int height) {
        super(parent, width, height);
    }

    @Override
    public void edgeEvent(EdgeEvent edgeEvent) {
        Ball ball = new Ball();
        switch (edgeEvent.getReceivingEdge()) {
            case TOP: {
                ball.setVelocity(edgeEvent.getVelocity());
                ball.setLocation(new PVector((float) (graphics.width * edgeEvent.getLocation()), 0.0f));
                break;
            }
            case RIGHT: {
                ball.setVelocity(edgeEvent.getVelocity());
                ball.setLocation(new PVector(graphics.width, (float) (graphics.height * edgeEvent.getLocation())));
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
        return "BigBoxLeft";
    }

    @Override
    public void update() {
        Iterator<Ball> it = balls.iterator();
        while (it.hasNext()) {
            Ball ball = it.next();
            ball.getLocation().set(ball.getLocation().x + ball.getVelocity().x, ball.getLocation().y + ball.getVelocity().y);
            if (ball.getLocation().x > graphics.width) {
                double ratio = (double) ball.getLocation().y / (double) graphics.height;
                EdgeEvent edgeEvent = new EdgeEvent(getName(), EdgeEnum.RIGHT, EdgeEnum.LEFT, ratio, ball.getVelocity());
                publishEvent(edgeEvent);
                it.remove();
                continue;
            }
            if (ball.getLocation().y < 0) {
                double ratio = (double) ball.getLocation().x / (double) graphics.width;
                EdgeEvent edgeEvent = new EdgeEvent(getName(), EdgeEnum.TOP, EdgeEnum.LEFT, ratio, ball.getVelocity());
                publishEvent(edgeEvent);
                it.remove();
                continue;
            }
            if (ball.getLocation().x < 0) {
                ball.getVelocity().set(ball.getVelocity().x * -1, ball.getVelocity().y);
            }
            if (ball.getLocation().y > graphics.height) {
                ball.getVelocity().set(ball.getVelocity().x, ball.getVelocity().y * -1);
            }
        }
    }
}
