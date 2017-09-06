package com.tofti.zcs;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ZCSUtility {

    public static double round(final double d, final double increment) {
        return ((int)((d + 0.5 * increment) / increment) * increment);
    }

    public static String getFileName(String prefix) {
        // "yyyy.MMMMM.dd.hh:mm.ss.SS"
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yy-HH_mm_ss_SS");
        return prefix + "-" + fmt.format(new Date());
    }

    public static double[] calculateMovingAverage(int[] d, int n) {
        double[] movingAvg = new double[d.length];
        int sum = 0;
        for(int i = 0; i < d.length; i++) {
            if(i < n) {
                sum = sum + d[i];
                movingAvg[i] = sum / (double)(i + 1);
            }
            else {
                sum = sum - d[i - n];
                sum = sum + d[i];
                movingAvg[i] = sum / (double)n;
            }
        }
        return movingAvg;
    }
}