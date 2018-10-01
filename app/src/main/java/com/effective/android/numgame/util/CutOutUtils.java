package com.effective.android.numgame.util;

import android.support.annotation.Nullable;
import android.util.Pair;

import com.effective.android.numgame.bean.SubmitFeedBack;

import java.util.List;

/**
 * 使用裁剪方法处理
 * created bg yummylau
 * 2018-09-30
 */
public class CutOutUtils {

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
        if (!GameUtils.assertSubmitUnit(result) || !GameUtils.assertSubmitUnit(input) || data == null) {
            return null;
        }
        Pair<Integer, Integer> inputResult = GameUtils.calResultForEachSubmit(input, result);
        if (inputResult != null && inputResult.first == 4) {
            return new SubmitFeedBack(true, inputResult);
        }
        for (int i = 0; i < data.size(); i++) {
            int[] item = data.get(i);
            if (!GameUtils.submitResultEquals(inputResult, GameUtils.calResultForEachSubmit(item, input))) {
                data.remove(i);
                i--;
            }
        }
        return new SubmitFeedBack(false, inputResult, data, data.get(0),data.size());
    }
}
