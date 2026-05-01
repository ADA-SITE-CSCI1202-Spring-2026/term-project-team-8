package aresbase.engine;

import aresbase.model.*;
import java.io.*;
import java.util.*;

public class SaveLoadManager {

    private static final String SAVE_FILE = "colony_save.csv";

    public static void save(ResourceManager resourceManager, Deque<ColonyTask> queue) throws IOException {
        new File("saves").mkdirs();
        try (PrintWriter writer = new PrintWriter(new FileWriter("saves/" + SAVE_FILE))) {

            // Line 1: credits
            writer.println("CREDITS," + resourceManager.getCredits());

            // Lines 2-5: resources
            for (Resource r : Resource.values()) {
                writer.println("RESOURCE," + r.name() + "," + resourceManager.getAmount(r));
            }

            // Remaining lines: tasks in queue order
            for (ColonyTask task : queue) {
                StringBuilder sb = new StringBuilder();
                sb.append("TASK,");
                sb.append(task.getTaskType()).append(",");
                sb.append(task.getName()).append(",");
                sb.append(task.getReward()).append(",");
                sb.append(task.getProcessorType()).append(",");
                // requirements: KEY:VAL|KEY:VAL
                StringJoiner reqs = new StringJoiner("|");
                task.getRequirements().forEach((res, amt) ->
                    reqs.add(res.name() + ":" + amt));
                sb.append(reqs);
                writer.println(sb);
            }
        }
    }

    public static void load(ResourceManager resourceManager, Deque<ColonyTask> queue) throws IOException {
        File file = new File("saves/" + SAVE_FILE);
        if (!file.exists()) throw new IOException("No save file found.");

        queue.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                switch (parts[0]) {
                    case "CREDITS" -> {
                        int credits = Integer.parseInt(parts[1]);
                        // Reset credits by spending all then adding correct amount
                        int current = resourceManager.getCredits();
                        if (current > 0) resourceManager.spendCredits(current);
                        resourceManager.addCredits(credits);
                    }
                    case "RESOURCE" -> {
                        Resource res = Resource.valueOf(parts[1]);
                        int target = Integer.parseInt(parts[2]);
                        int current = resourceManager.getAmount(res);
                        int diff = target - current;
                        if (diff > 0) resourceManager.restock(res, diff);
                        else if (diff < 0) {
                            Map<Resource, Integer> consume = new HashMap<>();
                            consume.put(res, -diff);
                            resourceManager.consume(consume);
                        }
                    }
                    case "TASK" -> {
                        String taskType    = parts[1];
                        String name        = parts[2];
                        int reward         = Integer.parseInt(parts[3]);
                        String procType    = parts[4];
                        Map<Resource, Integer> reqs = new HashMap<>();
                        if (parts.length > 5 && !parts[5].isEmpty()) {
                            for (String req : parts[5].split("\\|")) {
                                String[] kv = req.split(":");
                                reqs.put(Resource.valueOf(kv[0]), Integer.parseInt(kv[1]));
                            }
                        }
                        ColonyTask task = switch (taskType) {
                            case "LifeSupportTask" -> new LifeSupportTask(name, reqs, reward);
                            case "ResearchTask"    -> new ResearchTask(name, reqs, reward);
                            default                -> new EngineeringTask(name, reqs, reward, procType);
                        };
                        queue.addLast(task);
                    }
                }
            }
        }
    }
}