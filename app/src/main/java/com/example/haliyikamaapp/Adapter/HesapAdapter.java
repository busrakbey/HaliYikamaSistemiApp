package com.example.haliyikamaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Hesap;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.MusteriDetayKayitActivity;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HesapAdapter extends RecyclerView.Adapter<HesapAdapter.MyViewHolder> {

    private List<Hesap> data;
    private Context mContext;
    HaliYikamaDatabase db;


    public HesapAdapter(Context mContext, List<Hesap> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.hesap_item, parent, false);
        return new MyViewHolder(itemView);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Hesap myListData = data.get(position);

        if(data.get(position).getTarih() != null ) {
            if(!data.get(position).getTarih().contains(".")) {
                Timestamp stamp = new Timestamp(Long.valueOf(data.get(position).getTarih()));
                Date date = new Date(stamp.getTime());
                DateFormat f = new SimpleDateFormat("dd.MM.yyyy");
                holder.hesap_tarih_item.setText(f.format(date));
            }
            else
                holder.hesap_tarih_item.setText(data.get(position).getTarih());

        }else
            holder.hesap_tarih_item.setText(null);

        holder.hesap_islem_kaynagi_item.setText(data.get(position).getIslemNedeni());
        if(data.get(position).getIslemTuru() != null && data.get(position).getIslemTuru().equalsIgnoreCase("Giriş")) {
            holder.hesap_islem_turu_item.setText( data.get(position).getIslemTuru() + " ↑ " );
            holder.hesap_islem_turu_item.setTextColor(Color.GREEN);
        }

        else if(data.get(position).getIslemTuru() != null && data.get(position).getIslemTuru().equalsIgnoreCase("Çıkış")) {
            holder.hesap_islem_turu_item.setText(data.get(position).getIslemTuru() + " ↓ ");
            holder.hesap_islem_turu_item.setTextColor(Color.RED);
        }
        else
            holder.hesap_islem_turu_item.setText(null);
        holder.hesap_sube_item.setText(data.get(position).getSubeAdi());
        holder.hesap_tutar_itme.setText("Tutar: " + (data.get(position).getTutar() != null  ? data.get(position).getTutar().toString() : null));
        holder.chesap_kaynak_item.setText( (data.get(position).getKaynakAdi() != null ? "Kaynak: " + data.get(position).getKaynakAdi() : null));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void removeItem(final int position, final List<Hesap> data) {
        data.remove(position);
        notifyItemRemoved(position);

        db = HaliYikamaDatabase.getInstance(mContext);

    }

    public void restoreItem(Hesap item, final int position) {
        data.add(position, item);
        notifyItemInserted(position);



    }

    public List<Hesap> getData() {
        return data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView hesap_tarih_item, hesap_islem_kaynagi_item, hesap_sube_item, hesap_tutar_itme, hesap_islem_turu_item, chesap_kaynak_item;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.hesap_tarih_item = (TextView) itemView.findViewById(R.id.hesap_tarih_item);
            this.hesap_islem_kaynagi_item = (TextView) itemView.findViewById(R.id.hesap_islem_kaynagi_item);
            this.hesap_sube_item = (TextView) itemView.findViewById(R.id.hesap_sube_item);
            this.hesap_tutar_itme = (TextView) itemView.findViewById(R.id.hesap_tutar_item);
            this.hesap_islem_turu_item = (TextView) itemView.findViewById(R.id.hesap_islem_turu_item);
            this.chesap_kaynak_item = (TextView) itemView.findViewById(R.id.hesap_kaynak_item);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}
