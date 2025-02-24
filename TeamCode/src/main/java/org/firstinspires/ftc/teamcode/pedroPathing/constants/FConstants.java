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

        // ALL BELOW TUNED 02/23
        FollowerConstants.mass = 13.22;

        FollowerConstants.xMovement = 60.75618219853995;
        FollowerConstants.yMovement = 39.57715327145708;

        FollowerConstants.forwardZeroPowerAcceleration = -30;
        FollowerConstants.lateralZeroPowerAcceleration = -61;

        // TODO TUNE BELOW
        FollowerConstants.translationalPIDFCoefficients.setCoefficients(0.1, 0, 0, 0);
        FollowerConstants.useSecondaryTranslationalPID = true;
        FollowerConstants.secondaryTranslationalPIDFCoefficients.setCoefficients(0.3,0,0.01,0); // Not being used, @see useSecondaryTranslationalPID

        FollowerConstants.headingPIDFCoefficients.setCoefficients(2,0,0.105,0);
        FollowerConstants.useSecondaryHeadingPID = false;
        FollowerConstants.secondaryHeadingPIDFCoefficients.setCoefficients(2,0,0.1,0); // Not being used, @see useSecondaryHeadingPID

        FollowerConstants.drivePIDFCoefficients.setCoefficients(0.02, 0, 0.001, 0.6, 0);
        FollowerConstants.useSecondaryDrivePID = true;
        FollowerConstants.secondaryDrivePIDFCoefficients.setCoefficients(0.02,0,0.000079,0.6,0); // Not being used, @see useSecondaryDrivePID

        FollowerConstants.zeroPowerAccelerationMultiplier = 4;
        FollowerConstants.centripetalScaling = 0.00052;

        FollowerConstants.pathEndTimeoutConstraint = 500;
        FollowerConstants.pathEndTValueConstraint = 0.995;
        FollowerConstants.pathEndVelocityConstraint = 0.1;
        FollowerConstants.pathEndTranslationalConstraint = 0.1;
        FollowerConstants.pathEndHeadingConstraint = 0.007;
    }
}
