package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.utils.MenuHelper;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/** @noinspection unused */
@Config
@TeleOp(name = "Testing Suite", group = "TeleOp")
public class TestingSuite extends LinearOpMode {
    private static final String[] MOTOR_OPTIONS = {
            Settings.Hardware.IDs.FRONT_LEFT_MOTOR,
            Settings.Hardware.IDs.FRONT_RIGHT_MOTOR,
            Settings.Hardware.IDs.REAR_LEFT_MOTOR,
            Settings.Hardware.IDs.REAR_RIGHT_MOTOR,
            Settings.Hardware.IDs.SLIDE_VERTICAL_LEFT,
            Settings.Hardware.IDs.SLIDE_VERTICAL_RIGHT,
            Settings.Hardware.IDs.SLIDE_HORIZONTAL,
    };

    private static final String[] SERVO_OPTIONS = {
            Settings.Hardware.IDs.WRIST,
            Settings.Hardware.IDs.ROTATOR,
            Settings.Hardware.IDs.LEFT_SHOULDER,
            Settings.Hardware.IDs.RIGHT_SHOULDER,
            Settings.Hardware.IDs.INTAKE_CLAW,
            Settings.Hardware.IDs.OUTTAKE_CLAW,
    };

    private static final String[] DUAL_MOTOR_OPTIONS = {
            "DUAL_MOTOR_SLIDE_VERTICAL",
    };

    private static final String[] DUAL_SERVO_OPTIONS = {
            "DUAL_SERVO_SHOULDERS",
    };
    private static final String[] SENSOR_OPTIONS = {
            Settings.Hardware.IDs.SLIDE_VERTICAL_TOUCH_SENSOR,
            Settings.Hardware.IDs.LIMELIGHT + " yellow detection",
            Settings.Hardware.IDs.LIMELIGHT + " red detection",
            Settings.Hardware.IDs.LIMELIGHT + " blue detection",
            Settings.Hardware.IDs.COLOR_SENSOR,
    };

    private static final String[] LIST_OPTIONS = Stream.concat(Stream.concat(Arrays.stream(MOTOR_OPTIONS),
            Arrays.stream(DUAL_MOTOR_OPTIONS)), Stream.concat(Stream.concat(Arrays.stream(SERVO_OPTIONS),
            Arrays.stream(DUAL_SERVO_OPTIONS)), Arrays.stream(SENSOR_OPTIONS))).toArray(String[]::new);

    @Override
    public void runOpMode() {
        AtomicBoolean menuActive = new AtomicBoolean(true);
        AtomicInteger listSelection = new AtomicInteger(0);
        AtomicBoolean listConfirmed = new AtomicBoolean(false);
        final String[] selectedItem = new String[1];
        AtomicBoolean isMotor = new AtomicBoolean(true);
        AtomicBoolean isServo = new AtomicBoolean(false);


        while (opModeIsActive() || !isStopRequested() && menuActive.get()) {
            telemetry.addLine("=== Motor/Servo Testing Selection ===");
            if (!listConfirmed.get()) {
                telemetry.addLine("\nSelect Motor/Servo to Test:");
                MenuHelper.displayMenuOptions(telemetry, LIST_OPTIONS, listSelection.get());
            }

            MenuHelper.handleControllerInput(this, gamepad1, !listConfirmed.get(), () -> {
                if (gamepad1.dpad_up) {
                    listSelection.set((listSelection.get() - 1 + LIST_OPTIONS.length) % LIST_OPTIONS.length);
                } else if (gamepad1.dpad_down) {
                    listSelection.set((listSelection.get() + 1) % LIST_OPTIONS.length);
                } else if (gamepad1.a) {
                    selectedItem[0] = LIST_OPTIONS[listSelection.get()];
                    isMotor.set(listSelection.get() < MOTOR_OPTIONS.length + DUAL_MOTOR_OPTIONS.length);
                    isServo.set(listSelection.get() >= MOTOR_OPTIONS.length + DUAL_MOTOR_OPTIONS.length && listSelection.get() < MOTOR_OPTIONS.length + DUAL_MOTOR_OPTIONS.length + SERVO_OPTIONS.length + DUAL_SERVO_OPTIONS.length);
                    listConfirmed.set(true);
                    menuActive.set(false);
                }
            });

            telemetry.update();

            if (!menuActive.get()) {
                if (isMotor.get()) {
                    if (selectedItem[0].equals("DUAL_MOTOR_SLIDE_VERTICAL")) {
                        DcMotor motor1 = hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.SLIDE_VERTICAL_LEFT);
                        DcMotor motor2 = hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.SLIDE_VERTICAL_RIGHT);
                        motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        motor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        motor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                        motor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                        motor2.setDirection(DcMotorSimple.Direction.REVERSE);
                        waitForStart();
                        while (opModeIsActive()) {
                            float power = -gamepad1.left_trigger - gamepad2.left_trigger + gamepad1.right_trigger + gamepad2.right_trigger;
                            motor1.setPower(power);
                            motor2.setPower(power);
                            telemetry.addData("Dual Motor Power", "%.2f", power);
                            telemetry.addData("Left Motor Position", motor1.getCurrentPosition());
                            telemetry.addData("Right Motor Position", motor2.getCurrentPosition());

                            telemetry.update();
                        }
                    } else {
                        DcMotor testMotor = hardwareMap.get(DcMotor.class, selectedItem[0]);
                        testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        testMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                        waitForStart();
                        while (opModeIsActive()) {
                            float power = -gamepad1.left_trigger - gamepad2.left_trigger + gamepad1.right_trigger + gamepad2.right_trigger;
                            testMotor.setPower(power);
                            telemetry.addData("Motor Power", "%.2f", power);
                            telemetry.addData("Motor Position", testMotor.getCurrentPosition());
                            telemetry.update();
                        }
                    }
                } else if (isServo.get()) {
                    if (selectedItem[0].equals("DUAL_SERVO_SHOULDERS")) {
                        Servo servo1 = hardwareMap.get(Servo.class, Settings.Hardware.IDs.LEFT_SHOULDER);
                        Servo servo2 = hardwareMap.get(Servo.class, Settings.Hardware.IDs.RIGHT_SHOULDER);
                        servo2.setDirection(Servo.Direction.REVERSE);
                        double position = 0.5;
                        servo1.setPosition(position);
                        servo2.setPosition(position);
                        waitForStart();
                        while (opModeIsActive()) {
                            if (gamepad1.left_trigger > 0.5 || gamepad2.left_trigger > 0.5) {
                                position = Math.max(0, position - 0.05);
                            } else if (gamepad1.right_trigger > 0.5 || gamepad2.right_trigger > 0.5) {
                                position = Math.min(1, position + 0.05);
                            }
                            servo1.setPosition(position);
                            servo2.setPosition(position);
                            telemetry.addData("Dual Servo Position", "%.3f", position);
                            telemetry.update();
                        }
                    } else {
                        Servo testServo = hardwareMap.get(Servo.class, selectedItem[0]);
                        double position = 0.5;
                        testServo.setPosition(position);
                        waitForStart();
                        boolean lastLeftTrigger = false;
                        boolean lastRightTrigger = false;
                        while (opModeIsActive()) {
                            if ((gamepad1.left_trigger > 0.5 || gamepad2.left_trigger > 0.5) && !lastLeftTrigger) {
                                position = Math.max(0, position - 0.05);
                            } else if ((gamepad1.right_trigger > 0.5 || gamepad2.right_trigger > 0.5) && !lastRightTrigger) {
                                position = Math.min(1, position + 0.05);
                            }
                            testServo.setPosition(position);
                            lastLeftTrigger = gamepad1.left_trigger > 0.5 || gamepad2.left_trigger > 0.5;
                            lastRightTrigger = gamepad1.right_trigger > 0.5 || gamepad2.right_trigger > 0.5;
                            telemetry.addData("Servo Position", "%.3f", position);
                            telemetry.update();
                        }
                    }
                } else {
                    if (Objects.equals(selectedItem[0], SENSOR_OPTIONS[0])) {
                        RevTouchSensor touchSensor = hardwareMap.get(RevTouchSensor.class, selectedItem[0]);
                        waitForStart();
                        while (opModeIsActive()) {
                            telemetry.addData("Sensor State", touchSensor.isPressed() ? "Pressed" : "Not Pressed");
                            telemetry.update();
                        }
                    }
                    if (Objects.equals(selectedItem[0], SENSOR_OPTIONS[1])) {
                        Limelight3A limelight = hardwareMap.get(Limelight3A.class, Settings.Hardware.IDs.LIMELIGHT);
                        telemetry.setMsTransmissionInterval(10);
                        limelight.pipelineSwitch(1);
                        limelight.start();
                        limelight.setPollRateHz(100);
                        waitForStart();
                        while (opModeIsActive()) {
                            LLResult result = limelight.getLatestResult();
                                    // Access positional results
                                    telemetry.addData("Target X", result.getTx());
                                    telemetry.addData("Target Y", result.getTy());
                                    telemetry.addLine("Target Size: " + result.getTa()*100 + "%");

                                    telemetry.update();
                                }
                    }
                    if (Objects.equals(selectedItem[0], SENSOR_OPTIONS[2])) {
                        Limelight3A limelight = hardwareMap.get(Limelight3A.class, Settings.Hardware.IDs.LIMELIGHT);
                        telemetry.setMsTransmissionInterval(10);
                        limelight.pipelineSwitch(2);
                        limelight.start();
                        limelight.setPollRateHz(100);
                        waitForStart();
                        while (opModeIsActive()) {
                            LLResult result = limelight.getLatestResult();
                            // Access positional results
                            telemetry.addData("Target X", result.getTx());
                            telemetry.addData("Target Y", result.getTy());
                            telemetry.addLine("Target Size: " + result.getTa()*100 + "%");

                            telemetry.update();
                        }
                    }
                    if (Objects.equals(selectedItem[0], SENSOR_OPTIONS[3])) {
                        Limelight3A limelight = hardwareMap.get(Limelight3A.class, Settings.Hardware.IDs.LIMELIGHT);
                        telemetry.setMsTransmissionInterval(10);
                        limelight.pipelineSwitch(3);
                        limelight.start();
                        limelight.setPollRateHz(100);
                        waitForStart();
                        while (opModeIsActive()) {
                            LLResult result = limelight.getLatestResult();
                            // Access positional results
                            telemetry.addData("Target X", result.getTx());
                            telemetry.addData("Target Y", result.getTy());
                            telemetry.addLine("Target Size: " + result.getTa()*100 + "%");

                            telemetry.update();
                        }
                    }
                    if (Objects.equals(selectedItem[0], SENSOR_OPTIONS[4])) {
                        RevColorSensorV3 colorSensor = hardwareMap.get(RevColorSensorV3.class, Settings.Hardware.IDs.COLOR_SENSOR);
                        colorSensor.initialize();
                        waitForStart();
                        while (opModeIsActive()) {
                            telemetry.addData("Distance", colorSensor.getDistance(DistanceUnit.CM));
                            telemetry.addData("Light Detected", colorSensor.getLightDetected());
                            telemetry.addLine("RGB: (" + colorSensor.red() + colorSensor.green() + colorSensor.blue() + ")");
                            telemetry.update();
                        }
                    }
                }
                listConfirmed.set(false);
                menuActive.set(true);
            }
        }
    }
}