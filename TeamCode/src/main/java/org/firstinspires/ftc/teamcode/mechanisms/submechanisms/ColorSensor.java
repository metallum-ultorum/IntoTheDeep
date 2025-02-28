package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevColorSensorV3;

/**
 * Incoming Yap Session:
 * Limelight returns Tx and Ty values, which return angles for where a detected object is,
 * and trig is required to get pixel or distance values.
 * IMPORTANT: Tx and Ty are zero when no desired object is detected.
 * Contact Rishu if any of this is confusing
 */
@Config
public class ColorSensor {
    RevColorSensorV3 colorSensor;
    ColorSensorOption desiredColors = ColorSensorOption.RED_YELLOW;
    double[] rgbValues = {0,0,0};
    static double[] yellowMinValues = { // TODO: TUNE
            100,
            100,
            0
    };
    static double[] yellowMaxValues = { // TODO: TUNE
            255,
            255,
            100
    };
    static double[] redMinValues = { // TODO: TUNE
            100,
            0,
            0
    };
    static double[] redMaxValues = { // TODO: TUNE
            255,
            100,
            100
    };
    static double[] blueMinValues = { // TODO: TUNE
            0,
            0,
            100
    };
    static double[] blueMaxValues = { // TODO: TUNE
            100,
            100,
            255
    };

    public ColorSensor(RevColorSensorV3 colorSensorV3) {
        this.colorSensor = colorSensorV3;
    }

    /**
     * Initializes Color Sensor and enables LED
     */
    public void init() {
        colorSensor.initialize();
        colorSensor.enableLed(true);
    }

    /**
     * Updates the data and checks if there is a desired object detected
     * @return whether the gamepad should vibrate based on detected object
     */
    public boolean update() {
        rgbValues[0] = colorSensor.red();
        rgbValues[1] = colorSensor.green();
        rgbValues[2] = colorSensor.blue();
        return checkForDesiredColors();
    }

    public boolean checkForDesiredColors() {
        switch (desiredColors) {
            case YELLOW:
                return isYellow();
            case RED:
                return isRed();
            case BLUE:
                return isBlue();
            case RED_YELLOW:
                return isYellow() || isRed();
            case BLUE_YELLOW:
                return isYellow() || isBlue();
            default:
                return false;
        }
    }

    public boolean isYellow() {
        return checkMinMaxValues(yellowMinValues, yellowMaxValues);
    }

    public boolean isRed() {
        return checkMinMaxValues(redMinValues, redMaxValues);
    }

    public boolean isBlue() {
        return checkMinMaxValues(blueMinValues, blueMaxValues);
    }

    public boolean checkMinMaxValues(double[] minValues, double[] maxValues) {
        return rgbValues[0] > minValues[0]
            && rgbValues[0] < maxValues[0]
            && rgbValues[1] > minValues[1]
            && rgbValues[1] < maxValues[1]
            && rgbValues[2] > minValues[2]
            && rgbValues[2] < maxValues[2];
    }

    public void setDesiredColors(ColorSensorOption newDesiredColors) {
        desiredColors = newDesiredColors;
    }

    public enum ColorSensorOption {
        YELLOW,
        RED,
        BLUE,
        RED_YELLOW,
        BLUE_YELLOW
    }
}
