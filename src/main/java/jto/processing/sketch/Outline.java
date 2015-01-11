package jto.processing.sketch;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joconnor on 1/4/2015.
 */
public class Outline extends AbstractSketch {

    private static final Integer LINE_WIDTH = 10;
    private static final Integer COUNT_MAX = 5;
    private static final int NUM_DOTS = 100;
    private static final int NUM_LINES = 200;
    private static final float DISTANCE_THRESHOLD = 100.0f;
    private int counter = 0;
    private int rand = 0;
    private boolean showAllBorders = false;
    private boolean showRandomDots = false;
    private List<Rectangle> borders = new ArrayList<Rectangle>();

    public Outline(final PApplet parent, final int width, final int height) {
        super(parent, width, height);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void draw() {
        graphics.beginDraw();
        graphics.background(0);
        drawBorders();
        drawRandomDots();
        graphics.endDraw();
    }

    private void drawBorders() {
        graphics.noStroke();
        if (!showAllBorders) {
            Rectangle border = borders.get(rand);
            graphics.rect(border.points.get(0).x, border.points.get(0).y, border.points.get(1).x, border.points.get(1).y);
        } else {
            for (Rectangle border : borders) {
                graphics.rect(border.points.get(0).x, border.points.get(0).y, border.points.get(1).x, border.points.get(1).y);
            }
        }
    }

    private void drawRandomDots() {
        graphics.stroke(255);
        graphics.strokeWeight(2);
        if (!showRandomDots) {
            return;
        }
        List<PVector> dots = new ArrayList<PVector>();
        for (int i = 0; i < NUM_DOTS; i++) {
            dots.add(new PVector(parent.random(graphics.width), parent.random(graphics.height)));
        }
        for (int i = 0; i < NUM_LINES; i++) {
            PVector a = dots.get((int) parent.random(dots.size()));
            PVector b = dots.get((int) parent.random(dots.size()));
            if (a != b && (a.dist(b) < DISTANCE_THRESHOLD)) {
                graphics.line(a.x, a.y, b.x, b.y);
            }
        }
    }

    @Override
    public String getName() {
        return "Outline";
    }

    @Override
    public void keyEvent(KeyEvent event) {
        if (event.getKey() == 'a' && event.getAction() == KeyEvent.RELEASE) {
            showAllBorders = !showAllBorders;
        } else if (event.getKey() == 's' && event.getAction() == KeyEvent.RELEASE) {
            showRandomDots = !showRandomDots;
        }

    }

    @Override
    public void mouseEvent(MouseEvent event) {

    }

    @Override
    public void setup() {
        graphics.rectMode(PConstants.CORNERS);
        borders.add(new Rectangle(0, 0, LINE_WIDTH, graphics.height));
        borders.add(new Rectangle(0, 0, graphics.width, LINE_WIDTH));
        borders.add(new Rectangle(graphics.width - LINE_WIDTH, 0, graphics.width, graphics.height));
        borders.add(new Rectangle(0, graphics.height - LINE_WIDTH, graphics.width, graphics.height));
    }

    @Override
    public void update() {
        counter = (counter + 1) % COUNT_MAX;
        if (counter == 0) {
            rand = (int) parent.random(0, 4);
        }
    }

    private static class Rectangle {
        public List<PVector> points = new ArrayList<PVector>();

        public Rectangle(final List<PVector> points) {
            this.points = points;
        }

        public Rectangle(int ax, int ay, int dx, int dy) {
            PVector a = new PVector(ax, ay);
            points.add(a);

            PVector b = new PVector(dx, dy);
            points.add(b);
        }
    }
}
