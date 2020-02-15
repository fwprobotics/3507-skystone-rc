package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.motors.RevRobotics20HdHexMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.Telemetry;


public class Drivetrain {

    private DcMotor frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive;
    
    public enum DriveDirections {
        STANDARD, REVERSE
    }

    public LinearOpMode l;
    public Telemetry realTelemetry;
    
    public DriveDirections driveDirection = DriveDirections.STANDARD;
    private int direction = 1;
    private boolean inputButtonPressed;

    private static final MotorConfigurationType MOTOR_CONFIG =
            MotorConfigurationType.getMotorType(RevRobotics20HdHexMotor.class);


    public static class TeleOpDTConstants {
        public static double turning_modifier = 0.8;
        public static double y_modifier = 0.9;
        public static double x_modifier = 0.9;

    }


    public Drivetrain(LinearOpMode Input, HardwareMap hardwareMap, Telemetry telemetry){

        l = Input;
        realTelemetry = telemetry;
        realTelemetry.setAutoClear(true);

        backLeftDrive = hardwareMap.dcMotor.get("backLeftDrive");
        backRightDrive = hardwareMap.dcMotor.get("backRightDrive");
        frontLeftDrive = hardwareMap.dcMotor.get("frontLeftDrive");
        frontRightDrive = hardwareMap.dcMotor.get("frontRightDrive");

//        backLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        backRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        frontLeftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//        frontRightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        l.idle();
    }

    public void JoystickMovement(double leftStickY, double leftStickX, double rightStickX){

        //Sets motor values based on adding and subtracting joystick values
        double LeftX = -leftStickX * TeleOpDTConstants.x_modifier * direction;
        double LeftY = -leftStickY * TeleOpDTConstants.y_modifier * direction;
        // double LeftY = (LeftY / 1.07) * (0.62 * (LeftY * LeftY) + 0.45);;
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
    
    public void toggleYDirection(boolean inputButton){
        if (inputButton && !inputButtonPressed) {
            switch (driveDirection) {
                case STANDARD:
                    direction = -1;
                    driveDirection = DriveDirections.REVERSE;
                    break;
                case REVERSE:
                    direction = 1;
                    driveDirection = DriveDirections.STANDARD;
                    break;
            }
        }

        if (!inputButton) {
            inputButtonPressed = false;
        }
    }

}
