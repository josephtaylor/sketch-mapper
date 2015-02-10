import com.sun.media.jai.tilecodec.*;
import jto.processing.sketch.*;
import javax.media.jai.tilecodec.*;
import ixagon.SurfaceMapper.*;
import com.sun.media.jai.util.*;
import com.sun.media.jai.codecimpl.fpx.*;
import com.sun.media.jai.iterator.*;
import javax.media.jai.util.*;
import com.sun.media.jai.widget.*;
import com.sun.media.jai.remote.*;
import com.sun.media.jai.codecimpl.*;
import javax.media.jai.widget.*;
import javax.media.jai.remote.*;
import jto.processing.surface.mapper.menu.*;
import com.sun.media.jai.codecimpl.util.*;
import com.sun.media.jai.opimage.*;
import javax.media.jai.registry.*;
import javax.media.jai.*;
import jto.processing.surface.mapper.*;
import javax.media.jai.operator.*;
import com.sun.media.jai.rmi.*;
import com.sun.media.jai.codec.*;
import controlP5.*;
import com.sun.media.jai.mlib.*;
import javax.media.jai.iterator.*;



private SurfaceMapperGui surfaceMapperGui;

public void setup() {
  size(800, 600, PConstants.OPENGL);
  surfaceMapperGui = new SurfaceMapperGui(this);
  surfaceMapperGui.addSketch(new TestSketch(this, width / 2, height / 2));  
}

public void draw() {
  surfaceMapperGui.draw();
}


