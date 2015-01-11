package jto.processing.surface.mapper;


import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import ixagon.SurfaceMapper.SuperSurface;
import ixagon.SurfaceMapper.SurfaceMapper;
import jto.processing.sketch.Sketch;
import jto.processing.surface.mapper.menu.BezierOptionsMenu;
import jto.processing.surface.mapper.menu.ProgramOptionsMenu;
import jto.processing.surface.mapper.menu.QuadOptionsMenu;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class SurfaceMapperGui {

    public static final String LOAD_LAYOUT_HANDLER_METHOD_NAME = "loadLayoutHandler";
    public static final String SAVE_LAYOUT_HANDLER_METHOD_NAME = "saveLayoutHandler";
    private final PApplet parent;
    int initialSurfaceResolution = 6;
    // Custom GUI objects
    ControlP5 controlP5;
    int mostRecentSurface = 0;
    // SurfaceMapper variables
    private PGraphics graphicsOffScreen;
    private SurfaceMapper surfaceMapper;
    private QuadOptionsMenu quadOptions;
    private BezierOptionsMenu bezierOptions;
    private ProgramOptionsMenu programOptions;
    private PImage backgroundImage;

    public SurfaceMapperGui(final PApplet parent) {
        this.parent = parent;
        parent.registerMethod("mouseEvent", this);
        parent.registerMethod("keyEvent", this);

        // Setup the ControlP5 GUI
        controlP5 = new ControlP5(parent);

        controlP5.addListener(new ControlListener() {
            @Override
            public void controlEvent(ControlEvent controlEvent) {
                controlEventDelegate(controlEvent);
            }
        });

        // Initialize custom menus
        quadOptions = new QuadOptionsMenu(this, parent, controlP5);
        bezierOptions = new BezierOptionsMenu(parent, controlP5);
        programOptions = new ProgramOptionsMenu(parent, controlP5);

        // Hide the menus
        //quadOptions.hide();
        bezierOptions.hide();

        // Update the GUI for the default surface
        quadOptions.setSurfaceName("0");
        bezierOptions.setSurfaceName("0");

        // Create an off-screen buffer (makes graphics go fast!)
        graphicsOffScreen = parent.createGraphics(parent.width, parent.height, PApplet.OPENGL);

        // Create new instance of SurfaceMapper
        surfaceMapper = new SurfaceMapper(parent, parent.width, parent.height);
        surfaceMapper.setDisableSelectionTool(true);

        // Creates one surface with subdivision 3, at center of screen
        surfaceMapper.createQuadSurface(initialSurfaceResolution, parent.width / 2, parent.height / 2);

    }

    public void addSketch(Sketch sketch) {
        sketch.setup();
        this.surfaceMapper.getSketchList().add(sketch);
        if (surfaceMapper.getSurfaces().size() == 1) {
            surfaceMapper.getSurfaces().get(0).setSketch(sketch);
        }
        compileSourceLists();
    }

    private void compileSourceLists() {
        quadOptions.compileSourceList();
    }

    public void controlEventDelegate(ControlEvent e) {
        SuperSurface ss;
        int diff;

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
                parent.selectInput("Load layout", LOAD_LAYOUT_HANDLER_METHOD_NAME, null, this);
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
                break;

            // Quad Options -> increase resolution
            case 7:
                // Get the most recently active surface
                // This throws a bunch of gnarly errors to the console, but seems to work...
                ss = surfaceMapper.getSurfaceById(mostRecentSurface);
                ss.increaseResolution();
                break;

            // Quad Options -> decrease resolution
            case 8:
                ss = surfaceMapper.getSurfaceById(mostRecentSurface);
                ss.decreaseResolution();
                break;

            // Quad Options -> Source file
            case 9:
                for (Sketch sketch : surfaceMapper.getSketchList()) {
                    if (e.getGroup().captionLabel().getText().equals(sketch.getName())) {
                        surfaceMapper.getSurfaces().get(mostRecentSurface).setSketch(sketch);
                        break;
                    }
                }
                break;

            // RESERVED for Bezier Options-> name
            case 10:
                break;

            // Bezier Options -> increase resolution
            case 11:
                ss = surfaceMapper.getSurfaceById(mostRecentSurface);
                ss.increaseResolution();
                break;

            // Bezier Options -> decrease resolution
            case 12:
                ss = surfaceMapper.getSurfaceById(mostRecentSurface);
                ss.decreaseResolution();
                break;

            // Bezier Options -> increase horizontal force
            case 13:
                ss = surfaceMapper.getSurfaceById(mostRecentSurface);
                ss.increaseHorizontalForce();
                break;
            // Bezier Options -> decrease horizontal force
            case 14:
                ss = surfaceMapper.getSurfaceById(mostRecentSurface);
                ss.decreaseHorizontalForce();
                break;

            // Bezier Options -> increase vertical force
            case 15:
                ss = surfaceMapper.getSurfaceById(mostRecentSurface);
                ss.increaseVerticalForce();
                break;

            // Bezier Options -> decrease vertical force
            case 16:
                ss = surfaceMapper.getSurfaceById(mostRecentSurface);
                ss.decreaseVerticalForce();
                break;

            // Bezier Options -> Source file
            case 17:
                for (Sketch sketch : surfaceMapper.getSketchList()) {
                    if (e.getGroup().captionLabel().getText().equals(sketch.getName())) {
                        surfaceMapper.getSurfaces().get(mostRecentSurface).setSketch(sketch);
                        break;
                    }
                }
                break;
        }
    }

    public void keyEvent(KeyEvent event) {
        if (surfaceMapper.getMode() == surfaceMapper.MODE_CALIBRATE) {
            for (SuperSurface surface : surfaceMapper.getSelectedSurfaces()) {
                mostRecentSurface = surface.getId();
            }

            if (java.awt.event.KeyEvent.VK_DELETE == event.getKeyCode()) {
                Iterator<SuperSurface> it = surfaceMapper.getSurfaces().iterator();
                while(it.hasNext()) {
                    SuperSurface superSurface = it.next();
                    if (superSurface.getId() == mostRecentSurface) {
                        it.remove();
                        surfaceMapper.getSelectedSurfaces().clear();
                        break;
                    }
                }
            }
        }
        for (Sketch sketch : getSketchList()) {
            sketch.keyEvent(event);
        }
    }

    public void draw() {
        parent.background(0);

        // Empty out the off-screen renderer
        graphicsOffScreen.beginDraw();
        graphicsOffScreen.background(0);
        if ((null != backgroundImage) && surfaceMapper.getMode() == surfaceMapper.MODE_CALIBRATE) {
            graphicsOffScreen.image(backgroundImage, 0, 0);
        }
        graphicsOffScreen.endDraw();

        // Calibration mode
        if (surfaceMapper.getMode() == surfaceMapper.MODE_CALIBRATE) {
            surfaceMapper.render(graphicsOffScreen);

            // Show the GUI
            programOptions.show();

            // Render mode
        } else if (surfaceMapper.getMode() == surfaceMapper.MODE_RENDER) {
            // Hide the GUI
            quadOptions.hide();
            bezierOptions.hide();
            programOptions.hide();

            // Render each surface to the GLOS using their textures
            for (SuperSurface ss : surfaceMapper.getSurfaces()) {
                ss.getSketch().draw();
                ss.render(graphicsOffScreen, ss.getSketch().getPGraphics().get());
                ss.getSketch().update();
            }
        }

        // Display the GLOS to screen
        parent.image(graphicsOffScreen.get(), 0, 0, parent.width, parent.height);

        // Render any stray GUI elements over the GLOS
        if (surfaceMapper.getMode() == surfaceMapper.MODE_CALIBRATE) {
            programOptions.render();

            for (SuperSurface surface : surfaceMapper.getSelectedSurfaces()) {
                mostRecentSurface = surface.getId();
            }

            SuperSurface ss = surfaceMapper.getSurfaceById(mostRecentSurface);

            if (null == ss) {
                return;
            }
            if (ss.getSurfaceType() == ss.QUAD)
                quadOptions.render();
            else if (ss.getSurfaceType() == ss.BEZIER)
                bezierOptions.render();
        }
    }

    public List<Sketch> getSketchList() {
        return surfaceMapper.getSketchList();
    }

    public void loadLayoutHandler(File file) {
        if (null == file) {
            return;
        }
        surfaceMapper.load(file);
        mostRecentSurface = 0;
    }

    public void mouseEvent(MouseEvent event) {
        if (MouseEvent.RELEASE != event.getAction()) {
            return;
        }
        // Double click returns to calibration mode
        if (surfaceMapper.getMode() == surfaceMapper.MODE_RENDER && event.getCount() == 2) {
            surfaceMapper.toggleCalibration();
        }

        // Show and update the appropriate menu
        if (surfaceMapper.getMode() == surfaceMapper.MODE_CALIBRATE) {
            // Find selected surface
            for (SuperSurface surface : surfaceMapper.getSelectedSurfaces()) {
                mostRecentSurface = surface.getId();
            }

            SuperSurface surface = surfaceMapper.getSurfaceById(mostRecentSurface);

            if (null == surface) {
                return;
            }

            if (surface.getSurfaceType() == surface.QUAD) {
                bezierOptions.hide();
                quadOptions.show();

                quadOptions.setSurfaceName(String.valueOf(surface.getId()));
                if (null != surface.getSketch()) {
                    quadOptions.setSelectedSketch(surface.getSketch().getName());
                }
            } else if (surface.getSurfaceType() == surface.BEZIER) {
                quadOptions.hide();
                bezierOptions.show();
                if (null != surface.getSketch()) {
                    bezierOptions.setSurfaceName(String.valueOf(surface.getId()));
                    bezierOptions.setSelectedSketch(surface.getSketch().getName());
                }
            }
        }
    }

    public void removeSketch(Sketch sketch) {
        surfaceMapper.getSketchList().remove(sketch);
    }

    public void saveLayoutHandler(File file) {
        surfaceMapper.save(file);
    }

    public void setBackgroundImage(PImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setSketchList(List<Sketch> sketchList) {
        surfaceMapper.setSketchList(sketchList);
    }
}
