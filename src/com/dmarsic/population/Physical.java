package com.dmarsic.population;

public abstract class Physical {

    private Location location;
    private boolean interactive;

    public Physical(Location location, Boolean interactive) {
        this.location = location;
        this.interactive = interactive;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
