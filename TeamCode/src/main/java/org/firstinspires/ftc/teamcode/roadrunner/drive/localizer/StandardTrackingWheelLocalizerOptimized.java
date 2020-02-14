package org.firstinspires.ftc.teamcode.roadrunner.drive.localizer;

import android.support.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.openftc.revextensions2.ExpansionHubEx;
import org.openftc.revextensions2.ExpansionHubMotor;
import org.openftc.revextensions2.RevBulkData;

import java.util.Arrays;
import java.util.List;

/*
 * Sample tracking wheel localizer implementation assuming the standard configuration:
 *
 *    /--------------\
 *    |     ____     |
 *    |     ----     |
 *    | ||        || |
 *    | ||        || |
 *    |              |
 *    |              |
 *    \--------------/
 *
 * Note: this could be optimized significantly with REV bulk reads
 */

public class StandardTrackingWheelLocalizerOptimized extends ThreeTrackingWheelLocalizer {
    public static double TICKS_PER_REV = 8192;
    public static double WHEEL_RADIUS = 1; // in
    public static double GEAR_RATIO = 1.31; // output (wheel) speed / input (encoder) speed 1.2857
    public static double FRONT_ENCODER_TUNING = 1;

    public static double LATERAL_DISTANCE = 14.5; // in; distance between the left and right wheels
    public static double FORWARD_OFFSET = -5; // in; offset of the lateral wheel

    RevBulkData bulkDataLeft;
    RevBulkData bulkDataRight;
    ExpansionHubMotor leftEncoder, rightEncoder, frontEncoder;
    ExpansionHubEx expansionHubLeft;
    ExpansionHubEx expansionHubRight;

    public StandardTrackingWheelLocalizerOptimized(HardwareMap hardwareMap) {
        super(Arrays.asList(
                new Pose2d(0, LATERAL_DISTANCE / 2, 0), // left
                new Pose2d(0, -LATERAL_DISTANCE / 2, 0), // right
                new Pose2d(FORWARD_OFFSET, 0, Math.toRadians(90)) // front
        ));

        expansionHubLeft = hardwareMap.get(ExpansionHubEx.class, "Left Hub");
        expansionHubLeft = hardwareMap.get(ExpansionHubEx.class, "Right Hub");


        leftEncoder = (ExpansionHubMotor) hardwareMap.dcMotor.get("leftIntakeMotor");
        rightEncoder = (ExpansionHubMotor) hardwareMap.dcMotor.get("rightIntakeMotor");
        frontEncoder = (ExpansionHubMotor) hardwareMap.dcMotor.get("leftLiftMotor");
    }

    public static double encoderTicksToInches(int ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    public static double frontEncoderTicksToInches(int ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * FRONT_ENCODER_TUNING * ticks / TICKS_PER_REV;
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        return Arrays.asList(
                encoderTicksToInches(bulkDataLeft.getMotorCurrentPosition(leftEncoder)),
                encoderTicksToInches(bulkDataRight.getMotorCurrentPosition(rightEncoder)),
                frontEncoderTicksToInches(bulkDataLeft.getMotorCurrentPosition(frontEncoder))
        );
    }
}
