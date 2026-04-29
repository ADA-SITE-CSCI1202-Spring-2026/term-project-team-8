package aresbase.engine;

import aresbase.model.*;
import aresbase.processor.*;
import java.util.*;

public class SimulationEngine {
    private final ResourceManager resourceManager = new ResourceManager();
    private final Deque<ColonyTask> taskQueue = new ArrayDeque<>();
    private final List<IProcessor> processors = List.of(
        new EngineeringBay(), new MedicalWard(), new HydroponicsBay()
    );

    public ResourceManager getResourceManager() { return resourceManager; }
    public Deque<ColonyTask> getTaskQueue() { return taskQueue; }

    public void addTask(ColonyTask task) { taskQueue.addLast(task); }

    public String executeNext() {
        if (taskQueue.isEmpty()) return "INFO: Queue is empty.";

        ColonyTask task = taskQueue.pollFirst();

        if (!resourceManager.hasSufficient(task.getRequirements())) {
            List<String> missing = new ArrayList<>();
            task.getRequirements().forEach((res, amt) -> {
                if (resourceManager.getAmount(res) < amt) missing.add(res.label);
            });
            return "ERROR: \"" + task.getName() + "\" failed — insufficient "
                   + String.join(", ", missing) + ". Task discarded.";
        }

        resourceManager.consume(task.getRequirements());
        resourceManager.addCredits(task.getReward());

        for (IProcessor p : processors) {
            if (p.canProcess(task)) {
                return "OK: " + p.processTask(task) + ". +credit" + task.getReward();
            }
        }
        return "OK: \"" + task.getName() + "\" resolved. +credit" + task.getReward();
    }

    public String restock(Resource res) {
        int cost = 50;
        int amount = switch (res) {
            case OXYGEN      -> 20;
            case RATIONS     -> 15;
            case SPARE_PARTS -> 5;
            case POWER       -> 40;
        };
        if (!resourceManager.spendCredits(cost)) {
            return "ERROR: Insufficient credits. Need " + cost;
        }
        resourceManager.restock(res, amount);
        return "Synthesized +" + amount + res.unit + " " + res.label + ". -credit" + cost;
    }
}