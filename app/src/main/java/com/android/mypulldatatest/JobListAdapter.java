package com.android.mypulldatatest;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class JobListAdapter extends BaseAdapter {

    private ArrayList<Jobs.Item> mJobList = new ArrayList<>();
    private Context mContext;

    public JobListAdapter(Context context){
        this.mContext = context;
    }

    public void setmJobList(ArrayList<Jobs.Item> mJobList) {
        this.mJobList = mJobList;
    }

    @Override
    public int getCount() {
        return mJobList.size();
    }

    @Override
    public Object getItem(int i) {
        return mJobList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item,null);
            viewHolder.mPostionTextView = convertView.findViewById(R.id.position);
            viewHolder.mTitleTextView = convertView.findViewById(R.id.title);
            viewHolder.mSalaryTextView = convertView.findViewById(R.id.salary);
            viewHolder.mCompanyTextView = convertView.findViewById(R.id.company);
            viewHolder.mLocationTextView = convertView.findViewById(R.id.location);
            viewHolder.mPublishDateTextView = convertView.findViewById(R.id.publish_date);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Jobs.Item item = mJobList.get(i);
        viewHolder.mPostionTextView.setText("序号：" + (i+1));
        viewHolder.mTitleTextView.setText("职位：" + item.getTitle());
        viewHolder.mSalaryTextView.setText("薪水：" + (item.getSalaryRange().length() != 0 ? item.getSalaryRange() : "面议"));
        viewHolder.mCompanyTextView.setText("公司：" + item.getCompany());
        viewHolder.mLocationTextView.setText("地点：" + item.getLocation());
        viewHolder.mPublishDateTextView.setText("发布日期：" + item.getPublishDate());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext,JobWebViewActivity.class);
                i.putExtra("url",item.getHref());
                mContext.startActivity(i);
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView mPostionTextView;
        TextView mTitleTextView;
        TextView mSalaryTextView;
        TextView mCompanyTextView;
        TextView mLocationTextView;
        TextView mPublishDateTextView;
    }
}
