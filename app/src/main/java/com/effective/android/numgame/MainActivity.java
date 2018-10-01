package com.effective.android.numgame;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.effective.android.numgame.adapter.SubmitAdapter;
import com.effective.android.numgame.bean.SubmitFeedBack;
import com.effective.android.numgame.bean.SubmitItem;
import com.effective.android.numgame.util.EntropyUtils;
import com.effective.android.numgame.util.CutOutUtils;
import com.effective.android.numgame.util.GameUtils;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.effective.android.numgame.util.EntropyUtils.startRecommend;

public class MainActivity extends AppCompatActivity {

    private Button mSubmit, mResultHandler;
    private EditText mInput;

    private int[] result;
    private boolean isSuccess;

    private RecyclerView mSubmitList;
    private SubmitAdapter mSubmitAdapter;

    private List<int[]> allData;
    private int[] currentInput;

    private InputHandler mInputHandler;

    private static final int MESSAGE_LOG = 0;
    private static final int MESSAGE_INPUT = 1;


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
        EntropyUtils.startRecommend();
        mResultHandler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = GameUtils.makeRandomResult();
                isSuccess = false;
                mInput.setText("");
                mSubmitAdapter.clear();
                allData = GameUtils.makeAllData();
                startRecommend();
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
                    EntropyUtils.endRecommend();
                    Toast.makeText(MainActivity.this, "请重置随机数", Toast.LENGTH_SHORT).show();
                    return;
                }
                String input = mInput.getText().toString();
                currentInput = GameUtils.parseString2IntArray(input);
                mInput.setEnabled(false);
                mInput.setText("");
                mInput.setHint("正在计算...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SubmitFeedBack submitFeedBack = EntropyUtils.makeRecommendInput(
                                result,
                                currentInput,
                                allData);
                        Message message = Message.obtain();
                        message.obj = submitFeedBack;
                        message.what = MESSAGE_INPUT;
                        mInputHandler.sendMessage(message);
                    }
                }).start();
            }
        });
    }

    private void initData() {
        mSubmitAdapter = new SubmitAdapter(this);
        mSubmitList.setAdapter(mSubmitAdapter);
        result = GameUtils.makeRandomResult();
        allData = GameUtils.makeAllData();
        mInputHandler = new InputHandler(this);
    }

    public static class InputHandler extends Handler {

        private WeakReference<MainActivity> mainActivityWeakReference;

        public InputHandler(MainActivity mainActivity) {
            mainActivityWeakReference = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_LOG: {
                    Object obj = msg.obj;
                    if (obj != null && obj instanceof int[]) {
                        int[] sum = (int[]) obj;
                        for (int i = 0; i < sum.length; i++) {
                            Log.d("mainActivity", i + "完成次数：" + sum[i]);
                        }
                    }
                    break;
                }

                case MESSAGE_INPUT: {
                    Object obj = msg.obj;
                    mainActivityWeakReference.get().mInput.setEnabled(true);
                    if (obj != null && obj instanceof SubmitFeedBack && mainActivityWeakReference.get() != null) {
                        SubmitFeedBack submitFeedBack = (SubmitFeedBack) obj;
                        if (submitFeedBack.isSuccess()) {
                            mainActivityWeakReference.get().isSuccess = true;
                            mainActivityWeakReference.get().mInput.setHint("重置随机数开启下一盘！");
                            Toast.makeText(mainActivityWeakReference.get(), "恭喜你回答正确", Toast.LENGTH_SHORT).show();
                        } else {
                            String recommendString = GameUtils.parseIntArray2String(submitFeedBack.getRecommendInput());
                            if (!TextUtils.isEmpty(recommendString)) {
                                mainActivityWeakReference.get().mInput.setHint("推荐下次输入：" + recommendString);
                            }
                        }
                        String message = null;
                        if (submitFeedBack.getAfterFilterData() != null) {
                            message = " 当前约束项剩下为：" + submitFeedBack.getLastCount();
                        }
                        mainActivityWeakReference.get().mSubmitAdapter.insertItem(new SubmitItem(mainActivityWeakReference.get().currentInput, submitFeedBack.getSubmitResult(), message));
                    }
                    break;
                }
            }

        }
    }


    private void test5000Games(final boolean entropy, final int forCount) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int[] sum = new int[10];
                int[] start;
                int[] result = null;

                List<int[]> allData;
                for (int i = 0; i < forCount; i++) {
                    SubmitFeedBack submitFeedBack = null;
                    int count = 0;
                    if (entropy) {
                        startRecommend();
                    }
                    do {
                        if (submitFeedBack == null) {
                            result = GameUtils.makeRandomResult();
                            allData = GameUtils.makeAllData();
                            start = new int[]{0, 1, 2, 3};
                        } else {
                            allData = submitFeedBack.getAfterFilterData();
                            start = submitFeedBack.getRecommendInput();
                        }
                        if (entropy) {
                            submitFeedBack = EntropyUtils.makeRecommendInput(result, start, allData);
                        } else {
                            submitFeedBack = CutOutUtils.makeRecommendInput(result, start, allData);
                        }
                        count++;
                    } while (!submitFeedBack.isSuccess());
                    sum[count]++;
                    if (entropy) {
                        EntropyUtils.endRecommend();
                    }
                }
                Message message = Message.obtain();
                message.what = MESSAGE_LOG;
                message.obj = sum;
                mInputHandler.sendMessage(message);
            }
        }).start();
    }
}
