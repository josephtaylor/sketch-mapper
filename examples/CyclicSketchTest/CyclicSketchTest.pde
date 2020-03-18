/*
 This is a simple test sketch showing the usage of CyclicSketch allowing the looping of multiple sketches!
 This example takes three sketches (ShapesSketch, SpiralSketch, TestSketchCyclic) and loops through each sketch
 changing at a specified time interval as specified by the TIME_INTERVAL constant.
 */
import jto.processing.sketch.mapper.AbstractSketch;
import jto.processing.sketch.mapper.CyclicSketch;
import jto.processing.sketch.mapper.SketchMapper;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
import controlP5.*;

private static final int TWO = 2;
private static final int TIME_INTERVAL = 10000;

private SketchMapper sketchMapper;

@Override
  public void setup() {
  size(800, 600, P3D);
  sketchMapper = new SketchMapper(this);
  int sketchWidth = width / TWO;
  int sketchHeight = height / TWO;
  TestSketchCyclic testSketchCyclic = new TestSketchCyclic();
  ShapesSketch shapesSketch = new ShapesSketch();
  SpiralSketch spiralSketch = new SpiralSketch();
  sketchMapper.addSketch(new CyclicSketch(this, sketchWidth, sketchHeight, TIME_INTERVAL, testSketchCyclic, shapesSketch, spiralSketch));
}

@Override
  public void draw() {
  sketchMapper.draw();
}
