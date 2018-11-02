package com.dmarsic.population;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

public class Location {

    int x;
    int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return String.format("%d,%d", x, y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double distanceFrom(Location other) {
        return sqrt(pow(x - other.getX(), 2.0) + pow(y - other.getY(), 2.0));
    }
}
