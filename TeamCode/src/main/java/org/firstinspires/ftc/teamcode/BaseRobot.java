package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.Outtake;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.LinearActuator;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Wrist;
import org.firstinspires.ftc.teamcode.systems.DynamicInput;
import org.firstinspires.ftc.teamcode.systems.Logger;
import org.firstinspires.ftc.teamcode.systems.Odometry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** @noinspection FieldCanBeLocal, unused, RedundantSuppression */
public class BaseRobot {
    public final Map<String, DcMotor> motors = new HashMap<>();
    public final Map<String, Servo> servos = new HashMap<>();
    public final Map<String, Object> sensors = new HashMap<>();
    public final ElapsedTime runtime = new ElapsedTime();
    public final DcMotor frontLeftMotor;
    public final DcMotor frontRightMotor;
    public final DcMotor rearLeftMotor;
    public final DcMotor rearRightMotor;
    public PWMOutput led;
    public final DynamicInput input;
    public final HardwareMap hardwareMap;
    public final LinearOpMode parentOp;

    public final Telemetry telemetry;
    public final Logger logger;
    public Intake intake;
    public Outtake outtake;
    public LinearActuator linearActuator;
    public Odometry odometry;
    public Pause easeTransfer = new Pause(0);

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Core robot class that manages hardware initialization and basic
     * functionality.
     * Serves as the foundation for both autonomous and teleop operations.
     * Key Features:
     * - Hardware mapping and initialization
     * - Motor configuration and control
     * - Mechanism management (arm, odometry, etc.)
     * - Drive system implementation
     */
    public BaseRobot(HardwareMap hardwareMap, DynamicInput input, LinearOpMode parentOp, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.parentOp = parentOp;
        this.input = input;
        this.telemetry = telemetry;
        this.logger = new Logger(this);

        // Initialize and configure the motors
        frontLeftMotor = hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.FRONT_LEFT_MOTOR);
        frontRightMotor = hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.FRONT_RIGHT_MOTOR);
        rearLeftMotor = hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.REAR_LEFT_MOTOR);
        rearRightMotor = hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.REAR_RIGHT_MOTOR);

        // IF A WHEEL IS GOING THE WRONG DIRECTION CHECK WIRING red/black
        frontLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotor.Direction.FORWARD);
        rearLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        rearRightMotor.setDirection(DcMotor.Direction.FORWARD);

        frontLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rearLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rearRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        motors.put(Settings.Hardware.IDs.FRONT_LEFT_MOTOR, frontLeftMotor);
        motors.put(Settings.Hardware.IDs.FRONT_RIGHT_MOTOR, frontRightMotor);
        motors.put(Settings.Hardware.IDs.REAR_LEFT_MOTOR, rearLeftMotor);
        motors.put(Settings.Hardware.IDs.REAR_RIGHT_MOTOR, rearRightMotor);

        if (Settings.Deploy.INTAKE) {
            intake = new Intake(this);
        }

        if (Settings.Deploy.OUTTAKE) {
            outtake = new Outtake(this);
        }

        if (Settings.Deploy.LINEAR_ACTUATOR) {
            linearActuator = new LinearActuator(this);
        }

        if (Settings.Deploy.ODOMETRY) {
            odometry = new Odometry(this);
        }

        if (Settings.Deploy.LED) {
            led = hardwareMap.get(PWMOutput.class, Settings.Hardware.IDs.LED);
        }
    }

    public void shutDown() {
        logger.stop();
        scheduler.shutdown();
    }

    public void driveGamepads() {
        gamepadPrimary();
        gamepadAuxiliary();
    }

    /**
     * Implements mecanum drive calculations and motor control
     * 
     * @param drivePower  Forward/backward power (-1.0 to 1.0)
     * @param strafePower Left/right strafe power (-1.0 to 1.0)
     * @param rotation    Rotational power (-1.0 to 1.0)
     */
    public void mecanumDrive(double drivePower, double strafePower, double rotation) {
        // Adjust the values for strafing and rotation
        strafePower *= Settings.Movement.strafe_power_coefficient;
        double frontLeft = (drivePower + strafePower) * Settings.Movement.flip_movement - rotation;
        double frontRight = (drivePower - strafePower) * Settings.Movement.flip_movement + rotation;
        double rearLeft = (drivePower - strafePower) * Settings.Movement.flip_movement - rotation;
        double rearRight = (drivePower + strafePower) * Settings.Movement.flip_movement + rotation;

        logger.update("FRONT LEFT", String.valueOf(frontLeft));
        logger.update("FRONT RIGHT", String.valueOf(frontRight));
        logger.update("REAR LEFT", String.valueOf(rearLeft));
        logger.update("REAR RIGHT", String.valueOf(rearRight));

        frontLeftMotor.setPower(frontLeft);
        frontRightMotor.setPower(frontRight);
        rearLeftMotor.setPower(rearLeft);
        rearRightMotor.setPower(rearRight);
    }

    /**
     * Processes primary gamepad inputs for robot movement
     * Handles boost/brake modifiers and applies power to drive system
     */
    public void gamepadPrimary() {
        DynamicInput.Movements directions = input.getMovements();
        DynamicInput.Actions actions = input.getActions();

        double boost = actions.boostAmount;
        double brake = actions.brakeAmount;

        double powerMultiplier = 1 + (boost * 2) - brake;
        double rotation = directions.rotation * powerMultiplier;
        double strafePower = directions.x * powerMultiplier;
        double drivePower = directions.y * powerMultiplier;

        logger.update("X", String.valueOf(directions.x));
        logger.update("Y", String.valueOf(directions.y));
        logger.update("strafe", String.valueOf(strafePower));

        /*
         * Drives the motors based on the given power/rotation
         */
        mecanumDrive(drivePower, strafePower, rotation);
    }

    /**
     * Processes auxiliary gamepad inputs for mechanism control
     * Manages arm, claw, and wrist operations
     */
    public void gamepadAuxiliary() {
        DynamicInput.ContextualActions contextualActions = input.getContextualActions();
        if (Settings.Deploy.INTAKE) {

            if (contextualActions.intakeIn) {
                intake.intakeClaw.close();
            } else if (contextualActions.intakeOut) {
                intake.intakeClaw.open();
            }

            if (contextualActions.justWristUp) {
                intake.wrist.cyclePosition();
            } else if (contextualActions.wristDown) {
                intake.wrist.setPosition(Wrist.Position.HORIZONTAL);
            }
            logger.update("freaky?", String.valueOf(input.subSettings.freaky_horizontal));
            if (input.subSettings.freaky_horizontal) {
                if (contextualActions.extendHorizontal) {
                    intake.horizontalSlide.increment();
                } else if (contextualActions.retractHorizontal) {
                    intake.horizontalSlide.decrement();
                }
            } else {
                if (contextualActions.justExtendHorizontal) {
                    intake.horizontalSlide.extend();
                }
                if (contextualActions.justRetractHorizontal) {
                    intake.horizontalSlide.retract();
                }
            }
            intake.rotator.setPosition((contextualActions.rotator/4) + 0.25);
        }

        if (Settings.Deploy.OUTTAKE) {
            if (input.mainSettings.freaky_vertical) {
                if (contextualActions.extendVertical) {
                    outtake.verticalSlide.increment();
                } else if (contextualActions.retractVertical) {
                    outtake.verticalSlide.decrement();
                }
            } else {
                if (contextualActions.justExtendVertical) {
                    outtake.verticalSlide.extend();
                }
                if (contextualActions.justRetractVertical) {
                    outtake.verticalSlide.retract();
                }
            }

            if (contextualActions.justToggleClaw) {
                if (outtake.outtakeClaw.opened && Settings.Movement.easeTransfer) {
                    outtake.outtakeClaw.close();
                    intake.intakeClaw.open();
                }
            }

            if (contextualActions.justShoulderUp) {
                outtake.linkage.cyclePosition();
            }

            if (contextualActions.justFlipMovement) {
                Settings.Movement.flip_movement *= -1;
            }

        }

        if (Settings.Deploy.LINEAR_ACTUATOR) {
            DynamicInput.Actions actions = input.getActions();

            if (actions.linearActuatorExtend) {
                linearActuator.extend();
            }
            if (actions.linearActuatorRetract) {
                linearActuator.retract();
            }
        }

        if (Settings.Deploy.LED) {
            if (input.subCtrl.touchpad_finger_1) {
                led.setPulseWidthOutputTime(1000 + (int) (800 * input.subCtrl.touchpad_finger_1_x));
                led.setPulseWidthPeriod((int) (10000 * input.subCtrl.touchpad_finger_1_y));
            }
        }
    }

    private void setMode(DcMotor.RunMode mode) {
        // Set motor mode for all motors
        for (DcMotor motor : motors.values()) {
            motor.setMode(mode);
        }
    }

    private boolean areMotorsBusy() {
        // Check if any motor is busy
        for (DcMotor motor : motors.values()) {
            if (motor.isBusy()) {
                return true;
            }
        }
        return false;
    }

    private void stopMotors() {
        // Stop all motors
        for (DcMotor motor : motors.values()) {
            motor.setPower(0);
        }
    }

    private class Pause {
        ElapsedTime timestamp;
        long pauseTime;

        Pause(long ms) {
            timestamp = new ElapsedTime();
            pauseTime = ms;
        }

        public boolean shouldResume() {
            return pauseTime >= timestamp.milliseconds();
        }
    }

    public void scheduleTask(Runnable task, long delayMillis) {
        scheduler.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
    }
}