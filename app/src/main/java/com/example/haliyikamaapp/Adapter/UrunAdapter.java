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
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.UrunTanimlamaActivity;

import java.util.List;

public class UrunAdapter extends RecyclerView.Adapter<UrunAdapter.MyViewHolder> {

    private List<Urun> data;
    private Context mContext;


    public UrunAdapter(Context mContext, List<Urun> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public UrunAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sube_item, parent, false);
        return new UrunAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UrunAdapter.MyViewHolder holder, final int position) {
        final Urun myListData = data.get(position);
        HaliYikamaDatabase db = HaliYikamaDatabase.getInstance(mContext);
        holder.urun_adi_item.setText(data.get(position).getUrunAdi());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((UrunTanimlamaActivity)mContext).getEditMode(data.get(position).getMid());
            }
        });

        holder.sube_senkron_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((UrunTanimlamaActivity)mContext).postUrunService(data.get(position));
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

    public void restoreItem(Urun item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<Urun> getData() {
        return data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView urun_adi_item;
         ImageView sube_senkron_et;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.urun_adi_item = (TextView) itemView.findViewById(R.id.sube_tanim_ad);
            this.sube_senkron_et = (ImageView) itemView.findViewById(R.id.sube_senkron_et);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}
