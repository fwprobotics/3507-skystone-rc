package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.motors.RevRoboticsCoreHexMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
Class for controlling robot's intake. Very simple state machine for toggling on/off.
Probably will add some direction control later, just in case. Includes toggling for teleop
and set control for autonomous. By Jake, 1/27/20.
 */

public class Intake {

    public DcMotor leftIntakeMotor, rightIntakeMotor;
    public Servo pusherServoLeft;
    public Servo pusherServoRight;
    public LinearOpMode l;
    public Telemetry realTelemetry;

    public enum intakeStatuses {
        on, off
    }
    
    public enum intakeDirections {
        FORWARD, REVERSE
    }

    private intakeStatuses intakeStatus = intakeStatuses.off;
    public intakeDirections intakeDirection = intakeDirections.FORWARD;
    private boolean inputButtonPressed;
    private boolean inputButtonPressed2;
    private int direction = 1;

    private static final MotorConfigurationType MOTOR_CONFIG =
            MotorConfigurationType.getMotorType(RevRoboticsCoreHexMotor.class);



    public static class IntakeConstants {
        public static double intake_power = -0.9;

    }


    public Intake(LinearOpMode Input, HardwareMap hardwareMap, Telemetry telemetry){

        l = Input;
        realTelemetry = telemetry;
        realTelemetry.setAutoClear(true);

        leftIntakeMotor = hardwareMap.dcMotor.get("leftIntakeMotor");
        rightIntakeMotor = hardwareMap.dcMotor.get("rightIntakeMotor");
        pusherServoLeft = hardwareMap.servo.get("rubberServoLeft");
        pusherServoRight = hardwareMap.servo.get("rubberServoRight");


        leftIntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightIntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        pusherServoRight.setDirection(Servo.Direction.REVERSE);

    }

    public void toggleIntake(boolean inputButton){
        if (inputButton && !inputButtonPressed) {
            switch (intakeStatus) {
                case off:
                    leftIntakeMotor.setPower(IntakeConstants.intake_power * direction);
                    rightIntakeMotor.setPower(-0.75 * direction);

                    inputButtonPressed = true;
                    intakeStatus = intakeStatuses.on;

                    pusherServoLeft.setPosition(1 * direction);
                    pusherServoRight.setPosition(1 * direction); // Turn to keep block inside
                    break;
                case on:
                    leftIntakeMotor.setPower(0);
                    rightIntakeMotor.setPower(0);

                    inputButtonPressed = true;
                    intakeStatus = intakeStatuses.off;

                    pusherServoLeft.setPosition(0.5); // Stop
                    pusherServoRight.setPosition(0.5);
                    break;
            }
        }

        if (!inputButton) {
            inputButtonPressed = false;
        }
    }
    
    public void reverseIntake(boolean inputButton){
        if (inputButton && !inputButtonPressed2) {
            switch (intakeDirection) {
                case FORWARD:
                    direction = 1;
                    intakeDirection = intakeDirections.REVERSE;

                    break;
                case REVERSE:
                    direction = -1;
                    intakeDirection = intakeDirections.FORWARD;
                    break;
            }
        }

        if (!inputButton) {
            inputButtonPressed2 = false;
        }
    }

    public void setOn(){
        leftIntakeMotor.setPower(IntakeConstants.intake_power);
        rightIntakeMotor.setPower(IntakeConstants.intake_power);
        pusherServoLeft.setPosition(1);
        pusherServoRight.setPosition(1);

        intakeStatus = intakeStatuses.on;
    }

    public void setOff(){
        leftIntakeMotor.setPower(0);
        rightIntakeMotor.setPower(0);
        pusherServoLeft.setPosition(0.5);
        pusherServoRight.setPosition(0.5);

        intakeStatus = intakeStatuses.off;
    }

    public void release(){
        pusherServoLeft.setPosition(1);
        pusherServoRight.setPosition(1);
        l.sleep(200);
        pusherServoLeft.setPosition(0.5);
        pusherServoRight.setPosition(0.5);

    }

}
