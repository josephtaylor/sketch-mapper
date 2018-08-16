package jto.processing.sketch.mapper;

import java.io.File;
import java.util.Collections;

import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import ixagon.surface.mapper.SuperSurface;
import ixagon.surface.mapper.SurfaceMapper;
import processing.core.PApplet;

public class ControlWindow extends PApplet {

	int w, h;
	PApplet parent;
	
	ControlP5 controlP5;
	
    private static final String LOAD_LAYOUT_HANDLER_METHOD_NAME = "loadLayoutHandler";
    private static final String SAVE_LAYOUT_HANDLER_METHOD_NAME = "saveLayoutHandler";
    private int initialSurfaceResolution = 6;
    private SurfaceMapper surfaceMapper;
    private SketchMapper sketchMapper;
    private QuadOptionsMenu quadOptions;
    private BezierOptionsMenu bezierOptions;
    private ProgramOptionsMenu programOptions;

	public ControlWindow(PApplet parent, int w, int h, SurfaceMapper surfaceMapper, SketchMapper sketchMapper) {
		super();
		this.parent = parent;
		this.w = w;
		this.h = h;
		this.surfaceMapper = surfaceMapper;
		this.sketchMapper = sketchMapper;
		PApplet.runSketch(new String[] { this.getClass().getName() }, this);
		
		noLoop(); //Stop draw. ControlP5 throws an exception when dynamically adds controls and draw at the same time.
		controlP5 = new ControlP5(this);
		programOptions = new ProgramOptionsMenu(this, controlP5);
		quadOptions = new QuadOptionsMenu(sketchMapper, this, controlP5);
		bezierOptions = new BezierOptionsMenu(sketchMapper, this, controlP5);
		controlP5.addListener((ControlListener) this::controlEventDelegate);
		loop(); //Continue drawing.
	}

	public void settings() {
		size(320, 550);
	}

	public void setup() {

	}

	public void draw() {
		background(190);
		
	}

	public ProgramOptionsMenu getProgramOptions() {
		return programOptions;
	}
	
	public QuadOptionsMenu getQuadOptions() {
		return quadOptions;
	}
	
	public BezierOptionsMenu getBezierOptions() {
		return bezierOptions;
	}
	
    /**
     * callback function for processing's load dialog.
     *
     * @param file the file to load.
     */
    public void loadLayoutHandler(File file) {
        if (null == file) {
            return;
        }
        surfaceMapper.load(file);
        sketchMapper.resetMostRecentSurface();
    }
    
    /**
     * callback function for processing's save dialog.
     *
     * @param file the file to be saved.
     */
    public void saveLayoutHandler(File file) {
        surfaceMapper.save(file);
    }
	
    private void controlEventDelegate(ControlEvent e) {
        SuperSurface ss;

        switch (e.getId()) {
            // Program Options -> Create quad surface button
            case 1:
                ss = surfaceMapper.createQuadSurface(initialSurfaceResolution, parent.width / 2, parent.height / 2);

                // Add a reference to the default texture for this surface
                ss.setSketch(surfaceMapper.getSketchList().get(0));

                break;

            // Program Options -> Create bezier surface button
            case 2:
                ss = surfaceMapper.createBezierSurface(initialSurfaceResolution, parent.width / 2, parent.height / 2);

                // Add a reference to the default texture for this surface
                ss.setSketch(surfaceMapper.getSketchList().get(0));

                break;

            // Program Options -> Load layout button
            case 3:
            	selectInput("Load layout", LOAD_LAYOUT_HANDLER_METHOD_NAME, null, this);
                break;

            // Program Options -> Save layout button
            case 4:
                parent.selectOutput("Save layout", SAVE_LAYOUT_HANDLER_METHOD_NAME, null, this);
                break;
            // Program Options -> Switch to render mode
            case 5:
                surfaceMapper.toggleCalibration();
                break;

            // RESERVED for Quad Options > name
            case 6:
            	surfaceMapper.getSurfaces().get(sketchMapper.getMostRecentSurface()).setSurfaceName(quadOptions.getName());
                break;

            // Quad Options -> increase resolution
            case 7:
                // Get the most recently active surface
                // This throws a bunch of gnarly errors to the console, but seems to work...
                ss = surfaceMapper.getSurfaceById(sketchMapper.getMostRecentSurface());
                ss.increaseResolution();
                break;

            // Quad Options -> decrease resolution
            case 8:
                ss = surfaceMapper.getSurfaceById(sketchMapper.getMostRecentSurface());
                ss.decreaseResolution();
                break;

            // Quad Options -> Source file
            case 9:
                //for (Sketch sketch : surfaceMapper.getSketchList()) {
                //    if (e.getController().getLabel().equals(sketch.getName())) {
                //        surfaceMapper.getSurfaces().get(sketchMapper.getMostRecentSurface()).setSketch(sketch);
                //        break;
                //    }
                //}
            	surfaceMapper.getSurfaces().get(sketchMapper.getMostRecentSurface()).setSketch(surfaceMapper.getSketchList().get(((int) e.getController().getValue())));
            	surfaceMapper.getSurfaces().get(sketchMapper.getMostRecentSurface()).setSketchIndex((int) e.getController().getValue());
                break;

            // RESERVED for Bezier Options-> name
            case 10:
            	surfaceMapper.getSurfaces().get(sketchMapper.getMostRecentSurface()).setSurfaceName(bezierOptions.getName());
                break;

            // Bezier Options -> increase resolution
            case 11:
                ss = surfaceMapper.getSurfaceById(sketchMapper.getMostRecentSurface());
                ss.increaseResolution();
                break;

            // Bezier Options -> decrease resolution
            case 12:
                ss = surfaceMapper.getSurfaceById(sketchMapper.getMostRecentSurface());
                ss.decreaseResolution();
                break;

            // Bezier Options -> increase horizontal force
            case 13:
                ss = surfaceMapper.getSurfaceById(sketchMapper.getMostRecentSurface());
                ss.increaseHorizontalForce();
                break;
            // Bezier Options -> decrease horizontal force
            case 14:
                ss = surfaceMapper.getSurfaceById(sketchMapper.getMostRecentSurface());
                ss.decreaseHorizontalForce();
                break;

            // Bezier Options -> increase vertical force
            case 15:
                ss = surfaceMapper.getSurfaceById(sketchMapper.getMostRecentSurface());
                ss.increaseVerticalForce();
                break;

            // Bezier Options -> decrease vertical force
            case 16:
                ss = surfaceMapper.getSurfaceById(sketchMapper.getMostRecentSurface());
                ss.decreaseVerticalForce();
                break;

            // Bezier Options -> Source file
            case 17:
                //for (Sketch sketch : surfaceMapper.getSketchList()) {
                //    if (e.getController().getLabel().equals(sketch.getName())) {
                //        surfaceMapper.getSurfaces().get(sketchMapper.getMostRecentSurface()).setSketch(sketch);
                //        break;
                //    }
                //}
                surfaceMapper.getSurfaces().get(sketchMapper.getMostRecentSurface()).setSketch(surfaceMapper.getSketchList().get(((int) e.getController().getValue())));
                surfaceMapper.getSurfaces().get(sketchMapper.getMostRecentSurface()).setSketchIndex((int) e.getController().getValue());
                break;
            
            //bring front
            case 20:
            	Collections.swap(surfaceMapper.getSurfaces(),sketchMapper.getMostRecentSurface(), surfaceMapper.getSurfaces().size()-1);
                break;
                
            //bring back
            case 21:
            	Collections.swap(surfaceMapper.getSurfaces(), sketchMapper.getMostRecentSurface(), 0);
                break;
        }
    }
}
