package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Settings;

public class LinearActuator  {
    private final DcMotor actuatorMotor;

    public LinearActuator(DcMotor actuatorMotor) {
        this.actuatorMotor = actuatorMotor;
    }

    public void extend() {
        actuatorMotor.setTargetPosition(Settings.Hardware.LinearActuator.MAX);
    }

    public void retract() {
        actuatorMotor.setTargetPosition(Settings.Hardware.LinearActuator.MIN);
    }

    public void stop() {
        actuatorMotor.setTargetPosition(actuatorMotor.getCurrentPosition());
    }

    public void init() {
        retract();

        // Set to RUN_TO_POSITION mode for position control
        actuatorMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void reset() {
        actuatorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
}
