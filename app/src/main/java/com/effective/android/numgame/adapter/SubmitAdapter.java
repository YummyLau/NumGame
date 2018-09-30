package com.effective.android.numgame.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.effective.android.numgame.R;
import com.effective.android.numgame.adapter.viewholder.SubmitViewHolder;
import com.effective.android.numgame.bean.SubmitItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 每次提交记录 adapter
 * created bg yummylau
 * 2018-09-30
 */
public class SubmitAdapter extends RecyclerView.Adapter<SubmitViewHolder> {

    private Context mContext;
    private List<SubmitItem> data;

    public SubmitAdapter(Context context) {
        this.mContext = context;
        this.data = new ArrayList<>();
    }

    @Override
    public SubmitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.vh_submit_layout, null, false);
        return new SubmitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubmitViewHolder holder, int position) {
        holder.bindData(data.get(position));
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void insertItem(SubmitItem submitItem) {
        if (submitItem == null) {
            return;
        }
        data.add(submitItem);
        notifyItemInserted(data.size() - 1);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
