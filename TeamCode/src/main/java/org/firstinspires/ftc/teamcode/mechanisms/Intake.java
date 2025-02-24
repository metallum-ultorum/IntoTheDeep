package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.HorizontalSlide;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.IntakeClaw;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Rotator;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Wrist;

public class Intake {
    public final Rotator rotator;
    public final Wrist wrist;
    public final HorizontalSlide horizontalSlide;
    public final IntakeClaw intakeClaw;

    public Intake(Servo clawServo, DcMotor horizontalMotor, Servo rotatorServo, Servo wristServo) {
        intakeClaw = new IntakeClaw(clawServo);
        horizontalSlide = new HorizontalSlide(horizontalMotor);
        rotator = new Rotator(rotatorServo);
        wrist = new Wrist(wristServo);
    }

    public void init() {
        intakeClaw.init();
        horizontalSlide.init();
        rotator.init();
        wrist.init();
    }

    public void reset() {
        intakeClaw.reset();
        horizontalSlide.reset();
        rotator.reset();
        wrist.reset();
    }
}