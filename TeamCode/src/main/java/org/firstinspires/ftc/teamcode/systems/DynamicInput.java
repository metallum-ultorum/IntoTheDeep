package org.firstinspires.ftc.teamcode.systems;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.Settings;

public class DynamicInput {
    public final Gamepad mainCtrl;
    public final Gamepad subCtrl;
    public Settings.DefaultGamepadSettings mainSettings;
    public Settings.DefaultGamepadSettings subSettings;
    public Settings.ControllerProfile mainProfile;
    public Settings.ControllerProfile subProfile;

    // Track previous button states for justPressed functionality
    private boolean prevExtendHorizontal, prevRetractHorizontal, prevExtendVertical, prevRetractVertical, prevWrist,
            prevInwardClaw, prevOutwardClaw, prevToggleClaw, prevShoulderUp, prevShoulderDown, prevFlipMovement, prevIntakeIn;

    public DynamicInput(Gamepad gamepad1, Gamepad gamepad2, Settings.ControllerProfile mainProfile,
            Settings.ControllerProfile subProfile) {
        if (gamepad1 == null || gamepad2 == null || mainProfile == null || subProfile == null) {
            throw new IllegalArgumentException("All constructor parameters must be non-null");
        }
        this.mainCtrl = gamepad1;
        this.subCtrl = gamepad2;
        this.mainProfile = mainProfile;
        this.subProfile = subProfile;
        this.mainSettings = mainProfile.mainGamepad;
        this.subSettings = subProfile.subGamepad;
    }

    /**
     * @noinspection unused
     */ // Method to switch between different control profiles
    public void switchProfiles(String mainProfileName, String subProfileName) {
        boolean mainFound = false, subFound = false;
        for (Settings.ControllerProfile profile : Settings.MAIN_AVAILABLE_PROFILES) {
            if (profile.name.equals(mainProfileName)) {
                this.mainProfile = profile;
                this.mainSettings = profile.mainGamepad;
                mainFound = true;
            }
            if (profile.name.equals(subProfileName)) {
                this.subProfile = profile;
                this.subSettings = profile.subGamepad;
                subFound = true;
            }
        }
        if (!mainFound || !subFound) {
            throw new IllegalArgumentException("Invalid profile name(s)");
        }
    }

    public static class Movements {
        public final double up, right, down, left, rotation, x, y;

        public Movements(Gamepad mainCtrl, Settings.DefaultGamepadSettings mainSettings) {
            // Get mapped axis values with deadzone
            double leftStickY = applyDeadzone(
                    getAxisValue(mainCtrl, mainSettings.buttonMapping.moveForward),
                    mainSettings.stick_deadzone);
            double leftStickX = applyDeadzone(
                    getAxisValue(mainCtrl, mainSettings.buttonMapping.moveSideways),
                    mainSettings.stick_deadzone);
            double rightStickX = applyDeadzone(
                    getAxisValue(mainCtrl, mainSettings.buttonMapping.rotate),
                    mainSettings.stick_deadzone);

            // Apply sensitivities and inversion
            leftStickY *= mainSettings.invert_y_axis ? -1 : 1;
            leftStickX *= mainSettings.invert_x_axis ? -1 : 1;

            double upPower = (leftStickY < 0 ? -leftStickY : 0) * mainSettings.left_stick_sensitivity;
            double downPower = (leftStickY > 0 ? leftStickY : 0) * mainSettings.left_stick_sensitivity;
            double rightPower = (leftStickX > 0 ? leftStickX : 0) * mainSettings.left_stick_sensitivity;
            double leftPower = (leftStickX < 0 ? -leftStickX : 0) * mainSettings.left_stick_sensitivity;

            // Add absolute movement using mapped buttons
            if (getButtonState(mainCtrl, mainSettings.buttonMapping.moveUp))
                upPower = mainSettings.dpad_movement_speed;
            if (getButtonState(mainCtrl, mainSettings.buttonMapping.moveDown))
                downPower = mainSettings.dpad_movement_speed;
            if (getButtonState(mainCtrl, mainSettings.buttonMapping.moveRight))
                rightPower = mainSettings.dpad_movement_speed;
            if (getButtonState(mainCtrl, mainSettings.buttonMapping.moveLeft))
                leftPower = mainSettings.dpad_movement_speed;

            // Handle rotation based on settings
            double rotationRight;
            double rotationLeft;

            double rotation = rightStickX * mainSettings.right_stick_sensitivity;
            rotationRight = rotation > 0 ? rotation : 0;
            rotationLeft = rotation < 0 ? -rotation : 0;

            rotationRight += getButtonState(mainCtrl, mainSettings.buttonMapping.rotateRight)
                    ? mainSettings.bumper_rotation_speed
                    : 0;
            rotationLeft += getButtonState(mainCtrl, mainSettings.buttonMapping.rotateLeft)
                    ? mainSettings.bumper_rotation_speed
                    : 0;

            // Set final values
            this.up = upPower;
            this.right = rightPower;
            this.down = downPower;
            this.left = leftPower;
            this.rotation = rotationRight - rotationLeft;
            this.y = up - down;
            this.x = right - left;
        }

        private double applyDeadzone(double value, double deadzone) {
            return Math.abs(value) > deadzone ? value : 0;
        }
    }

    public static class ContextualActions extends Actions {
        public final boolean justExtendHorizontal, justRetractHorizontal, justRetractVertical, justExtendVertical,
                justWristUp, justInwardClaw, justOutwardClaw, justToggleClaw, justShoulderUp, justShoulderDown,
                justFlipMovement, justIntakeIn;

        public ContextualActions(Gamepad mainCtrl, Settings.DefaultGamepadSettings mainSettings,
                Gamepad subCtrl, Settings.DefaultGamepadSettings subSettings,
                                 boolean prevExtendHorizontal, boolean prevRetractHorizontal, boolean prevExtendVertical,
                                 boolean prevRetractVertical, boolean prevWrist, boolean prevInwardClaw, boolean prevOutwardClaw,
                                 boolean prevToggleClaw, boolean prevShoulderUp, boolean prevShoulderDown, boolean prevFlipMovement, boolean prevIntakeIn) {
            super(mainCtrl, mainSettings, subCtrl, subSettings);

            this.justExtendHorizontal = extendHorizontal && !prevExtendHorizontal;
            this.justRetractHorizontal = retractHorizontal && !prevRetractHorizontal;
            this.justRetractVertical = retractVertical && !prevRetractVertical;
            this.justExtendVertical = extendVertical && !prevExtendVertical;
            this.justWristUp = wristUp && !prevWrist;
            this.justInwardClaw = inwardClaw && !prevInwardClaw;
            this.justOutwardClaw = outwardClaw && !prevOutwardClaw;
            this.justToggleClaw = toggleClaw && !prevToggleClaw;
            this.justShoulderUp = shoulderUp && !prevShoulderUp;
            this.justShoulderDown = shoulderDown && !prevShoulderDown;
            this.justFlipMovement = flipMovement && !prevFlipMovement;
            this.justIntakeIn = intakeIn && !prevIntakeIn;
        }
    }

    public Movements getMovements() {
        return new Movements(mainCtrl, mainSettings);
    }

    public Actions getActions() {
        return new Actions(mainCtrl, mainSettings, subCtrl, subSettings);
    }

    public ContextualActions getContextualActions() {
        ContextualActions actions = new ContextualActions(mainCtrl, mainSettings, subCtrl, subSettings,
                prevExtendHorizontal, prevRetractHorizontal, prevExtendVertical, prevRetractVertical, prevWrist,
                prevInwardClaw, prevOutwardClaw, prevToggleClaw, prevShoulderUp, prevShoulderDown, prevFlipMovement, prevIntakeIn);

        // Update previous states
        prevExtendHorizontal = actions.extendHorizontal;
        prevRetractHorizontal = actions.retractHorizontal;
        prevExtendVertical = actions.extendVertical;
        prevRetractVertical = actions.retractVertical;
        prevWrist = actions.wristUp;
        prevInwardClaw = actions.inwardClaw;
        prevOutwardClaw = actions.outwardClaw;
        prevToggleClaw = actions.toggleClaw;
        prevShoulderUp = actions.shoulderUp;
        prevShoulderDown = actions.shoulderDown;
        prevFlipMovement = actions.flipMovement;
        prevIntakeIn = actions.intakeIn;

        return actions;
    }

    public static class Actions {
        public final boolean extendHorizontal, retractHorizontal, retractVertical, extendVertical, extensorBusy;
        public final boolean extendVerticalToChamber, extendVerticalToChamberPrep, extendVerticalToBasket, retractVerticalToTransfer;
        public final boolean intakeIn, intakeOut, intakeStop;
        public final boolean wristUp, wristDown;
        public final boolean ascendExtensorExtend, ascendExtensorRetract, ascendExtensorGround, ascendExtensorCeiling;
        public final double boostAmount, brakeAmount;
        public final boolean linearActuatorExtend, linearActuatorRetract;
        public final boolean inwardClaw, outwardClaw, toggleClaw;
        public final boolean shoulderUp, shoulderDown;
        public final boolean flipMovement;
        public final double rotator;

        public Actions(Gamepad mainCtrl, Settings.DefaultGamepadSettings mainSettings,
                Gamepad subCtrl, Settings.DefaultGamepadSettings subSettings) {
            this.extendHorizontal = getButtonState(subCtrl, subSettings.buttonMapping.extendHorizontal);
            this.retractHorizontal = getButtonState(subCtrl, subSettings.buttonMapping.retractHorizontal);
            this.retractVertical = getButtonState(mainCtrl, mainSettings.buttonMapping.retractVertical);
            this.extendVertical = getButtonState(mainCtrl, mainSettings.buttonMapping.extendVertical);
            this.extendVerticalToChamber = getButtonState(mainCtrl, mainSettings.buttonMapping.extendVerticalToChamber);
            this.extendVerticalToChamberPrep = getButtonState(mainCtrl, mainSettings.buttonMapping.extendVerticalToChamberPrep);
            this.extendVerticalToBasket = getButtonState(mainCtrl, mainSettings.buttonMapping.extendVerticalToBasket);
            this.retractVerticalToTransfer = getButtonState(mainCtrl, mainSettings.buttonMapping.retractVerticalToTransfer);
            this.extensorBusy = extendHorizontal || retractHorizontal || retractVertical;
            this.intakeIn = getButtonState(subCtrl, subSettings.buttonMapping.intakeIn);
            this.intakeOut = getButtonState(subCtrl, subSettings.buttonMapping.intakeOut);
            this.intakeStop = getButtonState(subCtrl, subSettings.buttonMapping.intakeStop);
            this.wristUp = getButtonState(subCtrl, subSettings.buttonMapping.wristUp);
            this.wristDown = getButtonState(subCtrl, subSettings.buttonMapping.wristDown);
            this.ascendExtensorExtend = getButtonState(subCtrl,
                    subSettings.buttonMapping.ascendExtensorExtend);
            this.ascendExtensorRetract = getButtonState(subCtrl,
                    subSettings.buttonMapping.ascendExtensorRetract);
            this.ascendExtensorGround = getButtonState(subCtrl,
                    subSettings.buttonMapping.ascendExtensorGround);
            this.ascendExtensorCeiling = getButtonState(subCtrl,
                    subSettings.buttonMapping.ascendExtensorCeiling);
            this.boostAmount = mainSettings.applyBoostCurve(
                    getAxisValue(mainCtrl, mainSettings.buttonMapping.boost));
            this.brakeAmount = mainSettings.applyBoostCurve(
                    getAxisValue(mainCtrl, mainSettings.buttonMapping.brake));
            this.linearActuatorExtend = getButtonState(subCtrl, subSettings.buttonMapping.linearActuatorExtend);
            this.linearActuatorRetract = getButtonState(subCtrl, subSettings.buttonMapping.linearActuatorRetract);
            this.inwardClaw = getButtonState(subCtrl, subSettings.buttonMapping.clawIn);
            this.outwardClaw = getButtonState(subCtrl, subSettings.buttonMapping.clawOut);
            this.toggleClaw = getButtonState(subCtrl, subSettings.buttonMapping.clawToggle);
            this.shoulderDown = getButtonState(subCtrl, subSettings.buttonMapping.shoulderDown);
            this.shoulderUp = getButtonState(subCtrl, subSettings.buttonMapping.shoulderUp);
            this.flipMovement = getButtonState(mainCtrl, mainSettings.buttonMapping.flipMovement);
            this.rotator = (getAxisValue(subCtrl, subSettings.buttonMapping.rotator)/-2) + 0.5;
        }
    }

    private static boolean getButtonState(Gamepad gamepad, Settings.GamepadButton button) {
        switch (button) {
            case A:
                return gamepad.a;
            case B:
                return gamepad.b;
            case X:
                return gamepad.x;
            case Y:
                return gamepad.y;
            case DPAD_UP:
                return gamepad.dpad_up;
            case DPAD_DOWN:
                return gamepad.dpad_down;
            case DPAD_LEFT:
                return gamepad.dpad_left;
            case DPAD_RIGHT:
                return gamepad.dpad_right;
            case LEFT_BUMPER:
                return gamepad.left_bumper;
            case RIGHT_BUMPER:
                return gamepad.right_bumper;
            case START:
                return gamepad.start;
            case BACK:
                return gamepad.back;
            case LEFT_STICK_BUTTON:
                return gamepad.left_stick_button;
            case RIGHT_STICK_BUTTON:
                return gamepad.right_stick_button;
            case GUIDE:
                return gamepad.guide;
            case OPTIONS:
                return gamepad.options;
            case LEFT_TRIGGER:
                return gamepad.left_trigger > 0;
            case RIGHT_TRIGGER:
                return gamepad.right_trigger > 0;
            default:
                throw new IllegalArgumentException("Unexpected button: " + button);
        }
    }

    private static double getAxisValue(Gamepad gamepad, Settings.GamepadAxis axis) {
        switch (axis) {
            case LEFT_TRIGGER:
                return gamepad.left_trigger;
            case RIGHT_TRIGGER:
                return gamepad.right_trigger;
            case LEFT_STICK_X:
                return gamepad.left_stick_x;
            case LEFT_STICK_Y:
                return gamepad.left_stick_y;
            case RIGHT_STICK_X:
                return gamepad.right_stick_x;
            case RIGHT_STICK_Y:
                return gamepad.right_stick_y;
            default:
                throw new IllegalArgumentException("Unexpected axis: " + axis);
        }
    }
}
