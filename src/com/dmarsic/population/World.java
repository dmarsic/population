package com.dmarsic.population;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public class World {

    private int sizeX;
    private int sizeY;

    int currentGeneration;
    int hunterPopulationSize = 20;
    int preyPopulationSize = 80;
    HashMap<String, Population> populations = new HashMap<>();

    final int sleepBetweenGenerations = 1000;
    final int sleepBetweenMovements = 200;

    GameGraphics g;

    private Physical[][] map;

    private final static Logger LOGGER = Logger.getLogger(Population.class.getName());

    public World(int sizeX, int sizeY) throws InterruptedException, WorldSaturatedException {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        map = new Physical[sizeX][sizeY];
        g = new GameGraphics(sizeX, sizeY);
        g.drawGrid();
        initPopulations();
    }

    public void initPopulations() throws InterruptedException {
        Sprite preySprite = SpriteStore.get().getSprite("graphics/8x8critters.png", 0, 0);
        Sprite hunterSprite = SpriteStore.get().getSprite("graphics/8x8critters.png", 4, 0);

        /* Individuals are trying to optimize to catch food. Their traits are:

        [0]: size
        Size contributes to catching, but speed is prevalent.
        On the other hand, the bigger they are, the more attractive prey they are.

        [1]: speed
        Speed contributes to catching.

        [2]: intelligence
        The higher intelligence they have, they are better at catching and avoiding
        being caught, so this trait should end up high.

        [3]: energy burst
        Individuals that can burst energy, have some advantage at surprising the prey
        or making a quick flight from the enemy.
        */


        int[] hunterWeights = {3, 6, 1, 4};
        int[] preyWeights = {4, 0, 1, 1};

        Population hunter = new Population();
        hunter.setWeights(hunterWeights);
        hunter.setSprite(hunterSprite);
        createIndividuals(hunter, hunterPopulationSize);
        populations.put("hunter", hunter);

        Population prey = new Population();
        prey.setWeights(preyWeights);
        prey.setSprite(preySprite);
        createIndividuals(prey, preyPopulationSize);
        populations.put("prey", prey);
    }

    public void runGenerations(int numberOfGenerations) throws InterruptedException {

        Population hunter = populations.get("hunter");
        Population prey = populations.get("prey");

        for (currentGeneration = 0; currentGeneration < numberOfGenerations; currentGeneration++) {

            System.out.println(String.format("xxx GENERATION %d xxx", currentGeneration));

            // LIFE FUNCTIONS
            eatNearest(hunter, prey);

            // Find population fitness
            hunter.fitness(prey);

            // Print best individual in this generation
            int bestChromosomeIdx = hunter.getBestChromosomeIdx();
            System.out.println(String.format("BEST OFFSPRING IN GENERATION %d: [%d] %s (%.2f)",
                    currentGeneration, bestChromosomeIdx, hunter.getPopulation().get(bestChromosomeIdx).toString(),
                    hunter.getFitness()[bestChromosomeIdx]));

            // Select parents for the next generation
            int[][] parentIndexes = hunter.selection();

            // Create new population by crossover
            List<Individual> newPopulation = hunter.crossover(parentIndexes);
            hunter.mutation(newPopulation);
            hunter.population = newPopulation;

            redraw();

            // Sleep between generations so they would be visible to the user
            Thread.sleep(sleepBetweenGenerations);
        }
    }
    
    private void createIndividuals(Population population, int populationCount) {
        for (int i = 0; i < populationCount; i++) {
            Location location = findAvailableLocation();
            Individual chromosome = new Individual(String.valueOf(i), location);
            population.addIndividual(chromosome);
            setOccupancy(location.getX(), location.getY(), chromosome);
        }
    }

    private Location findAvailableLocation() {
        Location location = new Location(0, 0);
        boolean placeOccupied = true;
        int tryLimit = sizeX * sizeY * 2;  // if we can't find a spot in this much, then it's really crowded.
        int tryCounter = 0;

        while (placeOccupied) {
            if (tryCounter >= tryLimit) {
                throw new WorldSaturatedException("Can't find available space in the World.");
            }

            int x = new Random().nextInt(sizeX);
            int y = new Random().nextInt(sizeY);
            location = new Location(x, y);
            placeOccupied = locationOccupied(x, y);
            tryCounter++;
        }
        return location;
    }

    private void eatNearest(Population hunter, Population prey) throws InterruptedException {
        for (Individual h : hunter.getPopulation()) {
            try {
                final long t0 = System.nanoTime();

                drawIndividualReach(g, h, hunter.getSprite().getHeight(), false);

                Individual firstAvailablePrey = h.findPreyWithinReach(prey).get(0);
                drawIndividualReach(g, firstAvailablePrey, prey.getSprite().getHeight(), true);
                final long t1 = System.nanoTime();

                LOGGER.info(String.format("First available prey: %s", firstAvailablePrey.toString()));
                LOGGER.info(String.format("Hunter's current stat: %s", h.toString()));
                h.move(firstAvailablePrey.getLocation());
                LOGGER.info(String.format("Hunter's new stat: %s", h.toString()));

                // let's say that the prey is eaten but a child is recreated somewhere else.
                firstAvailablePrey.move(findAvailableLocation());
                final long t2 = System.nanoTime();

                // Sleep between movements so we'd be able to follow the action
                Thread.sleep(sleepBetweenMovements);

                LOGGER.info(String.format("eatNearest time: %d | %d",
                        TimeUnit.NANOSECONDS.toMillis(t2 - t0),
                        TimeUnit.NANOSECONDS.toMillis(t1 - t0)));

                // redraw();

            } catch (IndexOutOfBoundsException e) {
                LOGGER.info(String.format("Hunter %s doesn't have prey in vicinity.", h.toString()));
            }
        }
    }

    private void drawPopulation(GameGraphics g, Population population) {

        for (Individual p : population.population) {

            // Draw sprite
            int blockX = p.getLocation().getX() * g.getBlockSize();
            int blockY = p.getLocation().getY() * g.getBlockSize();
            population.getSprite().draw(g.getContext(), blockX, blockY);
        }
    }

    private void drawIndividualReach(GameGraphics g, Individual individual, int blockSize, boolean shouldRedraw) {
        int radius = individual.getChromosome()[1] * blockSize;
        g.getContext().setColor(Color.DARK_GRAY);
        g.getContext().drawOval(
                individual.getLocation().getX() * blockSize - radius + blockSize / 2,
                individual.getLocation().getY() * blockSize - radius + blockSize / 2,
                2 * radius,
                2 * radius);
        if (shouldRedraw) {
            redraw();
        }
    }

    private void redraw() {
        g.drawGrid();

        // Draw current state
        for (Population population : populations.values()) {
            drawPopulation(g, population);
        }
        g.putText(10, 655, String.format("Generation: %d", currentGeneration));

        // Put out current graphics context and reset for the next frame
        g.flip();
        g.setContext();
    }



    public boolean locationOccupied(int blockX, int blockY) {
        return map[blockX][blockY] != null;
    }

    public void setOccupancy(int blockX, int blockY, Physical physical) {
        map[blockX][blockY] = physical;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }
}
