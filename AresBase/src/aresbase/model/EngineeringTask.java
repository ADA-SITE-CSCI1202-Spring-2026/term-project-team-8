package aresbase.model;

import java.util.HashMap;
import java.util.Map;

public class EngineeringTask extends ColonyTask {

    public EngineeringTask(String name, Map<Resource, Integer> requirements,
                           int reward, String processorType) {
        super(name, requirements, reward, processorType);
    }

    @Override
    public String getTaskType() { return "EngineeringTask"; }

    @Override
    public String getSeverity() { return "URGENT"; }

    @Override
    public ColonyTask copy() {
        return new EngineeringTask(getName(), new HashMap<>(getRequirements()), getReward(), getProcessorType());
    }
}