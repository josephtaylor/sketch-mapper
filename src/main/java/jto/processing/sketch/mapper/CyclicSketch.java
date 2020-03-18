package jto.processing.sketch.mapper;

import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.Arrays;
import java.util.List;

import static processing.core.PConstants.*;

public class CyclicSketch extends AbstractSketch {

    private static final int ZERO = 0;
    private static final String SKETCHES_REQUIRED = "Sketches Required!";
    private static final int INCREMENT = 1;
    private static final int OPACITY = 255;
    private static final int BLACK = 0;

    private int timeInterval;
    private List<AbstractCyclicSketch> sketches;
    private AbstractCyclicSketch currentSketch;
    private int startTime;

    public CyclicSketch(PApplet parent, int width, int height, int timeInterval, AbstractCyclicSketch... abstractCyclicSketches) {
        super(parent, width, height);
        this.timeInterval = timeInterval;
        setSketches(abstractCyclicSketches);
    }

    @Override
    public void setup() {
        setNextSketch();
        startTime = parent.millis();
    }

    @Override
    public void draw() {
        graphics.beginDraw();
        checkTime();
        currentSketch.draw();
        graphics.endDraw();
    }

    @Override
    public void keyEvent(KeyEvent keyEvent) {
        currentSketch.keyEvent(keyEvent);
    }

    @Override
    public void mouseEvent(MouseEvent mouseEvent) {
        currentSketch.mouseEvent(mouseEvent);
    }

    private void setSketches(AbstractCyclicSketch... abstractCyclicSketches) {
        checkSketches(abstractCyclicSketches);
        Arrays.stream(abstractCyclicSketches).forEach(this::constructCyclicSketches);
        sketches = Arrays.asList(abstractCyclicSketches);
    }

    private void checkSketches(AbstractCyclicSketch[] abstractCyclicSketches) {
        if (isNoSketches(abstractCyclicSketches)) {
            throw new IllegalArgumentException(SKETCHES_REQUIRED);
        }
    }

    private boolean isNoSketches(AbstractCyclicSketch... abstractCyclicSketches) {
        return abstractCyclicSketches.length == ZERO;
    }

    private void constructCyclicSketches(AbstractCyclicSketch abstractCyclicSketch) {
        abstractCyclicSketch.setParent(parent);
        abstractCyclicSketch.setGraphics(graphics);
    }

    private void setNextSketch() {
        int nextSketchIndex = (sketches.indexOf(currentSketch) + INCREMENT) % sketches.size();
        currentSketch = sketches.get(nextSketchIndex);
        resetGraphics();
        currentSketch.setup();
    }

    private void resetGraphics() {
        graphics.beginDraw();
        graphics.clear();
        graphics.colorMode(RGB, OPACITY);
        graphics.imageMode(CORNER);
        graphics.rectMode(CORNER);
        graphics.background(BLACK);
        graphics.stroke(BLACK);
        graphics.hint(ENABLE_OPTIMIZED_STROKE);
        graphics.endDraw();
    }

    private void checkTime() {
        if (hasTimeElapsed()) {
            graphics.clear();
            setNextSketch();
            startTime = parent.millis();
        }
    }

    private boolean hasTimeElapsed() {
        return parent.millis() > startTime + timeInterval;
    }

}
