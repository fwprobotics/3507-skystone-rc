package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.roadrunner.drive.mecanum.SampleMecanumDriveBase;
import org.firstinspires.ftc.teamcode.roadrunner.drive.mecanum.SampleMecanumDriveREV;

//VUCODE
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaSkyStone;
import org.firstinspires.ftc.robotcore.external.JavaUtil;


@Autonomous(group="Testing")
public class SkystoneTest extends LinearOpMode {

    public enum StonePostition {
        LEFT,
        MIDDLE,
        RIGHT
    }

    private VuforiaSkyStone vuforiaSkyStone;  // vucode
    private double stoneTargetPosition;
    public StonePostition stonepos;
    public int counter;


    public void runOpMode() {

        SampleMecanumDriveBase drive = new SampleMecanumDriveREV(hardwareMap);

        // VUCODE
        vuforiaSkyStone = new VuforiaSkyStone();  //vucode
        // Initialize Vuforia
        telemetry.addData("Status", "Initializing Vuforia. Please wait...");
        telemetry.update();
        initVuforia();
        // Activate here for camera preview.
        vuforiaSkyStone.activate();
        telemetry.addData(">>", "Vuforia initialized, press start to begin...");
        telemetry.update();

        waitForStart();

        /*
        START MOVE CODE
        */

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                    .forward(14)
                    .build()
        );

        while (!vuforiaSkyStone.track("Stone Target").isVisible && counter <= 500) {
            sleep(5);
            counter = counter + 1;
        }

        if (vuforiaSkyStone.track("Stone Target").isVisible) {
            stoneTargetPosition = vuforiaSkyStone.track("Stone Target").y;
            telemetry.addData("Pixels from left:", Double.parseDouble(JavaUtil.formatNumber(stoneTargetPosition, 2)));
            telemetry.addData("No Targets Detected", "Targets are not visible.");
            sleep(10000);
        } else {
            stoneTargetPosition = 10000.0;
        }

        if (stoneTargetPosition < -100) {
            stonepos = StonePostition.MIDDLE;
        }
        if (stoneTargetPosition > 0 && stoneTargetPosition < 500.0) {
            stonepos = StonePostition.RIGHT;
        } else if (stoneTargetPosition == 10000.0) {
            stonepos = StonePostition.LEFT;
        }
        telemetry.clearAll();
        telemetry.addData("Stone pos:", stonepos);
        telemetry.update();
        vuforiaSkyStone.deactivate();
        vuforiaSkyStone.close();

        switch (stonepos) {
            case LEFT:


                break;

            case MIDDLE:


                break;

            case RIGHT:

                break;
        }
    }

    private void initVuforia() {
        vuforiaSkyStone.initialize(
                "", // vuforiaLicenseKey
                VuforiaLocalizer.CameraDirection.BACK, // cameraDirection
                true, // useExtendedTracking
                true, // enableCameraMonitoring
                VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES, // cameraMonitorFeedback
                0, // dx
                0, // dy
                0, // dz
                0, // xAngle
                -90, // yAngle
                0, // zAngle
                true); // useCompetitionFieldTargetLocations
    }
}
