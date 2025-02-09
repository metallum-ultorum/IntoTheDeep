package org.firstinspires.ftc.teamcode.mechanisms;

import org.firstinspires.ftc.teamcode.BaseRobot;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.GeckoWheels;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.HorizontalSlide;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.InnerWrist;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.OuterWrist;

public class Intake {
    public final OuterWrist outerWrist;
    public final InnerWrist innerWrist;
    public final HorizontalSlide horizontalSlide;
    public final GeckoWheels geckoWheels;

    public Intake(BaseRobot baseRobot) {
        geckoWheels = new GeckoWheels(baseRobot);
        horizontalSlide = new HorizontalSlide(baseRobot);
        outerWrist = new OuterWrist(baseRobot);
        innerWrist = new InnerWrist(baseRobot);
    }
}