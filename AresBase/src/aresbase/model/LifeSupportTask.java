package aresbase.model;

import java.util.HashMap;
import java.util.Map;

public class LifeSupportTask extends ColonyTask {

    public LifeSupportTask(String name, Map<Resource, Integer> requirements, int reward) {
        super(name, requirements, reward, "EngineeringBay");
    }

    @Override
    public String getTaskType() { return "LifeSupportTask"; }

    @Override
    public String getSeverity() { return "CRITICAL"; }

    @Override
    public ColonyTask copy() {
        return new LifeSupportTask(getName(), new HashMap<>(getRequirements()), getReward());
    }
}