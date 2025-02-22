package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Settings.ControllerProfile;
import org.firstinspires.ftc.teamcode.mechanisms.MechanismManager;
import org.firstinspires.ftc.teamcode.mechanisms.submechanisms.Wrist;
import org.firstinspires.ftc.teamcode.systems.Drivetrain;
import org.firstinspires.ftc.teamcode.systems.DynamicInput;
import org.firstinspires.ftc.teamcode.utils.MenuHelper;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Main TeleOp class for driver-controlled period.
 * Handles controller profile selection and robot operation during matches.
 */
@TeleOp(name = "MainOp", group = "TeleOp")
public class MainOp extends LinearOpMode {

    MechanismManager mechanisms;
    DynamicInput input;
    Drivetrain drivetrain;

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
        AtomicReference<ControllerProfile> mainProfile = new AtomicReference<>(Settings.DEFAULT_PROFILE);
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
        while (opModeIsActive()) {
            gamepadPrimary();
            gamepadAuxiliary();
        }
    }

    public boolean refreshMenu(AtomicReference<ControllerProfile> mainProfile, AtomicReference<ControllerProfile> subProfile,
                               AtomicInteger mainSelection, AtomicInteger subSelection, AtomicBoolean mainConfirmed, AtomicBoolean subConfirmed) {
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
                }
            }
        });

        // Display selections
        telemetry.addLine("\nSelected Profiles:");
        telemetry.addData("Main Controller", mainProfile.get().name + (mainConfirmed.get() ? " (Confirmed)" : ""));
        telemetry.addData("Sub Controller", subProfile.get().name + (subConfirmed.get() ? " (Confirmed)" : ""));

        // Check for menu completion
        if (mainConfirmed.get() && subConfirmed.get()) {
            return true;
        }

        telemetry.update();

        return false;
    }

    public void gamepadPrimary() {
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
            } else if (contextualActions.intakeOut) {
                mechanisms.intake.intakeClaw.open();
            }

            if (contextualActions.justWristUp) {
                mechanisms.intake.wrist.setPosition(Wrist.Position.VERTICAL);
            } else if (contextualActions.wristDown) {
                mechanisms.intake.wrist.setPosition(Wrist.Position.READY);
            }

            if (input.subSettings.freaky_horizontal) {
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
                }
            }
            mechanisms.intake.rotator.setPosition(contextualActions.rotator);
        }

        if (Settings.Deploy.OUTTAKE) {
            if (input.mainSettings.freaky_vertical) {
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
                    if (Settings.Movement.easeTransfer) {
                        mechanisms.intake.intakeClaw.open();
                    }
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

}
