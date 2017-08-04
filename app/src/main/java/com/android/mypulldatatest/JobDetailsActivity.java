package com.android.mypulldatatest;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobDetailsActivity extends AppCompatActivity {
    private String url;
    private TextView urlTextView;
    private Button goWebViewBtn;
    private String descForJobTitle;
    private String descForJobContent;
    private TextView jobDescriptionTextView,jobrequriementTextView;

    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    jobDescriptionTextView.setText(descForJobContent);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        if(i != null) {
            url = (String) i.getCharSequenceExtra("url");
        }
        setContentView(R.layout.activity_job_datails);
        urlTextView = (TextView) findViewById(R.id.url);
        urlTextView.setText("url :[ " + url + "]");
        goWebViewBtn = (Button) findViewById(R.id.go_webView);
        jobDescriptionTextView = (TextView) findViewById(R.id.job_discription);
        jobrequriementTextView = (TextView) findViewById(R.id.job_requirements);
        goWebViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(JobDetailsActivity.this,JobWebViewActivity.class);
                i.putExtra("url",url);
                startActivity(i);
            }
        });
        Log.d("lucy","url = " + url);
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(new Runnable() {
            @Override
            public void run() {
                crawl();
            }
        });

    }

    public void crawl(){
        try {
            Document document = Jsoup.connect(url).get();
            Elements elements = document.getElementsByClass("bmsg job_msg inbox");
            for(Element e : elements){
                descForJobTitle = e.select("span").first().text();
                String[] strs = e.text().split(" ");
                for (String str: strs){
                    Log.d("jsoup","str = " + str);
                }
                descForJobContent = e.text();
//                e.select("bmsg job_msg inbox br").text();


                Log.d("jsoup","lable = " + descForJobTitle + "\n");
                Log.d("jsoup","msg = " + e.text() + "\n" + ",strs length = " + strs.length);
                handler.sendEmptyMessage(1);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
