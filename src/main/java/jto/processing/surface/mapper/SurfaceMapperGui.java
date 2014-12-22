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
import processing.event.MouseEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SurfaceMapperGui {

    public static final String LOAD_LAYOUT_HANDLER_METHOD_NAME = "loadLayoutHandler";
    public static final String SAVE_LAYOUT_HANDLER_METHOD_NAME = "saveLayoutHandler";
    // File types that are accepted as textures
    private static final String[] imageTypes = {"jpg", "jpeg", "png", "gif", "bmp"};
    private static final String[] movieTypes = {"mp4", "mov", "avi"};
    private final PApplet parent;
    int initialSurfaceResolution = 6;
    // Custom GUI objects
    ControlP5 controlP5;
    int mostRecentSurface = 0;
    private List<Sketch> sketchList = new ArrayList<Sketch>();
    // SurfaceMapper variables
    private PGraphics graphicsOffScreen;
    private SurfaceMapper sm;
    private QuadOptionsMenu quadOptions;
    private BezierOptionsMenu bezierOptions;
    private ProgramOptionsMenu programOptions;

    public SurfaceMapperGui(final PApplet parent) {
        this.parent = parent;
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
        sm = new SurfaceMapper(parent, parent.width, parent.height);
        sm.setDisableSelectionTool(true);

        // Creates one surface with subdivision 3, at center of screen
        SuperSurface superSurface = sm.createQuadSurface(initialSurfaceResolution, parent.width / 2, parent.height / 2);

    }

    public void addSketch(Sketch sketch) {
        this.sketchList.add(sketch);
        if (sm.getSurfaces().size() == 1) {
            sm.getSurfaces().get(0).setSketch(sketch);
        }
    }

    public void controlEventDelegate(ControlEvent e) {
        SuperSurface ss;
        int diff;

        switch (e.getId()) {
            // Program Options -> Create quad surface button
            case 1:
                ss = sm.createQuadSurface(initialSurfaceResolution, parent.width / 2, parent.height / 2);

                // Add a reference to the default texture for this surface
                ss.setSketch(sketchList.get(0));

                break;

            // Program Options -> Create bezier surface button
            case 2:
                ss = sm.createBezierSurface(initialSurfaceResolution, parent.width / 2, parent.height / 2);

                // Add a reference to the default texture for this surface
                ss.setSketch(sketchList.get(0));

                break;

            // Program Options -> Load layout button
            case 3:
                parent.selectInput("Load layout", LOAD_LAYOUT_HANDLER_METHOD_NAME, null, this);
                break;

            // Program Options -> Save layout button
            case 4:
                parent.selectOutput("Save layout", SAVE_LAYOUT_HANDLER_METHOD_NAME, null, this);

                // Program Options -> Switch to render mode
            case 5:
                sm.toggleCalibration();
                break;

            // RESERVED for Quad Options > name
            case 6:
                break;

            // Quad Options -> increase resolution
            case 7:
                // Get the most recently active surface
                // This throws a bunch of gnarly errors to the console, but seems to work...
                ss = sm.getSurfaceById(mostRecentSurface);
                ss.increaseResolution();
                break;

            // Quad Options -> decrease resolution
            case 8:
                ss = sm.getSurfaceById(mostRecentSurface);
                ss.decreaseResolution();
                break;

            // Quad Options -> Source file
            case 9:
                for (Sketch sketch : sketchList) {
                    if (e.getGroup().captionLabel().getText().equals(sketch.getName())) {
                        sm.getSurfaces().get(mostRecentSurface).setSketch(sketch);
                        break;
                    }
                }
                break;

            // RESERVED for Bezier Options-> name
            case 10:
                break;

            // Bezier Options -> increase resolution
            case 11:
                ss = sm.getSurfaceById(mostRecentSurface);
                ss.increaseResolution();
                break;

            // Bezier Options -> decrease resolution
            case 12:
                ss = sm.getSurfaceById(mostRecentSurface);
                ss.decreaseResolution();
                break;

            // Bezier Options -> increase horizontal force
            case 13:
                ss = sm.getSurfaceById(mostRecentSurface);
                ss.increaseHorizontalForce();
                break;
            // Bezier Options -> decrease horizontal force
            case 14:
                ss = sm.getSurfaceById(mostRecentSurface);
                ss.decreaseHorizontalForce();
                break;

            // Bezier Options -> increase vertical force
            case 15:
                ss = sm.getSurfaceById(mostRecentSurface);
                ss.increaseVerticalForce();
                break;

            // Bezier Options -> decrease vertical force
            case 16:
                ss = sm.getSurfaceById(mostRecentSurface);
                ss.decreaseVerticalForce();
                break;

            // Bezier Options -> Source file
            case 17:
                for (Sketch sketch : sketchList) {
                    if (e.getGroup().captionLabel().getText().equals(sketch.getName())) {
                        sm.getSurfaces().get(mostRecentSurface).setSketch(sketch);
                        break;
                    }
                }
                break;
        }
    }

    public void draw() {
        parent.background(0);

        // Empty out the off-screen renderer
        graphicsOffScreen.beginDraw();
        graphicsOffScreen.endDraw();

        // Calibration mode
        if (sm.getMode() == sm.MODE_CALIBRATE) {
            sm.render(graphicsOffScreen);

            // Show the GUI
            programOptions.show();

            // Render mode
        } else if (sm.getMode() == sm.MODE_RENDER) {
            // Hide the GUI
            quadOptions.hide();
            bezierOptions.hide();
            programOptions.hide();

            // Render each surface to the GLOS using their textures
            for (SuperSurface ss : sm.getSurfaces()) {
                ss.getSketch().draw();
                ss.render(graphicsOffScreen, ss.getSketch().getGraphics().get());
            }
        }

        // Display the GLOS to screen
        parent.image(graphicsOffScreen.get(), 0, 0, parent.width, parent.height);

        // Render any stray GUI elements over the GLOS
        if (sm.getMode() == sm.MODE_CALIBRATE) {
            programOptions.render();

            SuperSurface ss = sm.getSurfaceById(mostRecentSurface);

            if (ss.getSurfaceType() == ss.QUAD)
                quadOptions.render();
            else if (ss.getSurfaceType() == ss.BEZIER)
                bezierOptions.render();
        }
    }

    public List<Sketch> getSketchList() {
        return sketchList;
    }

    public void loadLayoutHandler(File file) {
        if (null == file) {
            throw new RuntimeException("file for layout loading is null");
        }
        sm.load(file);
        mostRecentSurface = 0;
    }

    public void mouseReleased(MouseEvent event) {
        // Double click returns to calibration mode
        if (sm.getMode() == sm.MODE_RENDER && event.getCount() == 2) {
            sm.toggleCalibration();
        }

        // Show and update the appropriate menu
        if (sm.getMode() == sm.MODE_CALIBRATE) {
            // Find selected surface
            for (SuperSurface ss : sm.getSelectedSurfaces())
                mostRecentSurface = ss.getId();

            SuperSurface ss = sm.getSurfaceById(mostRecentSurface);

            if (ss.getSurfaceType() == ss.QUAD) {
                bezierOptions.hide();
                quadOptions.show();

                quadOptions.setSurfaceName(String.valueOf(ss.getId()));
            } else if (ss.getSurfaceType() == ss.BEZIER) {
                quadOptions.hide();
                bezierOptions.show();

                bezierOptions.setSurfaceName(String.valueOf(ss.getId()));
            }
        }
    }

    public void removeSketch(Sketch sketch) {
        this.sketchList.remove(sketch);
    }

    public void saveLayoutHandler(File file) {
        sm.save(file);
    }

    public void setSketchList(List<Sketch> sketchList) {
        this.sketchList = sketchList;
    }
}
