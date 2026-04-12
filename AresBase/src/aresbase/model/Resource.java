package aresbase.model;

public enum Resource {
    OXYGEN("Oxygen", "%", 100),
    RATIONS("Rations", "u", 60),
    SPARE_PARTS("Spare Parts", "u", 30),
    POWER("Power", "kW", 200);

    public final String label;
    public final String unit;
    public final int max;

    Resource(String label, String unit, int max) {
        this.label = label;
        this.unit = unit;
        this.max = max;
    }
}