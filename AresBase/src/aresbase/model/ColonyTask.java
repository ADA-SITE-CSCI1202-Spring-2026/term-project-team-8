package aresbase.model;

import java.util.Map;

public abstract class ColonyTask {
    private final String name;
    private final Map<Resource, Integer> requirements;
    private final int reward;
    private final String processorType;

    public ColonyTask(String name, Map<Resource, Integer> requirements,
                      int reward, String processorType) {
        this.name = name;
        this.requirements = requirements;
        this.reward = reward;
        this.processorType = processorType;
    }

    public String getName() { return name; }
    public Map<Resource, Integer> getRequirements() { return requirements; }
    public int getReward() { return reward; }
    public String getProcessorType() { return processorType; }

    public abstract String getTaskType();
    public abstract String getSeverity();
    public abstract ColonyTask copy();

    @Override
    public String toString() {
        return "[" + getTaskType() + "] " + name;
    }
}