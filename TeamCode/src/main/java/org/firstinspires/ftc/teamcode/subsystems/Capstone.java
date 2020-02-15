package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.firstinspires.ftc.teamcode.subsystems.V4B;

public class Capstone {

    public Servo capstoneServo;

    private V4B v4b;

    public CapstoneStatuses capstoneStatus;

    private boolean inputButtonPressed;

    public LinearOpMode l;
    public Telemetry realTelemetry;

    public enum CapstoneStatuses {
        UNPLACED,
        PLACED
    }

    public static class CapstoneConstants {

        public static double down_pos = 0;
        public static double up_pos = 0.8;

    }

    public Capstone(LinearOpMode Input, HardwareMap hardwareMap, Telemetry telemetry, V4B m_v4b){

        l = Input;
        realTelemetry = telemetry;
        v4b = m_v4b;

        capstoneServo = hardwareMap.servo.get("capstoneServo");
        capstoneServo.setPosition(CapstoneConstants.up_pos);

        capstoneStatus = CapstoneStatuses.UNPLACED;

    }

    public void control(boolean inputButton){
        if (inputButton & !inputButtonPressed) {
            inputButtonPressed = true;
            if (v4b.intakeSwitch.getState()) {
                switch (capstoneStatus) {
                    case UNPLACED:
                        v4b.SetMiddle();
                        capstoneServo.setPosition(CapstoneConstants.down_pos);
                        l.sleep(200);
                        capstoneStatus = CapstoneStatuses.PLACED;
                        v4b.Grab();
                        break;

                    case PLACED:
                        capstoneServo.setPosition(CapstoneConstants.up_pos);
                        capstoneStatus = CapstoneStatuses.UNPLACED;
                        break;

                }

            }
        }

    }

}
