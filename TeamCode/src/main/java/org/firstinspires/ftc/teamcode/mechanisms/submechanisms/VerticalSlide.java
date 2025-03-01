package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import static org.firstinspires.ftc.teamcode.Settings.Hardware.VerticalSlide.MOVEMENT_POWER;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Settings;

public class VerticalSlide implements ViperSlide {
    public final DcMotor verticalMotorLeft;
    public final DcMotor verticalMotorRight;
    private final RevTouchSensor touchSensor;
    private double encoderTarget;
    private VerticalPosition currentPosition;
    private int currentPositionValue;

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
        currentPositionValue = (currentPositionValue + 1) % positions.length;
        encoderTarget = positions[currentPositionValue].getValue();
        setPosition(encoderTarget);
    }

    @Override
    public void retract() {
        // Move to the previous position in the enum, looping back if needed
        VerticalPosition[] positions = VerticalPosition.values();
        currentPositionValue = (currentPositionValue - 1 + positions.length) % positions.length;
        encoderTarget = positions[currentPositionValue].getValue();
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
        switch (Settings.Deploy.VERTICAL_SLIDE_MODE) {
            case RIGHT_ONLY_RAW:
                verticalMotorRight.setPower(MOVEMENT_POWER);
                break;
            case LEFT_ONLY_RAW:
                verticalMotorLeft.setPower(MOVEMENT_POWER);
                break;
            case RAW:
                verticalMotorRight.setPower(MOVEMENT_POWER);
                verticalMotorLeft.setPower(MOVEMENT_POWER);
                break;
            default:
                if (encoderTarget - currentOffset < VerticalPosition.HIGH_BASKET.getValue()) {
                    encoderTarget += encoderTicks;
                }
                setPosition(encoderTarget);
                break;
        }
    }

    @Override
    public void decrement() {
        switch (Settings.Deploy.VERTICAL_SLIDE_MODE) {
            case RIGHT_ONLY_RAW:
                verticalMotorRight.setPower(-MOVEMENT_POWER);
                break;
            case LEFT_ONLY_RAW:
                verticalMotorLeft.setPower(-MOVEMENT_POWER);
                break;
            case RAW:
                verticalMotorRight.setPower(-MOVEMENT_POWER);
                verticalMotorLeft.setPower(-MOVEMENT_POWER);
                break;
            default:
                if (!Settings.Hardware.VerticalSlide.ENABLE_LOWER_LIMIT || !touchSensor.isPressed() || encoderTarget - currentOffset > VerticalPosition.TRANSFER.getValue()) {
                    encoderTarget -= Settings.Hardware.VerticalSlide.INCREMENTAL_MOVEMENT_POWER;
                }
                setPosition(encoderTarget);
                break;
        }
    }

    public void maybeStop() {
        switch (Settings.Deploy.VERTICAL_SLIDE_MODE) {
            case RAW:
                verticalMotorLeft.setPower(0);
                verticalMotorRight.setPower(0);
                break;
            case RIGHT_ONLY_RAW:
                verticalMotorRight.setPower(0);
                break;
            case LEFT_ONLY_RAW:
                verticalMotorLeft.setPower(0);
                break;
        }
    }

    public boolean isTouchingSensor() {
        return touchSensor.isPressed();
    }

    public void checkMotors() {
        switch (Settings.Deploy.VERTICAL_SLIDE_MODE) {
            case CUSTOM_RTP:
                if (Math.abs(verticalMotorRight.getCurrentPosition() - encoderTarget) < 30) {
                    verticalMotorRight.setPower(getIdlePower(encoderTarget));
                    verticalMotorLeft.setPower(getIdlePower(encoderTarget));
                } else if (encoderTarget > verticalMotorRight.getCurrentPosition()) {
                    verticalMotorRight.setPower(MOVEMENT_POWER);
                    verticalMotorLeft.setPower(MOVEMENT_POWER);
                } else {
                    verticalMotorRight.setPower(-MOVEMENT_POWER);
                    verticalMotorLeft.setPower(-MOVEMENT_POWER);
                }
                break;
            case RUN_TO_POSITION:
                if (Math.abs(verticalMotorRight.getCurrentPosition() - encoderTarget) < 5) {
                    verticalMotorRight.setPower(0);
                    verticalMotorLeft.setPower(0);
                } else {
                    verticalMotorRight.setPower(Settings.Hardware.VerticalSlide.MOVEMENT_POWER);
                    verticalMotorLeft.setPower(verticalMotorRight.getPower());
                }
                break;
            case RIGHT_ONLY_RTP:
                if (Math.abs(verticalMotorRight.getCurrentPosition() - encoderTarget) < 5) {
                    verticalMotorRight.setPower(0);
                } else {
                    verticalMotorRight.setPower(Settings.Hardware.VerticalSlide.MOVEMENT_POWER);
                }
                break;
        }
    }

    public double getIdlePower(double encoderTarget) {
        if (encoderTarget < 900) {
            return 0;
        }
        if (encoderTarget < 1200) {
            return 0.1;
        }
        if (encoderTarget < 2000) {
            return 0.2;
        }
        return 0.18;
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

        switch (Settings.Deploy.VERTICAL_SLIDE_MODE) {
            case CUSTOM_RTP:
                verticalMotorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                verticalMotorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            case RUN_TO_POSITION:
                verticalMotorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                verticalMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            case RIGHT_ONLY_RTP:
                verticalMotorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            case RIGHT_ONLY_RAW:
                verticalMotorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            case LEFT_ONLY_RAW:
                verticalMotorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            case RAW:
                verticalMotorRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                verticalMotorLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        this.currentPosition = VerticalPosition.TRANSFER;
        encoderTarget = verticalMotorRight.getTargetPosition();

    }

    public enum verticalRunModes {
        RUN_TO_POSITION,
        CUSTOM_RTP,
        RIGHT_ONLY_RTP,
        RAW,
        RIGHT_ONLY_RAW,
        LEFT_ONLY_RAW
    }
}
