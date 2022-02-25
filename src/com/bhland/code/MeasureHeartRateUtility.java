package com.bhland.code;

import java.io.File;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

public class MeasureHeartRateUtility {
    public static Float computeHeartRate(File videoFile) {
        if(!videoFile.exists()) {
            System.out.println("The video file does not exist or the video file path is invalid");
            return 0f;
        }

        // offset time to be used for discarding initial and last few seconds of the video in order to avoid
        // disturbance in the video being considered for calculations
        final float offset_milliseconds = 2000f;
        // values for computation
        // total duration of actual captured video
        final float totalDuration_ms = 14528f;
        // multiplier to convert seconds to microseconds
        final float microSecondsMultiplier = 1000000f;
        // multiplier to convert seconds to milliseconds
        final float milliSecondsMultiplier = 1000f;
        // for start time and end time discard first and last few seconds as provided by the
        // offset_milliseconds since it can have noise and disturbances due to the movement finger
        // and autofocus feature of camera
        // start time of the portion of the video considered for processing
        final float startTime_us = offset_milliseconds * milliSecondsMultiplier;
        // end time of the portion of the video considered for processing
        final float endTime_us = (totalDuration_ms - offset_milliseconds) * milliSecondsMultiplier;
        // frames per second of captured video
        final float fps = 25f;
        // total time span of the portion of the video considered for processing
        final float timeSpan_s = (endTime_us - startTime_us)/microSecondsMultiplier;
        // total number of frames considered for processing
        final float numberOfFrames = fps * timeSpan_s;
        // time increments at which frames are sampled
        final float sampleIncrement_us = microSecondsMultiplier/fps;
        // multiplier to convert heart rate per 1 minute
        final float heartRateMultiplier = timeSpan_s == 0? 0f : 60f/timeSpan_s;

        // 2D bitmap image subsampling parameters
        final int width = 100, height = 100, xSpacing = 10, ySpacing = 19;
        float i = startTime_us, j=0;
        ArrayList<Float> averageRedPixels = new ArrayList<>();
        while(j < numberOfFrames && i < endTime_us) {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Bitmap bitmap = retriever.getFrameAtTime((int)i, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            float redPixelsCumulative = 0f;
            // row of 2D bitmap image
            for(int x=0; x<width * xSpacing; x+=xSpacing) {
                // column of 2D bitmap image
                for(int y=0; y<height * ySpacing; y+=ySpacing) {
                    int pixel = bitmap.getPixel(x, y);
                    // to calculate weighted average of red, green, and blue pixel values with
                    // ratio 5:1:1, add all pixel values with the respective multipliers and divide
                    // by the 7
                    redPixelsCumulative += (((pixel >> 16) & 0xFF) * 5 + (pixel >> 8) & 0xFF + pixel & 0xFF)/7.0f;
                }
            }

            // divide the sum of all pixel values with the total number of pixels considered
            averageRedPixels.add(redPixelsCumulative/(height * width));
            i += sampleIncrement_us;
            j++;
        }

        final float threshold = 0.0001f;
        int heartRate = 0;
        Float prevValue = averageRedPixels.get(0);
        for(int k=1; k<averageRedPixels.size(); k++) {
            if(Math.abs(prevValue - averageRedPixels.get(k)) > threshold) {
                heartRate++;
                prevValue = averageRedPixels.get(k);
            }
        }

        return heartRate * heartRateMultiplier;
    }
}
