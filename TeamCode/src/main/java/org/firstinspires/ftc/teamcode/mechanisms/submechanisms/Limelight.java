package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

public class Limelight {
    Limelight3A limelight;
    LLResult currentResult;

    public Limelight(Limelight3A limelight) {
        this.limelight = limelight;
        init();
    }

    public void init() {
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    public void update() {
        currentResult = limelight.getLatestResult();
    }
}
