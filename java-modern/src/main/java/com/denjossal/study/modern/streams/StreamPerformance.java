package com.denjossal.study.modern.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Compares for-loop vs parallel stream performance with CPU-intensive filtering.
 */
public class StreamPerformance {

    private static final Logger logger = Logger.getLogger(StreamPerformance.class.getName());
    private static final Random random = new Random();

    public static void main(String[] args) {
        List<User> userList = generateUsers(100);

        List<String> forLoopResult = measurePerformance("For-loop", () -> {
            List<String> result = new ArrayList<>();
            for (User user : userList) {
                if (simulateIntensiveOperation(user.age()) > 50) {
                    result.add(user.fullName());
                }
            }
            return result;
        });

        List<String> parallelStreamResult = measurePerformance("Parallel Stream", () ->
                userList.parallelStream()
                        .filter(user -> simulateIntensiveOperation(user.age()) > 50)
                        .map(User::fullName)
                        .toList()
        );

        if (logger.isLoggable(Level.INFO)) {
            logger.info(String.format("Size of the for-loop list %d", forLoopResult.size()));
            logger.info(String.format("Size of the parallel-stream list %d", parallelStreamResult.size()));
        }
    }

    private static int simulateIntensiveOperation(int age) {
        int result = 0;
        for (int i = 0; i < 1_000_000; i++) {
            result += (int) Math.sqrt((double) age * i);
        }
        return result;
    }

    private static List<String> measurePerformance(String methodName, RunnableTask task) {
        long startTime = System.nanoTime();
        List<String> result = task.run();
        long endTime = System.nanoTime();

        double elapsedMilliseconds = (endTime - startTime) / 1_000_000.0;
        if (logger.isLoggable(Level.INFO)) {
            logger.info(String.format("%s execution time: %.2f ms", methodName, elapsedMilliseconds));
        }
        return result;
    }

    private static List<User> generateUsers(int numUsers) {
        List<User> userList = new ArrayList<>(numUsers);

        List<String> nameList = List.of(
                "Dianne Hall", "Harvey Douglas", "Elena Waters",
                "Gilbert Morris", "Callum Warburton", "Sheila Ware",
                "Ethel Chaplin", "Godfrey Prescott", "Faisal Morris",
                "George Percival"
        );

        for (int i = 0; i < numUsers; i++) {
            String fullName = nameList.get(random.nextInt(nameList.size()));
            int age = random.nextInt(100);
            userList.add(new User(UUID.randomUUID().toString(), fullName, age));
        }

        return userList;
    }

    @FunctionalInterface
    private interface RunnableTask {
        List<String> run();
    }
}
