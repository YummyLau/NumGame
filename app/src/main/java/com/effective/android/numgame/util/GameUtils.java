package com.effective.android.numgame.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 创建随机答案
 * created bg yummylau
 * 2018-09-30
 */
public class GameUtils {

    private static final int[] ORIGIN = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    public static final int CONTENT_COUNT = 4;
    public static final int ALL_SUBMIT_NUM = 10 * 9 * 8 * 7;

    /**
     * 断言一个提交记录
     *
     * @param result
     * @return
     */
    public static boolean assertSubmitUnit(int[] result) {
        return result != null && result.length == 4;
    }

    /**
     * 计算每次猜测有多少A和多少B
     *
     * @return
     */
    public static Pair<Integer, Integer> calResultForEachSubmit(@NonNull int[] submit, @NonNull int[] result) {
        if (submit == null || result == null
                || submit.length != CONTENT_COUNT
                || result.length != CONTENT_COUNT) {
            return new Pair<>(0, 0);
        }
        int aNum = 0;
        Set<Integer> resultId = new HashSet<>();
        for (int i = 0; i < CONTENT_COUNT; i++) {
            resultId.add(result[i]);
            if (submit[i] == result[i]) {
                aNum++;
            }
        }
        int bNum = 0;
        for (Integer integer : submit) {
            if (resultId.contains(integer)) {
                bNum++;
            }
        }
        return new Pair<>(aNum, bNum - aNum);
    }

    /**
     * 比较两个提交结果是否一致
     *
     * @param firstSubmit
     * @param secondSubmit
     * @return
     */
    public static boolean submitResultEquals(Pair<Integer, Integer> firstSubmit, Pair<Integer, Integer> secondSubmit) {
        return firstSubmit != null
                && secondSubmit != null
                && firstSubmit.first == secondSubmit.first
                && firstSubmit.second == secondSubmit.second;
    }


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

    /**
     * 创建所有的数据
     *
     * @return
     */
    public static List<int[]> makeAllData() {
        List<int[]> allData = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++) {
                    for (int l = 0; l < 10; l++) {
                        if (isDifferentNum(i, j, l, k)) {
                            int[] item = new int[]{i, j, k, l};
                            allData.add(item);
                        }
                    }
                }
            }
        }
        return allData;
    }

    public static boolean isDifferentNum(int i, int j, int k, int l) {
        Set<Integer> set = new HashSet<>();
        set.add(i);
        set.add(j);
        set.add(k);
        set.add(l);
        return set.size() == 4;
    }


    @Nullable
    public static int[] parseString2IntArray(String string) {
        int[] result = null;
        if (TextUtils.isEmpty(string) || string.length() != 4) {
            return result;
        }
        result = new int[CONTENT_COUNT];
        for (int i = 0; i < CONTENT_COUNT; i++) {
            try {
                result[i] = Integer.parseInt(string.substring(i, i + 1));
            } catch (Exception e) {
                return null;
            }
        }
        return result;
    }

    @Nullable
    public static String parseIntArray2String(int[] input){
        if(GameUtils.assertSubmitUnit(input)){
            StringBuilder builder = new StringBuilder("");
            for(Integer integer : input){
                builder.append(integer);
            }
            return builder.toString();
        }
        return null;
    }
}
