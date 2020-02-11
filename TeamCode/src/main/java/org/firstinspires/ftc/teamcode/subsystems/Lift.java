package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
Class for controlling lift on robot. WIP, teleop should
be okay currently - but no encoders sadly, hopefully it'll
work out. By Jake, 1/27/20.
 */


public class Lift{

    public DcMotor leftLiftMotor;
    public DcMotor rightLiftMotor;
    public LinearOpMode l;
    public Telemetry realTelemetry;

    public enum liftRunMode {
        AUTONOMOUS,
        TELEOP
    }

    public Lift(liftRunMode runmode, LinearOpMode Input, HardwareMap hardwareMap, Telemetry telemetry){

        l = Input;
        realTelemetry = telemetry;

        leftLiftMotor = hardwareMap.dcMotor.get("leftLiftMotor");
        rightLiftMotor = hardwareMap.dcMotor.get("rightLiftMotor");

        // Different motor configurations depending on use case
        if (runmode.equals(liftRunMode.AUTONOMOUS)){
            leftLiftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // This wont work rn with current encoder setup
            leftLiftMotor.setTargetPosition(0);
            leftLiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        if (runmode.equals(liftRunMode.TELEOP)){
            leftLiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            leftLiftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            leftLiftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

            rightLiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rightLiftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }

    }

    public void setPosition(int position){
        leftLiftMotor.setTargetPosition(position);
        leftLiftMotor.setPower(1);
        leftLiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void teleOpControl(double input){
        leftLiftMotor.setPower(input);
        rightLiftMotor.setPower(input);
    }


}
