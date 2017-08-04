package com.android.mypulldatatest;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Crawl.OnCallbackListener<Jobs.Item>{
    private EditText mEditText;
    private Button mSearchBtn;
    private ListView mListView;
    private ArrayList<Jobs.Item> mJobList = new ArrayList<>();
    private JobListAdapter mJobAdapter;
    private ProgressDialog dialog;
    private View mNoResultView;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    dialog.dismiss();
                    if(mJobList.size()> 0) {
                        if(mListView.getFooterViewsCount() != 0){
                            mListView.removeFooterView(mNoResultView);
                        }
                        mJobAdapter.notifyDataSetChanged();
                    }else{
                        mListView.addFooterView(mNoResultView);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("crawl .....");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 转圈风格

        init();

    }

    private void init(){
        mNoResultView = LayoutInflater.from(this).inflate(R.layout.no_result_layout,null);
        mEditText = (EditText) findViewById(R.id.edit_text);
        mSearchBtn = (Button) findViewById(R.id.search);
        mListView = (ListView) findViewById(R.id.listview);
        mJobAdapter = new JobListAdapter(this);
        mJobAdapter.setmJobList(mJobList);
        mListView.setAdapter(mJobAdapter);
        crawlJob();
    }

    private void crawlJob(){
        mSearchBtn.setOnClickListener(click);
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.show();

            String editStr = mEditText.getText().toString();
            if(TextUtils.isEmpty(editStr)){
                editStr = "android";
            }
            String finalEditStr = editStr;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Crawl crawl = new Jobs(finalEditStr);
                    crawl.setListener(MainActivity.this);
                    crawl.crawl();
                }
            }).start();

        }
    };

    @Override
    public void onCallback(ArrayList<Jobs.Item> results) {
        mJobList.clear();
        mJobList.addAll(results);
        handler.sendEmptyMessage(1);
    }
}
