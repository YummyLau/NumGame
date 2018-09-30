package com.effective.android.numgame;

import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.effective.android.numgame.adapter.SubmitAdapter;
import com.effective.android.numgame.bean.SubmitFeedBack;
import com.effective.android.numgame.bean.SubmitItem;
import com.effective.android.numgame.util.GameUtils;
import com.effective.android.numgame.util.ShuffleSoreUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button mSubmit, mResultHandler;
    private EditText mInput;

    private int[] result;
    private boolean isSuccess;

    private RecyclerView mSubmitList;
    private SubmitAdapter mSubmitAdapter;

    private List<int[]> allData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        mSubmitList = findViewById(R.id.submit_list);
        mSubmitList.setLayoutManager(new LinearLayoutManager(this));
        mResultHandler = findViewById(R.id.provide);
        mSubmit = findViewById(R.id.submit);
        mInput = findViewById(R.id.input);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int currentInputLength = s.toString().length();
                mSubmit.setEnabled(currentInputLength == 4);
                if (currentInputLength > 4) {
                    s.delete(4, currentInputLength);
                }
            }
        });
    }

    private void initListener() {
        mResultHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = ShuffleSoreUtils.makeRandomResult();
                isSuccess = false;
                mInput.setText("");
                mSubmitAdapter.clear();
                allData = GameUtils.makeAllData();
            }
        });
        mResultHandler.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                StringBuilder submitString = new StringBuilder();
                submitString.append("[");
                for (Integer integer : result) {
                    submitString.append(integer + ",");
                }
                submitString.delete(submitString.length() - 1, submitString.length());
                submitString.append("]");
                Toast.makeText(MainActivity.this, "答案 " + submitString.toString(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSuccess) {
                    Toast.makeText(MainActivity.this, "请重置随机数", Toast.LENGTH_SHORT).show();
                    return;
                }
                String input = mInput.getText().toString();
                int[] submitInput = GameUtils.parseString2IntArray(input);
                SubmitFeedBack submitFeedBack = GameUtils.makeRecommendInput(
                        result,
                        submitInput,
                        allData);
                mInput.setText("");
                if(submitFeedBack != null){
                    if (submitFeedBack.isSuccess()) {
                        isSuccess = true;
                        mInput.setHint("重置随机数开启下一盘！");
                        Toast.makeText(MainActivity.this, "恭喜你回答正确", Toast.LENGTH_SHORT).show();
                    }else{
                        String recommendString = GameUtils.parseIntArray2String(submitFeedBack.getRecommendInput());
                        if(!TextUtils.isEmpty(recommendString)){
                            mInput.setHint("推荐下次输入：" + recommendString);
                        }
                    }
                    String message = null;
                    if(submitFeedBack.getAfterFilterData() != null){
                        message = " 当前约束项剩下为：" + submitFeedBack.getAfterFilterData().size();
                    }

                    mSubmitAdapter.insertItem(new SubmitItem(submitInput, submitFeedBack.getSubmitResult(),message));
                }
            }
        });
    }

    private void initData() {
        mSubmitAdapter = new SubmitAdapter(this);
        mSubmitList.setAdapter(mSubmitAdapter);
        result = ShuffleSoreUtils.makeRandomResult();
        allData = GameUtils.makeAllData();
    }

}
