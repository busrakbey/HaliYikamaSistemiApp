package com.example.haliyikamaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Sms;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.SmsTanimlamaActivity;
import com.example.haliyikamaapp.UI.UruneFiyatTanimlamaActivity;
import com.example.haliyikamaapp.UI.UruneSubeTanimlamaActivity;

import java.util.List;

import static android.view.View.GONE;

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.MyViewHolder> {

    private List<Sms> data;
    private Context mContext;
    HaliYikamaDatabase db;


    public SmsAdapter(Context mContext, List<Sms> data) {
        this.data = data;
        this.mContext = mContext;
        db = HaliYikamaDatabase.getInstance(mContext);

    }

    @Override
    public SmsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sube_item, parent, false);
        return new SmsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SmsAdapter.MyViewHolder holder, final int position) {
        final Sms myListData = data.get(position);
        HaliYikamaDatabase db = HaliYikamaDatabase.getInstance(mContext);
        holder.urun_adi_item.setText(data.get(position).getBaslik());

        holder.urun_detay.setVisibility(GONE);
        holder.sube_senkron_et.setVisibility(GONE);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SmsTanimlamaActivity)mContext).getEditMode(data.get(position).getMid(), data.get(position).getId());
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Sms item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<Sms> getData() {
        return data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView urun_adi_item, urun_birim_item, urun_durum_item, urun_fiyat_item;
        ImageView sube_senkron_et, urun_detay;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.urun_adi_item = (TextView) itemView.findViewById(R.id.sube_tanim_ad);
            this.sube_senkron_et = (ImageView) itemView.findViewById(R.id.sube_senkron_et);

            this.urun_detay = (ImageView) itemView.findViewById(R.id.urun_detay);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}
