package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Settings;

/**
 * @noinspection unused
 */
public class Shoulder {
    public static double position = 0;
    public final Servo leftShoulderServo;
    public final Servo rightShoulderServo;
    public static final double placeForwardPos = Settings.Hardware.Servo.Shoulder.PLACE_FORWARD_POSITION;
    public static final double placeBackwardPos = Settings.Hardware.Servo.Shoulder.PLACE_BACKWARD_POSITION;

    public Shoulder(Servo leftShoulderServo, Servo rightShoulderServo) {
        this.leftShoulderServo = leftShoulderServo;
        this.rightShoulderServo = rightShoulderServo;
    }

    public void setPosition(Position newPosition) {
        double oldPosition = position;
        switch (newPosition) {
            case PLACE_FORWARD:
                position = placeForwardPos;
                break;
            case PLACE_BACKWARD:
            default:
                position = placeBackwardPos;
                break;
        }
        leftShoulderServo.setPosition(position);
        rightShoulderServo.setPosition(position);
    }

    public Position position() {
        if (position == placeForwardPos) {
            return Position.PLACE_FORWARD;
        } else if (position == placeBackwardPos) {
            return Position.PLACE_BACKWARD;
        } else {
            return Position.UNKNOWN;
        }
    }

    public void cyclePosition() {
        Position currentPosition = position();
        Position nextPosition;

        switch (currentPosition) {
            case PLACE_BACKWARD:
                nextPosition = Position.PLACE_FORWARD;
                break;
            case PLACE_FORWARD:
            default:
                nextPosition = Position.PLACE_BACKWARD;
                break;
        }

        setPosition(nextPosition);
    }

    public enum Position {
        PLACE_FORWARD,
        PLACE_BACKWARD,
        UNKNOWN,
    }

    public void init() {
        setPosition(Position.PLACE_FORWARD);
    }

    public void reset() {

    }

}
