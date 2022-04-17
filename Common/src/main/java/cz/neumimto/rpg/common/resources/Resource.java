package cz.neumimto.rpg.common.resources;


import java.util.HashMap;
import java.util.Map;

public class Resource {
    private Map<String, Double> maxValSource = new HashMap<>();
    private Map<String, Double> tickChangeSource = new HashMap<>();

    private double maxValue;
    private double value;
    private double tickChange;

    private final String type;

    public Resource(String type) {
        this.type = type;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String source, double maxValue) {
        maxValSource.put(source, maxValue);
        setMaxValue(maxValSource.values().stream().mapToDouble(value1 -> value1.doubleValue()).sum());
    }

    protected void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        if (value > maxValue) {
            value = maxValue;
        }
        this.value = value;
    }

    public double getTickChange() {
        return tickChange;
    }

    public void setTickChange(String source, double tickChange) {
        tickChangeSource.put(source, maxValue);
        this.tickChange = tickChangeSource.values().stream().mapToDouble(value1 -> value1.doubleValue()).sum();
    }

    public String getType() {
        return type;
    }
}
