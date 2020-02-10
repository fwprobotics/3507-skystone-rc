package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Capstone {

    public Servo capstoneServo;

    public LinearOpMode l;
    public Telemetry realTelemetry;

    public enum CapstoneStatus {
        LOCKED, UNLOCKED
    }

    public CapstoneStatus capstoneStatus = CapstoneStatus.LOCKED;

    private boolean aButtonDown;

    public static class CapstoneConstants {

        public static double down_pos = 0;
        public static double up_pos = 0.8;

    }

    public Capstone(LinearOpMode Input, HardwareMap hardwareMap, Telemetry telemetry){

        l = Input;
        realTelemetry = telemetry;

        capstoneServo = hardwareMap.servo.get("capstoneServo");
        capstoneServo.setPosition(CapstoneConstants.up_pos);

    }

    public void unlockCapstone(boolean inputButton){
        if (inputButton){
            capstoneStatus = CapstoneStatus.UNLOCKED;
        }
    }

    public void placeCapstone(boolean inputButton){
        if (inputButton && capstoneStatus == CapstoneStatus.UNLOCKED){
            capstoneServo.setPosition(CapstoneConstants.down_pos);
        }
    }
    

}
