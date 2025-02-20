package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.utils.MenuHelper;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/** @noinspection unused */
@Config
@TeleOp(name = "Testing Suite", group = "TeleOp")
public class TestingSuite extends LinearOpMode {
    // Please Update
    private static final String[] MOTOR_OPTIONS = {
            Settings.Hardware.IDs.FRONT_LEFT_MOTOR,
            Settings.Hardware.IDs.FRONT_RIGHT_MOTOR,
            Settings.Hardware.IDs.REAR_LEFT_MOTOR,
            Settings.Hardware.IDs.REAR_RIGHT_MOTOR,
            // Settings.Hardware.IDs.LINEAR_ACTUATOR,
            Settings.Hardware.IDs.SLIDE_VERTICAL_LEFT,
            Settings.Hardware.IDs.SLIDE_VERTICAL_RIGHT,
            Settings.Hardware.IDs.SLIDE_HORIZONTAL,
    };

    private static final String[] SERVO_OPTIONS = {
            Settings.Hardware.IDs.GECKO_LEFT,
            Settings.Hardware.IDs.GECKO_RIGHT,
            Settings.Hardware.IDs.WRIST_LEFT,
            Settings.Hardware.IDs.WRIST_RIGHT,
            Settings.Hardware.IDs.OUTER_WRIST,
            Settings.Hardware.IDs.LEFT_SHOULDER,
            Settings.Hardware.IDs.RIGHT_SHOULDER,
            Settings.Hardware.IDs.INTAKE_CLAW,
    };


    private static final String[] LIST_OPTIONS = Stream.concat(Arrays.stream(MOTOR_OPTIONS),
            Arrays.stream(SERVO_OPTIONS)).toArray(String[]::new);

    @Override
    public void runOpMode() {
        AtomicBoolean menuActive = new AtomicBoolean(true);
        AtomicInteger listSelection = new AtomicInteger(0);
        AtomicBoolean listConfirmed = new AtomicBoolean(false);
        final String[] selectedMotor = new String[1];
        final String[] selectedServo = new String[1];
        AtomicBoolean isMotor = new AtomicBoolean(true);
        AtomicBoolean continuousMode = new AtomicBoolean(false);

        while (opModeIsActive() || !isStopRequested() && menuActive.get()) {
            telemetry.addLine("=== Motor/Servo Testing Selection ===");

            if (!listConfirmed.get()) {
                telemetry.addLine("\nSelect Motor to Test:");
                MenuHelper.displayMenuOptions(telemetry, LIST_OPTIONS, listSelection.get());
            }

            MenuHelper.handleControllerInput(this, gamepad1, !listConfirmed.get(), () -> {
                if (gamepad1.dpad_up) {
                    listSelection.set((listSelection.get() - 1 + LIST_OPTIONS.length) % LIST_OPTIONS.length);
                } else if (gamepad1.dpad_down) {
                    listSelection.set((listSelection.get() + 1) % LIST_OPTIONS.length);
                } else if (gamepad1.a) {
                    if (listSelection.get() < MOTOR_OPTIONS.length) {
                        selectedMotor[0] = LIST_OPTIONS[listSelection.get()];
                        isMotor.set(true);
                    } else {
                        selectedServo[0] = LIST_OPTIONS[listSelection.get()];
                        isMotor.set(false);
                    }
                    listConfirmed.set(true);
                    menuActive.set(false);
                }
            });

            telemetry.addLine("\nSelected Item:");
            if (listSelection.get() < MOTOR_OPTIONS.length) {
                telemetry.addData("Motor",
                        MOTOR_OPTIONS[listSelection.get()] + (listConfirmed.get() ? " (Confirmed)" : ""));
            } else {
                telemetry.addData("Servo", SERVO_OPTIONS[listSelection.get() - MOTOR_OPTIONS.length]
                        + (listConfirmed.get() ? " (Confirmed)" : ""));
            }

            telemetry.update();

            if (!menuActive.get()) {
                if (isMotor.get()) {
                    DcMotor testMotor = hardwareMap.get(DcMotor.class, selectedMotor[0]);
                    testMotor.setDirection(DcMotor.Direction.FORWARD);
                    testMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    waitForStart();
                    while (opModeIsActive()) {
                        if (gamepad1.y) {
                            menuActive.set(true);
                            break;
                        }
                        if (gamepad1.b) {
                            testMotor.setPower(0);
                        }

                        float power = -gamepad1.left_trigger + gamepad1.right_trigger;
                        testMotor.setPower(power);

                        telemetry.addData("Testing Motor", MOTOR_OPTIONS[listSelection.get()]);
                        telemetry.addData("Controls", "LT/RT = power | B = stop | Y = menu");
                        telemetry.addData("Power", "%.2f", power);
                        telemetry.addData("Position", testMotor.getCurrentPosition());
                        telemetry.update();
                    }
                } else {
                    Servo testServo = hardwareMap.get(Servo.class, selectedServo[0]);
                    testServo.setDirection(Servo.Direction.FORWARD);
                    boolean ltLastClicked = false;
                    boolean rtLastClicked = false;
                    boolean fineControl = true;
                    double position = 0.5; // Start at middle position
                    testServo.setPosition(position);

                    waitForStart();
                    while (opModeIsActive()) {
                        if (gamepad1.y) {
                            menuActive.set(true);
                            break;
                        }
                        if (gamepad1.x) {
                            fineControl = !fineControl;
                        }
                        if (gamepad1.b) {
                            position = 0.5;
                            testServo.setPosition(position);
                        }

                        continuousMode.set(selectedServo[0].equals(Settings.Hardware.IDs.GECKO_LEFT) ||
                                selectedServo[0].equals(Settings.Hardware.IDs.GECKO_RIGHT));

                        double step = fineControl ? 0.05 : 0.2;

                        if (continuousMode.get()) {
                            if (gamepad1.left_trigger > 0.5) {
                                testServo.setPosition(1); // Full speed forward
                            } else if (gamepad1.right_trigger > 0.5) {
                                testServo.setPosition(0); // Full speed backward
                            } else {
                                testServo.setPosition(0.5); // Stop
                            }
                        } else {
                            if (gamepad1.left_trigger > 0.5 && !ltLastClicked && position > 0) {
                                ltLastClicked = true;
                                position = Math.max(0, position - step);
                            } else if (gamepad1.left_trigger <= 0.5) {
                                ltLastClicked = false;
                            }
                            if (gamepad1.right_trigger > 0.5 && !rtLastClicked && position < 1) {
                                rtLastClicked = true;
                                position = Math.min(1, position + step);
                            } else if (gamepad1.right_trigger <= 0.5) {
                                rtLastClicked = false;
                            }
                            testServo.setPosition(position);
                        }

                        telemetry.addData("Testing Servo", SERVO_OPTIONS[listSelection.get() - MOTOR_OPTIONS.length]);
                        telemetry.addData("Mode",
                                continuousMode.get() ? "Continuous" : (fineControl ? "Fine (0.05)" : "Coarse (0.2)"));
                        telemetry.addData("Controls", "LT/RT = move | X = toggle fine | B = center | Y = menu");
                        telemetry.addData("Target Position", "%.3f", position);
                        telemetry.addData("Current Position", "%.3f", testServo.getPosition());
                        telemetry.update();
                    }
                }

                listConfirmed.set(false);
                menuActive.set(true);
            }
        }
    }
}
