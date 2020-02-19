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

    private DcMotor leftIntakeMotor, rightIntakeMotor;
    private Servo pusherServoLeft;
    private Servo pusherServoRight;
    private LinearOpMode l;
    private Telemetry realTelemetry;

    public enum intakeStatuses {
        ON,
        OFF
    }
    
    public enum intakeDirections {
        FORWARD,
        REVERSE
    }

    private intakeStatuses intakeStatus = intakeStatuses.OFF;
    public intakeDirections intakeDirection = intakeDirections.FORWARD;
    private boolean inputButtonPressed;
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

    // TELEOP FUNCTIONS

    // Toggle on and off intake via inputButton
    public void toggleIntake(boolean inputButton){
        if (inputButton && !inputButtonPressed) {
            switch (intakeStatus) {
                case OFF:
                    inputButtonPressed = true;
                    intakeStatus = intakeStatuses.ON;
                    break;
                case ON:
                    inputButtonPressed = true;
                    intakeStatus = intakeStatuses.OFF;
                    break;
            }
        }

        if (!inputButton) {
            inputButtonPressed = false;
        }
    }

    // Sets power of intake depending on direction
    public void runIntake(){
        if (intakeStatus == intakeStatuses.ON) {
            leftIntakeMotor.setPower(IntakeConstants.intake_power * direction);
            rightIntakeMotor.setPower(-0.75 * direction);

            pusherServoLeft.setPosition(1 * direction);
            pusherServoRight.setPosition(1 * direction);

        }
        else if (intakeStatus == intakeStatuses.OFF) {
            leftIntakeMotor.setPower(0);
            rightIntakeMotor.setPower(0);

            pusherServoLeft.setPosition(0.5);
            pusherServoRight.setPosition(0.5);
        }
    }

    // While input button is held down intake is reversed
    public void directionControl(boolean inputButton) {
        if (!inputButton) {
            direction = 1;
            intakeDirection = intakeDirections.FORWARD;
        } else {
            direction = -1;
            intakeDirection = intakeDirections.REVERSE;
        }

    }

    // AUTONOMOUS FUNCTIONS

    public void setOn(){
        leftIntakeMotor.setPower(IntakeConstants.intake_power);
        rightIntakeMotor.setPower(-0.75);
        pusherServoLeft.setPosition(1);
        pusherServoRight.setPosition(1);

        intakeStatus = intakeStatuses.ON;
    }

    public void setOnReversed(){
        leftIntakeMotor.setPower(-IntakeConstants.intake_power);
        rightIntakeMotor.setPower(0.75);
        pusherServoLeft.setPosition(-1);
        pusherServoRight.setPosition(-1);

        intakeStatus = intakeStatuses.ON;
    }

    public void setOff(){
        leftIntakeMotor.setPower(0);
        rightIntakeMotor.setPower(0);
        pusherServoLeft.setPosition(0.5);
        pusherServoRight.setPosition(0.5);

        intakeStatus = intakeStatuses.OFF;
    }

    public void release(){
        pusherServoLeft.setPosition(1);
        pusherServoRight.setPosition(1);
        l.sleep(200);
        pusherServoLeft.setPosition(0.5);
        pusherServoRight.setPosition(0.5);

    }

}
