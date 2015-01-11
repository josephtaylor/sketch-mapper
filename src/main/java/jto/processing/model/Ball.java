package jto.processing.model;

import processing.core.PVector;

public class Ball {
    private PVector location;
    private PVector velocity;

    public Ball() {
        //default constructor
    }

    public Ball(final PVector location, final PVector velocity) {
        this.location = location;
        this.velocity = velocity;
    }

    public PVector getLocation() {
        return location;
    }

    public PVector getVelocity() {
        return velocity;
    }

    public void setLocation(PVector location) {
        this.location = location;
    }

    public void setVelocity(PVector velocity) {
        this.velocity = velocity;
    }
}
