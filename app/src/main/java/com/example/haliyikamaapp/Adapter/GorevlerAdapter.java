package com.example.haliyikamaapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Gorevler;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.MusteriGorevlerimDetayActivity;
import com.example.haliyikamaapp.UI.SiparisDetayActivity;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GorevlerAdapter extends RecyclerView.Adapter<GorevlerAdapter.MyViewHolder>  {

    private List<Gorevler> data;
    private Context mContext;
    HaliYikamaDatabase db;

    public GorevlerAdapter(Context mContext, List<Gorevler> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent == null) {
            return  null;

        }else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gorev_item, parent, false);

            return new MyViewHolder(itemView);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        db = HaliYikamaDatabase.getInstance(mContext);

        final Gorevler myListData = data.get(position);
        if(data.get(position).getSiparisTarihi() != null) {
            Timestamp stamp = new Timestamp(Long.valueOf(data.get(position).getSiparisTarihi()));
            DateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date date = new Date(stamp.getTime());
            holder.tarih_item.setText(f.format(date));
        }

        holder.siparis_tutari_item.setText(data.get(position).getTaskDescription());
        holder.sipariş_durumu_item.setText(data.get(position).getSiparisDurumu());

        if (data.get(position).getSiparisDurumu() != null) {
            if (data.get(position).getSiparisDurumu().equalsIgnoreCase("Teslim Alınacak"))
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#c056d9"));

            if (data.get(position).getSiparisDurumu().equalsIgnoreCase("Teslime Hazır"))
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#5cee36"));


            if (data.get(position).getSiparisDurumu().equalsIgnoreCase("Teslime Çıktı"))
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#ea3732"));


            if (data.get(position).getSiparisDurumu().equalsIgnoreCase("Yıkanacak"))
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#f48024"));
        }

        List<Musteri> musteriList = db.musteriDao().getMusteriForId(data.get(position).getMusteriId());
        holder.adres_item.setText(musteriList.size() > 0 ? ((musteriList.get(0).getAdres() != null ? musteriList.get(0).getAdres() : "") + " / "
                + (musteriList.get(0).getBolge() != null ?  musteriList.get(0).getBolge() : "")) : "");

        List<Siparis> siparisList = db.siparisDao().getSiparisForSiparisId(data.get(position).getSiparisId());

        holder.adres_item2.setText(siparisList.size() > 0 ? siparisList.get(0).getAciklama() : "Sipariş Notu Yok");

        holder.sube_item.setText(data.get(position).getSubeAdi());
        holder.musteri_adi.setText(data.get(position).getMusteriAdi() + " " + (data.get(position).getMusteriSoyadi() != null ? data.get(position).getMusteriSoyadi() : ""));
        holder.siparis_tutari_item.setText(data.get(position).getSiparisToplamTutar() != null ? String.valueOf(data.get(position).getSiparisToplamTutar()) : null);


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent musteri = new Intent(mContext, MusteriGorevlerimDetayActivity.class);
                musteri.putExtra("gorevMid" , String.valueOf(data.get(position).getMid()));
                musteri.putExtra("gorevId" , String.valueOf(data.get(position).getTaskId()));
                musteri.putExtra("siparisId" , String.valueOf(data.get(position).getSiparisId()));
                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(musteri);*/


                if (data.get(position).getTaskName().equalsIgnoreCase("TeslimAl")) {
                    List<Siparis> siparisList = null;
                    List<SiparisDetay> siparisDetayList = null;
                    List<Gorevler> gorevlerList = db.gorevlerDao().getGorevForId(Long.valueOf(data.get(position).getTaskId()));
                    if (gorevlerList.size() > 0) {
                        siparisList = db.siparisDao().getSiparisForSiparisId(gorevlerList.get(0).getSiparisId());
                        if (siparisList.size() > 0)
                            siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisId(siparisList.get(0).getId());
                        if (siparisDetayList.size() == 0)
                            siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisMid(siparisList.get(0).getMid());

                    }
                    Intent i = new Intent(mContext, SiparisDetayActivity.class);
                    // i.putExtra("gelenPage", "sipariş");
                    i.putExtra("siparisId", String.valueOf(data.get(position).getSiparisId()));
                    i.putExtra("subeMid", siparisList.get(0).getSubeMid() != null ? siparisList.get(0).getSubeMid().toString() : null);
                    i.putExtra("subeId", siparisList.get(0).getSubeId() != null ? siparisList.get(0).getSubeId().toString() : null);
                    i.putExtra("siparisMid", siparisList != null ? siparisList.get(0).getMid().toString() : null);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.getApplicationContext().startActivity(i);

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

    public void restoreItem(Gorevler item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<Gorevler> getData() {
        return data;
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView tarih_item, sipariş_durumu_item, siparis_tutari_item, sube_item, adres_item, musteri_adi, adres_item2;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.tarih_item = (TextView) itemView.findViewById(R.id.siparis_tarih_item);
            this.siparis_tutari_item = (TextView) itemView.findViewById(R.id.siparis_tutari_item);
            this.sipariş_durumu_item = (TextView) itemView.findViewById(R.id.siparis_durum_item);
            this.sube_item = (TextView) itemView.findViewById(R.id.siparis_sube_item);
            this.adres_item = (TextView) itemView.findViewById(R.id.siparis_adres_item);
            this.adres_item2 = (TextView) itemView.findViewById(R.id.siparis_adres_item2);

            this.musteri_adi = (TextView) itemView.findViewById(R.id.siparis_musteri_adi);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}

