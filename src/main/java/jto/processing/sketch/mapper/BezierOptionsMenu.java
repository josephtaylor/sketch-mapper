package jto.processing.sketch.mapper;


import controlP5.Button;
import controlP5.ControlFont;
import controlP5.ControlP5;
import controlP5.DropdownList;
import controlP5.Group;
import controlP5.Textfield;
import processing.core.PApplet;
import processing.core.PFont;

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
    private final SketchMapper sketchMapper;

    public BezierOptionsMenu(final SketchMapper sketchMapper, final PApplet parent, final ControlP5 controlP5) {
        this.sketchMapper = sketchMapper;
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
        bezierGroup.getCaptionLabel().getStyle().marginTop = 6;

        // Name textfield
        name = controlP5.addTextfield("Bezier surface name")
                .setPosition(20, 20)
                .setSize(200, 25)
                .setFont(smallFont)
                .setId(10)
                .setGroup(bezierGroup);

        // Increase resolution button
        increaseResolution = controlP5.addButton("+ Increase ")
                .setPosition(20, 90)
                .setSize(100, 20)
                .setId(11)
                .setGroup(bezierGroup);
        increaseResolution.getCaptionLabel().setFont(font).toUpperCase(false);

        // Decrease resolution button
        decreaseResolution = controlP5.addButton("- Decrease ")
                .setPosition(125, 90)
                .setSize(100, 20)
                .setId(12)
                .setGroup(bezierGroup);
        decreaseResolution.getCaptionLabel().setFont(font).toUpperCase(false);

        // Increase horizontal force button
        increaseHorizontalForce = controlP5.addButton("+ Increase  ")
                .setPosition(20, 140)
                .setSize(100, 20)
                .setId(13)
                .setGroup(bezierGroup);
        increaseHorizontalForce.getCaptionLabel().setFont(font).toUpperCase(false);

        // Decrease horizontal force button
        decreaseHorizontalForce = controlP5.addButton("- Decrease  ")
                .setPosition(125, 140)
                .setSize(100, 20)
                .setId(14)
                .setGroup(bezierGroup);
        decreaseHorizontalForce.getCaptionLabel().setFont(font).toUpperCase(false);

        // Increase vertical force button
        increaseVerticalForce = controlP5.addButton("+ Increase   ")
                .setPosition(20, 190)
                .setSize(100, 20)
                .setId(15)
                .setGroup(bezierGroup);
        increaseVerticalForce.getCaptionLabel().setFont(font).toUpperCase(false);

        // Decrease vertical force button
        decreaseVerticalForce = controlP5.addButton("- Decrease   ")
                .setPosition(125, 190)
                .setSize(100, 20)
                .setId(16)
                .setGroup(bezierGroup);
        decreaseVerticalForce.getCaptionLabel().setFont(font).toUpperCase(false);

        // Source file dropdown
        sourceList = controlP5.addDropdownList("Texture sketch list ")
                .setPosition(20, 260)
                .setSize(200, 150)
                .setBarHeight(20)
                .setItemHeight(20)
                .setId(17)
                .setGroup(bezierGroup);

        compileSourceList();

        sourceList.setCaptionLabel("Sketches");
        sourceList.getCaptionLabel().getStyle().marginTop = 5;
    }

    public void compileSourceList() {
        sourceList.clear();
        int i = 0;
        for (Sketch sketch : sketchMapper.getSketchList()) {
            sourceList.addItem(sketch.getName(), i);
            i++;
        }
    }

    public void hide() {
        bezierGroup.hide();
    }

    public void render() {
        if (bezierGroup.isOpen()) {
            parent.text("Resolution", bezierGroup.getPosition()[0] + 20, bezierGroup.getPosition()[1] + 85);
            parent.text("Horizontal force", bezierGroup.getPosition()[0] + 20, bezierGroup.getPosition()[1] + 135);
            parent.text("Vertical force", bezierGroup.getPosition()[0] + 20, bezierGroup.getPosition()[1] + 185);
            parent.text("Source file", bezierGroup.getPosition()[0] + 20, bezierGroup.getPosition()[1] + 235);
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
