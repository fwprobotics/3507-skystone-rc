package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.motors.RevRobotics20HdHexMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
Drivetrain subsystem. Currently holds teleop driving control. By Jake B, 2019.
 */

public class Drivetrain {

    private DcMotor frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive;

    public LinearOpMode l;
    public Telemetry realTelemetry;

    private boolean inputButtonPressed;

    private static final MotorConfigurationType MOTOR_CONFIG =
            MotorConfigurationType.getMotorType(RevRobotics20HdHexMotor.class);

    @Config
    public static class TeleOpDTConstants {
        public static double turning_modifier = 0.85;
        public static double y_modifier = 0.87;
        public static double x_modifier = 0.88;

    }


    public Drivetrain(LinearOpMode Input, HardwareMap hardwareMap, Telemetry telemetry){

        l = Input;
        realTelemetry = telemetry;
        realTelemetry.setAutoClear(true);

        backLeftDrive = hardwareMap.dcMotor.get("backLeftDrive");
        backRightDrive = hardwareMap.dcMotor.get("backRightDrive");
        frontLeftDrive = hardwareMap.dcMotor.get("frontLeftDrive");
        frontRightDrive = hardwareMap.dcMotor.get("frontRightDrive");

        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);

        l.idle();
    }

    public void JoystickMovement(double leftStickY, double leftStickX, double rightStickX){

        //Sets motor values based on adding and subtracting joystick values
        double LeftX = -leftStickX * TeleOpDTConstants.x_modifier;
        double LeftY = -leftStickY * TeleOpDTConstants.y_modifier;
        double RightX = -rightStickX * TeleOpDTConstants.turning_modifier;

        double frontLeftVal = ((LeftY - RightX) - LeftX);
        double frontRightVal = ((LeftY + RightX) + LeftX);
        double backLeftVal = ((LeftY - RightX) + LeftX);
        double backRightVal = ((LeftY + RightX) - LeftX);

        frontLeftDrive.setPower(frontLeftVal);
        frontRightDrive.setPower(frontRightVal);
        backLeftDrive.setPower(backLeftVal);
        backRightDrive.setPower(backRightVal);
    }
}