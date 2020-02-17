package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@Disabled
@TeleOp(group = "Testing")
public class EncoderTest extends LinearOpMode {

    private DcMotor leftEncoder, rightEncoder, frontEncoder;

    @Override
    public void runOpMode() {

        leftEncoder = hardwareMap.dcMotor.get("leftIntakeMotor");
        rightEncoder = hardwareMap.dcMotor.get("rightIntakeMotor");
        frontEncoder = hardwareMap.dcMotor.get("leftLiftMotor");

        telemetry.addLine("Ready and WAITING");
        telemetry.update();

        waitForStart();
        telemetry.clearAll();

        leftEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontEncoder.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        if (opModeIsActive()) {

            while (opModeIsActive()) {

                telemetry.addData("Left Encoder:", leftEncoder.getCurrentPosition());
                telemetry.addData("Right Encoder:", rightEncoder.getCurrentPosition());
                telemetry.addData("Front Encoder:", frontEncoder.getCurrentPosition());

                telemetry.update();

            }
        }
    }
}
