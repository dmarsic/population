package com.dmarsic;

import java.util.logging.Logger;

public class Run {

    static void runGenerations(int numberOfGenerations, Population hunter, Population prey) {

        for (int i = 0; i < numberOfGenerations; i++) {

            // System.out.println(String.format("xxx GENERATION %d xxx", i));

            // Find population fitness
            hunter.fitness(prey);

            // Select parents for the next generation
            int[][] parentIndexes = hunter.selection();

            // Create new population by crossover
            Individual[] newPopulation = hunter.crossover(parentIndexes);
            hunter.mutation(newPopulation);
            hunter.population = newPopulation;

            int bestChromosomeIdx = hunter.getBestChromosomeIdx();
            System.out.println(String.format("BEST OFFSPRING IN %3d: [%d] %s (%.2f)",
                    i, bestChromosomeIdx, hunter.getPopulation()[bestChromosomeIdx].toString(),
                    hunter.getFitness()[bestChromosomeIdx]));
        }
    }

    public static void main(String[] args) {

        int numberOfGenerations = 1;
        int hunterPopulationSize = 20;
        int preyPopulationSize = 80;

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

        Population hunter = new Population(hunterPopulationSize);
        hunter.setWeights(hunterWeights);
        System.out.println(hunter.toString());

        Population prey = new Population(preyPopulationSize);
        prey.setWeights(preyWeights);

        runGenerations(numberOfGenerations, hunter, prey);
    }
}
