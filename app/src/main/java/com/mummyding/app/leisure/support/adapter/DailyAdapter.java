package com.mummyding.app.leisure.support.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mummyding.app.leisure.R;
import com.mummyding.app.leisure.database.cache.ICache;
import com.mummyding.app.leisure.database.table.DailyTable;
import com.mummyding.app.leisure.model.daily.DailyBean;
import com.mummyding.app.leisure.ui.support.WebViewLocalActivity;

import com.mummyding.app.leisure.support.adapter.DailyAdapter.ViewHolder;


/**
 * Created by mummyding on 15-11-21.
 */
public class DailyAdapter extends BaseListAdapter<DailyBean,ViewHolder>{


    public DailyAdapter(Context context, ICache<DailyBean> cache) {
        super(context, cache);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View parentView = View.inflate(mContext,R.layout.item_daily,null);
        ViewHolder vh = new ViewHolder(parentView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DailyBean dailyBean = getItem(position);
        holder.title.setText(dailyBean.getTitle());
        holder.image.setImageURI(null);
        holder.image.setImageURI(Uri.parse(dailyBean.getImage()));
        holder.info.setText(dailyBean.getInfo());
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, WebViewLocalActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(mContext.getString(R.string.id_html_content), dailyBean.getDescription());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });

        if(isCollection){
            holder.collect_cb.setVisibility(View.GONE);
            holder.text.setText(R.string.text_remove);
            holder.text.setTextSize(20);
            holder.text.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            holder.text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(holder.parentView, R.string.notify_remove_from_collection,Snackbar.LENGTH_SHORT).
                            setAction(mContext.getString(R.string.text_ok), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mItems.contains(dailyBean) == false){
                                        return;
                                    }
                                    mCache.execSQL(DailyTable.updateCollectionFlag(dailyBean.getTitle(), 0));
                                    mCache.execSQL(DailyTable.deleteCollectionFlag(dailyBean.getTitle()));
                                    mItems.remove(position);
                                    notifyDataSetChanged();
                                }
                            })
                            .show();
                }
            });
            return;
        }

        holder.collect_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                dailyBean.setIs_collected(isChecked ? 1:0);
                mCache.execSQL(DailyTable.updateCollectionFlag(dailyBean.getTitle(), isChecked ? 1 : 0));
                if(isChecked){
                    mCache.addToCollection(dailyBean);
                }else{
                    mCache.execSQL(DailyTable.deleteCollectionFlag(dailyBean.getTitle()));
                }
            }
        });
        holder.collect_cb.setChecked(dailyBean.getIs_collected()==1 ? true:false);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private View parentView;
        private TextView title;
        private SimpleDraweeView image;
        private TextView info;
        private CheckBox collect_cb;
        private TextView text;
        public ViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;
            title = (TextView) parentView.findViewById(R.id.title);
            image = (SimpleDraweeView) parentView.findViewById(R.id.image);
            info = (TextView) parentView.findViewById(R.id.info);
            collect_cb = (CheckBox) parentView.findViewById(R.id.collect_cb);
            if(isCollection) {
                text = (TextView) parentView.findViewById(R.id.text);
            }
        }
    }
}