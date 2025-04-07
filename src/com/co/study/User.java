package com.co.study.examples;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.Random;

public class ListExample {

    private static final Logger logger = Logger.getLogger(ListExample.class.getName());
    private static final Random random = new Random();

    public static void main(String[] args) {
        // Generate the list of users
        List<User> userList = generateUsers(100);

        // Measure performance for For-loop
        List<String> forLoopResult = measurePerformance("For-loop", () -> {
            List<String> result = new ArrayList<>();
            for (User user : userList) {
                if (simulateIntensiveOperation(user.getAge()) > 50) { // Using complex computation
                    result.add(user.getFullName());
                }
            }
            return result;
        });

        // Measure performance for Parallel Stream
        List<String> parallelStreamResult = measurePerformance("Parallel Stream", () ->
                userList.parallelStream()
                        .filter(user -> simulateIntensiveOperation(user.getAge()) > 50) // Using complex computation
                        .map(User::getFullName)
                        .toList()
        );

        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("Size of the for-loop list %d", forLoopResult.size()));
        }

        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("Size of the parallel-stream list %d", parallelStreamResult.size()));
        }
    }

    /**
     * Simulate a CPU-intensive operation for filtering.
     */
    private static int simulateIntensiveOperation(int age) {
        int result = 0;
        for (int i = 0; i < 1_000_000; i++) {
            result += (int) Math.sqrt((double) age * i); // Perform some heavy computation
        }
        return result;
    }

    /**
     * Method to measure execution time for a specific task.
     */
    private static List<String> measurePerformance(String methodName, RunnableTask task) {
        long startTime = System.nanoTime();
        List<String> result = task.run(); // Execute the task being measured
        long endTime = System.nanoTime();

        double elapsedMilliseconds = (endTime - startTime) / 1_000_000.0;
        if (logger.isLoggable(java.util.logging.Level.INFO)) {
            logger.info(String.format("%s execution time: %.2f ms", methodName, elapsedMilliseconds));
        }
        return result;
    }

    /**
     * Generate a list of random users.
     */
    private static List<User> generateUsers(int numUsers) {
        List<User> userList = new ArrayList<>(numUsers); // Predefine capacity for better performance

        List<String> nameList = List.of(
                "Dianne Hall",
                "Harvey Douglas",
                "Elena Waters",
                "Gilbert Morris",
                "Callum Warburton",
                "Sheila Ware",
                "Ethel Chaplin",
                "Godfrey Prescott",
                "Faisal Morris",
                "George Percival"
        );

        for (int i = 0; i < numUsers; i++) {
            String fullName = nameList.get(random.nextInt(nameList.size()));
            int age = random.nextInt(100); // Age between 0 and 99
            userList.add(new User(UUID.randomUUID().toString(), fullName, age));
        }

        return userList;
    }

    /**
     * Functional interface for executing tasks.
     */
    @FunctionalInterface
    private interface RunnableTask {
        List<String> run();
    }
}
