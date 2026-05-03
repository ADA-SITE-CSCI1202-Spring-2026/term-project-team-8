package aresbase.model;

import java.util.HashMap;
import java.util.Map;

public class ResearchTask extends ColonyTask {

    public ResearchTask(String name, Map<Resource, Integer> requirements, int reward) {
        super(name, requirements, reward, "MedicalWard");
    }

    @Override
    public String getTaskType() { return "ResearchTask"; }

    @Override
    public String getSeverity() { return "ROUTINE"; }

    @Override
    public ColonyTask copy() {
        return new ResearchTask(getName(), new HashMap<>(getRequirements()), getReward());
    }
}