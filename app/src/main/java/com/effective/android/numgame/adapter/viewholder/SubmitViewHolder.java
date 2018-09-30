package com.effective.android.numgame.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.effective.android.numgame.R;
import com.effective.android.numgame.bean.SubmitItem;

/**
 * 每次提交记录的viewhodler
 * created bg yummylau
 * 2018-09-30
 */
public class SubmitViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;

    public SubmitViewHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.content);
    }

    public void bindData(SubmitItem submitItem) {
        if (submitItem != null) {
            StringBuilder submitString = new StringBuilder();
            if (submitItem.data != null && submitItem.data.length > 0) {
                submitString.append("[");
                for (Integer integer : submitItem.data) {
                    submitString.append(integer + ",");
                }
                submitString.delete(submitString.length() - 1, submitString.length());
                submitString.append("]");
            }
            if (submitItem.abResult != null) {
                submitString.append(" result: ");
                submitString.append(submitItem.abResult.first + "A");
                submitString.append(submitItem.abResult.second + "B");
            }
            textView.setText(submitString.toString());
        }
    }
}
