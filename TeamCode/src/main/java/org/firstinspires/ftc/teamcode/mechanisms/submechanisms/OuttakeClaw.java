package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Settings;

/**
 * @noinspection unused
 */
public class OuttakeClaw {
    public final Servo clawServo;
    public boolean opened = true;

    public OuttakeClaw(Servo clawServo) {
        this.clawServo = clawServo;
    }

    public void init() {
        close();
    }

    public void open() {
        clawServo.setPosition(Settings.Hardware.Servo.OuttakeClaw.OPEN);
        opened = true;
    }

    /* Close both servos */
    public void close() {
        clawServo.setPosition(Settings.Hardware.Servo.OuttakeClaw.CLOSED);
        opened = false;
    }

    public void toggle() {
        if (opened) {
            close();
        } else {
            open();
        }
    }

    public void reset() {
    }


}
