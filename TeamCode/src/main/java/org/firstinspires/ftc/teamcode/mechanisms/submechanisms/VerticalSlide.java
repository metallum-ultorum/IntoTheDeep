package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.BaseRobot;
import org.firstinspires.ftc.teamcode.Settings;

public class VerticalSlide implements ViperSlide {
    private final DcMotor verticalMotorLeft;
    private final DcMotor verticalMotorRight;
    private final BaseRobot baseRobot;
    private double encoderTarget;
    private VerticalPosition currentPosition;
    private final String LOG_PREFIX = "Vertical Slide: ";

    public VerticalSlide(@NonNull BaseRobot baseRobot) {
        this.baseRobot = baseRobot;
        this.verticalMotorLeft = baseRobot.hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.SLIDE_VERTICAL_LEFT);
        this.verticalMotorRight = baseRobot.hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.SLIDE_VERTICAL_RIGHT);

        verticalMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        verticalMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        verticalMotorRight.setDirection(DcMotor.Direction.REVERSE);

        setPosition(VerticalPosition.TRANSFER);

        // Set to RUN_TO_POSITION mode for position control
        verticalMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        verticalMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.currentPosition = VerticalPosition.TRANSFER;
        encoderTarget = verticalMotorLeft.getTargetPosition();
        encoderTarget = verticalMotorRight.getTargetPosition();
    }

    // Sets target position
    @Override
    public void setPosition(double position) {
        int targetPosition = (int) position;
        verticalMotorLeft.setTargetPosition(targetPosition);
        verticalMotorLeft.setPower(Settings.Hardware.Extensor.MOVEMENT_POWER);
        verticalMotorRight.setTargetPosition(targetPosition);
        verticalMotorRight.setPower(Settings.Hardware.Extensor.MOVEMENT_POWER);
    }

    // Converts position name to double
    public void setPosition(@NonNull VerticalPosition position) {
        this.currentPosition = position;
        this.setPosition(position.getValue()); // Use the value associated with the enum
    }

    public void extend() {
        // Move to the next position in the enum, looping back to the start if needed
        VerticalPosition[] positions = VerticalPosition.values();
        int nextIndex = (currentPosition.ordinal() + 1) % positions.length;
        setPosition(positions[nextIndex]);
    }

    @Override
    public void retract() {
        // Move to the previous position in the enum, looping back if needed
        VerticalPosition[] positions = VerticalPosition.values();
        int prevIndex = (currentPosition.ordinal() - 1 + positions.length) % positions.length;
        setPosition(positions[prevIndex]);
    }

    @Override
    public void min() {
        setPosition(VerticalPosition.TRANSFER);
    }

    @Override
    public void max() {
        setPosition(VerticalPosition.HIGH_BASKET);
    }

    @Override
    public void increment() {
        if (encoderTarget < VerticalPosition.HIGH_BASKET.getValue()) {
            encoderTarget += Settings.Hardware.VerticalSlide.FREAKY_MOVEMENT_POWER;
        }
        setPosition(encoderTarget);
    }

    @Override
    public void decrement() {
        if (!Settings.Hardware.VerticalSlide.ENABLE_LOWER_LIMIT || encoderTarget > VerticalPosition.TRANSFER.getValue() - 50) {
            encoderTarget -= Settings.Hardware.VerticalSlide.FREAKY_MOVEMENT_POWER;
        }
        setPosition(encoderTarget);
    }
}
