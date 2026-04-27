package aresbase.processor;

import aresbase.model.ColonyTask;

public interface IProcessor {
    boolean canProcess(ColonyTask task);
    String processTask(ColonyTask task);
    String getName();
}