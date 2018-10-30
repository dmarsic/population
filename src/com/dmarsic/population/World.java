package com.dmarsic.population;

import java.util.List;
import java.util.Random;


public class World {

    private int sizeX;
    private int sizeY;

    int numberOfGenerations = 20;
    int hunterPopulationSize = 20;
    int preyPopulationSize = 80;

    final int sleepBetweenGenerations = 1000;
    final int sleepBetweenMovements = 500;

    GameGraphics g;


    private Physical[][] map;

    public World(int sizeX, int sizeY) throws InterruptedException, WorldSaturatedException {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        map = new Physical[sizeX][sizeY];
        g = new GameGraphics(sizeX, sizeY);
        initPopulations();
    }

    public void initPopulations() throws InterruptedException, WorldSaturatedException {
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

        Population prey = new Population();
        prey.setWeights(preyWeights);
        prey.setSprite(preySprite);
        createIndividuals(prey, preyPopulationSize);

        runGenerations(g, numberOfGenerations, hunter, prey);
    }

    private void createIndividuals(Population population, int populationCount) throws WorldSaturatedException {
        for (int i = 0; i < populationCount; i++) {
            Location location = findAvailableLocation();
            Individual chromosome = new Individual(String.valueOf(i), location);
            population.addIndividual(chromosome);
            setOccupancy(location.getX(), location.getY(), chromosome);
        }
    }

    private Location findAvailableLocation() throws WorldSaturatedException {
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

    private void runGenerations(GameGraphics g, int numberOfGenerations, Population hunter, Population prey)
            throws InterruptedException, WorldSaturatedException {

        for (int i = 0; i < numberOfGenerations; i++) {

            System.out.println(String.format("xxx GENERATION %d xxx", i));

            // ALL LIFE FUNCTIONS HAPPEN BEFORE NEw GENERATION CALCULATION KICKS IN.
            // Eat prey
            for (Individual individual : hunter.getPopulation()) {
                try {
                    Individual firstAvailablePrey = individual.getReachablePrey().get(0);
                    individual.move(firstAvailablePrey.getLocation());
                    System.out.println(String.format(
                            "Moving %s to %d, %d",
                            individual.toString(),
                            individual.getLocation().getX(),
                            individual.getLocation().getY()));

                    // let's say that the prey is eaten but a child is recreated somewhere else.
                    firstAvailablePrey.move(findAvailableLocation());

                    // Sleep between movements so we'd be able to follow the action
                    Thread.sleep(sleepBetweenMovements);
                } catch(IndexOutOfBoundsException e) {
                    // This individual doesn't have any prey around.
                    System.out.println("Nothing around.");
                }
            }

            // Find population fitness
            hunter.fitness(prey);

            // Print best individual in this generation
            int bestChromosomeIdx = hunter.getBestChromosomeIdx();
            System.out.println(String.format("BEST OFFSPRING IN GENERATION %d: [%d] %s (%.2f)",
                    i, bestChromosomeIdx, hunter.getPopulation().get(bestChromosomeIdx).toString(),
                    hunter.getFitness()[bestChromosomeIdx]));

            // Select parents for the next generation
            int[][] parentIndexes = hunter.selection();

            // Create new population by crossover
            List<Individual> newPopulation = hunter.crossover(parentIndexes);
            hunter.mutation(newPopulation);
            hunter.population = newPopulation;

            // Draw current state
            drawPopulation(g, prey);
            drawPopulation(g, hunter);
            g.putText(10, 655, String.format("Generation: %d", i));

            // Put out current graphics context and reset for the next frame
            g.flip();
            g.setContext();

            // Sleep between generations so they would be visible to the user
            Thread.sleep(sleepBetweenGenerations);
        }
    }

    private void drawPopulation(GameGraphics g, Population population) {



        for (Individual p : population.population) {
            int blockX = p.getLocation().getX() * g.getBlockSize();
            int blockY = p.getLocation().getY() * g.getBlockSize();
            System.out.println(String.format("%s, %s: %s", blockX, blockY, p));
            population.getSprite().draw(g.getContext(), blockX, blockY);
        }

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
