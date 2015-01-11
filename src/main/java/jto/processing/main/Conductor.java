package jto.processing.main;

import jto.processing.event.EdgeEnum;
import jto.processing.sketch.ConductableSketch;
import jto.processing.sketch.bouncy.ball.*;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class Conductor {
    private final PApplet parent;
    private List<ConductableSketch> sketchList;

    public Conductor(final PApplet parent) {
        this.parent = parent;
    }

    public PApplet getParent() {
        return parent;
    }

    public List<ConductableSketch> getSketchList() {
        return sketchList;
    }

    public void setSketchList(List<ConductableSketch> sketchList) {
        this.sketchList = sketchList;
    }

    public void setup() {
        //create sketches
        ConductableSketch bigBoxFront = new BigBoxFront(parent, parent.width, parent.height);
        ConductableSketch bigBoxLeft = new BigBoxLeft(parent, parent.width / 3, parent.height);
        ConductableSketch bigBoxTop = new BigBoxTop(parent, parent.width, parent.height / 3);
        ConductableSketch smallBoxFront = new SmallBoxFront(parent, parent.width / 3, parent.height / 3);
        ConductableSketch smallBoxLeft = new SmallBoxLeft(parent, parent.width / 3, parent.height / 3);
        ConductableSketch smallBoxTop = new SmallBoxTop(parent, parent.width / 3, parent.height / 3);

        //setup listeners
        bigBoxFront.addListener(EdgeEnum.BOTTOM, smallBoxTop);
        bigBoxFront.addListener(EdgeEnum.RIGHT, smallBoxLeft);
        bigBoxFront.addListener(EdgeEnum.LEFT, bigBoxLeft);
        bigBoxFront.addListener(EdgeEnum.TOP, bigBoxTop);

        bigBoxLeft.addListener(EdgeEnum.RIGHT, bigBoxFront);
        bigBoxLeft.addListener(EdgeEnum.TOP, bigBoxTop);

        bigBoxTop.addListener(EdgeEnum.LEFT, bigBoxLeft);
        bigBoxTop.addListener(EdgeEnum.BOTTOM, bigBoxFront);

        smallBoxTop.addListener(EdgeEnum.BOTTOM, smallBoxFront);
        smallBoxTop.addListener(EdgeEnum.TOP, bigBoxFront);
        smallBoxTop.addListener(EdgeEnum.LEFT, smallBoxLeft);

        smallBoxLeft.addListener(EdgeEnum.LEFT, bigBoxFront);
        smallBoxLeft.addListener(EdgeEnum.RIGHT, smallBoxFront);
        smallBoxLeft.addListener(EdgeEnum.TOP, smallBoxTop);

        smallBoxFront.addListener(EdgeEnum.LEFT, smallBoxLeft);
        smallBoxFront.addListener(EdgeEnum.TOP, smallBoxTop);

        sketchList = new ArrayList<ConductableSketch>();
        sketchList.add(bigBoxFront);
        sketchList.add(bigBoxLeft);
        sketchList.add(bigBoxTop);
        sketchList.add(smallBoxFront);
        sketchList.add(smallBoxLeft);
        sketchList.add(smallBoxTop);
    }
}
