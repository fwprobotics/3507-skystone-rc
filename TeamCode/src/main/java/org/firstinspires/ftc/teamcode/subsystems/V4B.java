package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
Class for controlling robot's virtual 4-bar. Includes
control for nub and movement of 4-bar. By Jake, 1/27/20.
 */

public class V4B {

    public Servo servo1;
    public Servo servo2;
    public Servo nubHolderServo;

    public LinearOpMode l;
    public Telemetry realTelemetry;

    public enum NubServoStatuses {
        open, closed
    }

    public NubServoStatuses nubHolderServoStatus = NubServoStatuses.open;
    private boolean nubInputButtonPressed;

    public enum v4bStatuses {
        inside, low_scoring, waiting, high_scoring
    }

    public v4bStatuses v4bStatus = v4bStatuses.inside;
    private boolean v4bInputButtonPressed;
    private boolean HighPositionButtonPressed;
    private boolean LowPositionButtonPressed;
    private boolean ReleaseButtonPressed;

    public double setpos;


    public static class V4BConstants {

        public static double v4b_speed = 0.007; // How much to move V4B servos each loop

        public static double nub_open_pos = 0.32;
        public static double nub_closed_pos = 0;

        public static double v4b_low_scoring_pos = .89; // Outside robot limit
        public static double v4b_inside_pos = 0.05; // Inside robot limit
        public static double v4b_waiting_pos = 0.17; // Waiting to clamp a block inside the robot
        public static double v4b_high_scoring_pos = 0.7; // Height for lift down, 3 blocks high

    }

    public V4B(LinearOpMode Input, HardwareMap hardwareMap, Telemetry telemetry){

        l = Input;
        realTelemetry = telemetry;

        nubHolderServo = hardwareMap.servo.get("nubHolderServo"); // Servo to grab block

        servo1 = hardwareMap.servo.get("servo1"); // Really not important which is which
        servo2 = hardwareMap.servo.get("servo2");

        servo1.setPosition(V4BConstants.v4b_inside_pos);
        servo2.setPosition(V4BConstants.v4b_inside_pos);

        Grab();
        nubSetOpen();

    }

    public void nubGrabberControl(boolean inputButton){
        if (inputButton && nubHolderServoStatus.equals(NubServoStatuses.closed) && !nubInputButtonPressed) { // Opening
            nubHolderServo.setPosition(V4BConstants.nub_open_pos);
            nubInputButtonPressed = true;
            nubHolderServoStatus = NubServoStatuses.open;
        } else if (inputButton && nubHolderServoStatus.equals(NubServoStatuses.open) && !nubInputButtonPressed) { // Closing
            nubHolderServo.setPosition(V4BConstants.nub_closed_pos);
            nubHolderServoStatus = NubServoStatuses.closed;
            nubInputButtonPressed = true;
        }
        if (!inputButton) {
            nubInputButtonPressed = false;
        }
    }

    public void nubSetOpen(){
        nubHolderServo.setPosition(V4BConstants.nub_open_pos);
        nubHolderServoStatus = NubServoStatuses.open;
    }

    public void nubSetClosed(){
        nubHolderServo.setPosition(V4BConstants.nub_closed_pos);
        nubHolderServoStatus = NubServoStatuses.closed;
    }

    // TELEOP FUNCTIONS

    public void Grab(){
        v4bStatus = v4bStatuses.inside;
        setpos = V4BConstants.v4b_inside_pos;
        nubSetOpen();
    }

    public void Wait(){
        v4bStatus = v4bStatuses.waiting;
        setpos = V4BConstants.v4b_waiting_pos;
    }

    public void SetHighScoring(){
        v4bStatus = v4bStatuses.high_scoring;
        setpos = V4BConstants.v4b_high_scoring_pos;
    }

    public void SetLowScoring(){
        v4bStatus = v4bStatuses.low_scoring;
        setpos = V4BConstants.v4b_low_scoring_pos;
    }

    // AUTONOMOUS FUNCTIONS

    public void AutoGrab(){
        v4bStatus = v4bStatuses.inside;
        servo1.setPosition(V4BConstants.v4b_inside_pos);
        servo2.setPosition(V4BConstants.v4b_inside_pos);
        nubSetClosed();
    }

    public void AutoWait(){
        v4bStatus = v4bStatuses.waiting;
        servo1.setPosition(V4BConstants.v4b_waiting_pos);
        servo2.setPosition(V4BConstants.v4b_waiting_pos);
    }

    public void AutoSetHighScoring(){
        v4bStatus = v4bStatuses.high_scoring;
        setpos = V4BConstants.v4b_high_scoring_pos;
    }

    public void AutoSetLowScoring(){
        v4bStatus = v4bStatuses.low_scoring;
        servo1.setPosition(V4BConstants.v4b_low_scoring_pos);
        servo2.setPosition(V4BConstants.v4b_low_scoring_pos);
    }


    public void move4Bar(){
        double currentPos = servo1.getPosition();

        if (currentPos < setpos){
            servo1.setPosition(currentPos + V4BConstants.v4b_speed);
            servo2.setPosition(currentPos + V4BConstants.v4b_speed);
        } else if (currentPos > setpos){
            servo1.setPosition(currentPos - V4BConstants.v4b_speed);
            servo2.setPosition(currentPos - V4BConstants.v4b_speed);
        }

        if (currentPos < V4BConstants.v4b_inside_pos + .05) {
            nubSetClosed();
        }
    }

    public void startTeleop(){
        Wait();
        nubSetOpen();
    }

    public void v4bReleaseOrGrab(boolean inputButton){
        if (inputButton && !v4bInputButtonPressed) {
            switch (v4bStatus) {
                case inside:
                    v4bInputButtonPressed = true;
                    Wait();
                    nubSetOpen();
                    break;
                case low_scoring:
                case high_scoring:
                    nubSetOpen();
                    v4bInputButtonPressed = true;
                    Wait();
                    break;
                case waiting:
                    v4bInputButtonPressed = true;
                    Grab();
                    break;
            }
        }

        if (!inputButton) {
            v4bInputButtonPressed = false;
        }
    }

    public void HighPositionButton(boolean inputButton){
        if (inputButton && !HighPositionButtonPressed) {
            SetHighScoring();
            v4bInputButtonPressed = true;
        }

        if (!inputButton) {
            HighPositionButtonPressed = false;
        }
    }

    public void LowPositionButton(boolean inputButton){
        if (inputButton && !LowPositionButtonPressed) {
            SetLowScoring();
            LowPositionButtonPressed = true;
        }

        if (!inputButton) {
            LowPositionButtonPressed = false;
        }
    }


}
