package jto.processing.event;

import processing.core.PVector;

public class EdgeEvent {
    private final EdgeEnum sendingEdge;
    private final EdgeEnum receivingEdge;
    private final double location;
    private final PVector velocity;
    private final String sender;

    public EdgeEvent(final String sender, final EdgeEnum sendingEdge, final EdgeEnum receivingEdge, final double location, final PVector velocity) {
        this.sender = sender;
        this.sendingEdge = sendingEdge;
        this.receivingEdge = receivingEdge;
        this.location = location;
        this.velocity = velocity;
    }

    public double getLocation() {
        return location;
    }

    public EdgeEnum getReceivingEdge() {
        return receivingEdge;
    }

    public String getSender() {
        return sender;
    }

    public EdgeEnum getSendingEdge() {
        return sendingEdge;
    }

    public PVector getVelocity() {
        return velocity;
    }
}
