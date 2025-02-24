package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Settings;

/**
 * @noinspection unused
 */
public class Wrist {
    public static double position = 0;
    public static final double verticalPos = Settings.Hardware.Servo.Wrist.VERTICAL_POSITION;
    public static long rightServoDelay = 45;
    public static final double horizontalPos = Settings.Hardware.Servo.Wrist.HORIZONTAL_POSITION;
    public static final double readyPos = Settings.Hardware.Servo.Wrist.READY_POSITION;
    public final Servo wristServo;


    public Wrist(Servo wristServo) {
        this.wristServo = wristServo;
    }

    public void setPosition(Position newPosition) {
        double oldPosition = position;
        switch (newPosition) {
            case VERTICAL:
                position = verticalPos;
                break;
            case HORIZONTAL:
                position = horizontalPos;
                break;
            default:
                position = readyPos;
                break;
        }
        wristServo.setPosition(position);
    }

    public Position position() {
        if (position == verticalPos) {
            return Position.VERTICAL;
          }
        else if (position == readyPos) {
            return Position.READY;
        } else if (position == horizontalPos) {
            return Position.HORIZONTAL;
        } else {
            return Position.UNKNOWN;
        }
    }

    public void cyclePosition() {
        Position currentPosition = position();
        Position nextPosition;

        switch (currentPosition) {
            case READY:
                nextPosition = Position.VERTICAL;
                break;
            case VERTICAL:
                nextPosition = Position.READY;
                break;
            default:
                nextPosition = Position.READY; // Fallback to READY if unknown
                break;
        }

        setPosition(nextPosition);
    }

    public enum Position {
        HORIZONTAL,
        VERTICAL,
        READY,
        UNKNOWN,
    }

    public void init() {
        setPosition(Position.READY);
    }

    public void reset() {
    }

}
