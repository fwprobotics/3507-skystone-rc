package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
Class for controlling foundation hooks on robot. Includes
functions for teleop toggling and autonomous position setting. By Jake, Dec. 2019.
 */

public class FoundationHooks {

    public Servo leftFoundationServo;
    public Servo rightFoundationServo;
    public LinearOpMode l;
    public Telemetry realTelemetry;

    public enum hookPositions {
        up, down
    }

    private hookPositions hook_pos = hookPositions.down;

    private boolean aButtonDown;


    public static class FoundationHookConstants {

        public static double left_open_pos = 1;
        public static double left_closed_pos = 0;

        public static double right_open_pos = 1;
        public static double right_closed_pos = 0;
    }

    public FoundationHooks(LinearOpMode Input, HardwareMap hardwareMap, Telemetry telemetry){

        l = Input;
        realTelemetry = telemetry;

        leftFoundationServo = hardwareMap.servo.get("leftFoundationServo");
        leftFoundationServo.setDirection(Servo.Direction.REVERSE);

        rightFoundationServo = hardwareMap.servo.get("rightFoundationServo");
    }

    public void toggleHooks(boolean inputButton){
        if (inputButton && !aButtonDown) {
            switch (hook_pos) {
                case down:
                    leftFoundationServo.setPosition(FoundationHookConstants.left_open_pos);
                    rightFoundationServo.setPosition(FoundationHookConstants.right_open_pos);
                    hook_pos = hookPositions.up;
                    aButtonDown = true;
                    break;
                case up:
                    leftFoundationServo.setPosition(FoundationHookConstants.left_closed_pos);
                    rightFoundationServo.setPosition(FoundationHookConstants.right_closed_pos);
                    hook_pos = hookPositions.down;
                    aButtonDown = true;
                    break;
            }
        }

        if (!inputButton) {
            aButtonDown = false;
        }
    }

    public void open(){
        leftFoundationServo.setPosition(FoundationHookConstants.left_open_pos);
        rightFoundationServo.setPosition(FoundationHookConstants.right_open_pos);
        hook_pos = hookPositions.up;
    }

    public void close(){
        leftFoundationServo.setPosition(FoundationHookConstants.left_closed_pos);
        rightFoundationServo.setPosition(FoundationHookConstants.right_closed_pos);
        hook_pos = hookPositions.down;
    }

}
