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


@Autonomous(name = "Parking", group = "autonomous")
public class Parking extends LinearOpMode {

    FoundationHooks hooks;
    Intake intake;
    V4B v4b;
    Capstone capstone;

    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDriveBase drive = new SampleMecanumDriveREV(hardwareMap);
        hooks = new FoundationHooks(this, hardwareMap, telemetry);
        intake = new Intake(this, hardwareMap, telemetry);
        capstone = new Capstone(this, hardwareMap, telemetry, v4b);

        hooks.open();

        waitForStart();

        if (isStopRequested()) return;

        /* START MOVECODE */

        intake.release();

        drive.followTrajectorySync(
                drive.trajectoryBuilder()
                    .forward(10)
                    .build()
        );

    }
}
