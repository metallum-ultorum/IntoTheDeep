package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Reset Encoders", group = "Testing")
public class ResetSlideEncoders extends LinearOpMode {
    @Override
    public void runOpMode() {
        if (isStopRequested()) return;

        hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.SLIDE_VERTICAL_LEFT).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.SLIDE_VERTICAL_RIGHT).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardwareMap.get(DcMotor.class, Settings.Hardware.IDs.SLIDE_HORIZONTAL).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        requestOpModeStop();
    }
}
