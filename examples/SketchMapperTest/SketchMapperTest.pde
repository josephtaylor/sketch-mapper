import javax.media.jai.*;
import jto.processing.sketch.mapper.*;
import com.sun.media.jai.util.*;
import ixagon.surface.mapper.*;

private SketchMapper sketchMapper;

public void setup() {
  size(800, 600, OPENGL);
  sketchMapper = new SketchMapper(this);
  sketchMapper.addSketch(new TestSketch(this, width / 2, height / 2));
}

public void draw() {
  sketchMapper.draw();
}


