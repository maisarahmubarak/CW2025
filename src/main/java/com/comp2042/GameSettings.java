package com.comp2042;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class GameSettings {
    private static final DoubleProperty brightness = new SimpleDoubleProperty(1.0);
    private static final DoubleProperty volume = new SimpleDoubleProperty(50.0);

    public static DoubleProperty brightnessProperty() { return brightness; }
    public static DoubleProperty volumeProperty() { return volume; }
    
    public static double getBrightness() { return brightness.get(); }
    public static void setBrightness(double value) { brightness.set(value); }
    
    public static double getVolume() { return volume.get(); }
    public static void setVolume(double value) { volume.set(value); }
}
