package com.example.haliyikamaapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Bolge;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.SubeTanimlamaActivity;

import java.util.List;

public class BolgeAdapter extends RecyclerView.Adapter<BolgeAdapter.MyViewHolder> {

    private List<Bolge> data;
    private Context mContext;


    public BolgeAdapter(Context mContext, List<Bolge> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public BolgeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sube_item, parent, false);
        return new BolgeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BolgeAdapter.MyViewHolder holder, final int position) {
        final Bolge myListData = data.get(position);
        HaliYikamaDatabase db = HaliYikamaDatabase.getInstance(mContext);
        holder.urun_adi_item.setText(data.get(position).getBolge());
holder.urun_detay.setVisibility(View.GONE);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SubeTanimlamaActivity)mContext).getEditMode(data.get(position).getMid());
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

    public void restoreItem(Bolge item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<Bolge> getData() {
        return data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView urun_adi_item;
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
