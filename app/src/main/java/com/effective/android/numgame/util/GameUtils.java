package com.effective.android.numgame.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 游戏助手
 * created bg yummylau
 * 2018-09-30
 */
public class GameUtils {

    private static final int CONTENT_COUNT = 4;

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

    public static boolean assertSubmitNums(int[] submit) {
        return submit != null && submit.length == CONTENT_COUNT;
    }

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

    /**
     * 创建所有的数据
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

    private static boolean isDifferentNum(int i, int j, int k, int l) {
        Set<Integer> set = new HashSet<>();
        set.add(i);
        set.add(j);
        set.add(k);
        set.add(l);
        return set.size() == 4;
    }


    public static List<int[]> filter(int[] result, List<int[]> data) {
        //默认计算第一个的结果
        int[] firstOne = data.get(0);
        Pair<Integer, Integer> firstSubmit = calResultForEachSubmit(firstOne, result);
        for (int i = 0; i < data.size(); i++) {
            int[] item = data.get(i);
            Pair<Integer, Integer> itemSubmit = calResultForEachSubmit(item, result);
            if (firstSubmit != null && itemSubmit != null && firstSubmit.first == itemSubmit.first && firstSubmit.second == firstSubmit.second) {
                data.remove(i);
                i--;
            }
        }
        return data;
    }
}
