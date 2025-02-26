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

@Autonomous(name = "Chamber Auto", group = ".Competition Modes", preselectTeleOp = "MainOp")
@Config
public class ChamberPedroAuto extends OpMode {
    public double hpSpecimensPlaced = 0;
    private Follower follower;
    private MechanismManager mechanisms;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private double actionState;
    private Telemetry visualization;
    public static double sample2PushingYOffset = 0;
    /**
     * This is the variable where we store the state of our auto.
     * It is used by the pathUpdate method.
     */
    private int pathState;

    private Path initialPlaceOnChamber, prepSample1, pushSample1, prepSample2,
            pushSample2, prepSample3, pushSample3, initialGrabFromHumanPlayer, placeOnChamber,
            consolidateSpecimens, grabFromHumanPlayer, park;



    /**
     * Build the paths for the auto (adds, for example, constant/linear headings while doing paths)
     * It is necessary to do this so that all the paths are built before the auto starts.
     **/
    public void buildPaths() {
        initialPlaceOnChamber = new Path(
                // Line 1
                new BezierCurve(
                        new Point(10.767, 59.940, Point.CARTESIAN),
                        new Point(31.999, 64.417, Point.CARTESIAN),
                        new Point(39.076, 63.406, Point.CARTESIAN)
                )
        );
        prepSample1 = new Path(
                new BezierCurve(
                        new Point(40.297, 67.884, Point.CARTESIAN),
                        new Point(6.891, 32.677, Point.CARTESIAN),
                        new Point(69.199, 33.631, Point.CARTESIAN),
                        new Point(80.737, 26.162, Point.CARTESIAN),
                        new Point(61.168, 24.500, Point.CARTESIAN)
                )
        );
        prepSample1.setConstantHeadingInterpolation(Math.toRadians(0));

        pushSample1 = new Path(
                // Line 3
                new BezierLine(
                        new Point(61.168, 24.50, Point.CARTESIAN),
                        new Point(21.16653084252758, 23.685, Point.CARTESIAN))
        );
        pushSample1.setConstantHeadingInterpolation(Math.toRadians(0));


        prepSample2 = new Path(
                // Line 4
                new BezierCurve(
                        new Point(17.230, 24.185, Point.CARTESIAN),
                        new Point(70.768, 27.138, Point.CARTESIAN),
                        new Point(60.245, 12.923 + sample2PushingYOffset, Point.CARTESIAN)
                )
        );
        prepSample2.setConstantHeadingInterpolation(Math.toRadians(0));


        pushSample2 = new Path(
                // Line 5
                new BezierLine(
                        new Point(60.245, 12.923 + sample2PushingYOffset, Point.CARTESIAN),
                        new Point(21.16653084252758, 13.292 + sample2PushingYOffset, Point.CARTESIAN)
                )
        );
        pushSample2.setConstantHeadingInterpolation(Math.toRadians(0));

        prepSample3 = new Path(
                new BezierCurve(
                        new Point(17.784, 13.292, Point.CARTESIAN),
                        new Point(77.045, 18.092, Point.CARTESIAN),
                        new Point(60.061, 8.492, Point.CARTESIAN)
                )
        );
        prepSample3.setConstantHeadingInterpolation(Math.toRadians(0));

        pushSample3 = new Path(
                // Line 7
                new BezierLine(
                        new Point(60.164, 10.544, Point.CARTESIAN),
                        new Point(21.16653084252758, 10.544, Point.CARTESIAN)
                )
        );
        pushSample3.setConstantHeadingInterpolation(Math.toRadians(0));

        initialGrabFromHumanPlayer = new Path(
                // Line 8
                new BezierCurve(
                        new Point(17.599, 8.677, Point.CARTESIAN),
                        new Point(46.584, 28.615, Point.CARTESIAN),
                        new Point(10.478, 31.631, Point.CARTESIAN)
                )
        );
        initialGrabFromHumanPlayer.setConstantHeadingInterpolation(Math.toRadians(0));

        placeOnChamber = new Path(
                // Line 9
                new BezierCurve(
                        new Point(10.478, 31.631, Point.CARTESIAN),
                        new Point(16.032, 60.951, Point.CARTESIAN),
                        new Point(40.008, 75.105, Point.CARTESIAN)
                )
        );
        placeOnChamber.setConstantHeadingInterpolation(Math.toRadians(0));
        consolidateSpecimens = new Path(
                // Line 10
                new BezierLine(
                        new Point(40.008, 75.105, Point.CARTESIAN),
                        new Point(39.864, 63.984, Point.CARTESIAN)
                )
        );
        consolidateSpecimens.setConstantHeadingInterpolation(Math.toRadians(0));
        grabFromHumanPlayer = new Path(
                new BezierLine(
                        new Point(39.864, 63.984, Point.CARTESIAN),
                        new Point(10.544, 31.631, Point.CARTESIAN)
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
                follower.followPath(initialPlaceOnChamber);
                setPathState(1);
                break;
            case 1:
                double firstActionLengthSeconds = 1.5; // 1.5s to vertically place specimen
                double secondActionLengthSeconds = 0.2; // .2s to collapse

                if (!follower.isBusy() && actionState == 0) {
                    // once the robot finished previous trajectory for the first time, begin scoring
                    actionState = 1;
                    actionTimer.resetTimer();
                    score();
                }

                if (actionState == 1) {
                    // once the robot spent enough time scoring, collapse
                    if (actionTimer.getElapsedTimeSeconds() > firstActionLengthSeconds) {
                        actionState = 2;
                        actionTimer.resetTimer();
                        collapse();
                    }
                }

                if (actionState == 2) {
                    // once the robot spent enough time collapsing, continue pathing
                    if (actionTimer.getElapsedTimeSeconds() > secondActionLengthSeconds) {
                        actionState = 0;
                        /* Prep and push all the samples in a chain, then prep to grab the first specimen */
                        mechanisms.outtake.moveShoulderToBack(); // so we're ready to grab from hp later
                        follower.followPath(new PathChain(
                                prepSample1, pushSample1,
                                prepSample2, pushSample2,
                                prepSample3, pushSample3,
                                initialGrabFromHumanPlayer
                        ), true);
                        setPathState(2);
                    }
                }
                break;
            case 2:
                double prepLengthSeconds = 0.1; // 3 seconds to get ready to grab specimen
                //noinspection SpellCheckingInspection
                double grabLengthSeconds = 0.1; // half a second to yoinky sploinky

                if (!follower.isBusy() && actionState == 0) {
                    // once the robot finished previous trajectory for the first time, begin g
                    actionState = 1;
                    actionTimer.resetTimer();
                    prepGrab();
                }

                if (actionState == 1) {
                    // once the robot spent enough time scoring, collapse
                    if (actionTimer.getElapsedTimeSeconds() > prepLengthSeconds) {
                        actionState = 2;
                        actionTimer.resetTimer();
                        grab();
                    }
                }

                if (actionState == 2) {
                    // once the robot spent enough time collapsing, continue pathing
                    if (actionTimer.getElapsedTimeSeconds() > grabLengthSeconds) {
                        actionState = 0;
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

                double placementSeconds = 1.5;
                if (actionState == 1 && actionTimer.getElapsedTimeSeconds() >= placementSeconds) {
                    actionState = 0;
                    /* Move all the samples over to make room for more */
                    follower.followPath(consolidateSpecimens);
                    setPathState(4);
                }
                break;
            case 4:
                /* Wait until the specimens are consolidated */
                if (!follower.isBusy()) {
                    /* We're done placing */
                    collapse();

                    if (hpSpecimensPlaced >= 3) {
                        /* There's no more to get from the HP */
                        setPathState(5); // Done with the HP cycle
                    }

                    hpSpecimensPlaced += 1;
                    follower.followPath(grabFromHumanPlayer, true);
                    setPathState(2); // ! Loop back to 2 and place another HP specimen
                }
                break;
            case 5:
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

    public void score() {
        mechanisms.outtake.outtakeClaw.close();
        mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.HIGH_RUNG);
        mechanisms.outtake.moveShoulderToBack();
    }

    public void collapse() {
        mechanisms.outtake.outtakeClaw.open();
        mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.TRANSFER);
        mechanisms.outtake.shoulder.setPosition(Shoulder.Position.PLACE_FORWARD);
    }

    public void prepGrab() {
        mechanisms.outtake.outtakeClaw.open();
    }

    public void grab() {
        mechanisms.outtake.outtakeClaw.close();
        mechanisms.outtake.moveShoulderToFront();
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

