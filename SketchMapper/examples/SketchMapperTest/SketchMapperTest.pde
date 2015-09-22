/*
  This is a simple test sketch showing the
  The usage of the SketchMapper with one sketch.

  The sketch in TestSketch.pde is a very simple sketch that draws
  random ellipses.

  *** Note: This library requires that you have
            ControlP5 v. 2.2.5 installed and imported ! ***
*/
import controlP5.*;
import javax.media.jai.*;
import jto.processing.sketch.mapper.*;
import com.sun.media.jai.util.*;
import ixagon.surface.mapper.*;

private SketchMapper sketchMapper;

public void setup() {
  size(800, 600, OPENGL);

  //create our SketchMapper
  sketchMapper = new SketchMapper(this);

  //create a sketch and add it to the SketchMapper
  sketchMapper.addSketch(new TestSketch(this, width / 2, height / 2));
}

public void draw() {
  //must call this for the sketches and the GUI to be rendered.
  sketchMapper.draw();
}


