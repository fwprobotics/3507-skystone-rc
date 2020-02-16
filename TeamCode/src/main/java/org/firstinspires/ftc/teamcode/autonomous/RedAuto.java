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

    public enum StonePostitions{
        LEFT,
        MIDDLE,
        RIGHT
    }

    public StonePostitions stonepos;

    @Config
    public static class redAutoConstants{
        static double first_left_stone_x_pos = 24;

        public static double second_left_stone_x_pos = 36.5;
        public static double second_left_stone_y_pos = 3.5;
        public static double second_left_stone_turn = 32;

        public static double far_left_stone_x_pos = 34;
        public static double far_left_stone_y_pos = 20;
        public static double far_left_stone_heading = -90;

        public static double little_backup_dist = 6;

        public static double under_skybridge_spline_x = 24;
        public static double under_skybrige_spline_y = -32;
        public static double under_skybridge_spline_heading = 90;

        public static double foundation_x = 37;
        public static double foundation_y = -80;
        public static double foundation_heading = 185;

        public static double foundation_turn_x = 28;
        public static double foundation_turn_y = -37;
        public static double foundation_turn_heading = 84;

         static double middle_stone_x_pos = 0;
         static double middle_stone_y_pos = 0;

         static double right_stone_x_pos = 0;
         static double right_stone_y_pos = 0;



    }

    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDriveBase drive = new SampleMecanumDriveREV(hardwareMap);
        hooks = new FoundationHooks(this, hardwareMap, telemetry);
        intake = new Intake(this, hardwareMap, telemetry);
        v4b = new V4B(this, hardwareMap, telemetry, intake);
        capstone = new Capstone(this, hardwareMap, telemetry, v4b);

        stonepos = StonePostitions.LEFT;

        hooks.open();
        v4b.nubSetOpen();

        waitForStart();

        if (isStopRequested()) return;

        /* START MOVECODE */

        switch(stonepos) {
            case LEFT:
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.2, ()->{intake.setOn();return Unit.INSTANCE;})
                                .addMarker(0.5, ()->{v4b.AutoWait();return Unit.INSTANCE;})
                                .forward(redAutoConstants.first_left_stone_x_pos)
                                .build()

                );

                // Turn to face stone
                drive.turnSync(Math.toRadians(redAutoConstants.second_left_stone_turn));

                // Drive to collect stone
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .strafeTo(new Vector2d(redAutoConstants.second_left_stone_x_pos, redAutoConstants.second_left_stone_y_pos))
                            .back(redAutoConstants.little_backup_dist)
                            .build()
                );

                drive.turnSync(Math.toRadians(redAutoConstants.under_skybridge_spline_heading));

                // Under bridge and to foundation

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .reverse()
                            .addMarker(1.2, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                            .addMarker(1.3, ()->{intake.setOff();return Unit.INSTANCE;})
                            .splineTo(new Pose2d(redAutoConstants.under_skybridge_spline_x, redAutoConstants.under_skybrige_spline_y, Math.toRadians(redAutoConstants.under_skybridge_spline_heading)))
                            .build()
                );

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
                                .back(6)
                                .build()
                );

                sleep(400);

                // Pull foundation to the right spot and place the skystone

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .splineTo(new Pose2d(redAutoConstants.foundation_turn_x, redAutoConstants.foundation_turn_y, Math.toRadians(redAutoConstants.foundation_turn_heading)))
                            .addMarker(0.3, ()->{v4b.AutoSetLowScoring(); return Unit.INSTANCE;})
                            .addMarker(1.4, ()->{v4b.nubSetOpen(); return Unit.INSTANCE;})
                            .build()

                );

                hooks.open();
                v4b.AutoSetUnderSkybridge();

                sleep(400);

                // Going for the second block

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .forward(30)
                            .lineTo(new Vector2d(redAutoConstants.far_left_stone_x_pos, redAutoConstants.far_left_stone_y_pos))
                                .addMarker(2.2, ()->{v4b.AutoWait(); return Unit.INSTANCE;})
                                .addMarker(1.0, ()->{intake.setOn(); return Unit.INSTANCE;})
                            .forward(6)
                            .build()
                );

                sleep(200);

                // Back under skybridge

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .reverse()
                                .addMarker(1.8, ()->{v4b.AutoGrab();return Unit.INSTANCE;})
                                .addMarker(2.4, ()->{intake.setOff();return Unit.INSTANCE;})
                                .splineTo(new Pose2d(redAutoConstants.under_skybridge_spline_x, redAutoConstants.under_skybrige_spline_y, Math.toRadians(90)))
                                .build()
                );

                // Placing second stone

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .back(30)
                            .addMarker(0.8, ()->{v4b.AutoSetLowScoring(); return Unit.INSTANCE;})
                            .addMarker(1.2, ()->{v4b.nubSetOpen(); return Unit.INSTANCE;})
                            .addMarker(1.5, ()->{v4b.AutoSetUnderSkybridge(); return Unit.INSTANCE;})
                            .build()
                );

                // Pushing foundation into wall and parking

                drive.turnSync(-15);

                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                            .back(30)
                            .splineTo(new Pose2d(redAutoConstants.under_skybridge_spline_x, redAutoConstants.under_skybrige_spline_y, Math.toRadians(redAutoConstants.foundation_heading)))
                            .build()
                );


                break;

            case MIDDLE:
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.2, ()->{intake.setOn();return Unit.INSTANCE;})
                                .addMarker(0.5, ()->{v4b.AutoWait();return Unit.INSTANCE;})
                                .strafeTo(new Vector2d(redAutoConstants.middle_stone_x_pos, redAutoConstants.middle_stone_y_pos))
                                .build()

                );

                break;

            case RIGHT:
                drive.followTrajectorySync(
                        drive.trajectoryBuilder()
                                .addMarker(0.2, ()->{intake.setOn();return Unit.INSTANCE;})
                                .addMarker(0.5, ()->{v4b.AutoWait();return Unit.INSTANCE;})
                                .strafeTo(new Vector2d(redAutoConstants.right_stone_x_pos, redAutoConstants.right_stone_y_pos))
                                .build()

                );

                break;
        }

        sleep(2000);


    }
}
