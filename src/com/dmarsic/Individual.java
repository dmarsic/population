package com.dmarsic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Individual {

    int noOfGenes = 4;
    int maxGeneValue = 20;

    double reachFactor = 0.5;

    int[] chromosome = new int[noOfGenes];
    Location location;

    public Individual(Location location) {
        this.location = location;
        for (int i = 0; i < noOfGenes; i++) {
            chromosome[i] = new Random().nextInt(maxGeneValue) + 1;
        }
    }

    public Individual(int[] chromosome, Location location) {
        this.chromosome = chromosome;
        this.location = location;
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

    public Location getLocation() {
        return location;
    }

    public String toString() {
        return String.join("|", chromosome.toString());
    }

    public List<Individual> findPreyWithinReach(Population prey) {

        int speed = chromosome[1];
        double maxDistance = speed * reachFactor;
        List<Individual> reachable = new ArrayList<Individual>();

        for (Individual p : prey.population) {
            double distance = location.distanceFrom(p.location);
            if (distance < maxDistance) {
                reachable.add(p);
            }
        }

        return reachable;
    }
}
