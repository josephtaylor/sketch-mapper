package jto.processing.sketch.mapper;

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
    private DropdownList sourceList;
    private PFont smallFont;

    public QuadOptionsMenu(SketchMapper sketchMapper, PApplet parent, ControlP5 controlP5) {
        this.sketchMapper = sketchMapper;
        this.parent = parent;
        smallFont = parent.createFont("Verdana", 11, false);
        ControlFont font = new ControlFont(smallFont, 11);

        // Quad options group
        quadGroup = controlP5.addGroup("Quad options")
                .setPosition(20, 40)
                .setBackgroundHeight(190)
                .setWidth(250)
                .setBarHeight(20)
                .setBackgroundColor(parent.color(0, 50));
        quadGroup.captionLabel().style().marginTop = 6;

        // Name textfield
        name = controlP5.addTextfield("Quad surface name")
                .setPosition(20, 20)
                .setSize(200, 25)
                .setFont(smallFont)
                .setGroup(quadGroup);
        controlP5.getTooltip().register("Quad surface name", "Name of quad");

        // Increase resolution button
        increaseResolution = controlP5.addButton("+ Increase")
                .setPosition(20, 90)
                .setSize(100, 20)
                .setId(7)
                .setGroup(quadGroup);
        increaseResolution.captionLabel().setFont(font).toUpperCase(false);

        // Decrease resolution button
        decreaseResolution = controlP5.addButton("- Decrease")
                .setPosition(125, 90)
                .setSize(100, 20)
                .setId(8)
                .setGroup(quadGroup);
        decreaseResolution.captionLabel().setFont(font).toUpperCase(false);

        // Source file dropdown
        sourceList = controlP5.addDropdownList("sourcelist")
                .setPosition(20, 160)
                .setSize(200, 150)
                .setBarHeight(20)
                .setItemHeight(20)
                .setId(9)
                .setGroup(quadGroup);

        sourceList.captionLabel().set("Sketches");
        sourceList.captionLabel().style().marginTop = 5;
    }

    /**
     * **********************************************
     * Populate the source list with the filenames
     * of all the textures found in data/texures
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
            parent.text("Resolution", quadGroup.getPosition().x + 20, quadGroup.getPosition().y + 85);
            parent.text("Source file", quadGroup.getPosition().x + 20, quadGroup.getPosition().y + 135);
        }
    }

    public void setSurfaceName(String name) {
        this.name.setValue(name);
    }

    public void setSelectedSketch(String sketch) {
        this.sourceList.setLabel(sketch);
    }

    public void show() {
        quadGroup.show();
    }
}

