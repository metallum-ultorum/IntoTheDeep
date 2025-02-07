package org.firstinspires.ftc.teamcode.echolocate;

import com.acmerobotics.roadrunner.Pose2d;

import java.util.List;

public class Echolocate {

    public static Pose2d find_one() {
        // TODO logic
        return new Pose2d(0, 0, Math.toRadians(90));
    }

    public static List<Pose2d> find_many() {
        // TODO logic
        return List.of(new Pose2d(0, 0, Math.toRadians(90)),
                new Pose2d(0, 0, Math.toRadians(90)));
    }
}
