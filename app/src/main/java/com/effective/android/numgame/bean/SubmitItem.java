package com.effective.android.numgame.bean;

import android.util.Pair;

/**
 * 封装对应viewholder的项
 * created bg yummylau
 * 2018-09-30
 */
public class SubmitItem {

    public int[] data;

    public Pair<Integer, Integer> abResult;

    public SubmitItem(int[] data, Pair<Integer, Integer> abResult) {
        this.data = data;
        this.abResult = abResult;
    }
}
