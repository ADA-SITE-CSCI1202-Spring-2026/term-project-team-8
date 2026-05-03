package aresbase.engine;

import aresbase.model.*;
import java.util.*;

public class TaskGenerator {
    private static final Random rng = new Random();

    private static ColonyTask[] buildTemplates() {
        return new ColonyTask[]{
            new LifeSupportTask("O2 Scrubber Failure",
                new HashMap<>(Map.of(Resource.OXYGEN, 10, Resource.SPARE_PARTS, 2)), 80),
            new LifeSupportTask("Hull Breach Deck C",
                new HashMap<>(Map.of(Resource.SPARE_PARTS, 3, Resource.POWER, 10)), 100),
            new EngineeringTask("Solar Array Offline",
                new HashMap<>(Map.of(Resource.SPARE_PARTS, 2, Resource.POWER, 5)), 60, "EngineeringBay"),
            new EngineeringTask("Water Recycler Leak",
                new HashMap<>(Map.of(Resource.SPARE_PARTS, 1, Resource.RATIONS, 5)), 50, "HydroponicsBay"),
            new ResearchTask("Med Bay Diagnostic",
                new HashMap<>(Map.of(Resource.POWER, 15, Resource.RATIONS, 3)), 40),
            new ResearchTask("Hydroponics Check",
                new HashMap<>(Map.of(Resource.OXYGEN, 5, Resource.RATIONS, 10)), 35),
            new EngineeringTask("Comm Array Repair",
                new HashMap<>(Map.of(Resource.SPARE_PARTS, 1, Resource.POWER, 20)), 55, "EngineeringBay")
        };
    }

    public static ColonyTask generate() {
        ColonyTask[] templates = buildTemplates();
        return templates[rng.nextInt(templates.length)].copy();
    }

}