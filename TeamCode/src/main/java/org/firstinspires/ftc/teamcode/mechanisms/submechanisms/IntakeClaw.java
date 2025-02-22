package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.BaseRobot;
import org.firstinspires.ftc.teamcode.Settings;

/** @noinspection FieldCanBeLocal, unused */
public class IntakeClaw {
    public final Servo clawServo;
    private final BaseRobot baseRobot;
    private final HardwareMap hardwareMap;
    public boolean opened = true;

    public IntakeClaw(@NonNull BaseRobot baseRobot) {
        this.baseRobot = baseRobot;
        this.hardwareMap = baseRobot.hardwareMap;
        clawServo = hardwareMap.get(Servo.class, Settings.Hardware.IDs.INTAKE_CLAW);
        close();
    }

    public void open() {
        clawServo.setPosition(Settings.Hardware.Servo.IntakeClaw.OPEN);
        opened = true;
    }

    /* Close both servos */
    public void close() {
        clawServo.setPosition(Settings.Hardware.Servo.IntakeClaw.CLOSED);
        opened = false;
    }

    public void toggle() {
        if (opened) {
            close();
        } else {
            open();
        }
    }


}
