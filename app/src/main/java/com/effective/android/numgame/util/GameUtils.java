package com.effective.android.numgame.util;

import android.net.wifi.aware.PublishConfig;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Pair;

import com.effective.android.numgame.bean.SubmitFeedBack;

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
        if(assertSubmitUnit(input)){
            StringBuilder builder = new StringBuilder("");
            for(Integer integer : input){
                builder.append(integer);
            }
            return builder.toString();
        }
        return null;
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

    private static boolean isDifferentNum(int i, int j, int k, int l) {
        Set<Integer> set = new HashSet<>();
        set.add(i);
        set.add(j);
        set.add(k);
        set.add(l);
        return set.size() == 4;
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
     * 每次用户提交用于校验并生成对应的待定答案集合
     *
     * @param result 答案数组
     * @param input  输入数组
     * @param data   剩余可待验证的答案集合
     * @return
     */
    @Nullable
    public static SubmitFeedBack makeRecommendInput(int[] result, int[] input, List<int[]> data) {
        if (!assertSubmitUnit(result) || !assertSubmitUnit(input) || data == null) {
            return null;
        }
        Pair<Integer, Integer> inputResult = calResultForEachSubmit(input, result);
        if (inputResult != null && inputResult.first == 4) {
            return new SubmitFeedBack(true, inputResult);
        }
        for (int i = 0; i < data.size(); i++) {
            int[] item = data.get(i);
            if (!submitResultEquals(inputResult, calResultForEachSubmit(item, input))) {
                data.remove(i);
                i--;
            }
        }
        return new SubmitFeedBack(false, inputResult, data, data.get(0));
    }
}
