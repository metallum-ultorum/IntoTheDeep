package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.BaseRobot;
import org.firstinspires.ftc.teamcode.Settings;

/** @noinspection FieldCanBeLocal, unused */
public class Rotator {
    public static double position = 0;
    public final Servo rotator;
    private final BaseRobot baseRobot;
    private final HardwareMap hardwareMap;
    public static double horizontalPos = Settings.Hardware.Servo.Rotator.LEFT_LIMIT;
    public static double verticalPos = Settings.Hardware.Servo.Rotator.RIGHT_LIMIT;

    public Rotator(BaseRobot baseRobot) {
        this.baseRobot = baseRobot;
        this.hardwareMap = baseRobot.hardwareMap;
        rotator = hardwareMap.get(Servo.class, Settings.Hardware.IDs.OUTER_WRIST);
    }

    // Intended to be used with a joystick for variable control
    public void setPosition(double newPosition) {
        if(newPosition < horizontalPos) {
            newPosition = horizontalPos;
        } else if(newPosition > verticalPos) {
            newPosition = verticalPos;
        }
        position = newPosition;
        rotator.setPosition(position);
    }
}
