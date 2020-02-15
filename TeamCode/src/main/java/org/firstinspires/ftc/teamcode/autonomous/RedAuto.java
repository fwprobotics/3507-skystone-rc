package org.firstinspires.ftc.teamcode.autonomous;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.LED;

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
        public static double left_stone_x_pos = 0;
        public static double left_stone_y_pos = 0;

        public static double middle_stone_x_pos = 0;
        public static double middle_stone_y_pos = 0;

        public static double right_stone_x_pos = 20;
        public static double right_stone_y_pos = 0;

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
                                .strafeTo(new Vector2d(redAutoConstants.left_stone_x_pos, redAutoConstants.left_stone_y_pos))
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



    }
}
