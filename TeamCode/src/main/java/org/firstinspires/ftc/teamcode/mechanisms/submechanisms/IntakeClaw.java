package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Settings;

/**
 * @noinspection unused
 */
public class IntakeClaw {
    public final Servo clawServo;
    public boolean opened = true;

    public IntakeClaw(Servo clawServo) {
        this.clawServo = clawServo;
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

    public void init() {
        close();
    }
}
