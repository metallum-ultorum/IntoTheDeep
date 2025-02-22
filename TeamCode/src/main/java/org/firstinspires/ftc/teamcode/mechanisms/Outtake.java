package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.OuttakeClaw;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Shoulder;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.VerticalSlide;

public class Outtake {
    public final VerticalSlide verticalSlide;
    public final Shoulder linkage;
    public final OuttakeClaw outtakeClaw;

    public Outtake(DcMotor verticalMotorLeft, DcMotor verticalMotorRight, Servo leftShoulderServo, Servo rightShoulderServo, Servo clawServo) {
        verticalSlide = new VerticalSlide(verticalMotorLeft, verticalMotorRight);
        linkage = new Shoulder(leftShoulderServo, rightShoulderServo);
        outtakeClaw = new OuttakeClaw(clawServo);
    }

    public void init() {
        verticalSlide.init();
        linkage.init();
        outtakeClaw.init();
    }
}