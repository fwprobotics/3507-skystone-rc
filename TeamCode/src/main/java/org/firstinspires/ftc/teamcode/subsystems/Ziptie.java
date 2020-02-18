package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Ziptie {

    public Servo ziptieServo;

    public ZiptieStatuses ziptieStatus;

    private boolean inputButtonPressed;

    public LinearOpMode l;
    public Telemetry realTelemetry;

    public enum ZiptieStatuses {
        DEPLOYED,
        UNDEPLOYED
    }

    @Config
    public static class ZiptieConstants {

        public static double in_pos = 0;
        public static double out_pos = 1;

    }

    public Ziptie(LinearOpMode Input, HardwareMap hardwareMap, Telemetry telemetry){

        l = Input;
        realTelemetry = telemetry;

        ziptieServo = hardwareMap.servo.get("ziptieServo");
        ziptieServo.setPosition(ZiptieConstants.in_pos);

        ziptieStatus = ZiptieStatuses.UNDEPLOYED;

    }

    public void control(boolean inputButton){
        if (inputButton & !inputButtonPressed) {
            inputButtonPressed = true;
            switch (ziptieStatus) {
                case UNDEPLOYED:
                    ziptieServo.setPosition(ZiptieConstants.out_pos);
                    ziptieStatus = ZiptieStatuses.DEPLOYED;
                    break;

                case DEPLOYED:
                    ziptieServo.setPosition(ZiptieConstants.in_pos);
                    ziptieStatus = ZiptieStatuses.UNDEPLOYED;
                    break;

            }
        }

        else {
            inputButtonPressed = false;
        }

    }
}
