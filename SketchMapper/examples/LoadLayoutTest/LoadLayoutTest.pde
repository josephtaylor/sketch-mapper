/*
  This is a simple test sketch showing the
  The usage of the SketchMapper with one sketch.

  The sketch in TestSketch.pde is a very simple sketch that draws
  random ellipses.

  This example illustrates loading a default layout at SketchMapper creation.

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
  size(800, 600, P3D);

  //create our SketchMapper
  //in this case we load the layout from the data folder.
  //it can also be an absolute path to any location on the filesystem.
  sketchMapper = new SketchMapper(this, "test_layout.xml");

  //create a sketch and add it to the SketchMapper
  sketchMapper.addSketch(new TestSketch(this, width / 2, height / 2));
}

public void draw() {
  //must call this for the sketches and the GUI to be rendered.
  sketchMapper.draw();
}