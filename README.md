# Task Scheduling Program

## Description

A Java program implementing a genetic algorithm to solve the task scheduling problem on processors. The program aims to minimize the overall execution time of tasks on all available processors.

## Features

1. **Input File Generation:** The program allows for the generation of an input file containing information about the number of processors, the number of tasks, and the execution time of each task.

2. **File Reading:** The program reads data from an input file, enabling users to work with previously defined datasets.

3. **Genetic Algorithm:** Utilizes a genetic algorithm for evolutionary finding of an optimal solution for the task scheduling problem.

4. **Results Saving:** The program saves results to an output file, presenting on which processor and at what time each task should be executed.

## How to Run

1. Compile the program using the `javac Main.java` command.
2. Run the program using the `java Main` command.

## Genetic Algorithm Parameters

In the `Main.java` file, you can customize genetic algorithm parameters such as the crossover chance (`crossoverChance`), mutation chance (`mutationChance`), and the number of evaluations (`evaluations`).

## Sample Results

After the program finishes, the results will be saved in the `result.txt` file.

