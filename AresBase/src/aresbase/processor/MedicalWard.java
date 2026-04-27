package aresbase.processor;

import aresbase.model.ColonyTask;

public class MedicalWard implements IProcessor {

    @Override
    public boolean canProcess(ColonyTask task) {
        return task.getProcessorType().equals("MedicalWard");
    }

    @Override
    public String processTask(ColonyTask task) {
        return "Medical Ward resolved: " + task.getName();
    }

    @Override
    public String getName() { return "Medical Ward"; }
}