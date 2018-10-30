package com.dmarsic.population;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Individual extends Physical {

    String name;

    int noOfGenes = 4;
    int maxGeneValue = 20;

    double reachFactor = 0.5;

    int[] chromosome = new int[noOfGenes];
    List<Individual> reachablePrey = new ArrayList<>();

    private final static Logger LOGGER = Logger.getLogger(Individual.class.getName());

    public Individual(String name, Location location) {
        super(location, true);
        this.name = name;
        for (int i = 0; i < noOfGenes; i++) {
            chromosome[i] = new Random().nextInt(maxGeneValue) + 1;
        }
    }

    public Individual(int[] chromosome, Location location) {
        super(location, true);
        this.chromosome = chromosome;
    }

    public int getNoOfGenes() {
        return noOfGenes;
    }

    public int getMaxGeneValue() {
        return maxGeneValue;
    }

    public int[] getChromosome() {
        return chromosome;
    }

    public void setChromosome(int[] chromosome) {
        this.chromosome = chromosome;
    }

    public List<Individual> getReachablePrey() {
        return reachablePrey;
    }

    public String toString() {
        return Arrays.stream(chromosome).mapToObj(String::valueOf).collect(Collectors.joining("|"));
    }

    public List<Individual> findPreyWithinReach(Population prey) {

        int speed = chromosome[1];
        double maxDistance = speed * reachFactor;
        List<Individual> reachable = new ArrayList<>();

        for (Individual p : prey.population) {
            double distance = getLocation().distanceFrom(p.getLocation());
            if (distance < maxDistance) {
                reachable.add(p);
            }
        }

        reachablePrey = reachable;
        return reachable;
    }

    public void move(Location location) {
        super.setLocation(location);
    }
}
