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
     * Initializes Color Sensor
     */
    public void init() {
        colorSensor.initialize();
        colorSensor.enableLed(true);
    }

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
        return rgbValues[0] > yellowMinValues[0]
            && rgbValues[0] < yellowMaxValues[0]
            && rgbValues[1] > yellowMinValues[1]
            && rgbValues[1] < yellowMaxValues[1]
            && rgbValues[2] > yellowMinValues[2]
            && rgbValues[2] < yellowMaxValues[2];
    }

    public boolean isRed() {
        return rgbValues[0] > redMinValues[0]
            && rgbValues[0] < redMaxValues[0]
            && rgbValues[1] > redMinValues[1]
            && rgbValues[1] < redMaxValues[1]
            && rgbValues[2] > redMinValues[2]
            && rgbValues[2] < redMaxValues[2];
    }

    public boolean isBlue() {
        return rgbValues[0] > blueMinValues[0]
            && rgbValues[0] < blueMaxValues[0]
            && rgbValues[1] > blueMinValues[1]
            && rgbValues[1] < blueMaxValues[1]
            && rgbValues[2] > blueMinValues[2]
            && rgbValues[2] < blueMaxValues[2];
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
