package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.FoundationHooks;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Lift;
import org.firstinspires.ftc.teamcode.subsystems.V4B;
import org.firstinspires.ftc.teamcode.subsystems.Capstone;

@TeleOp(name = "MecanumDrive", group = "TeleOp")
public class MecanumDrive extends LinearOpMode {

    FoundationHooks foundationHooks;
    Drivetrain drivetrain;
    Lift lift;
    Intake intake;
    V4B v4b;
    Capstone capstone;

    @Override
    public void runOpMode() {

        foundationHooks = new FoundationHooks(this, hardwareMap, telemetry);
        drivetrain = new Drivetrain(this, hardwareMap, telemetry);
        lift = new Lift(Lift.liftRunMode.TELEOP, this, hardwareMap, telemetry);
        intake = new Intake(this, hardwareMap, telemetry);
        v4b = new V4B(this, hardwareMap, telemetry);
        capstone = new Capstone(this, hardwareMap, telemetry);

        // Ensuring correct subsystem statuses
        foundationHooks.open();
        v4b.nubSetOpen();

        telemetry.addLine("Ready and WAITING");
        telemetry.update();

        waitForStart();
        telemetry.clearAll();

        if (opModeIsActive()) {

            telemetry.clearAll();

            v4b.startTeleop();

            while (opModeIsActive()) {

                drivetrain.JoystickMovement(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

                foundationHooks.toggleHooks(gamepad1.y);

                intake.toggleIntake(gamepad1.b);
                intake.reverseIntake(gamepad1.a);
                intake.runIntake();

                lift.teleOpControl(gamepad2.right_stick_y);

                v4b.v4bReleaseOrGrab(gamepad2.a);
                v4b.HighPositionButton(gamepad2.dpad_up);
                v4b.LowPositionButton(gamepad2.dpad_down);

                v4b.move4Bar();
                
                capstone.control(gamepad2.back);

                telemetry.addData("Capstone Status:", capstone.capstoneStatus);
                telemetry.addData("State:", v4b.v4bStatus.toString());
                telemetry.addData("Current Pos:", v4b.setpos);

                telemetry.update();

            }
        }
    }
}
