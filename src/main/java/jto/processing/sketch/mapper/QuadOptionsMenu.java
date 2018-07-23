package jto.processing.sketch.mapper;

import java.util.Iterator;

import controlP5.*;
import processing.core.PApplet;
import processing.core.PFont;

public class QuadOptionsMenu {
    private final PApplet parent;
    private final SketchMapper sketchMapper;
    private Group quadGroup;
    private Textfield name;
    private Button increaseResolution;
    private Button decreaseResolution;
    private ScrollableList sourceList;
    private PFont smallFont;

    public QuadOptionsMenu(SketchMapper sketchMapper, PApplet parent, ControlP5 controlP5) {
        this.sketchMapper = sketchMapper;
        this.parent = parent;
        smallFont = parent.createFont("Verdana", 11, false);
        
        // Quad options group
        quadGroup = controlP5.addGroup("Quad Options")
                .setPosition(20, 230)
                .setBackgroundHeight(180)
                .setWidth(280)
                .setBarHeight(20)
                .setBackgroundColor(parent.color(0, 50));
        quadGroup.getCaptionLabel().getStyle().marginTop = 6;

        // Name textfield
        name = controlP5.addTextfield("QuadSurfaceName")
        		.setCaptionLabel("Quad surface name")
                .setPosition(10, 20)
                .setWidth(255)
                .setId(6)
                .setAutoClear(false)
                .setGroup(quadGroup);

        // Increase resolution button
        increaseResolution = controlP5.addButton("QuadIncreaseResolution")
        		.setCaptionLabel("+ resolution")
                .setPosition(10, 60)
                .setWidth(125)
                .setId(7)
                .setGroup(quadGroup);

        // Decrease resolution button
        decreaseResolution = controlP5.addButton("QuadDecreaseResolution")
        		.setCaptionLabel("- resolution")
                .setPosition(140, 60)
                .setWidth(125)
                .setId(8)
                .setGroup(quadGroup);

        // Source file dropdown
        sourceList = controlP5.addScrollableList("QuadSketchesSourcelist")
        		.setCaptionLabel("Sketches Sourcelist")
                .setPosition(10, 100)
                .setWidth(255)
        		.setType(ControlP5.LIST)
        		.setItemHeight(5)
                .setBarHeight(20)
                .setItemHeight(20)
                .setId(9)
                .setGroup(quadGroup);
    }

    /**
     * **********************************************
     * Populate the source list with the filenames
     * of all the textures found in data/textures
     * ************************************************
     */
    public void compileSourceList() {
        sourceList.clear();
        int i = 0;
        for (Sketch sketch : sketchMapper.getSketchList()) {
            sourceList.addItem(sketch.getName(), i);
            i++;
        }
    }

    public void hide() {
        quadGroup.hide();
    }

    public void render() {
        if (quadGroup.isOpen()) {
            //parent.text("Resolution", quadGroup.getPosition()[0] + 20, quadGroup.getPosition()[1] + 85);
            //parent.text("Source file", quadGroup.getPosition()[0] + 20, quadGroup.getPosition()[1] + 135);
        }
    }

    public void setSurfaceName(String name) {
        this.name.setValue(name);
    }

    public void setSelectedSketch(int sketchIndex) {
        this.sourceList.setValue(sketchIndex);
    }
    
    public String getSelectedSketch() {
        return this.sourceList.getLabel();
    }
    
    public String getName() {
        return this.name.getStringValue();
    }

    public void show() {
        quadGroup.show();
    }
}

