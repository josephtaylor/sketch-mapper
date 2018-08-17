package jto.processing.sketch.mapper;

import controlP5.ControlP5;
import controlP5.Group;
import processing.core.PApplet;
import processing.core.PFont;

public class ProgramOptionsMenu {
    private final PApplet parent;
    private Group programGroup;
    private PFont smallFont;

    public ProgramOptionsMenu(final PApplet parent, final ControlP5 controlP5) {
        this.parent = parent;
        smallFont = parent.createFont("Verdana", 11, false);

        // Program options menu
        programGroup = controlP5.addGroup("Program options")
                .setPosition(20, 40)
                .setBackgroundHeight(150)
                .setWidth(280)
                .setBarHeight(20)
                .setBackgroundColor(parent.color(0, 50));
        programGroup.getCaptionLabel().getStyle().marginTop = 6;

        // Create new quad button
        controlP5.addButton("Add Quad Surface")
                .setPosition(10, 20)
                .setWidth(125)
                .setId(1)
                .setGroup(programGroup);

        // Create new bezier button
        controlP5.addButton("Add Bezier Surface")
                .setPosition(140, 20)
                .setWidth(125)
                .setId(2)
                .setGroup(programGroup);

        // Load layout button
        controlP5.addButton("Load Layout from file")
                .setPosition(10, 60)
                .setWidth(125)
                .setId(3)
                .setGroup(programGroup);

        // Save layout button
        controlP5.addButton("Save Layout to file")
                .setPosition(140, 60)
                .setWidth(125)
                .setId(4)
                .setGroup(programGroup);

        // Render layout button
        controlP5.addButton("Render")
                .setPosition(10, 100)
                .setWidth(255)
                .setId(5)
                .setGroup(programGroup);
    }

    public void hide() {
        programGroup.hide();
    }

    public void render() {
        //if (programGroup.isOpen()) {
        //    parent.stroke(255, 150);
        //    parent.line(programGroup.getPosition()[0], programGroup.getPosition()[1] + 115, programGroup.getPosition()[0] + programGroup.getWidth(), programGroup.getPosition()[1] + 115);

        //    parent.stroke(255, 150);
        //    parent.line(programGroup.getPosition()[0], programGroup.getPosition()[1] + 177, programGroup.getPosition()[0] + programGroup.getWidth(), programGroup.getPosition()[1] + 177);

        //    parent.textFont(smallFont);
        //    parent.fill(255);
        //    parent.text("Double click to return to setup", programGroup.getPosition()[0] + 20, programGroup.getPosition()[1] + 245);
        //}
    }

    public void show() {
        programGroup.show();
    }
}

