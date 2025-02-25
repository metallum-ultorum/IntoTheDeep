package org.firstinspires.ftc.teamcode.mechanisms.submechanisms;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

/**
 * Incoming Yap Session:
 * Limelight returns Tx and Ty values, which return angles for where a detected object is,
 * and trig is required to get pixel or distance values.
 * IMPORTANT: Tx and Ty are zero when no desired object is detected.
 * Contact Rishu if any of this is confusing
 */
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

    /**
     * Initializes limelight with polling rate of 100 Hz
     */
    public void init() {
        limelight.pipelineSwitch(currentPipeline.ordinal() + 1);
        limelight.start();
        limelight.setPollRateHz(100);
    }

    /**
     * Updates the data and checks if there is a desired object detected
     * @return whether the gamepad should vibrate based on detected object
     */
    public boolean update() {
        currentResult = limelight.getLatestResult();
        if (currentResult.getTx() != 0 && currentResult.getTy() != 0 && lastTx != 0 && lastTy != 0) {
            return true;
        }
        lastTx = currentResult.getTx();
        lastTy = currentResult.getTy();
        return false;
    }

    /**
     * Switches the current pipeline to a new pipeline to change the color of the desired blocks
     * @param newPipeline The new pipeline to switch to:
     *                    YELLOW (1), RED (2), or BLUE (3)
     */
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
