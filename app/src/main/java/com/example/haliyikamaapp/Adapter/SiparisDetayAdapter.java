package com.example.haliyikamaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.SiparisDetayKayitActivity;

import java.util.List;

public class SiparisDetayAdapter extends RecyclerView.Adapter<SiparisDetayAdapter.MyViewHolder> {

    private List<SiparisDetay> data;
    private Context mContext;


    public SiparisDetayAdapter(Context mContext, List<SiparisDetay> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public SiparisDetayAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.siparis_detay_item, parent, false);
        return new SiparisDetayAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SiparisDetayAdapter.MyViewHolder holder, final int position) {
        final SiparisDetay myListData = data.get(position);
        holder.urun_adi_item.setText("Ürün Adı");
        holder.olcu_birimi_item.setText("Ölçü Birimi");
        holder.miktar_item.setText("Miktar");
        holder.fiyat_item.setText("100 TL");


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, SiparisDetayKayitActivity.class);
                musteri.putExtra("siparisDetayMid", String.valueOf(data.get(position).getMid()));
                musteri.putExtra("siparisMid", String.valueOf(data.get(position).getMustId()));
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
