package com.dmarsic;

import java.util.HashMap;

public class Run {

    static void runGenerations(int numberOfGenerations, Population hunter, Population prey) {

        for (int i = 0; i < numberOfGenerations; i++) {

            // System.out.println(String.format("xxx GENERATION %d xxx", i));

            // Find population fitness
            hunter.fitness(prey.population[0], prey.getWeights());

            // Select parents for the next generation
            int[][] parents = hunter.selection();

            // Create new population by crossover
            int[][] newPopulation = hunter.crossover(parents);
            hunter.mutation(newPopulation);
            hunter.population = newPopulation;

            int bestChromosomeIdx = hunter.getBestChromosomeIdx();
            System.out.println(String.format("BEST OFFSPRING IN %3d: [%d] %s (%.2f)",
                    i, bestChromosomeIdx, hunter.repr(hunter.getPopulation()[bestChromosomeIdx]),
                    hunter.getFitness()[bestChromosomeIdx]));
        }
    }

    public static void main(String[] args) {

        int numberOfGenerations = 150;
        int hunterPopulationSize = 200;
        int preyPopulationSize = 800;

                /* Individuals are trying to optimize to catch food. Their traits are:

        [0]: size
        [1]: speed
        [2]: intelligence
        [3]: energy burst

        Size contributes to catching, but speed is prevalent.
        On the other hand, the bigger they are, the more attractive prey they are.
        The higher intelligence they have, they are better at catching and avoiding
        being caught, so this trait should end up high.
        Individuals that can burst energy, have some advantage at surprising the prey
        or making a quick flight from the enemy.
        */

        int[] hunterWeights = {3, 6, 8, 4};
        int[] preyWeights = {4, 3, 9, 1};

        Population hunter = new Population(hunterPopulationSize);
        hunter.setWeights(hunterWeights);

        Population prey = new Population(preyPopulationSize);
        prey.setWeights(preyWeights);

        runGenerations(numberOfGenerations, hunter, prey);
    }
}
