package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Settings;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.LinearActuator;

public class MechanismManager {
    public Intake intake;
    public Outtake outtake;
    public LinearActuator linearActuator;

    public MechanismManager(HardwareMap hardwareMap) {
        if (Settings.Deploy.INTAKE) {
            Servo clawServo = hardwareMap.get(Servo.class, Settings.Hardware.IDs.INTAKE_CLAW);
            DcMotor horizontalMotor = hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.SLIDE_HORIZONTAL);
            Servo rotatorServo = hardwareMap.get(Servo.class, Settings.Hardware.IDs.ROTATOR);
            Servo wristServo = hardwareMap.get(Servo.class, Settings.Hardware.IDs.WRIST);
            intake = new Intake(clawServo, horizontalMotor, rotatorServo, wristServo);
        }

        if (Settings.Deploy.OUTTAKE) {
            Servo clawServo = hardwareMap.get(Servo.class, Settings.Hardware.IDs.OUTTAKE_CLAW);
            DcMotor verticalMotorLeft = hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.SLIDE_VERTICAL_LEFT);
            DcMotor verticalMotorRight = hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.SLIDE_VERTICAL_RIGHT);
            Servo leftShoulderServo = hardwareMap.get(Servo.class, Settings.Hardware.IDs.LEFT_SHOULDER);
            Servo rightShoulderServo = hardwareMap.get(Servo.class, Settings.Hardware.IDs.RIGHT_SHOULDER);
            outtake = new Outtake(verticalMotorLeft, verticalMotorRight, leftShoulderServo, rightShoulderServo, clawServo);
        }

        if (Settings.Deploy.LINEAR_ACTUATOR) {
            DcMotor actuatorMotor = hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.LINEAR_ACTUATOR);
            linearActuator = new LinearActuator(actuatorMotor);
        }
    }

    public void init() {
        if (Settings.Deploy.INTAKE) {
            intake.init();
        }
        if (Settings.Deploy.OUTTAKE) {
            outtake.init();
        }
        if (Settings.Deploy.LINEAR_ACTUATOR) {
            linearActuator.init();
        }
    }
}
