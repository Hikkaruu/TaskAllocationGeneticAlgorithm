# Task Scheduling Program

## Description

A Java program implementing a genetic algorithm to solve the task scheduling problem on processors. The program aims to minimize the overall execution time of tasks on all available processors.

## Features

1. **Input File Generation:** The program allows for the generation of an input file containing information about the number of processors, the number of tasks, and the execution time of each task.

2. **File Reading:** The program reads data from an input file, enabling users to work with previously defined datasets.

3. **Genetic Algorithm:** Utilizes a genetic algorithm for evolutionary finding of an optimal solution for the task scheduling problem.

4. **Results Saving:**
    - **Results:** The program saves results to an output file, presenting on which processor and at what time each task should be executed.
    - **Average Fitness:** The program calculates and records the average fitness of the population during each generation, providing insights into the convergence of the algorithm.
    - **Best Current Solution:** Tracks the best solution within the current population, aiding in understanding the progress of the genetic algorithm.
    - **Best Global Solution:** Maintains the best solution found across all generations, representing the overall optimal solution discovered.

5. **Input file structure:**

     ```plaintext
    3 7
    19 35 74 91 11 69 43
    ```  
    - **The first line** specifies the number of processors (3) and the number of tasks (7).
    - **The second line** contains the execution times of each task. In this case, the execution times are 19, 35, 74, 91, 11, 69, and 43.
    
7. ## How to Run

1. Compile the program using the `javac Main.java` command.
2. Run the program using the `java Main` command.

7. ## Genetic Algorithm Parameters

In the `Main.java` file, you can customize genetic algorithm parameters such as the crossover chance (`crossoverChance`), mutation chance (`mutationChance`), and the number of evaluations (`evaluations`).

8. ## Sample Results

After the program finishes, the final results will be saved in the `result.txt` file.

