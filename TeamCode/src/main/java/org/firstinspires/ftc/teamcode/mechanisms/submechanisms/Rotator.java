package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Settings;

/**
 * @noinspection unused
 */
public class Rotator {
    public static double position = 0;
    public static final double horizontalPos = Settings.Hardware.Servo.Rotator.LEFT_LIMIT;
    public static final double verticalPos = Settings.Hardware.Servo.Rotator.RIGHT_LIMIT;
    public final Servo rotatorServo;

    public Rotator(Servo rotatorServo) {
        this.rotatorServo = rotatorServo;
    }

    // Intended to be used with a joystick for variable control
    public void setPosition(double newPosition) {
        if(newPosition < horizontalPos) {
            newPosition = horizontalPos;
        } else if(newPosition > verticalPos) {
            newPosition = verticalPos;
        }
        position = newPosition;
        rotatorServo.setPosition(position);
    }

    public void init() {
        setPosition(horizontalPos);
    }

    public void reset() {
    }
}
