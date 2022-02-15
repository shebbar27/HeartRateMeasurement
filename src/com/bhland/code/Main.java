package com.bhland.code;

import java.io.File;

public class Main {

    private final static String VIDEO_FILE_PATH = "../res/FingerTipVideo.mp4";

    public static void main(String[] args) {
        File videoFile = new File(VIDEO_FILE_PATH);
        int heartRate = MeasureHeartRateUtility.ComputeHeartRate(videoFile);
        System.out.println("The measure heart rate is: " + heartRate);
    }
}
