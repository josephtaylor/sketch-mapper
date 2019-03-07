package jto.processing.sketch.mapper;


import ixagon.surface.mapper.SuperSurface;
import ixagon.surface.mapper.SurfaceMapper;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.util.Iterator;
import java.util.List;

import static ixagon.surface.mapper.SurfaceMapper.MODE_CALIBRATE;
import static ixagon.surface.mapper.SurfaceMapper.MODE_RENDER;

public class SketchMapper {

    private final PApplet parent;
    private int initialSurfaceResolution = 6;
    private int mostRecentSurface = 0;
    // SurfaceMapper variables
    private PGraphics graphicsOffScreen;
    private SurfaceMapper surfaceMapper;
    private QuadOptionsMenu quadOptions;
    private BezierOptionsMenu bezierOptions;
    private ProgramOptionsMenu programOptions;
    private PImage backgroundImage;
    private String layoutFilename;
    private boolean firstDraw = true;
    private ControlWindow controlWindow;

    /**
     * Constructor for SketchMapper objects.
     *
     * @param parent the parent sketch.
     */
    public SketchMapper(final PApplet parent) {
        this(parent, null);
    }

    /**
     * Constructor for SketchMapper objects.
     *
     * @param parent   the parent sketch.
     * @param filename the filename of the layout to load.
     */
    public SketchMapper(final PApplet parent, final String filename) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass("controlP5.ControlP5");
        } catch (ClassNotFoundException e) {
            PApplet.println("SketchMapper requires the ControlP5 library to also be installed.");
            PApplet.println("Please install ControlP5 version 2.2.6 via the Contribution Manager and import into this sketch.");
            throw new ControlP5MissingException();
        }
        try {
            this.parent = parent;

            //register our handler methods in this object on our parent.
            parent.registerMethod("mouseEvent", this);
            parent.registerMethod("keyEvent", this);

            // Create an off-screen buffer (makes graphics go fast!)
            graphicsOffScreen = parent.createGraphics(parent.width, parent.height, PApplet.P3D);

            // Create new instance of SurfaceMapper
            surfaceMapper = new SurfaceMapper(parent, parent.width, parent.height);
            surfaceMapper.setDisableSelectionTool(true);

            // Initialize custom menus
            controlWindow = new ControlWindow(parent, parent.width, parent.height, surfaceMapper, this);
            quadOptions = controlWindow.getQuadOptions();
            bezierOptions = controlWindow.getBezierOptions();
            programOptions = controlWindow.getProgramOptions();

            // Hide the menus
            bezierOptions.hide();

            //if a file is specified, load it and skip default initialization.
            if (null != filename && filename.trim().length() > 0) {
                layoutFilename = filename;
            }

            // Update the GUI for the default surface
            quadOptions.setSurfaceName("0");
            bezierOptions.setSurfaceName("0");
        } catch (Exception e) {
            PApplet.println("Something went wrong in the initialization of SketchMapper");
            e.printStackTrace();
            //rethrow so processing halts.
            throw e;
        }
    }

    /**
     * Adds the sketch to the list of sketches.
     *
     * @param sketch the sketch to add.
     */
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
        bezierOptions.compileSourceList();
    }

    /**
     * The draw method. Invoke this in the parent sketch's draw method
     * which overrides {@link processing.core.PApplet#draw}.
     */
    public void draw() {

        /*
            Deferred loading of layout until first draw so all added sketches are present in the list of sketches.
            Only need to load the layout on the first frame draw.
            Not the prettiest solution but it works...
         */
        if (firstDraw) {
            if (null != layoutFilename) {
                surfaceMapper.load(parent.dataFile(layoutFilename));
            } else {
                // Creates one surface at center of screen
                surfaceMapper.createQuadSurface(initialSurfaceResolution, parent.width / 2, parent.height / 2);
            }

            // add sketches to surfaces, if not set
            if (surfaceMapper.getSketchList().size() > 0) {
                Sketch defaultSketch = surfaceMapper.getSketchList().get(0);

                for (SuperSurface ss : surfaceMapper.getSurfaces()) {
                    Sketch s = ss.getSketch();
                    if (s == null) {
                        ss.setSketch(defaultSketch);
                    }
                }
            }

            firstDraw = false;
        }

        parent.background(0);

        // Empty out the off-screen renderer
        graphicsOffScreen.beginDraw();
        graphicsOffScreen.background(0);
        if ((null != backgroundImage) && surfaceMapper.getMode() == MODE_CALIBRATE) {
            graphicsOffScreen.image(backgroundImage, 0, 0);
        }
        graphicsOffScreen.endDraw();

        // Calibration mode
        if (surfaceMapper.getMode() == MODE_CALIBRATE) {
            surfaceMapper.render(graphicsOffScreen);

            // Show the GUI
            programOptions.show();

            // Render mode
        } else if (surfaceMapper.getMode() == MODE_RENDER) {
            // Hide the GUI
            quadOptions.hide();
            bezierOptions.hide();
            programOptions.hide();
            // Render each surface to the GLOS using their textures
            for (SuperSurface ss : surfaceMapper.getSurfaces()) {
                Sketch s = ss.getSketch();
                if (s != null) {
                    s.draw();
                    ss.render(parent.g, s.getPGraphics().get());
                } else {
                    PApplet.println("Sketch not set?");
                }
            }
        }

        // Display the GLOS to screen
        if (surfaceMapper.getMode() == MODE_CALIBRATE) {
            parent.image(graphicsOffScreen.get(), 0, 0, parent.width, parent.height);
        }

        // Render any stray GUI elements over the GLOS
        if (surfaceMapper.getMode() == MODE_CALIBRATE) {
            programOptions.render();

            for (SuperSurface surface : surfaceMapper.getSelectedSurfaces()) {
                mostRecentSurface = surface.getId();
            }

            SuperSurface ss = surfaceMapper.getSurfaceById(mostRecentSurface);

            if (null == ss) {
                return;
            }
            if (ss.getSurfaceType() == SuperSurface.QUAD) {
                quadOptions.render();
            } else if (ss.getSurfaceType() == SuperSurface.BEZIER) {
                bezierOptions.render();
            }
        }
    }

    public List<Sketch> getSketchList() {
        return surfaceMapper.getSketchList();
    }

    /**
     * Invoked whenever a KeyEvent happens.
     *
     * @param event the KeyEvent.
     */
    public void keyEvent(KeyEvent event) {
        if (surfaceMapper.getMode() == MODE_CALIBRATE) {
            for (SuperSurface surface : surfaceMapper.getSelectedSurfaces()) {
                mostRecentSurface = surface.getId();
            }

            if (java.awt.event.KeyEvent.VK_DELETE == event.getKeyCode()) {
                Iterator<SuperSurface> it = surfaceMapper.getSurfaces().iterator();
                while (it.hasNext()) {
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

    /**
     * Invoked whenever a mouseEvent happens.
     *
     * @param event the mouse event.
     */
    public void mouseEvent(MouseEvent event) {
        for (Sketch sketch : getSketchList()) {
            sketch.mouseEvent(event);
        }

        if (MouseEvent.RELEASE != event.getAction()) {
            return;
        }
        // Double click returns to calibration mode
        if (surfaceMapper.getMode() == MODE_RENDER && event.getCount() == 2) {
            surfaceMapper.toggleCalibration();
        }

        // Show and update the appropriate menu
        if (surfaceMapper.getMode() == MODE_CALIBRATE) {
            // Find selected surface
            for (SuperSurface surface : surfaceMapper.getSelectedSurfaces()) {
                mostRecentSurface = surface.getId();
            }

            SuperSurface surface = surfaceMapper.getSurfaceById(mostRecentSurface);

            if (null == surface) {
                return;
            }

            if (surface.getSurfaceType() == SuperSurface.QUAD) {
                bezierOptions.hide();
                quadOptions.show();

                quadOptions.setSurfaceName(surface.getSurfaceName());
                if (null != surface.getSketch()) {
                    quadOptions.setSelectedSketch(surface.getSketchIndex());
                }
            } else if (surface.getSurfaceType() == SuperSurface.BEZIER) {
                quadOptions.hide();
                bezierOptions.show();

                bezierOptions.setSurfaceName(surface.getSurfaceName());
                if (null != surface.getSketch()) {
                    bezierOptions.setSelectedSketch(surface.getSketchIndex());
                }
            }
        }
    }

    public int getMostRecentSurface() {
        return mostRecentSurface;
    }

    public void resetMostRecentSurface() {
        mostRecentSurface = 0;
    }

    public boolean isMouseWithinAnySurface() {
        return surfaceMapper.findActiveSurface(parent.mouseX, parent.mouseY);
    }

    /**
     * Removes the given sketch from the list of sketches.
     *
     * @param sketch the sketch to be removed.
     */
    public void removeSketch(Sketch sketch) {
        surfaceMapper.getSketchList().remove(sketch);
    }

    /**
     * This allows you set a background image. It will be displayed behind
     * any rendered surfaces.
     *
     * @param backgroundImage the background image.
     */
    public void setBackgroundImage(PImage backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setSketchList(List<Sketch> sketchList) {
        surfaceMapper.setSketchList(sketchList);
    }
    
}
