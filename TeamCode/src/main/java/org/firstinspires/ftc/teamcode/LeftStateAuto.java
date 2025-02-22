package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Settings.Autonomous.FieldPositions.BASKET_POSE;
import static org.firstinspires.ftc.teamcode.Settings.Autonomous.FieldPositions.LEFT_SAMPLE_1_VEC;
import static org.firstinspires.ftc.teamcode.Settings.Autonomous.FieldPositions.LEFT_SAMPLE_2_VEC;
import static org.firstinspires.ftc.teamcode.Settings.Autonomous.FieldPositions.LEFT_SAMPLE_3_VEC;
import static org.firstinspires.ftc.teamcode.Settings.Autonomous.speedyAccel;
import static org.firstinspires.ftc.teamcode.Settings.Autonomous.speedyVel;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Linkage;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.ViperSlide;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Wrist;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.systems.AdaptiveCalibration;
import org.firstinspires.ftc.teamcode.systems.DynamicInput;

@Autonomous(name = "Left State Auto", group = "Autonomous")
public class LeftStateAuto extends LinearOpMode {
    private BaseRobot baseRobot;
    private MecanumDrive roadRunner;
    private Pose2d initialPose;
    public AdaptiveCalibration adaptiveCalibration;

    @Override
    public void runOpMode() {
        adaptiveCalibration = AdaptiveCalibration.getInstance();
        DynamicInput dynamicInput = new DynamicInput(gamepad1, gamepad2,
                Settings.DEFAULT_PROFILE, Settings.DEFAULT_PROFILE);
        baseRobot = new BaseRobot(hardwareMap, dynamicInput, this, telemetry);

        initialPose = Settings.Autonomous.FieldPositions.LEFT_INITIAL_POSE;
        roadRunner = new MecanumDrive(hardwareMap, initialPose);
        adaptiveCalibration.initialize(roadRunner);

        telemetry.addData("Status", "Ready to start on the left side");
        telemetry.update();
        waitForStart();

        run();
    }

    public void run() {
        baseRobot.intake.wrist.setPosition(Wrist.Position.VERTICAL);
        TrajectoryActionBuilder previousChamberTrajectory = gameLoopSetup();
        int samplesTaken = 0;
        while (samplesTaken <= 3) {
            adaptiveCalibration.calibrateRuntime(new AdaptiveCalibration.RuntimeCalibrationPayload(), roadRunner);
            previousChamberTrajectory = placeLoop(previousChamberTrajectory, samplesTaken);
            samplesTaken++;
        }
        baseRobot.logger.update("Autonomous phase", "Parking");
        gameLoopEnd(previousChamberTrajectory);
        baseRobot.logger.update("Autonomous phase", "Victory is ours");
    }

    public TrajectoryActionBuilder gameLoopSetup() {
        baseRobot.logger.update("Autonomous phase", "Placing initial specimen on chamber");
        TrajectoryActionBuilder placingTrajectory = getPlacingTrajectory(roadRunner.actionBuilder(initialPose));
        baseRobot.outtake.outtakeClaw.close();
        baseRobot.outtake.verticalSlide.setPosition(Settings.Hardware.VerticalSlide.HIGH_RUNG_PREP_AUTO);
        baseRobot.outtake.linkage.setPosition(Linkage.Position.PLACE_BACKWARD);

        Actions.runBlocking(
                new SequentialAction(
                        placingTrajectory.build(),
                        placeBasket()));

        return placingTrajectory;
    }

    public TrajectoryActionBuilder placeLoop(TrajectoryActionBuilder previousTrajectory, int samplesTaken) {
        baseRobot.telemetry.addData("Autonomous phase", "Grabbing next sample");
        baseRobot.telemetry.update();
        baseRobot.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.TRANSFER);
        baseRobot.outtake.linkage.setPosition(Linkage.Position.PLACE_BACKWARD);
        previousTrajectory = getNextSample(previousTrajectory, samplesTaken);
        baseRobot.logger.update("Autonomous phase", "Getting next sample");
        previousTrajectory = placeSample(previousTrajectory);
        return previousTrajectory;
    }

    public TrajectoryActionBuilder gameLoopEnd(TrajectoryActionBuilder previousPose) {
        TrajectoryActionBuilder parkingTrajectory = getParkingTrajectory(previousPose);

        Actions.runBlocking(
                new SequentialAction(
                        parkingTrajectory.build()));

        return parkingTrajectory;
    }

    public TrajectoryActionBuilder placeSample(TrajectoryActionBuilder previousTrajectory) {
        baseRobot.outtake.linkage.setPosition(Linkage.Position.PLACE_FORWARD);
        TrajectoryActionBuilder placingTrajectory = getPlacingTrajectory(previousTrajectory);
        Actions.runBlocking(
                new SequentialAction(
                        placingTrajectory.build(),
                        placeBasket()
                ));
        return placingTrajectory;
    }


    private TrajectoryActionBuilder getParkingTrajectory(TrajectoryActionBuilder previousTrajectory) {
        return previousTrajectory.endTrajectory().fresh()
                .strafeTo(Settings.Autonomous.FieldPositions.LEFT_PARK_POSE.position);
    }

    private Vector2d vectorize(Pose2d pose) {
        return new Vector2d(pose.position.x, pose.position.y);

    }

    private TrajectoryActionBuilder getPlacingTrajectory(TrajectoryActionBuilder previousTrajectory) {
        return previousTrajectory.endTrajectory().fresh()
                .strafeToLinearHeading(vectorize(BASKET_POSE), BASKET_POSE.heading, speedyVel, speedyAccel);
    }

    private TrajectoryActionBuilder getNextSample(TrajectoryActionBuilder previousTrajectory, int samplesTaken) {
        TrajectoryActionBuilder nextSampleTrajectory;
        switch (samplesTaken) {
            case 0:
                nextSampleTrajectory = previousTrajectory.endTrajectory().fresh()
                        .splineToConstantHeading(LEFT_SAMPLE_1_VEC, Math.toRadians(90))
                        .turnTo(Math.toRadians(90));
                break;
            case 1:
                nextSampleTrajectory = previousTrajectory.endTrajectory().fresh()
                        .splineToConstantHeading(LEFT_SAMPLE_2_VEC, Math.toRadians(90))
                        .turnTo(Math.toRadians(90));
                break;
            case 2:
            default:
                nextSampleTrajectory = previousTrajectory.endTrajectory().fresh()
                        .splineToConstantHeading(LEFT_SAMPLE_3_VEC, Math.toRadians(90))
                        .turnTo(Math.toRadians(90));
        }

        Actions.runBlocking(new SequentialAction(nextSampleTrajectory.build(), loadSample()));

        return nextSampleTrajectory;
    }

    public Action placeBasket() {
        return new PlaceBasket();
    }

    public class PlaceBasket implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            baseRobot.outtake.linkage.setPosition(Linkage.Position.PLACE_FORWARD);
            baseRobot.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.HIGH_BASKET);
            pause(1000);
            baseRobot.outtake.linkage.setPosition(Linkage.Position.PLACE_BACKWARD);
            pause(500);
            baseRobot.outtake.outtakeClaw.open();
            pause(100);
            baseRobot.outtake.linkage.setPosition(Linkage.Position.PLACE_FORWARD);
            pause(300);
            return false;
        }
    }


    public Action loadSample() {
        return new LoadSample();
    }

    public class LoadSample implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket packet) {
            baseRobot.intake.intakeClaw.close();
            baseRobot.intake.horizontalSlide.setPosition(ViperSlide.HorizontalPosition.EXPANDED);
            sleep(500);
            baseRobot.intake.horizontalSlide.setPosition(ViperSlide.HorizontalPosition.COLLAPSED);
            baseRobot.intake.wrist.setPosition(Wrist.Position.VERTICAL);
            sleep(300);
            baseRobot.outtake.linkage.setPosition(Linkage.Position.TRANSFER);
            baseRobot.outtake.outtakeClaw.open();
            sleep(200);
            baseRobot.outtake.outtakeClaw.close();
            sleep(100);
            baseRobot.intake.intakeClaw.open();
            baseRobot.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.HIGH_BASKET);
            sleep(100);
            baseRobot.intake.wrist.setPosition(Wrist.Position.HORIZONTAL);
            return false;
        }
    }

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public enum PlacementHeight {
        CHAMBER_LOW,
        CHAMBER_HIGH
    }
}