package aresbase.engine;

import aresbase.model.ColonyTask;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Generic utility for querying a collection of ColonyTask subclasses.
 * Demonstrates bounded generics + streams in a meaningful context:
 * the UI or engine can ask questions like "how many CRITICAL tasks are queued?"
 * without coupling that logic to any specific task type.
 *
 * @param <T> any subtype of ColonyTask
 */
public class TaskFilter<T extends ColonyTask> {

    private final Collection<T> source;

    public TaskFilter(Collection<T> source) {
        this.source = source;
    }

    /** Returns all tasks matching the given predicate. */
    public List<T> filter(Predicate<T> predicate) {
        return source.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    /** Counts tasks matching the given predicate. */
    public long count(Predicate<T> predicate) {
        return source.stream().filter(predicate).count();
    }

    /** Returns true if any task matches the predicate. */
    public boolean anyMatch(Predicate<T> predicate) {
        return source.stream().anyMatch(predicate);
    }

    /** Returns the total credit reward of all matching tasks. */
    public int totalReward(Predicate<T> predicate) {
        return source.stream()
                .filter(predicate)
                .mapToInt(ColonyTask::getReward)
                .sum();
    }
}