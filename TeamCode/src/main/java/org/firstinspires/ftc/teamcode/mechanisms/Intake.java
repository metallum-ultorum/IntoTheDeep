package org.firstinspires.ftc.teamcode.mechanisms;

import org.firstinspires.ftc.teamcode.BaseRobot;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.HorizontalSlide;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.IntakeClaw;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Rotator;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Wrist;

public class Intake {
    public final Rotator rotator;
    public final Wrist wrist;
    public final HorizontalSlide horizontalSlide;
    public final IntakeClaw intakeClaw;

    public Intake(BaseRobot baseRobot) {
        intakeClaw = new IntakeClaw(baseRobot);
        horizontalSlide = new HorizontalSlide(baseRobot);
        rotator = new Rotator(baseRobot);
        wrist = new Wrist(baseRobot);
    }
}