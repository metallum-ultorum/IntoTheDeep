package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;

import java.lang.reflect.Field;

/**
 * @noinspection CanBeFinal, unused
 */
@Config
public class Settings {

    // Movement settings
    @Config
    public static class Movement {
        /**
         * Multiplier applied to strafe movements to compensate for mechanical
         * differences
         */
        public static double strafe_power_coefficient = 1.2;
        /** Default speed for autonomous movements */
        public static double default_autonomous_speed = 0.6;
        /** Flips movement to make movement easier while the robot is backwards **/
        public static int flip_movement = 1;
        /**
         * Determines if the gecko wheels outtake a little when closing the claw to ease
         * transfer
         **/
        public static boolean easeTransfer = false;
    }

    public static final ControllerProfile AGNEY_PROFILE = new ControllerProfile("Mr. Boost Button",
            new DefaultGamepadSettings() {
                {
                    // Customize main gamepad settings
                    dpad_movement_speed = 0.6;
                    bumper_rotation_speed = 0.8;

                    incremental_vertical = true;
                }

                @Override
                public double applyBoostCurve(double input) {
                    return BoostCurves.exponential(input);
                }
            },
            new DefaultGamepadSettings() {
                {
                    // Customize sub gamepad settings
                    trigger_threshold = 0.1;
                }
            }
    );
    public static final ControllerProfile BEN_PROFILE = new ControllerProfile("Ben's Secret Sauce",
            new DefaultGamepadSettings() {
                {
                    // Customize main gamepad settings
                    dpad_movement_speed = 0.8;
                    bumper_rotation_speed = 0.7;
                }

                @Override
                public double applyBoostCurve(double input) {
                    return BoostCurves.smooth(input);
                }
            }, new DefaultGamepadSettings() {
                {
                    // Customize sub gamepad settings
                    trigger_threshold = 0.2;
                }
    });
    public static final ControllerProfile CONNER_PROFILE = new ControllerProfile("Conner's profile",
            new DefaultGamepadSettings() {
                {
                    dpad_movement_speed = 0.6;
                    bumper_rotation_speed = 0.9;
                }

                @Override
                public double applyBoostCurve(double input) {
                    return BoostCurves.quadratic(input);
                }
            }, new DefaultGamepadSettings() {
                {
                    // Customize sub gamepad settings
                    trigger_threshold = 0.15;
                }
    });
    public static final ControllerProfile RISHU_PROFILE = new ControllerProfile("Rishu (my goat)",
            new DefaultGamepadSettings() {
                {
                    dpad_movement_speed = 0.5;
                    bumper_rotation_speed = 0.9;
                }

                @Override
                public double applyBoostCurve(double input) {
                    return BoostCurves.linear(input);
                }
            }, new DefaultGamepadSettings() {
        {
            // Customize sub gamepad settings
            trigger_threshold = 0.1;
            buttonMapping.wristUp = GamepadButton.DPAD_UP;
            buttonMapping.wristDown = GamepadButton.DPAD_DOWN;
        }
    });

    // Gamepad settings
    @Config
    public static class DefaultGamepadSettings {
        /** Sensitivity multiplier for left stick input */
        public static double left_stick_sensitivity = 1.0;
        /** Speed for dpad-based absolute movement, from 0 to 1 */
        public static double dpad_movement_speed = 0.3;
        public static double trigger_threshold = 0.1;

        /** Deadzone for stick inputs to prevent drift */
        public static double stick_deadzone = 0.05;

        /** Sensitivity multiplier for right stick input */
        public static double right_stick_sensitivity = 0.7;

        /** Bumper rotation speed */
        public static double bumper_rotation_speed = 0.8;

        /** Whether to invert Y axis controls */
        public static boolean invert_y_axis = false;

        /** Whether to invert X axis controls */
        public static boolean invert_x_axis = false;

        /** Whether to use right stick for rotation instead of bumpers */
        public static boolean use_right_stick_rotation = true;

        /* Whether to move based on rotation or absolute heading */

        public static boolean use_absolute_positioning = false;

        public static boolean incremental_horizontal = false;

        public static boolean incremental_vertical = false;

        public final ButtonMapping buttonMapping;

        public DefaultGamepadSettings() {
            this.buttonMapping = new ButtonMapping();
        }

        /**
         * Applies a mathematical curve to the boost input to adjust control response
         *
         * @param input Raw input value between 0 and 1
         * @return Modified input value between 0 and 1
         */
        public double applyBoostCurve(double input) {
            // Default implementation: simple clamp between 0 and 1
            return Math.max(0, Math.min(1, input));
        }
    }

    public enum GamepadButton {
        // Face buttons
        A, B, X, Y,

        // D-pad
        DPAD_UP, DPAD_DOWN, DPAD_LEFT, DPAD_RIGHT,

        // Shoulder buttons
        LEFT_BUMPER, RIGHT_BUMPER,

        // Center buttons
        START, BACK, GUIDE,

        // Stick buttons
        LEFT_STICK_BUTTON, RIGHT_STICK_BUTTON,
        OPTIONS,
        RIGHT_TRIGGER, LEFT_TRIGGER
    }

    public enum WristPosition {
        LEFT, RIGHT
    }

    public enum GamepadAxis {
        LEFT_TRIGGER, RIGHT_TRIGGER,
        LEFT_STICK_X, LEFT_STICK_Y,
        RIGHT_STICK_X, RIGHT_STICK_Y
    }

    // Deploy flags
    @Config
    public static class Deploy {
        // Core Mechanisms
        public static final boolean INTAKE = true;
        public static final boolean OUTTAKE = true;
        public static final boolean LINEAR_ACTUATOR = false;

        public static AutonomousMode AUTONOMOUS_MODE_LEFT = AutonomousMode.BASKET;
        public static AutonomousMode AUTONOMOUS_MODE_RIGHT = AutonomousMode.CHAMBER;

        public enum AutonomousMode {
            JUST_PARK, JUST_PLACE, CHAMBER, BASKET
        }
    }

    public static String getDisabledFlags() {
        StringBuilder enabledFlags = new StringBuilder();

        Field[] fields = Deploy.class.getFields();

        for (Field field : fields) {
            try {
                if (!field.getBoolean(null)) {
                    enabledFlags.append(field.getName()).append(", ");
                }
            } catch (IllegalAccessException ignored) {
            }
        }

        return enabledFlags.toString();
    }

    public static class ControllerProfile {
        public final String name;
        public final DefaultGamepadSettings mainGamepad;
        public final DefaultGamepadSettings subGamepad;

        public ControllerProfile(String name, DefaultGamepadSettings main, DefaultGamepadSettings sub) {
            this.name = name;
            this.mainGamepad = main;
            this.subGamepad = sub;
        }
    }

    public static final ControllerProfile DEFAULT_PROFILE = new ControllerProfile(
            "default",
            new DefaultGamepadSettings(),
            new DefaultGamepadSettings());
    public static final ControllerProfile[] MAIN_AVAILABLE_PROFILES = {
            DEFAULT_PROFILE,
            AGNEY_PROFILE,
            BEN_PROFILE,
            CONNER_PROFILE,
            RISHU_PROFILE
    };
    public static final ControllerProfile[] SUB_AVAILABLE_PROFILES = {
            DEFAULT_PROFILE,
            AGNEY_PROFILE,
            BEN_PROFILE,
            CONNER_PROFILE,
            RISHU_PROFILE
    };

    @Config
    public static class Calibration {
        /**
         * Multiplier applied to strafe movements to compensate for mechanical
         * differences
         */
        public static double headingTolerance = 0.02;
        //        public static Vector2d spacialTolerance = new Vector2d(0.5, 0.5);
        public static double CALIBRATION_APPROXIMATION_COEFFICIENT = 0;
    }

    @Config
    public static class Assistance {
        public static double inverseLateralMultiplier = 50; // move at full power at 30 inches laterally away, going down to 0.0333333333 at 1 inch away
        public static double minimumRotationCorrectionThreshold = Math.PI / 70; // Don't correct heading within 0.1570796327
        public static double approachSpeed = 0.5; // if within an inch it's good enough
        public static double limelightWindowSize = 40; // degrees
    }

    // Hardware settings
    @Config
    public static class Hardware {
        /** Encoder counts per full motor revolution */
        public static final double COUNTS_PER_REVOLUTION = 10323.84; // ish? may need to recalculate later
        /** Diameter of the odometry wheels in inches */
        public static final double WHEEL_DIAMETER_INCHES = 3.5;

        // Servo positions
        @Config
        public static class Servo {
            @Config
            public static class OuttakeClaw {
                /** Values for open and closed positions on the outtake claw */
                public static double OPEN = 0;
                public static double CLOSED = 1;
            }
            @Config
            public static class IntakeClaw {
                /** Values for open and closed positions on the outtake claw */
                public static double OPEN = 0.5;
                public static double CLOSED = 0.8;
            }

            @Config
            public static class Wrist {
                public static double HORIZONTAL_POSITION = .53;
                public static double VERTICAL_POSITION = 0.88;
                public static double READY_POSITION = .65;
            }

            @Config
            public static class Rotator {
                public static double LEFT_LIMIT = 0.1;
                public static double RIGHT_LIMIT = 1;
                public static double CENTER = (LEFT_LIMIT + RIGHT_LIMIT) / 2;
            }

            @Config
            public static class Shoulder {
                // TODO: TUNE WHEN NEW SERVO GOES IN
                public static double TRANSFER_POSITION = 0.1;
                public static double PLACE_FORWARD_POSITION = 0;
                public static double PLACE_BACKWARD_POSITION = 0.65;

            }
        }

        @Config
        public static class IDs {
            public static final String IMU = "imu";
            public static final String LED = "led";

            // Drive motors
            public static final String FRONT_LEFT_MOTOR = "frontLeft";
            public static final String FRONT_RIGHT_MOTOR = "frontRight";
            public static final String REAR_LEFT_MOTOR = "rearLeft";
            public static final String REAR_RIGHT_MOTOR = "rearRight";

            // Arm components
            public static final String SLIDE_VERTICAL_LEFT = "slideVerticalLeft";
            public static final String SLIDE_VERTICAL_RIGHT = "slideVerticalRight";
            public static final String SLIDE_HORIZONTAL = "slideHorizontal";
            public static final String LINEAR_ACTUATOR = "linearActuator";
            public static final String WRIST = "wrist";
            public static final String ROTATOR = "rotator";
            public static final String LEFT_SHOULDER = "shoulderLeft";
            public static final String RIGHT_SHOULDER = "shoulderRight";
            public static final String INTAKE_CLAW = "intakeClaw";
            public static final String OUTTAKE_CLAW = "outtakeClaw";
            public static final String PINPOINT = "pinpoint";
            public static final String SLIDE_VERTICAL_TOUCH_SENSOR = "verticalSlideSensor";
            public static final String LIMELIGHT = "limelight";
            public static final String COLOR_SENSOR = "colorSensor";
        }

        @Config
        public static class VerticalSlide {
            // Positions in encoder ticks

            public static int TRANSFER = 0;
            public static int LOW_RUNG = 80;
            public static int LOW_BASKET = 550;
            public static int HANG_RUNG_1 = 3350;

            public static int HIGH_RUNG_PREP_AUTO = 1890;
            public static int HIGH_RUNG = 2250;
            public static int HIGH_BASKET = 3350;

            // Motor power settings
            public static double MOVEMENT_POWER = 1;
            public static double IDLE_POWER = 0.2;

            public static double INCREMENTAL_MOVEMENT_POWER = 20;

            public static boolean ENABLE_LOWER_LIMIT = false;
        }

        @Config
        public static class HorizontalSlide {
            // Positions in encoder ticks
            // TODO: TUNE
            public static int COLLAPSED = 0;
            public static int LEVEL_1 = 82;
            public static int EXPANDED = 250;

            // Motor power settings
            public static double MOVEMENT_POWER = 0.5;
            public static int INCREMENTAL_MOVEMENT_POWER = 15;
        }

        @Config
        public static class LinearActuator {
            // Positions in encoder ticks
            public static int MAX = 1000;
            public static int MIN = 0;

            public static double SPEED = 0.5;
        }

        @Config
        public static class Intake {
            public static double SPEED = -1;
        }
    }

    // Autonomous settings
    @Config
    public static class Autonomous {
        public static boolean ECHOLOCATE_ENABLED = false;

        @Config
        public static class Movement {
            public static int ENCODERS_NEEDED_TO_CORRECT_ODOMETRY = 3;
        }

        @Config
        public static class Timing {
            /** Pause duration after claw operations (milliseconds) */
            public static long CLAW_PAUSE = 500;
            public static long WRIST_PAUSE = 1000;
            public static long EXTENSOR_PAUSE = 2500;
        }
    }

    public static class ButtonMapping {
        // Extensor controls
        public GamepadButton extendHorizontal = GamepadButton.B;
        public GamepadButton retractHorizontal = GamepadButton.X;
        public GamepadButton extendVerticalToChamber = GamepadButton.A;
        public GamepadButton extendVerticalToChamberPrep = GamepadButton.X;
        public GamepadButton extendVerticalToBasket = GamepadButton.Y;
        public GamepadButton retractVerticalToTransfer = GamepadButton.B;

        public final GamepadButton retractVertical = GamepadButton.LEFT_BUMPER;
        public final GamepadButton extendVertical = GamepadButton.RIGHT_BUMPER;

        // Movement controls
        public final GamepadAxis moveForward = GamepadAxis.LEFT_STICK_Y;
        public final GamepadAxis moveSideways = GamepadAxis.LEFT_STICK_X;
        public final GamepadAxis rotate = GamepadAxis.RIGHT_STICK_X;
        public final GamepadButton flipMovement = GamepadButton.GUIDE;

        public GamepadButton rotateRight = GamepadButton.X;
        public GamepadButton rotateLeft = GamepadButton.A;

        // Claw controls
        public final GamepadButton intakeIn = GamepadButton.LEFT_TRIGGER;
        public final GamepadButton intakeOut = GamepadButton.RIGHT_TRIGGER;
        public final GamepadButton intakeStop = GamepadButton.OPTIONS;
        public final GamepadButton clawIn = GamepadButton.OPTIONS;
        public final GamepadButton clawOut = GamepadButton.START;
        public final GamepadButton clawToggle = GamepadButton.RIGHT_STICK_BUTTON;
        public final GamepadAxis rotator = GamepadAxis.RIGHT_STICK_X;

        // Inner Wrist controls
        public GamepadButton wristUp = GamepadButton.DPAD_LEFT;
        public GamepadButton wristDown = GamepadButton.DPAD_RIGHT;

        // Ascend extensor controls
        public final GamepadButton ascendExtensorExtend = GamepadButton.DPAD_RIGHT;
        public final GamepadButton ascendExtensorRetract = GamepadButton.DPAD_LEFT;
        public final GamepadButton ascendExtensorGround = GamepadButton.DPAD_DOWN;
        public final GamepadButton ascendExtensorCeiling = GamepadButton.DPAD_UP;

        public final GamepadAxis boost = GamepadAxis.RIGHT_TRIGGER;
        public final GamepadAxis brake = GamepadAxis.LEFT_TRIGGER;

        // Single-direction movement controls
        public GamepadButton moveUp = GamepadButton.DPAD_UP;
        public GamepadButton moveDown = GamepadButton.DPAD_DOWN;
        public GamepadButton moveLeft = GamepadButton.DPAD_LEFT;
        public GamepadButton moveRight = GamepadButton.DPAD_RIGHT;

        // Shoulder controls
        public GamepadButton shoulderUp = GamepadButton.LEFT_STICK_BUTTON;
        public GamepadButton shoulderDown = GamepadButton.RIGHT_STICK_BUTTON;

        // Linear Actuator controls
        public final GamepadButton linearActuatorExtend = GamepadButton.Y;
        public final GamepadButton linearActuatorRetract = GamepadButton.A;
    }

    // Add this new class after the GamepadAxis enum
    public static class BoostCurves {
        public static double linear(double input) {
            return Math.max(0, Math.min(1, input));
        }

        // Quadratic - slower start, faster end
        public static double quadratic(double input) {
            input = Math.max(0, Math.min(1, input));
            return Math.pow(input, 2);
        }

        // Cubic - even slower start
        public static double cubic(double input) {
            input = Math.max(0, Math.min(1, input));
            return Math.pow(input, 3);
        }

        // Square root - faster start, slower end
        public static double squareRoot(double input) {
            input = Math.max(0, Math.min(1, input));
            return Math.sqrt(input);
        }

        // Sine wave - smooth S-curve
        public static double smooth(double input) {
            input = Math.max(0, Math.min(1, input));
            return (Math.sin((input - 0.5) * Math.PI) + 1) / 2;
        }

        // Step function - binary on/off at threshold
        public static double step(double input, double threshold) {
            return input >= threshold ? 1.0 : 0.0;
        }

        // Exponential - very slow start, very fast end
        public static double exponential(double input) {
            input = Math.max(0, Math.min(1, input));
            return (Math.exp(input * 3) - 1) / (Math.exp(3) - 1);
        }

        // Custom curve generator - allows for fine-tuning
        public static double custom(double input, double power) {
            input = Math.max(0, Math.min(1, input));
            return Math.pow(input, power);
        }
    }
}
