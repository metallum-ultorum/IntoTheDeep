package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mechanisms.MechanismManager;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Shoulder;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.ViperSlide;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Wrist;
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
    private static Path initialDropInBasket;
    private static Path grabSample1;
    private static Path grabSample2;
    private static Path grabSample3;
    private static Path returnSample;
    private static Path park;
    public int samplesScored = 0;
    private Follower follower;
    private MechanismManager mechanisms;
    private Timer pathTimer, actionTimer, opmodeTimer;
    private double actionState;
    private Telemetry visualization;
    /**
     * This is the variable where we store the state of our auto.
     * It is used by the pathUpdate method.
     */
    private int pathState;

    /**
     * Build the paths for the auto (adds, for example, constant/linear headings while doing paths)
     * It is necessary to do this so that all the paths are built before the auto starts.
     **/
    public void buildPaths() {
        initialDropInBasket = new Path(
                // Line 1
                new BezierCurve(
                        new Point(9.757, 84.983, Point.CARTESIAN),
                        new Point(42.362, 79.294, Point.CARTESIAN),
                        new Point(13.800, 130.423, Point.CARTESIAN)
                )
        );
        initialDropInBasket.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-45));

        grabSample1 = new Path(
                new BezierLine(
                        new Point(13.800, 130.423, Point.CARTESIAN),
                        new Point(33.877, 120.891, Point.CARTESIAN)
                )
        );
        grabSample1.setLinearHeadingInterpolation(Math.toRadians(-45), Math.toRadians(0));

        grabSample2 = new Path(
                // Line 4
                new BezierLine(
                        new Point(13.728, 130.423, Point.CARTESIAN),
                        new Point(33.804, 131.723, Point.CARTESIAN)
                )
        );
        grabSample2.setLinearHeadingInterpolation(Math.toRadians(-45), Math.toRadians(0));

        grabSample3 = new Path(
                new BezierLine(
                        new Point(13.728, 130.423, Point.CARTESIAN),
                        new Point(34.960, 131.868, Point.CARTESIAN)
                )
        );
        grabSample3.setLinearHeadingInterpolation(Math.toRadians(-45), Math.toRadians(45));

        returnSample = new Path(
                // Lines 3, 5, 7
                new BezierLine(
                        new Point(33.877, 120.891, Point.CARTESIAN),
                        new Point(13.728, 130.423, Point.CARTESIAN)
                )
        );
        returnSample.setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(-45));

        park = new Path(
                new BezierCurve(
                        new Point(13.728, 130.423, Point.CARTESIAN),
                        new Point(64.749, 129.123, Point.CARTESIAN),
                        new Point(60.272, 96.626, Point.CARTESIAN)
                )
        );
        park.setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(90));
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
                prepDrop();
                follower.followPath(initialDropInBasket);
                setPathState(1);
                break;
            case 1:
                telemetry.addData("VS: ", mechanisms.outtake.verticalSlide.verticalMotorRight.getCurrentPosition()
                        - ViperSlide.VerticalPosition.HIGH_BASKET.getValue());
                double dropTime = 0.1;
                double shoulderMoveTime = 0.7;
                boolean slideFinishedExtending =
                        Math.abs(mechanisms.outtake.verticalSlide.verticalMotorRight.getCurrentPosition()
                                - ViperSlide.VerticalPosition.HIGH_BASKET.getValue()) < 50;
                // wait until the robot finished previous trajectory and is ready to drop
                if (!follower.isBusy() && slideFinishedExtending && actionState == 0) {
                    mechanisms.outtake.moveShoulderToBack();
                    actionState = 1;
                    actionTimer.resetTimer();
                } else if (actionState == 0) {
                    mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.HIGH_BASKET);
                }

                if (actionState == 1) {
                    if (actionTimer.getElapsedTimeSeconds() > shoulderMoveTime) {
                        drop();
                        samplesScored += 1;
                        actionTimer.resetTimer();
                        actionState = 2;
                    }
                }

                if (actionState == 2) {
                    // after giving the robot a smidgen to drop, collapse and start next trajectory
                    if (actionTimer.getElapsedTimeSeconds() > dropTime) {
                        collapse();
                        setPathState(2);
                        actionState = 0;
                        actionTimer.resetTimer();
                        prepGrab();
                        switch (samplesScored) {
                            case 1:
                                follower.followPath(grabSample1);
                                break;
                            case 2:
                                follower.followPath(grabSample2);
                                break;
                            case 3:
                                follower.followPath(grabSample3);
                                break;
                            default:
                                setPathState(3);
                        }
                    }
                }
                break;
            case 2:
                double clawCloseSeconds = 0.1;
                double collapseSeconds = 0.5;
                double transferSeconds = 0.2;
                if (!follower.isBusy() && actionState == 0) {
                    // theoretically, we're on top of the sample with the claw
                    actionState = 1;
                    grab();
                    actionTimer.resetTimer();
                }

                if (actionState == 1) {
                    // once claw is closed, transfer sample from intake to outtake
                    if (actionTimer.getElapsedTimeSeconds() > clawCloseSeconds) {
                        collapse();
                        actionState = 2;
                        actionTimer.resetTimer();
                    }
                }

                if (actionState == 2) {
                    // once the robot spent enough time collapsing, transfer and pathing
                    if (actionTimer.getElapsedTimeSeconds() > collapseSeconds) {
                        transferSample();
                        actionState = 3;
                        actionTimer.resetTimer();
                    }
                }

                if (actionState == 3) {
                    if (actionTimer.getElapsedTimeSeconds() > transferSeconds) {
                        actionState = 0;
                        actionTimer.resetTimer();
                        mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.HIGH_BASKET);
                        follower.followPath(returnSample);
                        setPathState(1);
                    }
                }
                break;
            case 3:
                /* We have placed all presets, so park */
                if (!follower.isBusy()) {
                    follower.followPath(park);
                    actionState = 0;
                    actionTimer.resetTimer();
                    mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.HANG_RUNG_1);
                    setPathState(4);
                }
                break;
            case 4:
                boolean slideDoneExtending =
                        Math.abs(mechanisms.outtake.verticalSlide.verticalMotorRight.getCurrentPosition()
                                - ViperSlide.VerticalPosition.HANG_RUNG_1.getValue()) < 50;
                if (!follower.isBusy() && slideDoneExtending) {
                    // once the slide is done extending, and we are at park position, hold on the bar
                    mechanisms.outtake.moveShoulderToBack();
                    actionState = 0;
                    actionTimer.resetTimer();
                    setPathState(-1); // all done!
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
        telemetry.addData("samples scored", samplesScored);
        telemetry.addLine("action state: " + actionState + " for " + actionTimer.getElapsedTimeSeconds() + "seconds");
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        follower.telemetryDebug(visualization);
        telemetry.update();
    }

    public void prepDrop() {
        mechanisms.outtake.moveShoulderToFront();
        mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.HIGH_BASKET);
    }

    public void drop() {
        mechanisms.outtake.outtakeClaw.open();
    }

    public void collapse() {
        mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.TRANSFER);
        mechanisms.outtake.moveShoulderToFront();
        mechanisms.intake.wrist.setPosition(Wrist.Position.VERTICAL);
        mechanisms.intake.horizontalSlide.setPosition(ViperSlide.HorizontalPosition.COLLAPSED);
        mechanisms.intake.rotator.setPosition(0.5);
        mechanisms.intake.intakeClaw.close();
    }

    public void transferSample() {
        mechanisms.outtake.outtakeClaw.close();
        mechanisms.intake.intakeClaw.open();
    }

    public void prepGrab() {
        mechanisms.outtake.outtakeClaw.open();
        mechanisms.outtake.shoulder.setPosition(Shoulder.Position.PLACE_FORWARD);
        mechanisms.intake.intakeClaw.open();
        mechanisms.intake.rotator.setPosition(0.5);
        mechanisms.intake.horizontalSlide.setPosition(ViperSlide.HorizontalPosition.LEVEL_1);
        mechanisms.intake.wrist.setPosition(Wrist.Position.HORIZONTAL);
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
        follower.setStartingPose(new Pose(9.757, 84.983));
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

