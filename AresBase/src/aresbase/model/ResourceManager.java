package aresbase.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
    private final HashMap<Resource, Integer> resources = new HashMap<>();
    private int credits;

    public ResourceManager() {
        credits = 500;
        resources.put(Resource.OXYGEN,      85);
        resources.put(Resource.RATIONS,     40);
        resources.put(Resource.SPARE_PARTS, 12);
        resources.put(Resource.POWER,       160);
    }

    public boolean hasSufficient(Map<Resource, Integer> requirements) {
        for (Map.Entry<Resource, Integer> e : requirements.entrySet()) {
            if (resources.getOrDefault(e.getKey(), 0) < e.getValue()) return false;
        }
        return true;
    }

    public void consume(Map<Resource, Integer> requirements) {
        requirements.forEach((res, amt) ->
            resources.merge(res, -amt, Integer::sum));
    }

    public void restock(Resource res, int amount) {
        int current = resources.getOrDefault(res, 0);
        resources.put(res, Math.min(res.max, current + amount));
    }

    public void addCredits(int amount) { credits += amount; }

    public boolean spendCredits(int amount) {
        if (credits < amount) return false;
        credits -= amount;
        return true;
    }

    public int getCredits() { return credits; }
    public int getAmount(Resource res) { return resources.getOrDefault(res, 0); }
    public Map<Resource, Integer> getAll() { return Collections.unmodifiableMap(resources); }
}