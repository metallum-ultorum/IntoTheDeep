package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.Settings;

/**
 * @noinspection unused
 */
public class Rotator {
    public static double position = 0;
    public static final double leftLimit = Settings.Hardware.Servo.Rotator.LEFT_LIMIT;
    public static final double rightLimit = Settings.Hardware.Servo.Rotator.RIGHT_LIMIT;
    public static final double center = Settings.Hardware.Servo.Rotator.CENTER;
    public final Servo rotatorServo;

    public Rotator(Servo rotatorServo) {
        this.rotatorServo = rotatorServo;
    }

    // Intended to be used with a joystick for variable control
    public void setPosition(double targetPosition) {
        position = Range.clip(targetPosition, leftLimit, rightLimit);
        rotatorServo.setPosition(position);
    }

    public void init() {
        setPosition(center);
    }

    public void reset() {
    }
}
