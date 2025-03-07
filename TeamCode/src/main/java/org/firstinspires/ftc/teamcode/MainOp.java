package org.firstinspires.ftc.teamcode;

import com.pedropathing.localization.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Settings.ControllerProfile;
import org.firstinspires.ftc.teamcode.mechanisms.MechanismManager;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.LimelightManager;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Shoulder;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.ViperSlide;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Wrist;
import org.firstinspires.ftc.teamcode.systems.Drivetrain;
import org.firstinspires.ftc.teamcode.systems.DynamicInput;
import org.firstinspires.ftc.teamcode.utils.MenuHelper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main TeleOp class for driver-controlled period.
 * Handles controller profile selection and robot operation during matches.
 */
@TeleOp(name = "MainOp", group = ".Competition Modes")
public class MainOp extends LinearOpMode {

    MechanismManager mechanisms;
    DynamicInput input;
    Drivetrain drivetrain;
    GoBildaPinpointDriver manualPinpoint;
    boolean CHASSIS_DISABLED = false;
    boolean prevGamepadTriangle;
    LimelightManager.LimelightPipeline pipeline = LimelightManager.LimelightPipeline.BLUE;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    double storedTx;

    /**
     * Main execution flow:
     * 1. Displays controller profile selection menu
     * 2. Initializes robot with selected profiles
     * 3. Runs main control loop for driver operation
     * 4. Handles shutdown when OpMode ends
     */
    @Override
    public void runOpMode() {
        // Show profile selection menu for both controllers
        AtomicReference<ControllerProfile> mainProfile = new AtomicReference<>(Settings.AGNEY_PROFILE);
        AtomicReference<ControllerProfile> subProfile = new AtomicReference<>(Settings.DEFAULT_PROFILE);
        boolean menuConfirmed = false;
        AtomicInteger mainSelection = new AtomicInteger();
        AtomicInteger subSelection = new AtomicInteger();
        AtomicBoolean mainConfirmed = new AtomicBoolean(false);
        AtomicBoolean subConfirmed = new AtomicBoolean(false);

        while (!isStarted() && !isStopRequested() && !menuConfirmed) {
            menuConfirmed = refreshMenu(mainProfile, subProfile, mainSelection, subSelection, mainConfirmed, subConfirmed);
        }

        // Initialize robot systems
        mechanisms = new MechanismManager(hardwareMap);
        input = new DynamicInput(gamepad1, gamepad2, mainProfile.get(), subProfile.get());
        drivetrain = new Drivetrain(hardwareMap);
        // Main loop
        waitForStart();
        mechanisms.init();
        manualPinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        mechanisms.intake.limelight.setCurrentPipeline(pipeline);
        while (opModeIsActive()) {
            manualPinpoint.update();
            gamepadPrimary();
            gamepadAuxiliary();
            checkAutomationConditions();
            checkAssistanceConditions();
            mechanisms.outtake.verticalSlide.checkMotors();
            mechanisms.intake.colorSensor.update();
            telemetry.update();
        }
    }

    public boolean refreshMenu(AtomicReference<ControllerProfile> mainProfile, AtomicReference<ControllerProfile> subProfile,
                               AtomicInteger mainSelection, AtomicInteger subSelection, AtomicBoolean mainConfirmed, AtomicBoolean subConfirmed) {
        telemetry.addLine("\nSelected Profiles:");
        telemetry.addData("Main Controller", mainProfile.get().name + (mainConfirmed.get() ? " (Confirmed)" : ""));
        telemetry.addData("Sub Controller", subProfile.get().name + (subConfirmed.get() ? " (Confirmed)" : ""));
        telemetry.addLine("Limelight Color: " + (pipeline == LimelightManager.LimelightPipeline.BLUE ? "BLUE" : "RED") + ". Press ▲ to switch.");

        // Build options array
        String[] mainOptions = new String[Settings.MAIN_AVAILABLE_PROFILES.length + 1];
        for (int i = 0; i < Settings.MAIN_AVAILABLE_PROFILES.length; i++) {
            mainOptions[i] = Settings.MAIN_AVAILABLE_PROFILES[i].name;
        }
        mainOptions[mainOptions.length - 1] = "Confirm";

        String[] subOptions = new String[Settings.SUB_AVAILABLE_PROFILES.length + 1];
        for (int i = 0; i < Settings.SUB_AVAILABLE_PROFILES.length; i++) {
            subOptions[i] = Settings.SUB_AVAILABLE_PROFILES[i].name;
        }
        subOptions[subOptions.length - 1] = "Confirm";

        // Display menu header
        telemetry.addLine("=== Controller Profile Selection ===");
        // Main Controller Menu
        if (!mainConfirmed.get()) {
            telemetry.addLine("\nMain Controller (Gamepad 1):");
            MenuHelper.displayMenuOptions(telemetry, mainOptions, mainSelection.get());
        }

        // Sub Controller Menu
        if (!subConfirmed.get()) {
            telemetry.addLine("\nSub Controller (Gamepad 2):");
            MenuHelper.displayMenuOptions(telemetry, subOptions, subSelection.get());
        }

        // Handle controller inputs with debounce
        MenuHelper.handleControllerInput(this, gamepad1, !mainConfirmed.get(), () -> {
            if (gamepad1.dpad_up) {
                mainSelection.set((mainSelection.get() - 1 + mainOptions.length) % mainOptions.length);
            } else if (gamepad1.dpad_down) {
                mainSelection.set((mainSelection.get() + 1) % mainOptions.length);
            } else if (gamepad1.a) {
                if (mainSelection.get() < Settings.MAIN_AVAILABLE_PROFILES.length) {
                    mainProfile.set(Settings.MAIN_AVAILABLE_PROFILES[mainSelection.get()]);
                } else {
                    mainConfirmed.set(true);
                    gamepad1.rumble(200);
                }
            }
        });

        MenuHelper.handleControllerInput(this, gamepad2, !subConfirmed.get(), () -> {
            if (gamepad2.dpad_up) {
                subSelection.set((subSelection.get() - 1 + subOptions.length) % subOptions.length);
            } else if (gamepad2.dpad_down) {
                subSelection.set((subSelection.get() + 1) % subOptions.length);
            } else if (gamepad2.a) {
                if (subSelection.get() < Settings.SUB_AVAILABLE_PROFILES.length) {
                    subProfile.set(Settings.SUB_AVAILABLE_PROFILES[subSelection.get()]);
                } else {
                    subConfirmed.set(true);
                    gamepad2.rumble(200);
                }
            }
        });

        if (gamepad1.triangle) {
            if (!prevGamepadTriangle) {
                pipeline = pipeline == LimelightManager.LimelightPipeline.BLUE ? LimelightManager.LimelightPipeline.RED : LimelightManager.LimelightPipeline.BLUE;
                gamepad1.rumble(50);
                gamepad2.rumble(50);
            }
            prevGamepadTriangle = true;
        } else {
            prevGamepadTriangle = false;
        }


        // Set game controller colors based on pipeline
        if (pipeline == LimelightManager.LimelightPipeline.BLUE) {
            gamepad1.setLedColor(0, 0, 255, 1000);
            gamepad2.setLedColor(0, 0, 255, 1000);
        } else {
            gamepad1.setLedColor(255, 0, 0, 1000);
            gamepad2.setLedColor(255, 0, 0, 1000);
        }

        // Check for menu completion
        if (mainConfirmed.get() && subConfirmed.get()) {
            return true;
        }

        telemetry.update();

        return false;
    }

    public void gamepadPrimary() {
        if (CHASSIS_DISABLED) {
            return;
        }
        DynamicInput.Movements directions = input.getMovements();
        DynamicInput.Actions actions = input.getActions();

        double boost = actions.boostAmount;
        double brake = actions.brakeAmount;

        double powerMultiplier = 1 + (boost * 2) - brake;
        double rotation = directions.rotation * powerMultiplier;
        double strafePower = directions.x * powerMultiplier;
        double drivePower = directions.y * powerMultiplier;

        /*
         * Drives the motors based on the given power/rotation
         */
        drivetrain.mecanumDrive(drivePower, strafePower, rotation);
    }

    public void gamepadAuxiliary() {
        DynamicInput.ContextualActions contextualActions = input.getContextualActions();
        if (Settings.Deploy.INTAKE) {

            if (contextualActions.justIntakeIn) {
                mechanisms.intake.intakeClaw.close();
                scheduleTask(() -> mechanisms.intake.wrist.setPosition(Wrist.Position.VERTICAL), 200);
                mechanisms.outtake.outtakeClaw.open();
            } else if (contextualActions.intakeOut) {
                mechanisms.intake.intakeClaw.open();
            }

            if (contextualActions.justWristUp) {
                if (mechanisms.intake.horizontalSlide.currentPosition.getValue() > 30 && mechanisms.intake.intakeClaw.opened) {
                    mechanisms.intake.wrist.setPosition(Wrist.Position.READY);
                } else {
                    mechanisms.intake.wrist.setPosition(Wrist.Position.VERTICAL);
                }
            } else if (contextualActions.wristDown) {
                mechanisms.intake.wrist.setPosition(Wrist.Position.HORIZONTAL);
            }

            if (Settings.DefaultGamepadSettings.incremental_horizontal) {
                if (contextualActions.extendHorizontal) {
                    mechanisms.intake.horizontalSlide.increment();
                } else if (contextualActions.retractHorizontal) {
                    mechanisms.intake.horizontalSlide.decrement();
                }
            } else {
                if (contextualActions.justExtendHorizontal) {
                    mechanisms.intake.horizontalSlide.extend();
                }
                if (contextualActions.justRetractHorizontal) {
                    mechanisms.intake.horizontalSlide.retract();
                    if (mechanisms.intake.horizontalSlide.currentPosition == ViperSlide.HorizontalPosition.COLLAPSED){
                        mechanisms.outtake.outtakeClaw.open();
                        mechanisms.intake.wrist.setPosition(Wrist.Position.VERTICAL);
                    }
                }
            }
            mechanisms.intake.rotator.setPosition(contextualActions.rotator);
        }

        if (Settings.Deploy.OUTTAKE) {
            if (Settings.DefaultGamepadSettings.incremental_vertical) {
                if (mechanisms.outtake.verticalSlide.isTouchingSensor()) {
                    mechanisms.outtake.verticalSlide.increment(1);
                }
                if (contextualActions.justExtendVerticalToBasket) {
                    mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.HIGH_BASKET);
                } else if (contextualActions.justExtendVerticalToChamber) {
                    mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.HIGH_RUNG);
                } else if (contextualActions.justExtendVerticalToChamberPrep) {
                        mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.PREP_HIGH_RUNG);
                } else if (contextualActions.justRetractVerticalToTransfer) {
                    mechanisms.outtake.verticalSlide.setPosition(ViperSlide.VerticalPosition.TRANSFER);
                }
                if (contextualActions.extendVertical) {
                    mechanisms.outtake.verticalSlide.increment();
                } else if (contextualActions.retractVertical) {
                    mechanisms.outtake.verticalSlide.decrement();
                }
            } else {
                if (contextualActions.justExtendVertical) {
                    mechanisms.outtake.verticalSlide.extend();
                }
                if (contextualActions.justRetractVertical) {
                    mechanisms.outtake.verticalSlide.retract();
                }
            }

            if (contextualActions.justToggleClaw) {
                if (mechanisms.outtake.outtakeClaw.opened) {
                    mechanisms.outtake.outtakeClaw.close();
                    scheduleTask(() -> mechanisms.intake.intakeClaw.open(), 400);
                } else {
                    mechanisms.outtake.outtakeClaw.open();
                }
            }

            if (contextualActions.justShoulderUp) {
                mechanisms.outtake.shoulder.cyclePosition();
            }

            if (contextualActions.justFlipMovement) {
                Settings.Movement.flip_movement *= -1;
            }

        }

        if (Settings.Deploy.LINEAR_ACTUATOR) {
            DynamicInput.Actions actions = input.getActions();

            if (actions.linearActuatorExtend) {
                mechanisms.linearActuator.extend();
            }
            if (actions.linearActuatorRetract) {
                mechanisms.linearActuator.retract();
            }
        }
    }

    public void checkAutomationConditions() {
        if (Settings.Movement.easeTransfer) {
            // automatically transfer when everything is collapsed
            if (mechanisms.intake.horizontalSlide.currentPosition.getValue() <=
                    ViperSlide.HorizontalPosition.COLLAPSED.getValue() + 10
                    && mechanisms.outtake.verticalSlide.verticalMotorRight.getCurrentPosition() <=
                    ViperSlide.VerticalPosition.TRANSFER.getValue() + 10
                    && !mechanisms.intake.intakeClaw.opened
                    && mechanisms.outtake.outtakeClaw.clawServo.getPosition() > 0.8
                    && mechanisms.outtake.shoulder.position() == Shoulder.Position.PLACE_BACKWARD) {
                mechanisms.intake.intakeClaw.open();
                scheduleTask(() -> mechanisms.outtake.moveShoulderToBack(), 200);
            }
        }
    }

    public void checkAssistanceConditions() {
        if (mechanisms.intake.limelight.specimenDetected() && Math.abs(manualPinpoint.getHeading()) < 0.3) {
            if (gamepad1.touchpad) {
                gamepad1.setLedColor(0, 0, 255, 1000);
                drivetrain.lerpToOffset(
                        mechanisms.intake.limelight.limelight.getLatestResult().getTx(),
                        Settings.Assistance.approachSpeed,
                        wrappedHeading()
                );
                storedTx = mechanisms.intake.limelight.limelight.getLatestResult().getTx();
                CHASSIS_DISABLED = true;

            } else {
                gamepad1.rumble(50);
                gamepad1.setLedColor(0, 255, 0, 1000);
                CHASSIS_DISABLED = false;
            }
        } else if (CHASSIS_DISABLED && gamepad1.touchpad && storedTx != 0) {
            gamepad1.setLedColor(255, 0, 255, 1000);
            drivetrain.lerpToOffset(
                    mechanisms.intake.limelight.limelight.getLatestResult().getTx(),
                    0.35,
                    wrappedHeading()
            );
        } else {
            storedTx = 0;
            if (gamepad1.touchpad) {
                gamepad1.setLedColor(0, 255, 255, 1000);
                drivetrain.lerpToOffset(
                        0,
                        0,
                        wrappedHeading()
                );
                CHASSIS_DISABLED = true;
            } else {
                gamepad1.setLedColor(255, 0, 0, 1000);
                CHASSIS_DISABLED = false;
            }
        }
        telemetry.addData("limelight tx", mechanisms.intake.limelight.limelight.getLatestResult().getTx());
        telemetry.addData("limelight ty", mechanisms.intake.limelight.limelight.getLatestResult().getTy());
        telemetry.addData("limelight detects specimen?", mechanisms.intake.limelight.specimenDetected());
        telemetry.addData("heading", wrappedHeading());
    }

    public double wrappedHeading() {
        return (manualPinpoint.getHeading() + Math.PI) % (2 * Math.PI) - Math.PI;
    }

    public void scheduleTask(Runnable task, long delayMillis) {
        scheduler.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
    }

}
