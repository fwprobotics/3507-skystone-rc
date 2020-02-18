package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.roadrunner.drive.mecanum.SampleMecanumDriveBase;
import org.firstinspires.ftc.teamcode.roadrunner.drive.mecanum.SampleMecanumDriveREV;

import org.firstinspires.ftc.teamcode.subsystems.Capstone;
import org.firstinspires.ftc.teamcode.subsystems.FoundationHooks;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.V4B;

import kotlin.Unit;


@Autonomous(name = "Red Auto", group = "autonomous")
public class RedAuto extends LinearOpMode {

    FoundationHooks hooks;
    Intake intake;
    V4B v4b;
    Capstone capstone;

    StonePostitions stonepos;

    public enum StonePostitions{
        LEFT,
        MIDDLE,
        RIGHT
    }

    @Config
    public static class redAutoConstants{

        // Left stone variables

        static double first_left_stone_x_pos = 27;
        static double first_left_stone_y_pos = -3;

        static double second_left_stone_x_pos = 39;
        static double second_left_stone_y_pos = 3;
        static double second_left_stone_turn = 45;

        static double forward_to_second_stone_distance = 44;

        static double left_stone_little_backup_dist = 7;

        static double far_left_forward_amount = 14;
        static double far_left_strafe_amount = 15;

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

        public static double a_middle_stone_x_firstpos = 26.5;
        public static double a_middle_stone_y_firstpos = -8.52;

        public static double b_second_middle_stone_x_pos = 39;
        public static double b_second_middle_stone_y_pos = -7;
        public static double b_second_middle_stone_turn = 34.5;

        public static double c_middle_stone_little_backup = 5;

        public static double d_forward_to_second_middle_stone_distance = 35;

        public static double e_far_middle_forward_amount = 17;
        public static double e_far_middle_strafe_amount = 18;

        public static double f_after_strafe_amount = 7;

    }

    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDriveBase drive = new SampleMecanumDriveREV(hardwareMap);
        hooks = new FoundationHooks(this, hardwareMap, telemetry);
        intake = new Intake(this, hardwareMap, telemetry);
        v4b = new V4B(this, hardwareMap, telemetry, intake);
        capstone = new Capstone(this, hardwareMap, telemetry, v4b);

        stonepos = StonePostitions.MIDDLE;

        hooks.open();
        v4b.nubSetOpen();

        waitForStart();

        if (isStopRequested()) return;

        /* START MOVECODE */
        drive.setPoseEstimate(new Pose2d(0, -8.52, 0));
        intake.release();

        switch(stonepos) {
            case LEFT:

                // Making first drive up
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.5, ()->{v4b.AutoWait();return Unit.INSTANCE;})
                                .strafeTo(new Vector2d(redAutoConstants.first_left_stone_x_pos, redAutoConstants.first_left_stone_y_pos))
                                .build()

                );

                sleep(500);

                // Turn to face stone
                drive.turnSync(Math.toRadians(redAutoConstants.second_left_stone_turn));
                intake.setOn();
                // Drive forward to collect stone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .strafeTo(new Vector2d(redAutoConstants.second_left_stone_x_pos, redAutoConstants.second_left_stone_y_pos))
                            .build()
                );

                sleep(200);

                // Back off the stone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .back(redAutoConstants.left_stone_little_backup_dist)
                            .build()
                );

                // Inital turn away from stones
                drive.turnSync(Math.toRadians(redAutoConstants.under_skybridge_spline_heading));

                // Splining to sit under bridge
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .reverse()
                            .addMarker(1.8, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                            .addMarker(2.0, ()->{intake.setOff();return Unit.INSTANCE;})
                            .splineTo(new Pose2d(redAutoConstants.under_skybridge_spline_x, redAutoConstants.under_skybrige_spline_y, Math.toRadians(redAutoConstants.under_skybridge_spline_heading)))
                            .build()
                );

                // Splining to the foundation
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(0.1, ()->{v4b.nubSetClosed();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(redAutoConstants.foundation_x, redAutoConstants.foundation_y, Math.toRadians(redAutoConstants.foundation_heading)))
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
                            .splineTo(new Pose2d(redAutoConstants.foundation_turn_x + 2, redAutoConstants.foundation_turn_y, Math.toRadians(redAutoConstants.foundation_turn_heading)))
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
                            .strafeRight(redAutoConstants.far_left_strafe_amount)
                            .forward(redAutoConstants.far_left_forward_amount)
                                .addMarker(1.2, ()->{v4b.AutoWait(); return Unit.INSTANCE;})
                                .addMarker(0.8, ()->{intake.setOn(); return Unit.INSTANCE;})
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
                            .addMarker(0.5, ()->{v4b.AutoSetLowScoring(); return Unit.INSTANCE;})
                            .addMarker(1.2, ()->{v4b.nubSetOpen(); return Unit.INSTANCE;})
                            .addMarker(1.8, ()->{v4b.AutoWait(); return Unit.INSTANCE;})
                            .build()
                );

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
                                .build()

                );

                sleep(500);

                // Turn to face stone
                drive.turnSync(Math.toRadians(redAutoConstants.b_second_middle_stone_turn));
                intake.setOn();

                // Drive forward to collect stone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .strafeRight(7)
                                .forward(8)
//                                .strafeTo(new Vector2d(redAutoConstants.b_second_middle_stone_x_pos, redAutoConstants.b_second_middle_stone_y_pos))
                                //.addMarker(0.7, ()->{intake.setOnReversed(); return Unit.INSTANCE;})
                                //.addMarker(0.9, ()->{intake.setOff(); return Unit.INSTANCE;})
                                //.addMarker(0.92, ()->{intake.setOn(); return Unit.INSTANCE;})
                                .build()
                );

                sleep(3000);

                // Back off the stone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .back(redAutoConstants.c_middle_stone_little_backup)
                                .build()
                );

                // Inital turn away from stones
                drive.turnSync(Math.toRadians(redAutoConstants.under_skybridge_spline_heading - 60));

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
                                .splineTo(new Pose2d(redAutoConstants.foundation_x, redAutoConstants.foundation_y, Math.toRadians(redAutoConstants.foundation_heading)))
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
                                .splineTo(new Pose2d(redAutoConstants.foundation_turn_x - 0.7, redAutoConstants.foundation_turn_y, Math.toRadians(redAutoConstants.foundation_turn_heading)))
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
                                .forward(redAutoConstants.d_forward_to_second_middle_stone_distance)
                                .strafeRight(redAutoConstants.e_far_middle_strafe_amount)
                                .forward(redAutoConstants.e_far_middle_forward_amount)
                                .addMarker(0.8, ()->{intake.setOn(); return Unit.INSTANCE;})
                                .addMarker(1.2, ()->{v4b.AutoWait(); return Unit.INSTANCE;})
                                .build()
                );

                // Splining back to the foundation
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(1.8, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                                .addMarker(2.2, ()->{v4b.nubSetClosed();return Unit.INSTANCE;})
                                .addMarker(2.4, ()->{intake.setOff();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(redAutoConstants.under_skybridge_spline_x + 1, redAutoConstants.under_skybrige_spline_y, Math.toRadians(redAutoConstants.under_skybridge_spline_heading)))
                                .strafeRight(redAutoConstants.f_after_strafe_amount)
                                .build()
                );

                // Placing the second stone and driving backwards
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .back(50)
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
//                drive.followTrajectorySync(
//                        drive.trajectoryBuilder()
//                                .addMarker(0.2, ()->{intake.setOn();return Unit.INSTANCE;})
//                                .addMarker(0.5, ()->{v4b.AutoWait();return Unit.INSTANCE;})
//                                .strafeTo(new Vector2d(redAutoConstants.right_stone_x_pos, redAutoConstants.right_stone_y_pos))
//                                .build()
//
//                );

                break;
        }

    }
}
