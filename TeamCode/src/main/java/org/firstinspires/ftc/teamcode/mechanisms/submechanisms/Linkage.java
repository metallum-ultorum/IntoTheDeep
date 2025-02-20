package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.BaseRobot;
import org.firstinspires.ftc.teamcode.Settings;

/** @noinspection FieldCanBeLocal, unused */
public class Linkage {
    public static double position = 0;
    public final Servo leftShoulderServo;
    public final Servo rightShoulderServo;
    public static double transferPos = Settings.Hardware.Servo.Linkage.TRANSFER_POSITION;
    public static double placeForwardPos = Settings.Hardware.Servo.Linkage.PLACE_FORWARD_POSITION;
    public static double placeBackwardPos = Settings.Hardware.Servo.Linkage.PLACE_BACKWARD_POSITION;
    private final BaseRobot baseRobot;
    private final HardwareMap hardwareMap;

    public Linkage(BaseRobot baseRobot) {
        this.baseRobot = baseRobot;
        this.hardwareMap = baseRobot.hardwareMap;
        leftShoulderServo = hardwareMap.get(Servo.class, Settings.Hardware.IDs.LEFT_SHOULDER);
        rightShoulderServo = hardwareMap.get(Servo.class, Settings.Hardware.IDs.RIGHT_SHOULDER);
        leftShoulderServo.setDirection(Servo.Direction.REVERSE);
    }

    public void setPosition(Position newPosition) {
        double oldPosition = position;
        switch (newPosition) {
            case PLACE_FORWARD:
                position = placeForwardPos;
                break;
            case PLACE_BACKWARD:
                position = placeBackwardPos;
                break;
            case TRANSFER:
            default:
                position = transferPos;
                break;
        }
        leftShoulderServo.setPosition(position);
        rightShoulderServo.setPosition(position);
    }

    public Position position() {
        if (position == placeForwardPos) {
            return Position.PLACE_FORWARD;
        } else if (position == transferPos) {
            return Position.TRANSFER;
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
                nextPosition = Position.TRANSFER;
                break;
            case TRANSFER:
            default:
                nextPosition = Position.PLACE_BACKWARD;
                break;
        }

        setPosition(nextPosition);
    }

    public enum Position {
        TRANSFER,
        PLACE_FORWARD,
        PLACE_BACKWARD,
        UNKNOWN,
    }

}
