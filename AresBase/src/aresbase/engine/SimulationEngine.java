package aresbase.engine;

import aresbase.model.*;
import aresbase.processor.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SimulationEngine {
    private final ResourceManager resourceManager = new ResourceManager();
    private final Deque<ColonyTask> taskQueue = new ArrayDeque<>();
    private final List<IProcessor> processors = List.of(
        new EngineeringBay(), new MedicalWard(), new HydroponicsBay()
    );
 
    public ResourceManager getResourceManager() { return resourceManager; }
    public Deque<ColonyTask> getTaskQueue()      { return taskQueue; }
 
    public void addTask(ColonyTask task) { taskQueue.addLast(task); }
 
    public TaskFilter<ColonyTask> getQueueFilter() {
        return new TaskFilter<>(List.copyOf(taskQueue));
    }
 
    public int getRestockAmount(Resource res) {
        return switch (res) {
            case OXYGEN      -> 20;
            case RATIONS     -> 15;
            case SPARE_PARTS -> 5;
            case POWER       -> 40;
        };
    }
 
    public String executeNext() {
        if (taskQueue.isEmpty()) return "INFO: Queue is empty.";
 
        ColonyTask task = taskQueue.pollFirst();
 
        if (!resourceManager.hasSufficient(task.getRequirements())) {
            taskQueue.addFirst(task);
 
            String missing = task.getRequirements().entrySet().stream()
                    .filter(e -> resourceManager.getAmount(e.getKey()) < e.getValue())
                    .map(e -> e.getKey().label)
                    .collect(Collectors.joining(", "));
 
            return "ERROR: \"" + task.getName() + "\" failed — insufficient "
                   + missing + ". Task returned to queue.";
        }
 
        resourceManager.consume(task.getRequirements());
        resourceManager.addCredits(task.getReward());
 
        return processors.stream()
                .filter(p -> p.canProcess(task))
                .findFirst()
                .map(p -> "OK: " + p.processTask(task) + ". +credit" + task.getReward())
                .orElse("OK: \"" + task.getName() + "\" resolved. +credit" + task.getReward());
    }
 
    public String restock(Resource res) {
        int cost   = 50;
        int amount = getRestockAmount(res);
        if (!resourceManager.spendCredits(cost)) {
            return "ERROR: Insufficient credits. Need " + cost;
        }
        resourceManager.restock(res, amount);
        return "Synthesized +" + amount + res.unit + " " + res.label + ". -credit" + cost;
    }
 
    public String saveState() {
        try {
            SaveLoadManager.save(resourceManager, taskQueue);
            return "OK: State saved to saves/colony_save.csv";
        } catch (IOException e) {
            return "ERROR: Save failed — " + e.getMessage();
        }
    }
 
    public String loadState() {
        try {
            SaveLoadManager.load(resourceManager, taskQueue);
            return "OK: State loaded from saves/colony_save.csv";
        } catch (IOException e) {
            return "ERROR: Load failed — " + e.getMessage();
        }
    }
}