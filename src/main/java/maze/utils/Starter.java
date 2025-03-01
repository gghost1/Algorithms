package maze.utils;

import maze.solver.Pair;

import java.io.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class Starter {

    static String end = "";

//    public static void main(String[] args) throws IOException {
//        end = "7 3";
//        List<List<String>> maze = new ArrayList<>();
//        BufferedReader mapReader = new BufferedReader(new FileReader("Q:\\algorithms\\src\\main\\java\\maze\\utils\\maze.txt"));
//        String line;
//        while ((line = mapReader.readLine()) != null) {
//            List<String> row = new ArrayList<>(List.of(line.split("")));
//            maze.add(row);
//        }
//        List<Integer> answers = wrapper(maze, "Q:/algorithms/target/classes", "maze.solver.Main", new ArrayList<>(), 1);
//        System.out.println(answers.getFirst());
//    }

    public static void main(String[] args) throws IOException {
        List<Double> aStarTime = new ArrayList<>();
        List<Double> dfsTime = new ArrayList<>();
        for (int m = 0; m < 1000; m++) {
            if (m % 10 == 0) {
                System.out.println(m);
            }
            generateMap();
            List<List<String>> maze = new ArrayList<>();
            List<Double> expectedTime = new ArrayList<>();
            BufferedReader mapReader = new BufferedReader(new FileReader("Q:\\algorithms\\src\\main\\java\\maze\\utils\\maze.txt"));
            String line;
            while ((line = mapReader.readLine()) != null) {
                List<String> row = new ArrayList<>(List.of(line.split("")));
                maze.add(row);
            }

            List<Integer> expectedAnswer = wrapper(maze, "Q:/algorithms/target/classes", "maze.solver.Main", expectedTime, 2);

            dfsTime.add(expectedTime.get(0));
            aStarTime.add(expectedTime.get(1));

//            if (!Objects.equals(expectedAnswer.get(0), expectedAnswer.get(1))) {
//                System.out.println("!!");
//                for (List<String> row : maze) {
//                    System.out.println(String.join(" ", row));
//                }
//                System.out.println(expectedAnswer.get(0) + " " + expectedAnswer.get(1));
//            }

            if (expectedAnswer.getLast() == -1) {
                System.out.println(-1);
                for (List<String> row : maze) {
                    System.out.println(String.join(" ", row));
                }
                System.out.println(expectedAnswer + " " + expectedTime.get(0) + " " + expectedTime.get(1));
            }
        }

        double avarageAStar = mean(aStarTime);
        double avarageDfs = mean(dfsTime);
        double medianAStar = median(aStarTime);
        double medianDfs = median(dfsTime);
        double modeAStar = mode(aStarTime);
        double modeDfs = mode(dfsTime);

        double standardDeviationAStar = standardDeviation(aStarTime);
        double standardDeviationDfs = standardDeviation(dfsTime);

        System.out.println(avarageAStar + " -- " + avarageDfs);
        System.out.println(medianAStar + " -- " + medianDfs);
        System.out.println(modeAStar + " -- " + modeDfs);
        System.out.println(standardDeviationAStar + " -- " + standardDeviationDfs);

    }

    public static double standardDeviation(List<Double> times) {
        double mean = mean(times);
        double variance = times.stream()
                .mapToDouble(time -> Math.pow(time - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }

    public static double mean(List<Double> times) {
        return times.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public static long mode(List<Double> times) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (Integer time : times.stream().map(Double::intValue).toList()) {
            frequencyMap.put(time, frequencyMap.getOrDefault(time, 0) + 1);
        }
        return frequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElseThrow();
    }

    public static double median(List<Double> times) {
        List<Double> sorted = times.stream().sorted().toList();
        int middle = sorted.size() / 2;
        if (sorted.size() % 2 == 0) {
            return (sorted.get(middle - 1) + sorted.get(middle)) / 2.0;
        } else {
            return sorted.get(middle);
        }
    }

    public static List<Integer> wrapper(List<List<String>> maze, String algPath, String main, List<Double> time, int algCount) throws IOException {

        List<Integer> answer = new ArrayList<>();

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", algPath, main);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        PrintWriter writer = new PrintWriter(process.getOutputStream(), true); // Автоматическая очистка буфера
        for (int i = 0; i < algCount; i++) {

            writer.println("1");
            writer.println(end);
            writer.flush();
            while (true) {
                String[] line1 = reader.readLine().split(" ");
                if (line1[0].equals("e")) {
                    answer.add(Integer.parseInt(line1[1]));
                    break;
                }  else if (!line1[0].equals("m")) {
                    System.out.println(String.join(" ", line1));
                    System.out.println("Error");
                    break;
                }

                List<String> neighbors = new ArrayList<>();
                int x = Integer.parseInt(line1[1]);
                int y = Integer.parseInt(line1[2]);
//            System.out.println("m " + x + " " + y);

                for (Pair neighborDirection : Pair.directionsVisibility()) {
                    int neighborX = x + neighborDirection.x;
                    int neighborY = y + neighborDirection.y;

                    if (!(neighborX < 0 || neighborX >= maze.getFirst().size() || neighborY < 0 || neighborY >= maze.size())) {
                        String type = maze.get(neighborY).get(neighborX);
                        if (!type.equals(".") && !type.equals("N")) {
                            neighbors.add(neighborX + " " + neighborY + " " + type);
                        }

                    }
                }
                writer.println(neighbors.size());
//            System.out.println(neighbors.size());
                if (!neighbors.isEmpty()) {
                    writer.println(String.join("\n", neighbors));
//                System.out.println(String.join("\n", neighbors));
                }
            }
            time.add(Double.parseDouble(reader.readLine()));
        }
        process.destroy();
        return answer;
    }
    
    public static void generateMap() {
        List<List<String>> maze = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < 9; j++) {
                row.add(".");
            }
            maze.add(row);
        }
        maze.getFirst().set(0, "N");
        SecureRandom secureRandom = new SecureRandom();
        int x = secureRandom.nextInt(9);
        int y = secureRandom.nextInt(9);
        end = x + " " + y;
        maze.get(y).set(x, "K");
        for (int i = 0; i < secureRandom.nextInt(4); i++) {
            do {
                x = secureRandom.nextInt(9);
                y = secureRandom.nextInt(9);
            } while (!maze.get(y).get(x).equals(".") && !maze.get(y).get(x).equals("P"));
            maze.get(y).set(x, "A");
            for (Pair direction : agentDirections()) {
                int newX = x + direction.x;
                int newY = y + direction.y;
                if (newX >= 0 && newX < 9 && newY >= 0 && newY < 9) {
                    String type = maze.get(newY).get(newX);
                    if (type.equals(".")) {
                        maze.get(newY).set(newX, "P");
                    }
                }
            }
        }
        do {
            x = secureRandom.nextInt(9);
            y = secureRandom.nextInt(9);
        } while (!maze.get(y).get(x).equals(".") && !maze.get(y).get(x).equals("P"));
        maze.get(y).set(x, "S");
        for (Pair direction : sentinalDirections()) {
            int newX = x + direction.x;
            int newY = y + direction.y;
            if (newX >= 0 && newX < 9 && newY >= 0 && newY < 9) {
                String type = maze.get(newY).get(newX);
                if (type.equals(".")) {
                    maze.get(newY).set(newX, "P");
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Q:\\algorithms\\src\\main\\java\\maze\\utils\\maze.txt"))) {
            for (List<String> row : maze) {
                writer.write(String.join("", row));
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

        public static List<Pair> agentDirections() {
        List<Pair> agentDirections = new ArrayList<>(
                List.of(
                       Pair.of(0, -1),
                        Pair.of(0, 1),
                        Pair.of(-1, 0),
                        Pair.of(1, 0),
                        Pair.of(-1, -1),
                        Pair.of(1, -1),
                        Pair.of(-1, 1),
                        Pair.of(1, 1)
                )
        );
        return agentDirections;
    }

    public static List<Pair> sentinalDirections() {
        List<Pair> agentDirections = new ArrayList<>(
                List.of(
                        Pair.of(0, -1),
                        Pair.of(0, 1),
                        Pair.of(-1, 0),
                        Pair.of(1, 0)
                )
        );
        return agentDirections;
    }

}
