package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.dashboard.config.Config;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.roadrunner.drive.mecanum.SampleMecanumDriveBase;
import org.firstinspires.ftc.teamcode.roadrunner.drive.mecanum.SampleMecanumDriveREV;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;

import org.firstinspires.ftc.robotcore.external.JavaUtil;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaSkyStone;

import org.firstinspires.ftc.teamcode.subsystems.Capstone;
import org.firstinspires.ftc.teamcode.subsystems.FoundationHooks;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.V4B;

import kotlin.Unit;


@Autonomous(name = "Blue Auto", group = "autonomous")
public class BlueAuto extends LinearOpMode {

    // Subsystems
    FoundationHooks hooks;
    Intake intake;
    V4B v4b;
    Capstone capstone;

    // Stone vision stuff
    private VuforiaSkyStone vuforiaSkyStone;
    StonePostitions stonepos;
    int counter;
    private double stoneTargetPosition;

    public enum StonePostitions{
        LEFT,
        MIDDLE,
        RIGHT
    }

    @Config
    public static class blueAutoConstants {

        // Left stone variables

        // General

        static double under_skybridge_spline_x = 24;
        static double under_skybrige_spline_y = 32;
        static double under_skybridge_spline_heading = -90;

        public static double foundation_x = 37;
        public static double foundation_y = 80;
        public static double foundation_heading = -187;

        public static double foundation_turn_x = 27;
        public static double foundation_turn_y = 37;
        public static double foundation_turn_heading = -94;

        // Middle stone

        static double a_middle_stone_x_firstpos = 29;
        static double a_middle_stone_y_firstpos = 8.52;

        static double b_second_middle_stone_x_pos = 40.5;
        static double b_second_middle_stone_y_pos = 2;
        static double b_second_middle_stone_turn = -38;

        static double c_middle_stone_little_backup = 8;

        static double d_forward_to_second_middle_stone_distance = 45;
        static double d_turn_with_foundation = 90;
        static double d_strafe_with_foundation = 15;

        static double e_far_middle_forward_amount = 7;
        static double e_far_middle_strafe_amount = 14;

        static double f_after_strafe_amount = -4;

        // Right stone

        public static double a_first_turn_amount = -92;
        public static double a_first_strafe_amount = 22;
        public static double a_second_forward_amount = 5;

        public static double b_forward_to_second_stone_distance = 49;

        public static double c_far_left_forward_amount = 9;
        public static double c_far_left_strafe_amount = 21;

    }

    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDriveBase drive = new SampleMecanumDriveREV(hardwareMap);
        vuforiaSkyStone = new VuforiaSkyStone();

        // Subsystems
        hooks = new FoundationHooks(this, hardwareMap, telemetry);
        intake = new Intake(this, hardwareMap, telemetry);
        v4b = new V4B(this, hardwareMap, telemetry, intake);
        capstone = new Capstone(this, hardwareMap, telemetry, v4b);

        // Ensuring correct statuses
        v4b.nubSetOpen();
        hooks.open();

        // Starting up vuforia
        telemetry.addData("Status", "Initializing Vuforia. Please wait...");
        telemetry.update();
        //initVuforia();

        //vuforiaSkyStone.activate();
        telemetry.addData(">>", "Vuforia initialized, press START to begin autonomous, and hope it works :)");
        telemetry.update();


        waitForStart();

        if (isStopRequested()) return;

        drive.setPoseEstimate(new Pose2d(0, -8.52, 0));
        /* START MOVECODE */

        intake.release();

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                        .forward(14)
                        .build()
        );

        //locateSkystone();
        stonepos = StonePostitions.RIGHT;


        switch(stonepos) {
            case RIGHT:

                v4b.AutoWait();
                // Turning to be parallel to blocks
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .forward(6)
                                .build()
                );


                drive.turnSync(Math.toRadians(blueAutoConstants.a_first_turn_amount));

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .back(15)
                            .addMarker(0.5, ()->{intake.setOn(); return Unit.INSTANCE;})
                            .build()
                );

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .strafeLeft(blueAutoConstants.a_first_strafe_amount)
                                .addMarker(0.5, ()->{intake.setOn(); return Unit.INSTANCE;})
                                .build()
                );

                // Driving forward to collect block then strafing back to go under bridge
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .forward(blueAutoConstants.a_second_forward_amount)
                                .build()
                );

                // Splining to sit under bridge
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .strafeRight(blueAutoConstants.a_first_strafe_amount - 7)
                                .reverse()
                                .addMarker(1.4, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                                .addMarker(2.0, ()->{intake.setOff();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(blueAutoConstants.under_skybridge_spline_x + 1.5, blueAutoConstants.under_skybrige_spline_y, Math.toRadians(blueAutoConstants.under_skybridge_spline_heading - 2)))
                                .build()
                );

                sleep(200);

                // Splining to the foundation
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(0.1, ()->{v4b.nubSetClosed();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(blueAutoConstants.foundation_x, blueAutoConstants.foundation_y - 5, Math.toRadians(blueAutoConstants.foundation_heading)))
                                .build()

                );

                // Back into foundation and grab on
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.4, ()->{hooks.close();return Unit.INSTANCE;})
                                .back(5.5)
                                .strafeLeft(blueAutoConstants.d_strafe_with_foundation - 10)
                                .build()
                );

                sleep(400); // Wait for hooks to close

                drive.turnSync(Math.toRadians(blueAutoConstants.d_turn_with_foundation));

                // Pull foundation to the right spot (~under the skybridge) and place the skystone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .splineTo(new Pose2d(blueAutoConstants.foundation_turn_x + 2, blueAutoConstants.foundation_turn_y - 2, Math.toRadians(blueAutoConstants.foundation_turn_heading)))
                                .addMarker(0.3, ()->{v4b.AutoSetLowScoring(); return Unit.INSTANCE;})
                                .addMarker(1.4, ()->{v4b.nubSetOpen(); return Unit.INSTANCE;})
                                .build()

                );

                // Releasing foundation and setting 4-bar so we can drive under skybridge
                hooks.open();
                v4b.AutoSetUnderSkybridge();

                sleep(400);

                // Driving to far left block
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .forward(blueAutoConstants.b_forward_to_second_stone_distance)
                                .addMarker(1.2, ()->{v4b.AutoWait(); return Unit.INSTANCE;})
                                .addMarker(0.8, ()->{intake.setOn(); return Unit.INSTANCE;})
                                .build()
                );

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .strafeLeft(blueAutoConstants.c_far_left_strafe_amount)
                                .build()
                );

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .forward(blueAutoConstants.c_far_left_forward_amount)
                                .build()
                );

                sleep(500);

                // Splining back to the foundation
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(1.8, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                                .addMarker(2.4, ()->{intake.setOff();return Unit.INSTANCE;})
                                .addMarker(2.2, ()->{v4b.nubSetClosed();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(blueAutoConstants.under_skybridge_spline_x + 1.8, blueAutoConstants.under_skybrige_spline_y, Math.toRadians(blueAutoConstants.under_skybridge_spline_heading)))
                                .build()
                );

                // Placing the second stone and driving backwards
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .back(50)
                                .addMarker(0.01, ()->{hooks.close(); return  Unit.INSTANCE;})
                                .addMarker(0.5, ()->{v4b.AutoSetLowScoring(); return Unit.INSTANCE;})
                                .addMarker(1.2, ()->{v4b.nubSetOpen(); return Unit.INSTANCE;})
                                .addMarker(1.8, ()->{v4b.AutoWait(); return Unit.INSTANCE;})
                                .strafeLeft(6)
                                .build()
                );

                hooks.open();
                sleep(300);

                // Driving back under the skybridge to park
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .lineTo(new Vector2d(blueAutoConstants.under_skybridge_spline_x + 1.8, blueAutoConstants.under_skybrige_spline_y))
                                .build()
                );


                break;

            case MIDDLE:

                // Making first drive up

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.5, ()->{v4b.AutoWait();return Unit.INSTANCE;})
                                .strafeTo(new Vector2d(blueAutoConstants.a_middle_stone_x_firstpos, blueAutoConstants.a_middle_stone_y_firstpos))
                                .back(1.5)
                                .build()

                );

                // Turn to face stone
                drive.turnSync(Math.toRadians(blueAutoConstants.b_second_middle_stone_turn));
                intake.setOn();

                // Drive forward to collect stone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .strafeTo(new Vector2d(blueAutoConstants.b_second_middle_stone_x_pos, blueAutoConstants.b_second_middle_stone_y_pos))
                                .build()
                );

                sleep(450);

                // Back off the stone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .back(blueAutoConstants.c_middle_stone_little_backup)
                                .build()
                );

                // Splining to sit under bridge
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(0.2, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                                .addMarker(0.9, ()->{intake.setOff();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(blueAutoConstants.under_skybridge_spline_x + 1, blueAutoConstants.under_skybrige_spline_y, Math.toRadians(blueAutoConstants.under_skybridge_spline_heading)))
                                .build()
                );

                // Splining to the foundation
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(0.1, ()->{v4b.nubSetClosed();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(blueAutoConstants.foundation_x, blueAutoConstants.foundation_y + 5, Math.toRadians(blueAutoConstants.foundation_heading)))
                                .build()

                );

                // Back into foundation and grab on
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.4, ()->{hooks.close();return Unit.INSTANCE;})
                                .back(5)
                                .strafeLeft(blueAutoConstants.d_strafe_with_foundation)
                                .build()
                );

                sleep(400); // Wait for hooks to close

                drive.turnSync(Math.toRadians(blueAutoConstants.d_turn_with_foundation));

                // Pull foundation to the right spot (~under the skybridge) and place the skystone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .splineTo(new Pose2d(blueAutoConstants.foundation_turn_x + 2, blueAutoConstants.foundation_turn_y - 2, Math.toRadians(blueAutoConstants.foundation_turn_heading)))
                                .addMarker(0.3, ()->{v4b.AutoSetLowScoring(); return Unit.INSTANCE;})
                                .addMarker(1.4, ()->{v4b.nubSetOpen(); return Unit.INSTANCE;})
                                .build()

                );

                // Releasing foundation and setting 4-bar so we can drive under skybridge
                hooks.open();
                v4b.AutoSetUnderSkybridge();

                sleep(400);

                // Driving to far middle block
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .forward(blueAutoConstants.d_forward_to_second_middle_stone_distance)
                                .addMarker(0.8, ()->{intake.setOn(); return Unit.INSTANCE;})
                                .addMarker(1.2, ()->{v4b.AutoWait(); return Unit.INSTANCE;})
                                .build()
                );

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .strafeLeft(blueAutoConstants.e_far_middle_strafe_amount)
                                .build()
                );

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .forward(blueAutoConstants.e_far_middle_forward_amount)
                                .build()
                );

                sleep(500);

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(1.8, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                                .addMarker(2.2, ()->{v4b.nubSetClosed();return Unit.INSTANCE;})
                                .addMarker(2.4, ()->{intake.setOff();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(blueAutoConstants.under_skybridge_spline_x + 1, blueAutoConstants.under_skybrige_spline_y, Math.toRadians(blueAutoConstants.under_skybridge_spline_heading)))
                                .strafeRight(blueAutoConstants.f_after_strafe_amount)
                                .build()
                );

                // Placing the second stone and driving backwards
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .back(47)
                                .addMarker(0.8, ()->{v4b.AutoSetLowScoring(); return Unit.INSTANCE;})
                                .addMarker(1.4, ()->{v4b.nubSetOpen(); return Unit.INSTANCE;})
                                .addMarker(1.9, ()->{v4b.AutoWait(); return Unit.INSTANCE;})
                                .build()
                );

                // Driving back under the skybridge to park
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .lineTo(new Vector2d(blueAutoConstants.under_skybridge_spline_x, blueAutoConstants.under_skybrige_spline_y))
                                .build()
                );



                break;

            case LEFT:


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

    private void locateSkystone() {
        while (!vuforiaSkyStone.track("Stone Target").isVisible && counter <= 450) {
            sleep(5);
            counter = counter + 1;
        }

        if (vuforiaSkyStone.track("Stone Target").isVisible) {
            stoneTargetPosition = vuforiaSkyStone.track("Stone Target").y;
            telemetry.addData("Pixels from left:", Double.parseDouble(JavaUtil.formatNumber(stoneTargetPosition, 2)));
            telemetry.addData("No Targets Detected", "Targets are not visible.");
        } else {
            stoneTargetPosition = 10000.0;
        }

        if (stoneTargetPosition < -100) {
            stonepos = StonePostitions.LEFT;
        }
        if (stoneTargetPosition > 0 && stoneTargetPosition < 500.0) {
            stonepos = StonePostitions.MIDDLE;
        } else if (stoneTargetPosition == 10000.0) {
            stonepos = StonePostitions.RIGHT;
        }

        telemetry.clearAll();
        telemetry.addData("Stone pos:", stonepos);
        telemetry.update();
        vuforiaSkyStone.deactivate();
        vuforiaSkyStone.close();
    }

}
