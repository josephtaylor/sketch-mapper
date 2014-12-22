package jto.processing.surface.mapper.menu;

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
                .setPosition(parent.width - 300, 40)
                .setBackgroundHeight(300)
                .setWidth(280)
                .setBarHeight(20)
                .setBackgroundColor(parent.color(0, 50));
        programGroup.captionLabel().style().marginTop = 6;

        // Create new quad button
        controlP5.addButton("newQuad")
                .setPosition(10, 20)
                .setImages(
                        parent.loadImage("buttons/new-quad-off.png"),
                        parent.loadImage("buttons/new-quad-hover.png"),
                        parent.loadImage("buttons/new-quad-click.png"))
                .updateSize()
                .setId(1)
                .setGroup(programGroup);

        // Create new bezier button
        controlP5.addButton("newBezier")
                .setPosition(10, 65)
                .setImages(
                        parent.loadImage("buttons/new-bezier-off.png"),
                        parent.loadImage("buttons/new-bezier-hover.png"),
                        parent.loadImage("buttons/new-bezier-click.png"))
                .updateSize()
                .setId(2)
                .setGroup(programGroup);

        // Load layout button
        controlP5.addButton("loadLayout")
                .setPosition(10, 130)
                .setImages(
                        parent.loadImage("buttons/load-layout-off.png"),
                        parent.loadImage("buttons/load-layout-hover.png"),
                        parent.loadImage("buttons/load-layout-click.png"))
                .updateSize()
                .setId(3)
                .setGroup(programGroup);

        // Load layout button
        controlP5.addButton("saveLayout")
                .setPosition(130, 130)
                .setImages(
                        parent.loadImage("buttons/save-layout-off.png"),
                        parent.loadImage("buttons/save-layout-hover.png"),
                        parent.loadImage("buttons/save-layout-click.png"))
                .updateSize()
                .setId(4)
                .setGroup(programGroup);

        // Save layout button
        controlP5.addButton("switchRender")
                .setPosition(10, 195)
                .setImages(
                        parent.loadImage("buttons/switch-render-off.png"),
                        parent.loadImage("buttons/switch-render-hover.png"),
                        parent.loadImage("buttons/switch-render-click.png"))
                .updateSize()
                .setId(5)
                .setGroup(programGroup);
    }

    public void hide() {
        programGroup.hide();
    }

    public void render() {
        if (programGroup.isOpen()) {
            parent.stroke(255, 150);
            parent.line(programGroup.getPosition().x, programGroup.getPosition().y + 115, programGroup.getPosition().x + programGroup.getWidth(), programGroup.getPosition().y + 115);

            parent.stroke(255, 150);
            parent.line(programGroup.getPosition().x, programGroup.getPosition().y + 177, programGroup.getPosition().x + programGroup.getWidth(), programGroup.getPosition().y + 177);

            parent.textFont(smallFont);
            parent.fill(255);
            parent.text("Double click to return", programGroup.getPosition().x + 20, programGroup.getPosition().y + 245);

            parent.text("Hit Escape to close program", programGroup.getPosition().x + 20, programGroup.getPosition().y + 280);
        }
    }

    public void show() {
        programGroup.show();
    }
}

