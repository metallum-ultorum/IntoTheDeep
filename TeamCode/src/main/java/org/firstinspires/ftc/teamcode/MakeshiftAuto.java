package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

// RR-specific imports
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.AccelConstraint;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ProfileAccelConstraint;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.VelConstraint;
import com.acmerobotics.roadrunner.ftc.Actions;

// Non- RR imports
import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Linkage;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.ViperSlide;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.systems.DynamicInput;

import java.util.Arrays;

@Config
@Autonomous(name = "Fan Inspired", group = "Autonomous")
public class MakeshiftAuto extends LinearOpMode {
    private BaseRobot baseRobot;
    private FtcDashboard dashboard;


    @Override
    public void runOpMode() {
        // Initialize baseRobot
        VelConstraint baseVelConstraint = new MinVelConstraint(Arrays.asList(
                new TranslationalVelConstraint(50.0),
                new AngularVelConstraint(Math.PI / 2)
        ));
        AccelConstraint baseAccelConstraint = new ProfileAccelConstraint(-10.0, 25.0);
        DynamicInput dynamicInput = new DynamicInput(gamepad1, gamepad2,
                Settings.DEFAULT_PROFILE, Settings.DEFAULT_PROFILE);
        baseRobot = new BaseRobot(hardwareMap, dynamicInput, this, telemetry);
        dashboard = FtcDashboard.getInstance();
        baseRobot.outtake.claw.backward();
        Pose2d initialPose = new Pose2d(11.5, -60, Math.toRadians(90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        // vision here that outputs position
        int visionOutputPosition = 1;


        TrajectoryActionBuilder MoveSampleToHumanPlayerZone = drive.actionBuilder(new Pose2d(11.5, -60, Math.toRadians(270))).endTrajectory().fresh()
                .waitSeconds(1)
                .strafeToLinearHeading(new Vector2d(46, -35), Math.toRadians(90))
                .strafeTo(new Vector2d(46,-5))
                .setTangent(270)
                .splineToSplineHeading(new Pose2d(48, -5, Math.toRadians(90)), Math.toRadians(90))
                .strafeTo(new Vector2d(56+8,0+10))
                .strafeTo(new Vector2d(56+8,-50))
                .strafeTo(new Vector2d(52+8,0+10))
                .strafeTo(new Vector2d(62+11,0+10))
                .strafeTo(new Vector2d(62+11,-50));
        TrajectoryActionBuilder tab2 = drive.actionBuilder(initialPose)
                .lineToY(37)
                .setTangent(Math.toRadians(0))
                .lineToX(18)
                .waitSeconds(3)
                .setTangent(Math.toRadians(0))
                .lineToXSplineHeading(46, Math.toRadians(180))
                .waitSeconds(3);
        TrajectoryActionBuilder tab3 = drive.actionBuilder(initialPose)
                .lineToYSplineHeading(33, Math.toRadians(180))
                .waitSeconds(2)
                .strafeTo(new Vector2d(46, 30))
                .waitSeconds(3);
        Action PlaceSample = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(5, -28), Math.toRadians(270))
                .build();
        Action trajectoryActionCloseOut = MoveSampleToHumanPlayerZone.endTrajectory().fresh()
                .strafeTo(new Vector2d(48, 12))
                .build();

        while (!isStopRequested() && !opModeIsActive()) {
            int position = visionOutputPosition;
            telemetry.addData("Position during Init", position);
            telemetry.update();
        }

        int startPosition = visionOutputPosition;
        telemetry.addData("Starting Position", startPosition);
        telemetry.update();
        waitForStart();

        if (isStopRequested()) return;

        Action trajectoryActionChosen;
        if (startPosition == 1) {
            trajectoryActionChosen = MoveSampleToHumanPlayerZone.build();
        } else if (startPosition == 2) {
            trajectoryActionChosen = tab2.build();
        } else {
            trajectoryActionChosen = tab3.build();
        }

        Actions.runBlocking(
                new SequentialAction(
                        PlaceSample,
                        trajectoryActionChosen
//                        ,trajectoryActionCloseOut
                )
        );
    }

    public class HookChamber implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            baseRobot.outtake.claw.forward();
            pause(300);
            baseRobot.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.HIGH_RUNG);
            pause(2000);
            baseRobot.outtake.linkage.setPosition(Linkage.Position.PLACE);
            pause(2000);
            return false;
        }
    }

    public Action hookChamber() {
        return new MakeshiftAuto.HookChamber();
    }

    public class UnhookChamber implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            pause(300);
            baseRobot.outtake.claw.backward();
            pause(300);
            baseRobot.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.TRANSFER);
            baseRobot.outtake.linkage.setPosition(Linkage.Position.TRANSFER);
            return false;
        }
    }

    public Action unhookChamber() {
        return new MakeshiftAuto.UnhookChamber();
    }

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
