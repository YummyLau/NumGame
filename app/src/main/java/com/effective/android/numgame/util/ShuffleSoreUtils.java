package com.effective.android.numgame.util;

import android.support.annotation.NonNull;

/**
 * 创建随机答案
 * created bg yummylau
 * 2018-09-30
 */
public class ShuffleSoreUtils {

    private static final int[] ORIGIN = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    @NonNull
    public static int[] makeRandomResult() {
        for (int i = 0; i < ORIGIN.length - 1; i++) {
            int j = (int) (ORIGIN.length * Math.random());
            swap(ORIGIN, i, j);
        }
        int[] result = new int[4];
        result[0] = ORIGIN[0];
        result[1] = ORIGIN[1];
        result[2] = ORIGIN[2];
        result[3] = ORIGIN[3];
        return result;
    }

    /**
     * 交换两个数组
     *
     * @param data
     * @param i
     * @param j
     */
    private static void swap(int[] data, int i, int j) {
        if (data == null || data.length <= j || data.length <= i || i < 0 || j < 0 || i == j) {
            return;
        }
        data[i] = data[i] + data[j];
        data[j] = data[i] - data[j];
        data[i] = data[i] - data[j];
    }
}
