package io.github.leiriad.vibranium.utils;

import io.github.leiriad.vibranium.config.VibraniumConfigManager;

public class TemperatureUtils {

    public static boolean isFahrenheit() {
        return "FAHRENHEIT".equalsIgnoreCase(VibraniumConfigManager.INSTANCE.client.temperatureUnit);
    }

    public static int convert(int tempInCelsius) {
        if (isFahrenheit()) {
            return Math.round(tempInCelsius * 1.8f + 32);
        }
        return tempInCelsius;
    }

    public static String getUnitSymbol() {
        return isFahrenheit() ? "°F" : "°C";
    }

    public static String format(int tempInCelsius) {
        return convert(tempInCelsius) + " " + getUnitSymbol();
    }
}
