package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.teamcode.systems.DynamicInput;

public class Limelight {
    Limelight3A limelight;
    LLResult currentResult;
    LimelightPipeline currentPipeline = LimelightPipeline.YELLOW;
    double lastTx = 0;
    double lastTy = 0;

    public Limelight(Limelight3A limelight) {
        this.limelight = limelight;
        init();
    }

    public void init() {
        limelight.pipelineSwitch(currentPipeline.ordinal() + 1);
        limelight.start();
        limelight.setPollRateHz(100);
    }

    public boolean update() {
        currentResult = limelight.getLatestResult();
        if (currentResult.getTx() != 0 && currentResult.getTy() != 0 && lastTx != 0 && lastTy != 0) {
            return true;
        }
        lastTx = currentResult.getTx();
        lastTy = currentResult.getTy();
        return false;
    }

    public void setCurrentPipeline(LimelightPipeline newPipeline) {
        currentPipeline = newPipeline;
        limelight.pipelineSwitch(currentPipeline.ordinal() + 1);
    }

    public enum LimelightPipeline {
        YELLOW,
        RED,
        BLUE,
    }
}
