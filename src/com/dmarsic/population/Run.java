package com.dmarsic.population;

/*
Art credits:
https://opengameart.org/content/terrain-tileset
https://opengameart.org/content/8x8-critter-pack
 */

import java.util.List;

public class Run {

    public static void main(String[] args) throws InterruptedException, WorldSaturatedException {

        int worldSizeX = 80;
        int worldSizeY = 80;
        int numberOfGenerations = 20;

        World world = new World(worldSizeX, worldSizeY);
        world.runGenerations(numberOfGenerations);
    }
}
