package com.dmarsic;

import java.util.Random;

public class Population {

    int[][] population;
    int[] weights;
    float[] fitness;
    int bestChromosomeIdx = -1;
    int noOfTraits = 4;
    int maxTraitValue = 20;
    double mutationThreshold = 0.000;

    public Population(int populationSize) {
        population = new int[populationSize][noOfTraits];

        for (int c = 0; c < populationSize; c++) {
            int[] chromosome = new int[noOfTraits];

            for (int g = 0; g < noOfTraits; g++) {
                int gene = new Random().nextInt(maxTraitValue) + 1;
                    chromosome[g] = gene;
            }

            population[c] = chromosome;
        }

        //printPopulation(population);
    }

    public String repr(int[] chromosome) {
        String representation = "";
        for (int g = 0; g < noOfTraits; g++) {
            representation += String.format("%02d ", chromosome[g]);
        }
        return String.format("|[%s]|", representation);
    }

    public int getBestChromosomeIdx() {
        return bestChromosomeIdx;
    }

    public int[][] getPopulation() {
        return population;
    }

    public float[] getFitness() {
        return fitness;
    }

    public void setWeights(int[] weights) {
        this.weights = weights;
    }

    public int[] getWeights() {
        return weights;
    }

    private void printPopulation(int[][] population) {
        for (int c = 0; c < population.length; c++) {
            System.out.println(String.format("[%02d] %s", c, repr(population[c])));
        }
    }

    public float[] fitness(int[] preyChromosome, int[] preyWeights) {


        fitness = new float[population.length];
        float bestFitnessValue = 0;

        for (int c = 0; c < population.length; c++) {
            int[] chromosome = population[c];

            int fit = 0;
            for (int i = 0; i < chromosome.length; i++) {
                fit += weights[i] * chromosome[i] - preyWeights[i] * preyChromosome[i];
            }

            fitness[c] = (float) fit;
            if (fitness[c] > bestFitnessValue) {
                bestFitnessValue = fitness[c];
                bestChromosomeIdx = c;
            }
            //System.out.println(String.format("[%2d] %s, %.2f, %3d, %3d",
            //        c, repr(chromosome), fitness[c], canCatchFood, canBeCaught));
        }

        return fitness;
    }

    int rouletteSelect(float[] fitness) {
        // Taken directly from:
        // https://en.wikipedia.org/wiki/Fitness_proportionate_selection

        // find minimum fitness
        float minimumFitness = fitness[0];
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
        int[][] parents = new int[population.length][2];

        for (int i = 0; i < population.length; i++) {

            // Note: This world allows cloning, i.e. parent a is parent b
            parents[i][0] = rouletteSelect(fitness);
            parents[i][1] = rouletteSelect(fitness);
        }

        return parents;
    }

    public int[][] crossover(int[][] parents) {
        int [][] newPopulation = new int[population.length][noOfTraits];
        int crossoverPoint = noOfTraits / 2;

        for (int i = 0; i < parents.length; i++) {
            int[] newChromosome = new int[noOfTraits];
            for (int g = 0; g < noOfTraits; g++) {
                int parentAIdx = parents[i][0];
                int parentBIdx = parents[i][1];
                if (g < crossoverPoint) {
                    newChromosome[g] = population[parentAIdx][g];
                } else {
                    newChromosome[g] = population[parentBIdx][g];
                }
            }
            newPopulation[i] = newChromosome;
        }

        return newPopulation;
    }

    public int[][] mutation(int[][] population) {
        for (int i = 0; i < population.length; i++) {
            for (int g = 0; g < noOfTraits; g++) {
                double mutate = new Random().nextDouble();
                if (mutate > mutationThreshold) {
                    population[i][g] = new Random().nextInt(maxTraitValue) + 1;
                }
            }
        }

        return population;
    }



}
