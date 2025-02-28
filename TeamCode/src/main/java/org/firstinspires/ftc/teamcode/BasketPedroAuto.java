package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mechanisms.MechanismManager;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Shoulder;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.ViperSlide;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;

/**
 * This is an  auto that showcases movement and control of two servos autonomously.
 * It is a 0+4 (Specimen + Sample) bucket auto. It scores a neutral preload and then pickups 3 samples from the ground and scores them before parking.
 * There are examples of different ways to build paths.
 * A path progression method has been created and can advance based on time, position, or other factors.
 *
 * @author Baron Henderson - 20077 The Indubitables
 * @version 2.0, 11/28/2024
 */

@Autonomous(name = "Basket Auto", group = ".Competition Modes", preselectTeleOp = "MainOp")
@Config
public class BasketPedroAuto extends OpMode {
    public static double[] testOffset = {1.5, 6};
    private Follower follower;
    private MechanismManager mechanisms;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private double actionState;
    private Telemetry visualization;
    private static Path initialPlaceOnChamber;
    /**
     * This is the variable where we store the state of our auto.
     * It is used by the pathUpdate method.
     */
    private int pathState;
    private static Path prepSample1;
    private static Path pushSample1;
    private static Path prepSample2;
    private static Path pushSample2;
    private static Path prepSample3;
    private static Path pushSample3;
    private static Path grabFromHumanPlayer;
    private static Path park;
    public double hpSpecimensPlaced = 0;

    /**
     * Build the paths for the auto (adds, for example, constant/linear headings while doing paths)
     * It is necessary to do this so that all the paths are built before the auto starts.
     **/
    public void buildPaths() {
        initialPlaceOnChamber = new Path(
                // Line 1
                new BezierLine(
                        new Point(10.767, 59.940, Point.CARTESIAN),
                        new Point(40.008, 70.105, Point.CARTESIAN)
                )
        );
        initialPlaceOnChamber.setConstantHeadingInterpolation(Math.toRadians(0));

        prepSample1 = new Path(
                new BezierCurve(
                        new Point(40.297, 67.884, Point.CARTESIAN),
                        new Point(6.891, 32.677, Point.CARTESIAN),
                        new Point(69.199, 32.631, Point.CARTESIAN),
                        new Point(75.737, 26.162, Point.CARTESIAN),
                        new Point(61.168, 24.500, Point.CARTESIAN)
                )
        );
        prepSample1.setConstantHeadingInterpolation(Math.toRadians(0));

        pushSample1 = new Path(
                // Line 3
                new BezierLine(
                        new Point(61.168, 24.50, Point.CARTESIAN),
                        new Point(31.16653084252758, 23.685, Point.CARTESIAN))
        );
        pushSample1.setConstantHeadingInterpolation(Math.toRadians(0));


        prepSample2 = new Path(
                // Line 4
                new BezierCurve(
                        new Point(17.230, 24.185, Point.CARTESIAN),
                        new Point(70.768, 27.138, Point.CARTESIAN),
                        new Point(60.245, 15.923, Point.CARTESIAN)
                )
        );
        prepSample2.setConstantHeadingInterpolation(Math.toRadians(0));


        pushSample2 = new Path(
                // Line 5
                new BezierLine(
                        new Point(60.245, 15.923, Point.CARTESIAN),
                        new Point(31.16653084252758, 16.292, Point.CARTESIAN)
                )
        );
        pushSample2.setConstantHeadingInterpolation(Math.toRadians(0));

        prepSample3 = new Path(
                new BezierCurve(
                        new Point(17.784, 13.292, Point.CARTESIAN),
                        new Point(72.045, 18.092, Point.CARTESIAN),
                        new Point(60.061, 8.492 + 1, Point.CARTESIAN)
                )
        );
        prepSample3.setConstantHeadingInterpolation(Math.toRadians(0));

        pushSample3 = new Path(
                // Line 7
                new BezierCurve(
                        new Point(60.164, 10.544 + 1, Point.CARTESIAN),
                        new Point(14.5, 10.544 + 1, Point.CARTESIAN),
                        new Point(11.5, 10.544 + 3, Point.CARTESIAN)
                )
        );
        pushSample3.setConstantHeadingInterpolation(Math.toRadians(0));


        grabFromHumanPlayer = new Path(
                new BezierLine(
                        new Point(39.864, 63.984, Point.CARTESIAN),
                        new Point(11.544, 31.631, Point.CARTESIAN)
                )
        );
        grabFromHumanPlayer.setConstantHeadingInterpolation(Math.toRadians(0));
    }

    /**
     * This switch is called continuously and runs the pathing, at certain points, it triggers the action state.
     * Everytime the switch changes case, it will reset the timer. (This is because of the setPathState() method)
     * The followPath() function sets the follower to run the specific path, but does NOT wait for it to finish before moving on.
     */
    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                actionState = 0;
                mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.PREP_HIGH_RUNG);
                follower.followPath(initialPlaceOnChamber);
                setPathState(1);
                actionTimer.resetTimer();
                break;
            case 1:
                double firstActionLengthSeconds = 2; // to start vertically placing specimen
                double secondActionLengthSeconds = 0.9; // to vertically place specimen
                double thirdActionLengthSeconds = 0.2; // to collapse

                if (actionTimer.getElapsedTimeSeconds() > firstActionLengthSeconds && actionState == 0) {
                    // once the robot finished previous trajectory for the first time, begin scoring
                    actionState = 1;
                    actionTimer.resetTimer();
                    score();
                }

                if (actionState == 1) {
                    // once the robot spent enough time scoring, collapse
                    if (actionTimer.getElapsedTimeSeconds() > secondActionLengthSeconds) {
                        actionState = 2;
                        actionTimer.resetTimer();
                        collapse();
                    }
                }

                if (actionState == 2) {
                    // once the robot spent enough time collapsing, continue pathing
                    if (actionTimer.getElapsedTimeSeconds() > thirdActionLengthSeconds) {
                        setPathState(2);
                        actionState = 0;
                        /* Prep and push all the samples in a chain, then prep to grab the first specimen */
                        mechanisms.outtake.moveShoulderToBack();
                        prepGrab();
                        follower.followPath(new PathChain(
                                prepSample1, pushSample1,
                                prepSample2, pushSample2,
                                prepSample3, pushSample3
//                                ,initialGrabFromHumanPlayer
                        ), true);
                    }
                }
                break;
            case 2:
                double harsithAlignmentLengthSeconds = 0.1; // 0.1 seconds to give harsith time
                //noinspection SpellCheckingInspection
                double grabLengthSeconds = 0.3; // half a second to yoinky sploinky

                if (!follower.isBusy() && actionState == 0) {
                    // once the robot is in position to grab, wait a bit for harsith to align
                    actionState = 1;
                    actionTimer.resetTimer();
                }

                if (actionState == 1) {
                    // once we have waited a bit, grab from harsith
                    if (actionTimer.getElapsedTimeSeconds() > harsithAlignmentLengthSeconds) {
                        actionState = 2;
                        actionTimer.resetTimer();
                        grab();
                    }
                }

                if (actionState == 2) {
                    // once the robot spent enough time collapsing, continue pathing
                    if (actionTimer.getElapsedTimeSeconds() > grabLengthSeconds) {
                        prepScore();
                        actionState = 0;
                        // Line 9
                        Path placeOnChamber = new Path(
                                // Line 9
                                new BezierCurve(
                                        new Point(11.478, 31.631, Point.CARTESIAN),
                                        new Point(16.032, 60.951, Point.CARTESIAN),
                                        new Point(40.008, 67.105 - (hpSpecimensPlaced * 2), Point.CARTESIAN)
                                )
                        );
                        placeOnChamber.setConstantHeadingInterpolation(Math.toRadians(0));
                        follower.followPath(placeOnChamber);
                        setPathState(3);
                    }
                }
                break;
            case 3:
                /* Wait until we are in position to score */
                if (!follower.isBusy() && actionState == 0) {
                    /* Score Sample */
                    score();
                    actionTimer.resetTimer();
                    actionState = 1;
                }

                double placementSeconds = 0.5;
                if (actionState == 1 && actionTimer.getElapsedTimeSeconds() >= placementSeconds) {
                    actionState = 0;
                    collapse();

                    if (hpSpecimensPlaced >= 3) {
                        /* There's no more to get from the HP */
                        setPathState(4); // Done with the HP cycle
                    }

                    hpSpecimensPlaced += 1;
                    prepGrab();
                    actionState = 0;
                    setPathState(2); // ! Loop back to 2 and place another HP specimen
                    follower.followPath(grabFromHumanPlayer, true);
                }
                break;
            case 4:
                /* We have placed all specimens from the HP, so now either park or do echolocate */
                if (!follower.isBusy()) {
                    if (Settings.Autonomous.ECHOLOCATE_ENABLED) {
                        setPathState(1000); // TODO
                    } else {
                        follower.followPath(park);
                    }
                    setPathState(-1);
                }
                break;
        }
    }

    /**
     * These change the states of the paths and actions
     * It will also reset the timers of the individual switches
     **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    /**
     * This is the main loop of the OpMode, it will run repeatedly after clicking "Play".
     **/
    @Override
    public void loop() {
        mechanisms.outtake.verticalSlide.checkMotors();
        // These loop the movements of the robot
        follower.update();
        autonomousPathUpdate();

        // Feedback to Driver Hub
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        follower.telemetryDebug(visualization);
        telemetry.update();
    }

    public void prepScore() {
        mechanisms.outtake.moveShoulderToFront();
        mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.PREP_HIGH_RUNG);
    }

    public void score() {
        mechanisms.outtake.outtakeClaw.close();
        mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.HIGH_RUNG.getValue() + 150);
        mechanisms.outtake.moveShoulderToBack();
    }

    public void collapse() {
        mechanisms.outtake.outtakeClaw.open();
        mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.TRANSFER);
        mechanisms.outtake.shoulder.setPosition(Shoulder.Position.PLACE_FORWARD);
    }

    public void prepGrab() {
        mechanisms.outtake.outtakeClaw.open();
        mechanisms.outtake.shoulder.setPosition(Shoulder.Position.PLACE_BACKWARD);
    }

    public void grab() {
        mechanisms.outtake.outtakeClaw.close();
    }


    /**
     * This method is called once at the init of the OpMode.
     **/
    @Override
    public void init() {
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        actionTimer = new Timer();
        opmodeTimer.resetTimer();
        mechanisms = new MechanismManager(hardwareMap);

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);
        follower.setStartingPose(new Pose(10.767, 59.940));
        buildPaths();
        visualization = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        mechanisms.intake.horizontalSlide.reset();
        mechanisms.outtake.verticalSlide.reset();
    }

    /**
     * This method is called continuously after Init while waiting for "play".
     **/
    @Override
    public void init_loop() {
    }

    /**
     * This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system
     **/
    @Override
    public void start() {
        mechanisms.reset();
        mechanisms.init();
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    /**
     * We do not use this because everything should automatically disable
     **/
    @Override
    public void stop() {
    }
}

