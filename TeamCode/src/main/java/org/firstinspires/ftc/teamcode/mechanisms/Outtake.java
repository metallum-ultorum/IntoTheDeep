package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.OuttakeClaw;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Shoulder;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.VerticalSlide;

public class Outtake {
    public final VerticalSlide verticalSlide;
    public final Shoulder shoulder;
    public final OuttakeClaw outtakeClaw;

    public Outtake(DcMotor verticalMotorLeft, DcMotor verticalMotorRight, Servo leftShoulderServo, Servo rightShoulderServo, Servo clawServo, RevTouchSensor verticalMotorTouchSensor) {
        verticalSlide = new VerticalSlide(verticalMotorLeft, verticalMotorRight, verticalMotorTouchSensor);
        shoulder = new Shoulder(leftShoulderServo, rightShoulderServo);
        outtakeClaw = new OuttakeClaw(clawServo);
    }

    public void init() {
        verticalSlide.init();
        shoulder.init();
        outtakeClaw.init();
    }
}