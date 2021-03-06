package com.dmarsic.population;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Population {

    List<Individual> population = new ArrayList<>();
    int[] weights;
    double[] fitness;
    int bestChromosomeIdx = -1;
    double mutationThreshold = 0.000;
    Sprite sprite;

    private final static Logger LOGGER = Logger.getLogger(Population.class.getName());

    /**
     * Creates population of individuals (Chromosomes).
     *
     * @param populationSize Number of individuals in the population
     */
    public Population() {
    }

    public String toString() {
        return String.join("\n", population.toString());
    }

    public List<Individual> getPopulation() {
        return population;
    }

    public void addIndividual(Individual chromosome) {
        population.add(chromosome);
    }

    public int getBestChromosomeIdx() {
        return bestChromosomeIdx;
    }

    public double[] getFitness() {
        return fitness;
    }

    public void setWeights(int[] weights) {
        this.weights = weights;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public double[] fitness(Population prey) {

        /*

        Needs to be able to catch adjacent prey.

         */

        fitness = new double[population.size()];
        double bestFitnessValue = 0;

        for (int c = 0; c < population.size(); c++) {
            Individual individual = population.get(c);

            int fit = 0;

            List<Individual> preyWithinReach = individual.findPreyWithinReach(prey);
            for (Individual i : preyWithinReach) {
                LOGGER.log(Level.FINE, String.format("I: {}", i.getLocation().toString()));
            }

            // Simple fitness - how many reachable prey individuals we can outrun
            if (weights[2] <= 1) {
                fit = fitnessCanOutrun(individual, preyWithinReach);
                LOGGER.info(String.format("OutrunFitness: %s", fit));
            }

            fitness[c] = (double) fit;
            if (fitness[c] > bestFitnessValue) {
                bestFitnessValue = fitness[c];
                bestChromosomeIdx = c;
            }
            //System.out.println(String.format("[%2d] %s, %.2f, %3d, %3d",
            //        c, toString(chromosome), fitness[c], canCatchFood, canBeCaught));
        }

        return fitness;
    }

    private int fitnessCanOutrun(Individual individual, List<Individual> preyWithinReach) {
        int countCanOutrun = 0;
        int speed = individual.chromosome[1];
        for (Individual preyIndividual : preyWithinReach) {
            int preySpeed = preyIndividual.getChromosome()[1];
            if (speed > preySpeed) {
                countCanOutrun++;
            }
        }
        return countCanOutrun;
    }

    private int rouletteSelect(double[] fitness) {
        // Taken directly from:
        // https://en.wikipedia.org/wiki/Fitness_proportionate_selection

        // find minimum fitness
        double minimumFitness = fitness[0];
        for (int i = 1; i < fitness.length; i++) {
            if (fitness[i] < minimumFitness) {
                minimumFitness = fitness[i];
            }
        }
        if (minimumFitness < 0) {
            for (int i = 0; i < fitness.length; i++) {
                fitness[i] += -1 * minimumFitness;
            }
        }

        // calculate the total weight
        float fitness_sum = 0;
        for (int i = 0; i < fitness.length; i++) {
            fitness_sum += fitness[i];
        }

        // get a random value
        float value = new Random().nextFloat() * fitness_sum;

        // locate the random value based on the weights
        for (int i = 0; i < fitness.length; i++) {
            value -= fitness[i];
            if (value < 0) {
                return i;
            }
        }

        // when rounding error occurs, return the last item's index
        return fitness.length - 1;
    }

    public int[][] selection() {
        int[][] parentIndexes = new int[population.size()][2];

        for (int i = 0; i < population.size(); i++) {

            // Note: This world allows cloning, i.e. parent a is parent b
            parentIndexes[i][0] = rouletteSelect(fitness);
            parentIndexes[i][1] = rouletteSelect(fitness);
        }

        return parentIndexes;
    }

    public List<Individual> crossover(int[][] parentIndexes) {
        List<Individual> newPopulation = new ArrayList<>();

        for (int i = 0; i < parentIndexes.length; i++) {
            int parentAIdx = parentIndexes[i][0];
            int parentBIdx = parentIndexes[i][1];
            Individual parentA = population.get(parentAIdx);
            Individual parentB = population.get(parentBIdx);
            Location childLocation = determineChildLocation(parentA.getLocation(), parentB.getLocation());
            int noOfGenes = parentA.getNoOfGenes();
            int crossoverPoint = (int)(noOfGenes / 2);
            int[] parentAChromosome = parentA.getChromosome();
            int[] parentBChromosome = parentB.getChromosome();
            int[] childChromosome = new int[noOfGenes];
            for (int g = 0; g < noOfGenes; g++) {
                if (g < crossoverPoint) {
                    childChromosome[g] = parentAChromosome[g];
                } else {
                    childChromosome[g] = parentBChromosome[g];
                }
            }
            newPopulation.add(new Individual(childChromosome, childLocation));
        }

        return newPopulation;
    }

    public Location determineChildLocation(Location locationA, Location locationB) {
        return new Location(
                (int)((locationA.getX() + locationB.getX()) / 2),
                (int)((locationA.getY() + locationB.getY()) / 2));
    }

    public List<Individual> mutation(List<Individual> population) {
        for (Individual individual : population) {
            int[] mutatedChromosome = individual.getChromosome();
            for (int g = 0; g < individual.getNoOfGenes(); g++) {
                double mutate = new Random().nextDouble();
                if (mutate > mutationThreshold) {
                    mutatedChromosome[g] = new Random().nextInt(individual.getMaxGeneValue()) + 1;
                }
            }
            individual.setChromosome(mutatedChromosome);
        }

        return population;
    }
}
