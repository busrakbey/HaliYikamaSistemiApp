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
import com.example.haliyikamaapp.Model.Entity.UrunSube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.SubeTanimlamaActivity;
import com.example.haliyikamaapp.UI.UruneFiyatTanimlamaActivity;
import com.example.haliyikamaapp.UI.UruneSubeTanimlamaActivity;

import java.util.List;

public class UruneSubeAdapter extends RecyclerView.Adapter<UruneSubeAdapter.MyViewHolder> {

    private List<UrunSube> data;
    private Context mContext;
    HaliYikamaDatabase db;


    public UruneSubeAdapter(Context mContext, List<UrunSube> data) {
        this.data = data;
        this.mContext = mContext;
        db = HaliYikamaDatabase.getInstance(mContext);

    }

    @Override
    public UruneSubeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.urun_sube_item, parent, false);
        return new UruneSubeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UruneSubeAdapter.MyViewHolder holder, final int position) {
        final UrunSube myListData = data.get(position);
        HaliYikamaDatabase db = HaliYikamaDatabase.getInstance(mContext);
        holder.urun_adi_item.setText(data.get(position).getSubeAdi());

        holder.urun_birim_item.setText(db.olcuBirimDao().getOlcuBirimForId(data.get(position).getOlcuBirimId()).get(0).getOlcuBirimi());
      /*  holder.urun_fiyat_item.setText(db.urunFiyatDao().getForUrunSubeId(data.get(position).getId()).get(0).getBirimFiyat() != null ?
                db.urunFiyatDao().getForUrunSubeId(data.get(position).getId()).get(0).getBirimFiyat().toString() : null);*/


        holder.urun_durum_item.setText(data.get(position).getAktif() != null &&data.get(position).getAktif() == true ? "Aktif" : "" );
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((UruneSubeTanimlamaActivity)mContext).getEditMode(data.get(position).getMid());
            }
        });


        holder.fiyat_gir_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, UruneFiyatTanimlamaActivity.class);
                musteri.putExtra("urunSubeMid" , String.valueOf(data.get(position).getMid()));
                musteri.putExtra("urunSubeId" , String.valueOf(data.get(position).getId()));

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

    public void restoreItem(UrunSube item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<UrunSube> getData() {
        return data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView urun_adi_item, urun_birim_item, urun_durum_item, urun_fiyat_item;
        ImageView fiyat_gir_button;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.urun_adi_item = (TextView) itemView.findViewById(R.id.urun_sube_adi_item);
            this.urun_birim_item = (TextView) itemView.findViewById(R.id.urun_birim_item);
            this.urun_durum_item = (TextView) itemView.findViewById(R.id.urun_durum_item);
            this.urun_fiyat_item = (TextView) itemView.findViewById(R.id.urun_fiyat_item);

            this.fiyat_gir_button = (ImageView) itemView.findViewById(R.id.urun_fiyat_ekle);


            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}
