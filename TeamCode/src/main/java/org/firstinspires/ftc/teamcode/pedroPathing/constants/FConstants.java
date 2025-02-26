package org.firstinspires.ftc.teamcode.pedroPathing.constants;

import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.localization.Localizers;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Settings;

public class FConstants {
    static {
        FollowerConstants.localizers = Localizers.PINPOINT;

        FollowerConstants.leftFrontMotorName = Settings.Hardware.IDs.FRONT_LEFT_MOTOR;
        FollowerConstants.leftRearMotorName = Settings.Hardware.IDs.REAR_LEFT_MOTOR;
        FollowerConstants.rightFrontMotorName = Settings.Hardware.IDs.FRONT_RIGHT_MOTOR;
        FollowerConstants.rightRearMotorName = Settings.Hardware.IDs.REAR_RIGHT_MOTOR;

        FollowerConstants.leftFrontMotorDirection = DcMotorSimple.Direction.REVERSE;
        FollowerConstants.leftRearMotorDirection = DcMotorSimple.Direction.FORWARD;
        FollowerConstants.rightFrontMotorDirection = DcMotorSimple.Direction.REVERSE;
        FollowerConstants.rightRearMotorDirection = DcMotorSimple.Direction.REVERSE;

        // ALL BELOW TUNED 02/25
        FollowerConstants.mass = 13.22;

        FollowerConstants.xMovement = 60.466955015836035;
        FollowerConstants.yMovement = 48.59584683823521;

        FollowerConstants.forwardZeroPowerAcceleration = -35.30470204746488;
        FollowerConstants.lateralZeroPowerAcceleration = -70.46739169276297;

        // ALL TUNED 02/25
        FollowerConstants.translationalPIDFCoefficients.setCoefficients(4, 0.3, 0.1, 2);
        FollowerConstants.translationalPIDFFeedForward = 5;
        FollowerConstants.useSecondaryTranslationalPID = true;
        FollowerConstants.secondaryTranslationalPIDFCoefficients.setCoefficients(0.15, 0, 0.025, 0.0);
        FollowerConstants.secondaryTranslationalPIDFFeedForward = 0.02;

        FollowerConstants.headingPIDFCoefficients.setCoefficients(2,0,0.105,0);
        FollowerConstants.headingPIDFFeedForward = 0.03;
        FollowerConstants.useSecondaryHeadingPID = true;
        FollowerConstants.secondaryHeadingPIDFCoefficients.setCoefficients(2.0, 0, 0.15, 0);
        FollowerConstants.secondaryHeadingPIDFFeedForward = 0.015;

        FollowerConstants.drivePIDFCoefficients.setCoefficients(0.025, 0.0, 0.003, 0.0, 0);
        FollowerConstants.drivePIDFFeedForward = 0.3;
        FollowerConstants.useSecondaryDrivePID = true;
        FollowerConstants.secondaryDrivePIDFCoefficients.setCoefficients(0.02, 0, 0.0001, 0.6, 0);
        FollowerConstants.secondaryDrivePIDFFeedForward = 0.01;

        FollowerConstants.zeroPowerAccelerationMultiplier = 0.5;
        FollowerConstants.centripetalScaling = 0.01;

        FollowerConstants.pathEndTimeoutConstraint = 500;
        FollowerConstants.pathEndTValueConstraint = 0.995;
        FollowerConstants.pathEndVelocityConstraint = 0.2;
        FollowerConstants.pathEndTranslationalConstraint = 0.1;
        FollowerConstants.pathEndHeadingConstraint = 0.007;
    }
}
