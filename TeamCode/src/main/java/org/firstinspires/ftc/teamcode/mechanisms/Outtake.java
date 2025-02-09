package org.firstinspires.ftc.teamcode.mechanisms;

import org.firstinspires.ftc.teamcode.BaseRobot;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Claw;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Linkage;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.VerticalSlide;

public class Outtake {
    public final VerticalSlide verticalSlide;
    public final Linkage linkage;
    public final Claw claw;

    public Outtake(BaseRobot baseRobot) {
        verticalSlide = new VerticalSlide(baseRobot);
        linkage = new Linkage(baseRobot);
        claw = new Claw(baseRobot);
    }
}