package com.example.haliyikamaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.MusteriGorevlerimDetayActivity;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GorevlerDetayAdapter extends RecyclerView.Adapter<GorevlerDetayAdapter.MyViewHolder> {

    private List<SiparisDetay> data;
    private Context mContext;



    public GorevlerDetayAdapter(Context mContext, List<SiparisDetay> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public GorevlerDetayAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.siparis_detay_item, parent, false);
        return new GorevlerDetayAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GorevlerDetayAdapter.MyViewHolder holder, final int position) {
        final SiparisDetay myListData = data.get(position);
        HaliYikamaDatabase db = HaliYikamaDatabase.getInstance(mContext);
        List<Urun> allUrun = db.urunDao().getUrunForMid(data.get(position).getUrunMid());
        if (allUrun != null && allUrun.size() > 0)
            holder.urun_adi_item.setText(allUrun.get(0).getUrunAdi());

        holder.olcu_birimi_item.setText("Ölçü Birimi : ");
        holder.miktar_item.setText("Miktarı : " + (data.get(position).getMiktar() != null ? data.get(position).getMiktar().toString() : ""));
        holder.fiyat_item.setText(data.get(position).getBirimFiyat() != null ? data.get(position).getBirimFiyat().toString() + " TL" : "");


    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(SiparisDetay item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<SiparisDetay> getData() {
        return data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView urun_adi_item, olcu_birimi_item, miktar_item, fiyat_item;
        // ImageView isimBasHarfi_item;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.urun_adi_item = (TextView) itemView.findViewById(R.id.siparis_detay_urun_adi_item);
            this.olcu_birimi_item = (TextView) itemView.findViewById(R.id.siparis_detay_olcu_birimi_item);
            this.miktar_item = (TextView) itemView.findViewById(R.id.siparis_detay_miktar_item);
            this.fiyat_item = (TextView) itemView.findViewById(R.id.siparis_detay_birim_fiyati_item);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }
}

