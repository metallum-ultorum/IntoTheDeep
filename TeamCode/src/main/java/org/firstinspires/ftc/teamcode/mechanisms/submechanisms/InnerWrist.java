package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.BaseRobot;
import org.firstinspires.ftc.teamcode.Settings;

/** @noinspection FieldCanBeLocal, unused */
public class InnerWrist {
    public static double[] position = {0,0};
    public final Servo wristLeft;
    public final Servo wristRight;
    public static long rightServoDelay = 45;
    public static double[] verticalPos = Settings.Hardware.Servo.InnerWrist.VERTICAL_POSITION;
//    public final double[] chamberPos = Settings.Hardware.Servo.Wrist.CHAMBER_POSITION;
    public static double[] horizPos = Settings.Hardware.Servo.InnerWrist.HORIZONTAL_POSITION;

    private final BaseRobot baseRobot;
    private final HardwareMap hardwareMap;

    public InnerWrist(BaseRobot baseRobot) {
        this.baseRobot = baseRobot;
        this.hardwareMap = baseRobot.hardwareMap;
        wristLeft = hardwareMap.get(Servo.class, Settings.Hardware.IDs.INNER_WRIST_LEFT);
        wristRight = hardwareMap.get(Servo.class, Settings.Hardware.IDs.INNER_WRIST_RIGHT);
        wristRight.setDirection(Servo.Direction.REVERSE);
    }

    public void setPosition(Position newPosition) {
        double[] oldPosition = position;
        switch (newPosition) {
            case VERTICAL:
                position = verticalPos;
                break;
//            case CHAMBER:
//                position = chamberPos;
//                break;
            default:
                position = horizPos;
                break;
        }
        wristLeft.setPosition(position[0]);
        wristRight.setPosition(position[1]);
    }

    public Position position() {
        if (position == verticalPos) {
            return Position.VERTICAL;
          }
//        else if (position == chamberPos) {
//            return Position.CHAMBER;
//        }
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
            case HORIZONTAL:
                nextPosition = Position.VERTICAL;
                break;
            case VERTICAL:
                nextPosition = Position.HORIZONTAL;
                break;
            default:
                nextPosition = Position.HORIZONTAL; // Fallback to HORIZONTAL if unknown
                break;
        }

        setPosition(nextPosition);
    }

    public enum Position {
        HORIZONTAL,
        VERTICAL,
        CHAMBER,
        UNKNOWN,
    }

}
