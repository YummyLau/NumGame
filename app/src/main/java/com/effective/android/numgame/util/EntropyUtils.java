package com.effective.android.numgame.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;


import com.effective.android.numgame.bean.SubmitFeedBack;

import java.util.List;

import static java.lang.Math.log;

/**
 * 采用信息反馈处理
 * Created by Administrator on 2018/10/1.
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */

public class EntropyUtils {

    public static List<int[]> allSubmits;
    public static int[] hasErrorState;
    public static int[] hasSubmitState;

    public static void startRecommend() {
        allSubmits = GameUtils.makeAllData();
        hasSubmitState = new int[allSubmits.size()];
        hasErrorState = new int[allSubmits.size()];
    }

    public static void endRecommend() {
        if(allSubmits != null){
            allSubmits.clear();
        }
        hasSubmitState = null;
        hasErrorState = null;
    }

    /**
     * 先调用 startRecommend
     * 每次用户提交用于校验并生成对应的待定答案集合
     * 最后调用 endRecommend
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
        //获取反馈
        Pair<Integer, Integer> inputResult = GameUtils.calResultForEachSubmit(input, result);
        int feedBack = EntropyUtils.getFeedBack(input, result);

        //获取返回，发现不是答案，需要优化信息量
        int[] recommendInput;
        int lastCount = 0;
        if (feedBack == 20) {
            return new SubmitFeedBack(true, inputResult);
        } else {
            //删除部分分支
            lastCount = EntropyUtils.getItemsToSubmit(feedBack, input);
            //等于0异常
            if (lastCount == 1 || lastCount == 0) {
                recommendInput = EntropyUtils.getAnswer();
            } else {
                recommendInput = getSubmitInput();
            }
        }
        return new SubmitFeedBack(false, inputResult, data, recommendInput,lastCount);
    }


    public static int[] getSubmitInput() {
        double maxEntropy = 0;
        int[] feedBacks;
        int[] recommendSubmit = null;

        for (int i = 0; i < GameUtils.ALL_SUBMIT_NUM; i++) {

            if (hasSubmitState[i] == 1)
                continue;

            feedBacks = EntropyUtils.makeFeedBacks();
            getAllFeedBacks(feedBacks, allSubmits.get(i));

            double entropy = calEntropy(feedBacks);

            if (entropy < 0.00001) {
                hasSubmitState[i] = 1;
                continue;
            }

            // 寻找最大信息量
            if (maxEntropy < entropy) {
                maxEntropy = entropy;
                recommendSubmit = allSubmits.get(i);
            }
        }
        return recommendSubmit;
    }

    /**
     * 计算每一次得到的反馈，定义xAyB为 x*5+b为反馈 20位正确答案
     *
     * @param submit
     * @param result
     * @return
     */
    public static int getFeedBack(@NonNull int[] submit, @NonNull int[] result) {
        Pair<Integer, Integer> feedBack = GameUtils.calResultForEachSubmit(submit, result);
        if (feedBack != null) {
            return feedBack.first * 5 + feedBack.second;
        }
        return 0;
    }

    /**
     * 计算当前剩余item可提交数量
     *
     * @param feedback
     * @param currentSubmit
     * @return
     */
    public static int getItemsToSubmit(int feedback, int[] currentSubmit) {
        int sum = 0;
        for (int i = 0; i < GameUtils.ALL_SUBMIT_NUM; i++) {
            if (hasErrorState[i] == 1)
                continue;

            if (getFeedBack(allSubmits.get(i), currentSubmit) != feedback) {
                hasErrorState[i] = 1;
            } else {
                sum++;
            }
        }
        return sum;
    }

    /**
     * 当且仅当 getItemsToSubmit 返回1的时候是对的
     *
     * @return
     */
    public static int[] getAnswer() {
        for (int i = 0; i < GameUtils.ALL_SUBMIT_NUM; i++) {
            if (hasErrorState[i] == 1)
                continue;
            return allSubmits.get(i);
        }
        return null;
    }

    /**
     * 计算根据反馈计算信息量
     *
     * @return
     */
    public static double calEntropy(int[] feedBacks) {
        int sum = 0;
        double entropy = 0.;
        for (int i = 0; i < feedBacks.length; i++) {
            sum += feedBacks[i];
        }
        for (int j = 0; j < feedBacks.length; j++) {
            //跳过 0A0B 场景
            if (feedBacks[j] == 0)
                continue;
            //计算比重
            float tmp = (float) feedBacks[j] / sum;
            entropy += -1 * tmp * log(tmp);
        }
        return entropy;
    }

    /**
     * 计算当前提交与所有答案的反馈，并记录反馈列表
     *
     * @param feedBacks 反馈集合
     * @param submit    当前提交
     */
    public static void getAllFeedBacks(int[] feedBacks, int[] submit) {
        for (int i = 0; i < GameUtils.ALL_SUBMIT_NUM; i++) {
            if (hasErrorState[i] != 0) {
                continue;
            }
            feedBacks[getFeedBack(submit, allSubmits.get(i))]++;
        }
    }


    /**
     * 获取空反馈集
     *
     * @return
     */
    public static int[] makeFeedBacks() {
        return new int[(GameUtils.CONTENT_COUNT + 1) * (GameUtils.CONTENT_COUNT + 1)];
    }
}
