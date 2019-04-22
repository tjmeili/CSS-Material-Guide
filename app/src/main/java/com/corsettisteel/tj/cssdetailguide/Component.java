package com.corsettisteel.tj.cssdetailguide;

/**
 * Created by TJ on 2/11/2018.
 */

public class Component {

    private String shape;

    public Component() {
        shape = "";
    }

    public Component(String shape) {
        this.shape = shape;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    @Override
    public String toString() {
        return shape;
    }
}
