package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Settings;

public class VerticalSlide implements ViperSlide {
    private final DcMotor verticalMotorLeft;
    private final DcMotor verticalMotorRight;
    private final RevTouchSensor touchSensor;
    private double encoderTarget;
    private VerticalPosition currentPosition;
    private double verticalSlideOffset;

    public VerticalSlide(DcMotor verticalMotorLeft, DcMotor verticalMotorRight, RevTouchSensor verticalMotorTouchSensor) {
        this.verticalMotorLeft = verticalMotorLeft;
        this.verticalMotorRight = verticalMotorRight;
        this.touchSensor = verticalMotorTouchSensor;
    }

    // Sets target position
    @Override
    public void setPosition(double position) {
        int targetPosition = (int) position;
        verticalMotorLeft.setTargetPosition(targetPosition);
        verticalMotorLeft.setPower(Settings.Hardware.VerticalSlide.MOVEMENT_POWER);
        verticalMotorRight.setTargetPosition(targetPosition);
        verticalMotorRight.setPower(Settings.Hardware.VerticalSlide.MOVEMENT_POWER);
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
        if (!Settings.Hardware.VerticalSlide.ENABLE_LOWER_LIMIT || !touchSensor.isPressed()) {
            encoderTarget -= Settings.Hardware.VerticalSlide.FREAKY_MOVEMENT_POWER;
        }
        setPosition(encoderTarget);
    }

    public void resetEncoders() {
            verticalMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            verticalMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void init() {
//        resetEncoders();
        verticalMotorRight.setDirection(DcMotor.Direction.REVERSE);

        setPosition(VerticalPosition.TRANSFER);

        verticalMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        verticalMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.currentPosition = VerticalPosition.TRANSFER;
        encoderTarget = verticalMotorRight.getTargetPosition();
    }
}
