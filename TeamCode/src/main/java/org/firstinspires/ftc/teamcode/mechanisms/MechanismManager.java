package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.hardware.rev.RevTouchSensor;
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
            Limelight3A limelight3A = hardwareMap.get(Limelight3A.class, Settings.Hardware.IDs.LIMELIGHT);
            RevColorSensorV3 colorSensor = hardwareMap.get(RevColorSensorV3.class, Settings.Hardware.IDs.COLOR_SENSOR);
            intake = new Intake(clawServo, horizontalMotor, rotatorServo, wristServo, limelight3A, colorSensor);
        }

        if (Settings.Deploy.OUTTAKE) {
            Servo clawServo = hardwareMap.get(Servo.class, Settings.Hardware.IDs.OUTTAKE_CLAW);
            DcMotor verticalMotorLeft = hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.SLIDE_VERTICAL_LEFT);
            DcMotor verticalMotorRight = hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.SLIDE_VERTICAL_RIGHT);
            Servo leftShoulderServo = hardwareMap.get(Servo.class, Settings.Hardware.IDs.LEFT_SHOULDER);
            Servo rightShoulderServo = hardwareMap.get(Servo.class, Settings.Hardware.IDs.RIGHT_SHOULDER);
            RevTouchSensor verticalSlideTouchSensor = hardwareMap.get(RevTouchSensor.class, Settings.Hardware.IDs.SLIDE_VERTICAL_TOUCH_SENSOR);
            outtake = new Outtake(verticalMotorLeft, verticalMotorRight, leftShoulderServo, rightShoulderServo, clawServo, verticalSlideTouchSensor);
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

    public void reset() {
        if (Settings.Deploy.INTAKE) {
            intake.reset();
        }
        if (Settings.Deploy.OUTTAKE) {
            outtake.reset();
        }
        if (Settings.Deploy.LINEAR_ACTUATOR) {
            linearActuator.reset();
        }
    }
}
