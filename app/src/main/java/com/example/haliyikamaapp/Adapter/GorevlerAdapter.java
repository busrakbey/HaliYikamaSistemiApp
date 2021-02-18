package com.example.haliyikamaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.haliyikamaapp.Model.Entity.Gorevler;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.MusteriGorevlerimDetayActivity;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GorevlerAdapter extends RecyclerView.Adapter<GorevlerAdapter.MyViewHolder>  {

    private List<Gorevler> data;
    private Context mContext;


    public GorevlerAdapter(Context mContext, List<Gorevler> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gorev_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Gorevler myListData = data.get(position);
        if(data.get(position).getStartTime() != null) {
            Timestamp stamp = new Timestamp(Long.valueOf(data.get(position).getStartTime()));
            DateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date date = new Date(stamp.getTime());
            holder.tarih_item.setText(f.format(date));
        }

        holder.siparis_tutari_item.setText(data.get(position).getDescription());
        holder.sipariş_durumu_item.setText(data.get(position).getCategory());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, MusteriGorevlerimDetayActivity.class);
                musteri.putExtra("gorevMid" , String.valueOf(data.get(position).getMid()));
                musteri.putExtra("gorevId" , String.valueOf(data.get(position).getId()));
                musteri.putExtra("siparisId" , String.valueOf(data.get(position).getSiparisId()));
                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(musteri);
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

    public void restoreItem(Gorevler item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<Gorevler> getData() {
        return data;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView tarih_item, sipariş_durumu_item, siparis_tutari_item, sube_item;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.tarih_item = (TextView) itemView.findViewById(R.id.siparis_tarih_item);
            this.siparis_tutari_item = (TextView) itemView.findViewById(R.id.siparis_tutari_item);
            this.sipariş_durumu_item = (TextView) itemView.findViewById(R.id.siparis_durum_item);
            this.sube_item = (TextView) itemView.findViewById(R.id.siparis_sube_item);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}

