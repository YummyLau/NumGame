package com.effective.android.numgame.bean;

import android.util.Pair;

import java.util.List;

/**
 * 每次提交的返回
 * Created by Administrator on 2018/10/1.
 * Email: yummyl.lau@gmail.com
 * blog: yummylau.com
 */

public class SubmitFeedBack {
    boolean isSuccess;
    Pair<Integer, Integer> submitResult;
    List<int[]> afterFilterData;
    int[] recommendInput;

    public SubmitFeedBack(boolean isSuccess, Pair<Integer, Integer> submitResult) {
        this.isSuccess = isSuccess;
        this.submitResult = submitResult;
    }

    public SubmitFeedBack(boolean isSuccess, Pair<Integer, Integer> submitResult, List<int[]> afterFilterData, int[] recommendInput) {
        this.isSuccess = isSuccess;
        this.submitResult = submitResult;
        this.afterFilterData = afterFilterData;
        this.recommendInput = recommendInput;
    }

    public List<int[]> getAfterFilterData() {
        return afterFilterData;
    }

    public int[] getRecommendInput() {
        return recommendInput;
    }

    public Pair<Integer, Integer> getSubmitResult() {
        return submitResult;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
