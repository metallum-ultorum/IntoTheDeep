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
    private double currentOffset;

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
        verticalMotorRight.setTargetPosition(targetPosition);
    }

    // Converts position name to double
    public void setPosition(@NonNull VerticalPosition position) {
        this.currentPosition = position;
        encoderTarget = position.getValue();
        this.setPosition(encoderTarget); // Use the value associated with the enum
    }

    public void extend() {
        // Move to the next position in the enum, looping back to the start if needed
        VerticalPosition[] positions = VerticalPosition.values();
        int nextIndex = (currentPosition.ordinal() + 1) % positions.length;
        encoderTarget = positions[nextIndex].getValue();
        setPosition(encoderTarget);
    }

    @Override
    public void retract() {
        // Move to the previous position in the enum, looping back if needed
        VerticalPosition[] positions = VerticalPosition.values();
        int prevIndex = (currentPosition.ordinal() - 1 + positions.length) % positions.length;
        encoderTarget = positions[prevIndex].getValue();
        setPosition(encoderTarget);
    }

    @Override
    public void max() {
        setPosition(VerticalPosition.HIGH_BASKET);
    }

    @Override
    public void increment() {
        if (encoderTarget - currentOffset < VerticalPosition.HIGH_BASKET.getValue()) {
            encoderTarget += Settings.Hardware.VerticalSlide.INCREMENTAL_MOVEMENT_POWER;
        }
        setPosition(encoderTarget);
    }

    public void increment(int encoderTicks) {
        if (encoderTarget - currentOffset < VerticalPosition.HIGH_BASKET.getValue()) {
            encoderTarget += encoderTicks;
        }
        setPosition(encoderTarget);
    }

    @Override
    public void decrement() {
        if (!Settings.Hardware.VerticalSlide.ENABLE_LOWER_LIMIT || !touchSensor.isPressed() || encoderTarget - currentOffset > VerticalPosition.TRANSFER.getValue()) {
            encoderTarget -= Settings.Hardware.VerticalSlide.INCREMENTAL_MOVEMENT_POWER;
        }
        setPosition(encoderTarget);
    }

    public boolean isTouchingSensor() {
        return touchSensor.isPressed();
    }

    public void checkMotors() {
        if (Math.abs(verticalMotorRight.getCurrentPosition() - encoderTarget) < 5) {
            verticalMotorRight.setPower(0);
            if (Math.abs((verticalMotorRight.getCurrentPosition() - verticalMotorLeft.getCurrentPosition())) < 10) {
                verticalMotorLeft.setPower(0);
            }
        } else {
            verticalMotorLeft.setPower(Settings.Hardware.VerticalSlide.MOVEMENT_POWER);
            verticalMotorRight.setPower(Settings.Hardware.VerticalSlide.MOVEMENT_POWER);
        }
    }

    public void setToZero() {
        currentOffset = encoderTarget;
    }

    public void reset() {
            verticalMotorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            verticalMotorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void init() {
        verticalMotorRight.setDirection(DcMotor.Direction.REVERSE);

        setPosition(VerticalPosition.TRANSFER);

        verticalMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        verticalMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.currentPosition = VerticalPosition.TRANSFER;
        encoderTarget = verticalMotorRight.getTargetPosition();
    }
}
