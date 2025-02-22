package org.firstinspires.ftc.teamcode.mechanisms;

import org.firstinspires.ftc.teamcode.BaseRobot;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.OuttakeClaw;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Shoulder;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.VerticalSlide;

public class Outtake {
    public final VerticalSlide verticalSlide;
    public final Shoulder linkage;
    public final OuttakeClaw outtakeClaw;

    public Outtake(BaseRobot baseRobot) {
        verticalSlide = new VerticalSlide(baseRobot);
        linkage = new Shoulder(baseRobot);
        outtakeClaw = new OuttakeClaw(baseRobot);
    }
}