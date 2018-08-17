package jto.processing.sketch.mapper;


import controlP5.Button;
import controlP5.ControlFont;
import controlP5.ControlP5;
import controlP5.DropdownList;
import controlP5.Group;
import controlP5.ScrollableList;
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
    private Button bringfront;
    private Button bringback;
    private ScrollableList sourceList;
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
                .setPosition(20, 230)
                .setBackgroundHeight(300)
                .setWidth(280)
                .setBarHeight(20)
                .setBackgroundColor(parent.color(0, 50));
        bezierGroup.getCaptionLabel().getStyle().marginTop = 6;

        // Name textfield
        name = controlP5.addTextfield("BezierSurfaceName")
        		.setCaptionLabel("Bezier surface name")
                .setPosition(10, 20)
                .setWidth(255)
                .setAutoClear(false)
                .setId(10)
                .setGroup(bezierGroup);

        // Increase resolution button
        increaseResolution = controlP5.addButton("BezierIncreaseResolution")
        		.setCaptionLabel("+ resolution")
                .setPosition(10, 60)
                .setWidth(125)
                .setId(11)
                .setGroup(bezierGroup);

        // Decrease resolution button
        decreaseResolution = controlP5.addButton("BezierDecreaseResolution")
        		.setCaptionLabel("- resolution")
                .setPosition(140, 60)
                .setWidth(125)
                .setId(12)
                .setGroup(bezierGroup);

        // Increase horizontal force button
        increaseHorizontalForce = controlP5.addButton("BezierIncreaseHorizontal")
        		.setCaptionLabel("+ Horizontal Force")
                .setPosition(10, 100)
                .setWidth(125)
                .setId(13)
                .setGroup(bezierGroup);

        // Decrease horizontal force button
        decreaseHorizontalForce = controlP5.addButton("BezierDecreaseHorizontal")
        		.setCaptionLabel("- Horizontal Force")
                .setPosition(140, 100)
                .setWidth(125)
                .setId(14)
                .setGroup(bezierGroup);

        // Increase vertical force button
        increaseVerticalForce = controlP5.addButton("BezierIncreaseVertical")
        		.setCaptionLabel("+ Vertical Force")
                .setPosition(10, 140)
                .setWidth(125)
                .setId(15)
                .setGroup(bezierGroup);

        // Decrease vertical force button
        decreaseVerticalForce = controlP5.addButton("BezierDecreaseVertical")
        		.setCaptionLabel("- Vertical Force")
                .setPosition(140, 140)
                .setWidth(125)
                .setId(16)
                .setGroup(bezierGroup);
        
        // Bring front
        bringfront = controlP5.addButton("BezierBringfront")
        		.setCaptionLabel("Bring to Front")
                .setPosition(10, 180)
                .setWidth(125)
                .setId(20)
                .setGroup(bezierGroup);
        
        // Bring back
        bringback = controlP5.addButton("BezierBringback")
        		.setCaptionLabel("Bring to Back")
                .setPosition(140, 180)
                .setWidth(125)
                .setId(21)
                .setGroup(bezierGroup);

        // Source file dropdown
        sourceList = controlP5.addScrollableList("Sketches Sourcelist")
        		.setCaptionLabel("Sketches Sourcelist")
                .setPosition(10, 220)
                .setWidth(255)
        		.setType(ControlP5.LIST)
        		.setItemHeight(5)
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
            //parent.text("Resolution", bezierGroup.getPosition()[0] + 20, bezierGroup.getPosition()[1] + 85);
            //parent.text("Horizontal force", bezierGroup.getPosition()[0] + 20, bezierGroup.getPosition()[1] + 135);
            //parent.text("Vertical force", bezierGroup.getPosition()[0] + 20, bezierGroup.getPosition()[1] + 185);
            //parent.text("Source file", bezierGroup.getPosition()[0] + 20, bezierGroup.getPosition()[1] + 235);
        }
    }

    public void setSurfaceName(String name) {
        this.name.setValue(name);
    }

    public void setSelectedSketch(int sketchIndex) {
        this.sourceList.setValue(sketchIndex);
    }

    public void show() {
        bezierGroup.show();
    }

	public String getName() {
		return this.name.getStringValue();
	}
}
