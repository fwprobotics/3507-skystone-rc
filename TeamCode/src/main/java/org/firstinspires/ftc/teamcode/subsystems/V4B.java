package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/*
Class for controlling robot's virtual 4-bar. Includes
control for nub and movement of 4-bar. By Jake, 1/27/20.
 */

public class V4B {

    public Servo servo1;
    public Servo servo2;
    public Servo nubHolderServo;

    public DigitalChannel intakeSwitch;

    public LinearOpMode l;
    public Telemetry realTelemetry;
    private Intake intake;

    public enum NubServoStatuses {
        OPEN,
        CLOSED
    }

    public NubServoStatuses nubHolderServoStatus = NubServoStatuses.OPEN;
    private boolean nubInputButtonPressed;

    public enum v4bStatuses {
        INSIDE,
        LOW_SCORING,
        WAITING,
        HIGH_SCORING,
        MIDDLE
    }

    public v4bStatuses v4bStatus = v4bStatuses.INSIDE;
    private boolean v4bInputButtonPressed;
    private boolean HighPositionButtonPressed;
    private boolean LowPositionButtonPressed;

    public double setpos;


    public static class V4BConstants {

        public static double v4b_speed = 0.008; // How much to move V4B servos each loop

        public static double nub_open_pos = 0.32;
        public static double nub_closed_pos = 0;

        public static double v4b_low_scoring_pos = .89; // Outside robot limit
        public static double v4b_inside_pos = 0.05; // Inside robot limit
        public static double v4b_waiting_pos = 0.17; // Waiting to clamp a block INSIDE the robot
        public static double v4b_high_scoring_pos = 0.7; // Height for lift DOWN, 3 blocks high
        public static double v4b_middle_pos = 0.47; // Height for ~straigh up
        public static double v4b_under_skybridge_pos = 0.80; // Height to go under skybridge

    }

    public V4B(LinearOpMode Input, HardwareMap hardwareMap, Telemetry telemetry, Intake m_intake){

        l = Input;
        realTelemetry = telemetry;
        intake = m_intake;
        intakeSwitch = hardwareMap.digitalChannel.get("intakeSwitch");

        nubHolderServo = hardwareMap.servo.get("nubHolderServo"); // Servo to grab block

        servo1 = hardwareMap.servo.get("servo1"); // Really not important which is which
        servo2 = hardwareMap.servo.get("servo2");

        servo1.setPosition(V4BConstants.v4b_inside_pos);
        servo2.setPosition(V4BConstants.v4b_inside_pos);

        Grab();
        nubSetOpen();

    }

    public void nubGrabberControl(boolean inputButton){
        if (inputButton && nubHolderServoStatus.equals(NubServoStatuses.CLOSED) && !nubInputButtonPressed) { // Opening
            nubHolderServo.setPosition(V4BConstants.nub_open_pos);
            nubInputButtonPressed = true;
            nubHolderServoStatus = NubServoStatuses.OPEN;
        } else if (inputButton && nubHolderServoStatus.equals(NubServoStatuses.OPEN) && !nubInputButtonPressed) { // Closing
            nubHolderServo.setPosition(V4BConstants.nub_closed_pos);
            nubHolderServoStatus = NubServoStatuses.CLOSED;
            nubInputButtonPressed = true;
        }
        if (!inputButton) {
            nubInputButtonPressed = false;
        }
    }

    public void nubSetOpen(){
        nubHolderServo.setPosition(V4BConstants.nub_open_pos);
        nubHolderServoStatus = NubServoStatuses.OPEN;
    }

    public void nubSetClosed(){
        nubHolderServo.setPosition(V4BConstants.nub_closed_pos);
        nubHolderServoStatus = NubServoStatuses.CLOSED;
        intake.setOff();
    }

    // TELEOP FUNCTIONS

    public void Grab(){
        v4bStatus = v4bStatuses.INSIDE;
        setpos = V4BConstants.v4b_inside_pos;
        nubSetOpen();
    }

    public void Wait(){
        v4bStatus = v4bStatuses.WAITING;
        setpos = V4BConstants.v4b_waiting_pos;
    }

    public void SetMiddle(){
        v4bStatus = v4bStatuses.MIDDLE;
        setpos = V4BConstants.v4b_middle_pos;
    }

    public void SetHighScoring(){
        v4bStatus = v4bStatuses.HIGH_SCORING;
        setpos = V4BConstants.v4b_high_scoring_pos;
    }

    public void SetLowScoring(){
        v4bStatus = v4bStatuses.LOW_SCORING;
        setpos = V4BConstants.v4b_low_scoring_pos;
    }

    // AUTONOMOUS FUNCTIONS

    public void AutoGrab(){
        v4bStatus = v4bStatuses.INSIDE;
        servo1.setPosition(V4BConstants.v4b_inside_pos);
        servo2.setPosition(V4BConstants.v4b_inside_pos);
        nubSetClosed();
    }

    public void AutoWait(){
        v4bStatus = v4bStatuses.WAITING;
        servo1.setPosition(V4BConstants.v4b_waiting_pos);
        servo2.setPosition(V4BConstants.v4b_waiting_pos);
    }

    public void AutoSetHighScoring(){
        v4bStatus = v4bStatuses.HIGH_SCORING;
        setpos = V4BConstants.v4b_high_scoring_pos;
    }

    public void AutoSetLowScoring(){
        v4bStatus = v4bStatuses.LOW_SCORING;
        servo1.setPosition(V4BConstants.v4b_low_scoring_pos);
        servo2.setPosition(V4BConstants.v4b_low_scoring_pos);
    }

    public void AutoSetUnderSkybridge(){
        servo1.setPosition(V4BConstants.v4b_under_skybridge_pos);
        servo2.setPosition(V4BConstants.v4b_under_skybridge_pos);
    }

    // Actually moving the 4-bar
    public void move4Bar(){
        double currentPos = servo1.getPosition();

        if (currentPos < setpos){
            servo1.setPosition(currentPos + V4BConstants.v4b_speed);
            servo2.setPosition(currentPos + V4BConstants.v4b_speed);
        } else if (currentPos > setpos){
            servo1.setPosition(currentPos - V4BConstants.v4b_speed);
            servo2.setPosition(currentPos - V4BConstants.v4b_speed);
        }

        if (intakeSwitch.getState() && v4bStatus == v4bStatuses.WAITING) {
            Grab();
        }

        if (currentPos < V4BConstants.v4b_inside_pos + .05) {
            nubSetClosed();
        }
    }

    public void startTeleop(){
        Wait();
        nubSetOpen();
    }

    // Control Functions

    public void v4bReleaseOrGrab(boolean inputButton){
        if (inputButton && !v4bInputButtonPressed) {
            switch (v4bStatus) {
                case INSIDE:
                    v4bInputButtonPressed = true;
                    Wait();
                    nubSetOpen();
                    break;
                case LOW_SCORING:
                case HIGH_SCORING:
                    nubSetOpen();
                    v4bInputButtonPressed = true;
                    Wait();
                    break;
                case WAITING:
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
