package org.firstinspires.ftc.teamcode.mechanisms;

import org.firstinspires.ftc.teamcode.BaseRobot;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.IntakeClaw;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Linkage;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.OuttakeClaw;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.VerticalSlide;

public class Outtake {
    public final VerticalSlide verticalSlide;
    public final Linkage linkage;
    public final OuttakeClaw outtakeClaw;

    public Outtake(BaseRobot baseRobot) {
        verticalSlide = new VerticalSlide(baseRobot);
        linkage = new Linkage(baseRobot);
        outtakeClaw = new OuttakeClaw(baseRobot);
    }
}