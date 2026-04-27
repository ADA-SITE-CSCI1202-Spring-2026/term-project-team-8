package aresbase.processor;

import aresbase.model.ColonyTask;

public class EngineeringBay implements IProcessor {

    @Override
    public boolean canProcess(ColonyTask task) {
        return task.getProcessorType().equals("EngineeringBay");
    }

    @Override
    public String processTask(ColonyTask task) {
        return "Engineering Bay resolved: " + task.getName();
    }

    @Override
    public String getName() { return "Engineering Bay"; }
}