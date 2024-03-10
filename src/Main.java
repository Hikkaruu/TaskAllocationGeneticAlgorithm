import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.io.IOException;

public class Main {
    public static String[] executionTimes;
    public static int numberOfProcessors;
    public static int numberOfTasks;
    public static final int populationSize = 10;

    public static void createFile(int numberOfProcessors, int numberOfTasks, int rangeFrom, int rangeTo) {
        String path = "file1.txt";
        BufferedWriter bw = null;
        try {
            FileWriter fw = new FileWriter(path);
            bw = new BufferedWriter(fw);

            bw.write(numberOfProcessors + " " + numberOfTasks);
            bw.newLine();
            Random rand = new Random();
            for (int i = 0; i < numberOfTasks; i++) {
                int number = rand.nextInt(rangeTo - rangeFrom + 1) + rangeFrom;
                bw.write(number + " ");
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Cannot create the file");
        }
    }

    public static void readFromFile(String path) {
        BufferedReader reader = null;
        try {
            FileReader fileReader = new FileReader(path);
            reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            String[] size = line.split(" ");
            numberOfProcessors = Integer.parseInt(size[0]);
            numberOfTasks = Integer.parseInt(size[1]);

            String line2 = reader.readLine();
            executionTimes = line2.split(" ");

            for (int i = 0; i < numberOfTasks; i++) {
                int executionTime = Integer.parseInt(executionTimes[i]);
                //System.out.println("Execution time of task " + (i + 1) + ": " + executionTime);
            }
            //System.out.println(numberOfProcessors + " " + numberOfTasks);
        } catch (IOException e) {
            System.out.println("Cannot save the file");
        }
    }

    public static void printPopulation(List<int[]> population, String how) {

        if (how.equals("complex")) {
            int j = 1;
            for (int[] chromosome : population) {
                System.out.print("Chromosome " + j + ": | ");
                j++;
                for (int i = 0; i < numberOfTasks; i++) {
                    System.out.print("Task " + (i + 1) + " -> Processor " + chromosome[i] + " | ");
                }
                System.out.println();
            }
        }
        if (how.equals("simple")) {
            int j = 1;
            for (int[] chromosome : population) {
                System.out.print("Chromosome " + j + ": | ");
                j++;
                for (int i = 0; i < numberOfTasks; i++) {
                    System.out.print(chromosome[i] + ",");
                }
                System.out.println();
            }
        }
    }

    public static List<int[]> buildPopulation() {
        List<int[]> population = new ArrayList<>();

        int[] chromosome;
        for (int i = 0; i < populationSize; i++) {
            chromosome = new int[numberOfTasks];
            Random random = new Random();

            // Prepare a list of available processors
            List<Integer> availableProcessors = new ArrayList<>();
            for (int k = 0; k < numberOfProcessors; k++) {
                availableProcessors.add(k);
            }

            // Assign at least one processor for each task
            for (int j = 0; j < numberOfProcessors; j++) {
                int processor = availableProcessors.remove(random.nextInt(availableProcessors.size()));
                chromosome[j] = processor;
            }

            // Assign the remaining processors randomly
            for (int j = numberOfProcessors; j < chromosome.length; j++) {
                chromosome[j] = random.nextInt(numberOfProcessors);
            }

            // Shuffle the chromosome array
            List<Integer> chromosomeList = Arrays.stream(chromosome).boxed().collect(Collectors.toList());
            Collections.shuffle(chromosomeList);
            chromosome = chromosomeList.stream().mapToInt(Integer::intValue).toArray();

            population.add(chromosome);
        }
        return population;
    }

    public static int evaluateChromosome(int[] chromosome) {
        int[] processorTimes = new int[numberOfProcessors];

        for (int i = 0; i < numberOfTasks; i++) {
            int processor = chromosome[i];
            int executionTime = Integer.parseInt(executionTimes[i]);
            processorTimes[processor] += executionTime;
        }

        int maxTime = processorTimes[0];
        for (int i = 1; i < numberOfProcessors; i++) {
            if (processorTimes[i] > maxTime) {
                maxTime = processorTimes[i];
            }
        }
        return maxTime;
    }

    public static List<int[]> selection(List<int[]> population) {
        double[] evaluations = new double[population.size()];
        double totalEvaluation = 0;
        double totalEvaluation2 = 0;

        // Calculate evaluations for each chromosome and the total evaluation
        for (int i = 0; i < population.size(); i++) {
            int[] chromosome = population.get(i);
            double evaluation = evaluateChromosome(chromosome);
            evaluations[i] = evaluation;
            totalEvaluation += evaluation;
        }

        // Calculate the selection probability for each chromosome
        double[] probability = new double[population.size()];
        for (int i = 0; i < population.size(); i++) {
            double evaluation = evaluations[i];
            evaluations[i] = totalEvaluation / evaluation;
            totalEvaluation2 += evaluations[i];
        }

        for (int i = 0; i < population.size(); i++) {
            probability[i] = evaluations[i] / totalEvaluation2;
        }

        // Create intervals based on the selection probability
        double[] intervals = new double[population.size() + 1];
        double totalIntervals = 0;
        for (int i = 0; i < population.size(); i++) {
            totalIntervals += probability[i];
            intervals[i + 1] = totalIntervals;
        }

        // Create a new population with the same size
        int selectedIndex = 0;
        List<int[]> newPopulation = new ArrayList<>();
        for (int i = 0; i < population.size(); i++) {
            // Randomly select a chromosome based on the intervals
            double randomValue = Math.random();
            for (int j = 0; j < intervals.length - 1; j++) {
                if (randomValue >= intervals[j] && randomValue < intervals[j + 1]) {
                    selectedIndex = j;
                    break;
                }
            }
            // Add the selected chromosome to the new population
            newPopulation.add(population.get(selectedIndex));
        }

        return newPopulation;
    }

    public static List<int[]> crossover(List<int[]> population, int chance) {
        List<int[]> pop = population;
        Random rng = new Random();
        int doesCrossover;
        int[] parent1, parent2;
        int condition, r2Index, temp = 0;
        int p1, p2;

        for (int i = 0; i < pop.size(); i++) {
            doesCrossover = rng.nextInt(100) + 1;

            if (doesCrossover <= chance) {
                parent1 = pop.get(i);
                r2Index = rng.nextInt(pop.size());

                while (r2Index == i) {
                    r2Index = rng.nextInt(pop.size());
                }

                parent2 = pop.get(r2Index);

                p1 = rng.nextInt(parent1.length);
                p2 = rng.nextInt(parent1.length) + p1;

                for (int j = 0; j < p1; j++) {
                    temp = parent1[j];
                    parent1[j] = parent2[j];
                    parent2[j] = temp;
                }

                for (int j = p2; j < parent1.length; j++) {
                    temp = parent1[j];
                    parent1[j] = parent2[j];
                    parent2[j] = temp;
                }

                List<Integer> child1 = new ArrayList<>();
                for (int element : parent1) {
                    child1.add(element);
                }

                List<Integer> child2 = new ArrayList<>();
                for (int element : parent2) {
                    child2.add(element);
                }

                // Parent 1

                // Check if it contains all processors
                List<Integer> missingProcessors = new ArrayList<>();
                int count = 0;

                for (int l = 0; l < numberOfProcessors; l++) {
                    if (!child1.contains(l)) {
                        missingProcessors.add(l);
                        count++;
                    }
                }

                Map<Integer, List<Integer>> duplicateIndexes = new HashMap<>();

                IntStream.range(0, child1.size())
                        .filter(p -> {
                            int element = child1.get(p);
                            return duplicateIndexes.computeIfAbsent(element, k -> new ArrayList<>()).add(p);
                        })
                        .toArray();

                int[] duplicatePlaces = duplicateIndexes.values().stream()
                        .filter(l -> l.size() > 1)
                        .flatMap(List::stream)
                        .mapToInt(Integer::intValue)
                        .toArray();

                List<Integer> duplicateIndexesList = new ArrayList<>();
                for (int k = 0; k < duplicatePlaces.length; k++) {
                    duplicateIndexesList.add(duplicatePlaces[k]);
                }

                int index;
                for (int m = 0; m < missingProcessors.size(); m++) {
                    index = rng.nextInt(duplicateIndexesList.size());
                    child1.set(duplicateIndexesList.get(index), missingProcessors.get(m));
                    duplicateIndexesList.clear();
                    duplicateIndexes.clear();

                    IntStream.range(0, child1.size())
                            .filter(p -> {
                                int element = child1.get(p);
                                return duplicateIndexes.computeIfAbsent(element, k -> new ArrayList<>()).add(p);
                            })
                            .toArray();

                    duplicatePlaces = duplicateIndexes.values().stream()
                            .filter(l -> l.size() > 1)
                            .flatMap(List::stream)
                            .mapToInt(Integer::intValue)
                            .toArray();

                    for (int k = 0; k < duplicatePlaces.length; k++) {
                        duplicateIndexesList.add(duplicatePlaces[k]);
                    }
                }

                for (int k = 0; k < parent1.length; k++) {
                    parent1[k] = child1.get(k);
                }

                // Parent 2

                // Check if it contains all processors
                List<Integer> missingProcessors2 = new ArrayList<>();
                int count2 = 0;

                for (int l = 0; l < numberOfProcessors; l++) {
                    if (!child2.contains(l)) {
                        missingProcessors2.add(l);
                        count2++;
                    }
                }

                Map<Integer, List<Integer>> duplicateIndexes2 = new HashMap<>();

                IntStream.range(0, child2.size())
                        .filter(p -> {
                            int element = child2.get(p);
                            return duplicateIndexes2.computeIfAbsent(element, k -> new ArrayList<>()).add(p);
                        })
                        .toArray();

                int[] duplicatePlaces2 = duplicateIndexes2.values().stream()
                        .filter(l -> l.size() > 1)
                        .flatMap(List::stream)
                        .mapToInt(Integer::intValue)
                        .toArray();

                List<Integer> duplicateIndexesList2 = new ArrayList<>();
                for (int k = 0; k < duplicatePlaces2.length; k++) {
                    duplicateIndexesList2.add(duplicatePlaces2[k]);
                }

                int index2;
                for (int m = 0; m < missingProcessors2.size(); m++) {
                    index2 = rng.nextInt(duplicateIndexesList2.size());
                    child2.set(duplicateIndexesList2.get(index2), missingProcessors2.get(m));
                    duplicateIndexesList2.clear();
                    duplicateIndexes2.clear();

                    IntStream.range(0, child2.size())
                            .filter(p -> {
                                int element = child2.get(p);
                                return duplicateIndexes2.computeIfAbsent(element, k -> new ArrayList<>()).add(p);
                            })
                            .toArray();

                    duplicatePlaces2 = duplicateIndexes2.values().stream()
                            .filter(l -> l.size() > 1)
                            .flatMap(List::stream)
                            .mapToInt(Integer::intValue)
                            .toArray();

                    for (int k = 0; k < duplicatePlaces2.length; k++) {
                        duplicateIndexesList2.add(duplicatePlaces2[k]);
                    }
                }

                for (int k = 0; k < parent2.length; k++) {
                    parent2[k] = child2.get(k);
                }

                pop.set(i, parent1);
                pop.set(r2Index, parent2);
            }
        }
        return pop;
    }

    public static List<int[]> mutation(List<int[]> population, int chance) {
        List<int[]> pop = new ArrayList<>(population);
        Random rng = new Random();
        int doesMutate;
        int[] temp;
        int temp2;

        for (int i = 0; i < pop.size(); i++) {
            temp = Arrays.copyOf(pop.get(i), pop.get(i).length);

            for (int j = 0; j < temp.length; j++) {
                doesMutate = rng.nextInt(100) + 1;
                temp2 = temp[j];

                if (doesMutate <= chance) {
                    do {
                        temp[j] = rng.nextInt(numberOfProcessors);
                    } while (temp[j] == temp2);
                }
            }

            pop.set(i, temp);
        }

        for (int i = 0; i < pop.size(); i++) {
            List<Integer> child1 = new ArrayList<>();

            for (int element : pop.get(i)) {
                child1.add(element);
            }

            // Check if it contains all processors
            List<Integer> missingProcessors = new ArrayList<>();
            int count = 0;

            for (int l = 0; l < numberOfProcessors; l++) {
                if (!child1.contains(l)) {
                    missingProcessors.add(l);
                    count++;
                }
            }

            Map<Integer, List<Integer>> duplicateIndexes = new HashMap<>();

            IntStream.range(0, child1.size())
                    .filter(p -> {
                        int element = child1.get(p);
                        return duplicateIndexes.computeIfAbsent(element, k -> new ArrayList<>()).add(p);
                    })
                    .toArray();

            int[] duplicatePlaces = duplicateIndexes.values().stream()
                    .filter(l -> l.size() > 1)
                    .flatMap(List::stream)
                    .mapToInt(Integer::intValue)
                    .toArray();

            List<Integer> duplicateIndexesList = new ArrayList<>();
            for (int k = 0; k < duplicatePlaces.length; k++) {
                duplicateIndexesList.add(duplicatePlaces[k]);
            }

            int index;
            for (int m = 0; m < missingProcessors.size(); m++) {
                index = rng.nextInt(duplicateIndexesList.size());
                child1.set(duplicateIndexesList.get(index), missingProcessors.get(m));
                duplicateIndexesList.clear();
                duplicateIndexes.clear();

                IntStream.range(0, child1.size())
                        .filter(p -> {
                            int element = child1.get(p);
                            return duplicateIndexes.computeIfAbsent(element, k -> new ArrayList<>()).add(p);
                        })
                        .toArray();

                duplicatePlaces = duplicateIndexes.values().stream()
                        .filter(l -> l.size() > 1)
                        .flatMap(List::stream)
                        .mapToInt(Integer::intValue)
                        .toArray();

                for (int k = 0; k < duplicatePlaces.length; k++) {
                    duplicateIndexesList.add(duplicatePlaces[k]);
                }
            }

            for (int k = 0; k < pop.get(i).length; k++) {
                pop.get(i)[k] = child1.get(k);
            }
        }

        return pop;
    }

    public static List<int[]> evolutionaryAlgorithm(List<int[]> population, int crossoverChance, int mutationChance, int evaluations) {
        List<int[]> pop = population;
        List<int[]> list = new ArrayList<>();
        list.add(pop.get(0));
        List<Integer> fitness = new ArrayList<>();
        int bestFitness, currentBest = evaluateChromosome(pop.get(0));
        int[] bestGlobal = new int[numberOfTasks];
        int[] bestCurrent;

        String path1 = "average.txt";
        String path2 = "bestCurrent.txt";
        String path3 = "bestGlobal.txt";
        BufferedWriter bw_avg = null;
        BufferedWriter bw_bc = null;
        BufferedWriter bw_bg = null;

        try {
            FileWriter fw_avg = new FileWriter(path1);
            FileWriter fw_bc = new FileWriter(path2);
            FileWriter fw_bg = new FileWriter(path3);
            bw_avg = new BufferedWriter(fw_avg);
            bw_bc = new BufferedWriter(fw_bc);
            bw_bg = new BufferedWriter(fw_bg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bestFitness = evaluateChromosome(pop.get(0));
        int bestCurrentFitness, averageFitness, sum = 0;
        List<Integer> epochFitness = new ArrayList<>();
        bestCurrent = list.get(0);
        bestGlobal = list.get(0);

        while (evaluations > 0) {
            pop = crossover(pop, crossoverChance);
            pop = mutation(pop, mutationChance);
            pop = selection(pop);
            evaluations--;

            for (int i = 1; i < pop.size(); i++) {
                bestFitness = evaluateChromosome(pop.get(i));

                if (bestFitness < currentBest) {
                    currentBest = bestFitness;
                    list.set(0, pop.get(i));
                    bestCurrent = list.get(0);
                }

                epochFitness.add(bestFitness);
            }

            bestCurrentFitness = Collections.min(epochFitness);

            for (int i = 0; i < epochFitness.size(); i++) {
                sum += epochFitness.get(i);
            }

            averageFitness = sum / epochFitness.size();
            epochFitness.clear();
            sum = 0;

            if (evaluateChromosome(bestCurrent) < evaluateChromosome(bestGlobal)) {
                bestGlobal = Arrays.copyOf(bestCurrent, bestCurrent.length);
            }

            try {
                bw_avg.write(String.valueOf(averageFitness));
                bw_avg.newLine();
                bw_bc.write(String.valueOf(bestCurrentFitness));
                bw_bc.newLine();
                bw_bg.write(String.valueOf(evaluateChromosome(bestGlobal)));
                bw_bg.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            bw_avg.close();
            bw_bc.close();
            bw_bg.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        list.set(0, bestGlobal);
        return list;
    }

    public static void saveResult(List<int[]> result) {
        List<List<Integer>> results = new ArrayList<>();

        for (int i = 0; i < result.get(0).length; i++) {
            int processor = result.get(0)[i];
            int time = Integer.parseInt(executionTimes[i]);

            while (results.size() <= processor) {
                results.add(new ArrayList<>());
            }

            List<Integer> durations = results.get(processor);
            durations.add(time);
        }

        BufferedWriter bw = null;
        String path = "result.txt";

        try {
            FileWriter fw = new FileWriter(path);
            bw = new BufferedWriter(fw);

            for (int i = 0; i < results.size(); i++) {
                List<Integer> durations = results.get(i);
                bw.write("Processor" + (i + 1) + ": ");

                for (int duration : durations) {
                    bw.write(duration + " ");
                }

                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        readFromFile("file2.txt");

        //First population
        List<int[]> population = buildPopulation();


        List<int[]> result;
        result = evolutionaryAlgorithm(population, 40, 3, 200);
        System.out.println("evaluation:" + evaluateChromosome(result.get(0)));
        System.out.println();

        for (int i = 0; i < result.get(0).length; i++) {
            System.out.print(result.get(0)[i] + ", ");
        }

        System.out.println();

        for (int i = 0; i <executionTimes.length; i++) {
            System.out.print(executionTimes[i] + ", ");
        }

        System.out.println();

        saveResult(result);
    }
}


