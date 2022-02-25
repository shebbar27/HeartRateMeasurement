package com.bhland.code;

import java.io.File;

public class Main {

    private final static String VIDEO_FILE_PATH = "res/FingerTipVideo";

    public static void main(String[] args) {
        File videoFile = new File(VIDEO_FILE_PATH);
        Float heartRate = MeasureHeartRateUtility.computeHeartRate(videoFile);
        System.out.println("The measured heart rate is: " + heartRate);
    }
}
