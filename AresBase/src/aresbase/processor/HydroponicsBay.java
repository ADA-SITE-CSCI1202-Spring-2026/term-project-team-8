package aresbase.processor;

import aresbase.model.ColonyTask;

public class HydroponicsBay implements IProcessor {

    @Override
    public boolean canProcess(ColonyTask task) {
        return task.getProcessorType().equals("HydroponicsBay");
    }

    @Override
    public String processTask(ColonyTask task) {
        return "Hydroponics Bay resolved: " + task.getName();
    }

    @Override
    public String getName() { return "Hydroponics Bay"; }
}