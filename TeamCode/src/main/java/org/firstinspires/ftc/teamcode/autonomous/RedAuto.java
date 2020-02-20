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


@Autonomous(name = "Red Auto", group = "autonomous")
public class RedAuto extends LinearOpMode {

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
    public static class redAutoConstants{

        // Left stone variables

        public static double first_turn_amount = 90;
        public static double first_strafe_amount = 28;
        public static double second_forward_amount = 5;

         static double first_left_stone_x_pos = 29;
         static double first_left_stone_y_pos = -2.5;

         static double second_left_stone_x_pos = 32;
         static double second_left_stone_y_pos = 4;
         static double second_left_stone_turn = 37;

        public static double forward_to_second_stone_distance = 40;

        public static double left_stone_little_backup_dist = 7;

        public static double far_left_forward_amount = 12;
        public static double far_left_strafe_amount = 22;

        // General

        static double under_skybridge_spline_x = 24;
        static double under_skybrige_spline_y = -32;
        static double under_skybridge_spline_heading = 90;

        static double foundation_x = 37;
        static double foundation_y = -80;
        static double foundation_heading = 187;

        static double foundation_turn_x = 27;
        static double foundation_turn_y = -37;
        static double foundation_turn_heading = 94;

        // Middle stone

        static double a_middle_stone_x_firstpos = 27.5;
        static double a_middle_stone_y_firstpos = -8.52;

        static double b_second_middle_stone_x_pos = 40.5;
        static double b_second_middle_stone_y_pos = -7;
        static double b_second_middle_stone_turn = 32;

        static double c_middle_stone_little_backup = 5;

        public static double d_forward_to_second_middle_stone_distance = 42;

        public static double e_far_middle_forward_amount = 19;
        public static double e_far_middle_strafe_amount = 17;

        static double f_after_strafe_amount = 6;

        // Right stone

         static double a_right_stone_x_firstpos = 23;
         static double a_right_stone_y_firstpos = -19;

         static double b_second_right_stone_turn = 30;
         static double b_second_right_stone_forward = 10;

         static double c_right_stone_little_backup = 5;

         static double d_forward_to_second_right_stone_distance = 26;

         static double e_far_right_forward_amount = 18;
         static double e_far_right_strafe_amount = 20;

         static double f_right_strafe_amount = 6;

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
        initVuforia();

        vuforiaSkyStone.activate();
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

        locateSkystone();


        switch(stonepos) {
            case LEFT:

                v4b.AutoWait();
                // Turning to be parallel to blocks
                drive.turnSync(Math.toRadians(redAutoConstants.first_turn_amount));

                // Strafing to line up with the left block (pushing the other two away)
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .back(1.5)
                            .strafeRight(redAutoConstants.first_strafe_amount)
                            .build()
                );

                intake.setOn();

                // Driving forward to collect block then strafing back to go under bridge
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .forward(redAutoConstants.second_forward_amount)
                            .strafeLeft(redAutoConstants.first_strafe_amount - 5)
                            .build()
                );

                // Splining to sit under bridge
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .reverse()
                            .addMarker(1.4, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                            .addMarker(2.0, ()->{intake.setOff();return Unit.INSTANCE;})
                            .splineTo(new Pose2d(redAutoConstants.under_skybridge_spline_x + 1, redAutoConstants.under_skybrige_spline_y, Math.toRadians(redAutoConstants.under_skybridge_spline_heading + 2)))
                            .build()
                );

                sleep(200);

                // Splining to the foundation
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(0.1, ()->{v4b.nubSetClosed();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(redAutoConstants.foundation_x, redAutoConstants.foundation_y + 3, Math.toRadians(redAutoConstants.foundation_heading + 2)))
                                .build()

                );

                // Back into foundation and grab on
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.4, ()->{hooks.close();return Unit.INSTANCE;})
                                .back(5)
                                .build()
                );

                sleep(300); // Wait for hooks to close

                // Pull foundation to the right spot (~under the skybridge) and place the skystone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .splineTo(new Pose2d(redAutoConstants.foundation_turn_x - 1.8, redAutoConstants.foundation_turn_y, Math.toRadians(redAutoConstants.foundation_turn_heading)))
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
                            .forward(redAutoConstants.forward_to_second_stone_distance)
                                .addMarker(1.2, ()->{v4b.AutoWait(); return Unit.INSTANCE;})
                                .addMarker(0.8, ()->{intake.setOn(); return Unit.INSTANCE;})
                            .build()
                );

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .strafeRight(redAutoConstants.far_left_strafe_amount)
                                .build()
                );

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .forward(redAutoConstants.far_left_forward_amount)
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
                                .splineTo(new Pose2d(redAutoConstants.under_skybridge_spline_x + 1.8, redAutoConstants.under_skybrige_spline_y, Math.toRadians(redAutoConstants.under_skybridge_spline_heading)))
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
                            .lineTo(new Vector2d(redAutoConstants.under_skybridge_spline_x, redAutoConstants.under_skybrige_spline_y))
                            .build()
                );


                break;

            case MIDDLE:
                // Making first drive up

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.5, ()->{v4b.AutoWait();return Unit.INSTANCE;})
                                .strafeTo(new Vector2d(redAutoConstants.a_middle_stone_x_firstpos, redAutoConstants.a_middle_stone_y_firstpos))
                                .back(1.5)
                                .build()

                );

                // Turn to face stone
                drive.turnSync(Math.toRadians(redAutoConstants.b_second_middle_stone_turn));
                intake.setOn();

                // Drive forward to collect stone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .strafeTo(new Vector2d(redAutoConstants.b_second_middle_stone_x_pos, redAutoConstants.b_second_middle_stone_y_pos))
                                .build()
                );

                sleep(450);

                // Back off the stone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .back(redAutoConstants.c_middle_stone_little_backup)
                                .build()
                );

                // Inital turn away from stones
                drive.turnSync(Math.toRadians(redAutoConstants.under_skybridge_spline_heading - 80));

                // Splining to sit under bridge
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(0.2, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                                .addMarker(0.9, ()->{intake.setOff();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(redAutoConstants.under_skybridge_spline_x + 1, redAutoConstants.under_skybrige_spline_y, Math.toRadians(redAutoConstants.under_skybridge_spline_heading)))
                                .build()
                );

                // Splining to the foundation
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(0.1, ()->{v4b.nubSetClosed();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(redAutoConstants.foundation_x, redAutoConstants.foundation_y + 3, Math.toRadians(redAutoConstants.foundation_heading)))
                                .build()

                );

                // Back into foundation and grab on
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.4, ()->{hooks.close();return Unit.INSTANCE;})
                                .back(5)
                                .build()
                );

                sleep(300); // Wait for hooks to close

                // Pull foundation to the right spot (~under the skybridge) and place the skystone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .splineTo(new Pose2d(redAutoConstants.foundation_turn_x - 1.2, redAutoConstants.foundation_turn_y, Math.toRadians(redAutoConstants.foundation_turn_heading)))
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
                                .forward(redAutoConstants.d_forward_to_second_middle_stone_distance)
                                .addMarker(0.8, ()->{intake.setOn(); return Unit.INSTANCE;})
                                .addMarker(1.2, ()->{v4b.AutoWait(); return Unit.INSTANCE;})
                                .build()
                );

                sleep(500);

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .strafeRight(redAutoConstants.e_far_middle_strafe_amount)
                                .build()
                );

                sleep(500);

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .forward(redAutoConstants.e_far_middle_forward_amount)
                                .build()
                );

                sleep(500);

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(1.8, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                                .addMarker(2.2, ()->{v4b.nubSetClosed();return Unit.INSTANCE;})
                                .addMarker(2.4, ()->{intake.setOff();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(redAutoConstants.under_skybridge_spline_x + 1, redAutoConstants.under_skybrige_spline_y, Math.toRadians(redAutoConstants.under_skybridge_spline_heading)))
                                .strafeRight(redAutoConstants.f_after_strafe_amount + 1.5)
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
                                .lineTo(new Vector2d(redAutoConstants.under_skybridge_spline_x, redAutoConstants.under_skybrige_spline_y))
                                .build()
                );


                break;

            case RIGHT:
                // Making first drive up

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.5, ()->{v4b.AutoWait();return Unit.INSTANCE;})
                                .strafeTo(new Vector2d(redAutoConstants.a_right_stone_x_firstpos, redAutoConstants.a_right_stone_y_firstpos))
                                .build()

                );

                // Turn to face stone
                drive.turnSync(Math.toRadians(redAutoConstants.b_second_right_stone_turn));
                intake.setOn();

                // Drive forward to collect stone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .forward(redAutoConstants.b_second_right_stone_forward)
                                .build()
                );

                sleep(300);

                // Back off the stone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .back(redAutoConstants.c_right_stone_little_backup)
                                .build()
                );

                // Splining to sit under bridge
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(0.8, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                                .addMarker(0.9, ()->{intake.setOff();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(redAutoConstants.under_skybridge_spline_x, redAutoConstants.under_skybrige_spline_y, Math.toRadians(redAutoConstants.under_skybridge_spline_heading)))
                                .build()
                );

                // Splining to the foundation
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(0.1, ()->{v4b.nubSetClosed();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(redAutoConstants.foundation_x, redAutoConstants.foundation_y + 3, Math.toRadians(redAutoConstants.foundation_heading)))
                                .build()

                );

                // Back into foundation and grab on
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.4, ()->{hooks.close();return Unit.INSTANCE;})
                                .back(5)
                                .build()
                );

                sleep(300); // Wait for hooks to close

                // Pull foundation to the right spot (~under the skybridge) and place the skystone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .splineTo(new Pose2d(redAutoConstants.foundation_turn_x - 1.2, redAutoConstants.foundation_turn_y, Math.toRadians(redAutoConstants.foundation_turn_heading)))
                                .addMarker(0.3, ()->{v4b.AutoSetLowScoring(); return Unit.INSTANCE;})
                                .addMarker(1.4, ()->{v4b.nubSetOpen(); return Unit.INSTANCE;})
                                .build()

                );

                // Releasing foundation and setting 4-bar so we can drive under skybridge
                hooks.open();
                v4b.AutoSetUnderSkybridge();

                sleep(400);

                // Driving to far right block
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .forward(redAutoConstants.d_forward_to_second_right_stone_distance)
                                .strafeRight(redAutoConstants.e_far_right_strafe_amount)
                                .forward(redAutoConstants.e_far_right_forward_amount)
                                .addMarker(0.8, ()->{intake.setOn(); return Unit.INSTANCE;})
                                .addMarker(1.2, ()->{v4b.AutoWait(); return Unit.INSTANCE;})
                                .build()
                );

                // Going back to the foundation
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .strafeLeft(23)
                                .addMarker(1.3, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                                .addMarker(1.7, ()->{v4b.nubSetClosed();return Unit.INSTANCE;})
                                .addMarker(2.4, ()->{intake.setOff();return Unit.INSTANCE;})
                                .strafeTo(new Vector2d(redAutoConstants.under_skybridge_spline_x - 1, redAutoConstants.under_skybrige_spline_y))
                                .strafeRight(redAutoConstants.f_right_strafe_amount)
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
                                .lineTo(new Vector2d(redAutoConstants.under_skybridge_spline_x, redAutoConstants.under_skybrige_spline_y))
                                .build()
                );

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
            stonepos = StonePostitions.MIDDLE;
        }
        if (stoneTargetPosition > 0 && stoneTargetPosition < 500.0) {
            stonepos = StonePostitions.RIGHT;
        } else if (stoneTargetPosition == 10000.0) {
            stonepos = StonePostitions.LEFT;
        }

        telemetry.clearAll();
        telemetry.addData("Stone pos:", stonepos);
        telemetry.update();
        vuforiaSkyStone.deactivate();
        vuforiaSkyStone.close();
    }

}
