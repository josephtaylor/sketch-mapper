package jto.processing.surface.mapper.menu;


import controlP5.*;
import processing.core.PApplet;
import processing.core.PFont;

import java.io.File;

public class BezierOptionsMenu {
    private final PApplet parent;
    private Group bezierGroup;
    private Textfield name;
    private Button increaseResolution;
    private Button decreaseResolution;
    private Button increaseHorizontalForce;
    private Button decreaseHorizontalForce;
    private Button increaseVerticalForce;
    private Button decreaseVerticalForce;
    private DropdownList sourceList;
    private PFont smallFont;

    public BezierOptionsMenu(final PApplet parent, final ControlP5 controlP5) {
        this.parent = parent;
        // Initialize the font
        smallFont = parent.createFont("Verdana", 11, false);
        ControlFont font = new ControlFont(smallFont, 11);

        // Quad options group
        bezierGroup = controlP5.addGroup("Bezier options")
                .setPosition(20, 40)
                .setBackgroundHeight(300)
                .setWidth(250)
                .setBarHeight(20)
                .setBackgroundColor(parent.color(0, 50));
        bezierGroup.captionLabel().style().marginTop = 6;

        // Name textfield
        name = controlP5.addTextfield("Bezier surface name")
                .setPosition(20, 20)
                .setSize(200, 25)
                .setFont(smallFont)
                .setId(10)
                .setGroup(bezierGroup);
        controlP5.getTooltip().register("Bezier surface name", "Name of bezier surface");

        // Increase resolution button
        increaseResolution = controlP5.addButton("+ Increase ")
                .setPosition(20, 90)
                .setSize(100, 20)
                .setId(11)
                .setGroup(bezierGroup);
        increaseResolution.captionLabel().setFont(font).toUpperCase(false);
        controlP5.getTooltip().register("+ Increase ", "Increase resolution");

        // Decrease resolution button
        decreaseResolution = controlP5.addButton("- Decrease ")
                .setPosition(125, 90)
                .setSize(100, 20)
                .setId(12)
                .setGroup(bezierGroup);
        decreaseResolution.captionLabel().setFont(font).toUpperCase(false);
        controlP5.getTooltip().register("- Decrease ", "Decrease resolution");

        // Increase horizontal force button
        increaseHorizontalForce = controlP5.addButton("+ Increase  ")
                .setPosition(20, 140)
                .setSize(100, 20)
                .setId(13)
                .setGroup(bezierGroup);
        increaseHorizontalForce.captionLabel().setFont(font).toUpperCase(false);
        controlP5.getTooltip().register("+ Increase  ", "Increase horizontal force");

        // Decrease horizontal force button
        decreaseHorizontalForce = controlP5.addButton("- Decrease  ")
                .setPosition(125, 140)
                .setSize(100, 20)
                .setId(14)
                .setGroup(bezierGroup);
        decreaseHorizontalForce.captionLabel().setFont(font).toUpperCase(false);
        controlP5.getTooltip().register("- Decrease  ", "Decrease horizontal force");

        // Increase vertical force button
        increaseVerticalForce = controlP5.addButton("+ Increase   ")
                .setPosition(20, 190)
                .setSize(100, 20)
                .setId(15)
                .setGroup(bezierGroup);
        increaseVerticalForce.captionLabel().setFont(font).toUpperCase(false);
        controlP5.getTooltip().register("+ Increase   ", "Increase vertical force");

        // Decrease vertical force button
        decreaseVerticalForce = controlP5.addButton("- Decrease   ")
                .setPosition(125, 190)
                .setSize(100, 20)
                .setId(16)
                .setGroup(bezierGroup);
        decreaseVerticalForce.captionLabel().setFont(font).toUpperCase(false);
        controlP5.getTooltip().register("- Decrease   ", "Decrease vertical force");

        // Source file dropdown
        sourceList = controlP5.addDropdownList("Texture sketch list ")
                .setPosition(20, 260)
                .setSize(200, 150)
                .setBarHeight(20)
                .setItemHeight(20)
                .setId(17)
                .setGroup(bezierGroup);

        compileSourceList();

        sourceList.captionLabel().set("Sketches");
        sourceList.captionLabel().style().marginTop = 5;
    }

    public void compileSourceList() {
        File file = new File(parent.sketchPath + "/data/textures");

        if (file.isDirectory()) {
            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++) {
                sourceList.addItem(files[i].getName(), i);
            }
        }
    }

    public void hide() {
        bezierGroup.hide();
    }

    public void render() {
        if (bezierGroup.isOpen()) {
            parent.text("Resolution", bezierGroup.getPosition().x + 20, bezierGroup.getPosition().y + 85);
            parent.text("Horizontal force", bezierGroup.getPosition().x + 20, bezierGroup.getPosition().y + 135);
            parent.text("Vertical force", bezierGroup.getPosition().x + 20, bezierGroup.getPosition().y + 185);
            parent.text("Source file", bezierGroup.getPosition().x + 20, bezierGroup.getPosition().y + 235);
        }
    }

    public void setSurfaceName(String name) {
        this.name.setValue(name);
    }

    public void setSelectedSketch(String sketch) {
        this.sourceList.setLabel(sketch);
    }

    public void show() {
        bezierGroup.show();
    }
}
