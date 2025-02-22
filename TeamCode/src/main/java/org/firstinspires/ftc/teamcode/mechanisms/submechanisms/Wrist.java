package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.BaseRobot;
import org.firstinspires.ftc.teamcode.Settings;

/** @noinspection FieldCanBeLocal, unused */
public class Wrist {
    public static double[] position = {0,0};
    public final Servo wristLeft;
    public final Servo wristRight;
    public static long rightServoDelay = 45;
    public static double[] verticalPos = Settings.Hardware.Servo.Wrist.VERTICAL_POSITION;
//    public final double[] chamberPos = Settings.Hardware.Servo.Wrist.CHAMBER_POSITION;
public static double[] horizPos = Settings.Hardware.Servo.Wrist.HORIZONTAL_POSITION;
public static double[] readyPos = Settings.Hardware.Servo.Wrist.READY_POSITION;

    private final BaseRobot baseRobot;
    private final HardwareMap hardwareMap;

    public Wrist(BaseRobot baseRobot) {
        this.baseRobot = baseRobot;
        this.hardwareMap = baseRobot.hardwareMap;
        wristLeft = hardwareMap.get(Servo.class, Settings.Hardware.IDs.WRIST_LEFT);
        wristRight = hardwareMap.get(Servo.class, Settings.Hardware.IDs.WRIST_RIGHT);
        wristRight.setDirection(Servo.Direction.REVERSE);
    }

    public void setPosition(Position newPosition) {
        double[] oldPosition = position;
        switch (newPosition) {
            case VERTICAL:
                position = verticalPos;
                break;
            case HORIZONTAL:
                position = horizPos;
                break;
            default:
                position = readyPos;
                break;
        }
        wristLeft.setPosition(position[0]);
        wristRight.setPosition(position[1]);
    }

    public Position position() {
        if (position == verticalPos) {
            return Position.VERTICAL;
          }
        else if (position == readyPos) {
            return Position.READY;
        }
        else if (position == horizPos) {
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
        CHAMBER,

        UNKNOWN,
    }

}
