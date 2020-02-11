package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Capstone {

    public Servo capstoneServo;

    public CapstoneStatuses capstoneStatus;

    public LinearOpMode l;
    public Telemetry realTelemetry;

    public enum CapstoneStatuses {
        LOCKED,
        UNLOCKED,
        PLACED
    }

    public static class CapstoneConstants {

        public static double down_pos = 0;
        public static double up_pos = 0.8;

    }

    public Capstone(LinearOpMode Input, HardwareMap hardwareMap, Telemetry telemetry){

        l = Input;
        realTelemetry = telemetry;

        capstoneServo = hardwareMap.servo.get("capstoneServo");
        capstoneServo.setPosition(CapstoneConstants.up_pos);

        capstoneStatus = CapstoneStatuses.LOCKED;

    }

    public void control(boolean inputButton){
        if (inputButton) {
            switch (capstoneStatus){
                case LOCKED:
                    capstoneStatus = CapstoneStatuses.UNLOCKED;
                    realTelemetry.speak("Capstone Unlocked");
                    break;

                case UNLOCKED:
                    capstoneServo.setPosition(CapstoneConstants.down_pos);
                    capstoneStatus = CapstoneStatuses.PLACED;
                    break;

            }
        }
    }

}
