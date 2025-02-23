package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Settings;

public class HorizontalSlide implements ViperSlide {
    private final DcMotor horizontalMotor;
    private double encoderTarget;
    private HorizontalPosition currentPosition;

    public HorizontalSlide(DcMotor horizontalMotor) {
        this.horizontalMotor = horizontalMotor;
    }

    // Sets target position
    @Override
    public void setPosition(double position) {
        int targetPosition = (int) position;
        horizontalMotor.setTargetPosition(targetPosition);
        horizontalMotor.setPower(Settings.Hardware.HorizontalSlide.MOVEMENT_POWER);
    }

    // Converts position name to double
    public void setPosition(@NonNull HorizontalPosition position) {
        this.currentPosition = position;
        this.setPosition(position.getValue()); // Use the value associated with the enum
    }

    public void extend() {
        // Move to the next position in the enum, looping back to the start if needed
        HorizontalPosition[] positions = HorizontalPosition.values();
        int nextIndex = (currentPosition.ordinal() + 1) % positions.length;
        setPosition(positions[nextIndex]);
    }

    @Override
    public void retract() {
        // Move to the previous position in the enum, looping back if needed
        HorizontalPosition[] positions = HorizontalPosition.values();
        int prevIndex = (currentPosition.ordinal() - 1 + positions.length) % positions.length;
        setPosition(positions[prevIndex]);
    }

    @Override
    public void max() {
        setPosition(HorizontalPosition.EXPANDED);
    }

    @Override
    public void increment() {
        encoderTarget += Settings.Hardware.HorizontalSlide.FREAKY_MOVEMENT_POWER;
        setPosition(encoderTarget);
    }

    @Override
    public void decrement() {
        encoderTarget -= Settings.Hardware.HorizontalSlide.FREAKY_MOVEMENT_POWER;
        setPosition(encoderTarget);
    }

    public void init() {
        setPosition(HorizontalPosition.COLLAPSED);

        // Set to RUN_TO_POSITION mode for position control
        horizontalMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.currentPosition = HorizontalPosition.COLLAPSED;
        encoderTarget = horizontalMotor.getTargetPosition();
    }

    public void reset() {
        horizontalMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
}
