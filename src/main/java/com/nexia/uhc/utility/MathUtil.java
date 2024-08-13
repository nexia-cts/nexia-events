package com.nexia.uhc.utility;

import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Random;

public abstract class MathUtil {
    public static String convertTicksToTime(int ticks) {
        int totalSeconds = ticks / 20;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;

        return String.format(
                "%s:%s",
                (minutes <= 9) ? "0" + minutes : minutes,
                (seconds <= 9) ? "0" + seconds : seconds
        );
    }

    public static ArrayList<int[]> createInitialPositions(Random random, int numberOfPositions, int lowerLimit, int upperLimit) {
        ArrayList<int[]> positions = new ArrayList<>(numberOfPositions);

        for(int j = 0; j < numberOfPositions; ++j) {
            int[] ints = {Mth.nextInt(random, lowerLimit, upperLimit), Mth.nextInt(random, lowerLimit, upperLimit)};
            positions.add(ints);
        }

        return positions;
    }
}
