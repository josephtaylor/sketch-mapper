package jto.processing.sketch;

import jto.processing.event.EdgeEnum;
import jto.processing.event.EdgeEvent;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joconnor on 1/11/2015.
 */
public abstract class ConductableSketch extends AbstractSketch {
    protected Map<EdgeEnum, List<ConductableSketch>> listeners = new HashMap<EdgeEnum, List<ConductableSketch>>();

    public ConductableSketch(final PApplet parent, final int width, final int height) {
        super(parent, width, height);
    }

    public void addListener(final EdgeEnum edge, final ConductableSketch sketch) {
        if (null == listeners.get(edge)) {
            List<ConductableSketch> sketchList = new ArrayList<ConductableSketch>();
            sketchList.add(sketch);
            listeners.put(edge, sketchList);
            System.out.println("Added " + sketch.getName() + " to the " + edge.name() + " of " + getName());
            return;
        }
        listeners.get(edge).add(sketch);
        System.out.println("Added " + sketch.getName() + " to the " + edge.name() + " of " + getName());
    }

    public abstract void edgeEvent(final EdgeEvent edgeEvent);

    public Map<EdgeEnum, List<ConductableSketch>> getListeners() {
        return listeners;
    }

    public void publishEvent(final EdgeEvent event) {
        if (null == listeners.get(event.getSendingEdge())) {
            System.out.println("No listeners for this edge on " + getName() + ": Receiving: " + event.getReceivingEdge().name() + " Sending: " + event.getSendingEdge().name());
            return;
        }
        for (ConductableSketch sketch : listeners.get(event.getSendingEdge())) {
//            System.out.println("Sending event: Receiving: " + event.getReceivingEdge().name() + " of " + sketch.getName()
//                    + " Sending: " + event.getSendingEdge().name() + " of " + event.getSender());
            sketch.edgeEvent(event);
        }
    }

    public void setListeners(Map<EdgeEnum, List<ConductableSketch>> listeners) {
        this.listeners = listeners;
    }
}
