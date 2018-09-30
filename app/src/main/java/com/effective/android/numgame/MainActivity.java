package com.effective.android.numgame;

import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.effective.android.numgame.adapter.SubmitAdapter;
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
                if (TextUtils.isEmpty(input)) {
                    testAll();
                    Toast.makeText(MainActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int[] submit = GameUtils.parseString2IntArray(input);
                if (GameUtils.assertSubmitNums(submit)) {
                    Pair<Integer, Integer> submitResult = GameUtils.calResultForEachSubmit(submit, result);
                    if (submitResult.first == 4) {
                        isSuccess = true;
                        Toast.makeText(MainActivity.this, "恭喜你回答正确", Toast.LENGTH_SHORT).show();
                    }
                    mSubmitAdapter.insertItem(new SubmitItem(submit, submitResult));
                    mInput.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "请确保输入4个数字", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void testAll() {
        Log.d("MainActivity", "当前有数据： " + allData.size());
        allData = GameUtils.filter(result, allData);
        if (allData.size() > 0) {
            int[] item = allData.get(0);
            Log.d("MainActivity", "建议下次读取： " + "[" + item[0] + item[1] + item[2] + item[3] + "]");
        }

    }

    private void initData() {
        mSubmitAdapter = new SubmitAdapter(this);
        mSubmitList.setAdapter(mSubmitAdapter);
        result = ShuffleSoreUtils.makeRandomResult();
    }

}
