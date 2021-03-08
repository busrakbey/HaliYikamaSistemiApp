package com.example.haliyikamaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.OlcuBirim;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Urun;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.SiparisDetayKayitActivity;

import java.util.List;

public class EklenenUrunlerAdapter extends RecyclerView.Adapter<EklenenUrunlerAdapter.MyViewHolder> {

    private List<SiparisDetay> data;
    private Context mContext;

    List<Siparis> siparisList;

    public EklenenUrunlerAdapter(Context mContext, List<SiparisDetay> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public EklenenUrunlerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.eklenen_urunler_item, parent, false);
        return new EklenenUrunlerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EklenenUrunlerAdapter.MyViewHolder holder, final int position) {
        final SiparisDetay myListData = data.get(position);
        final HaliYikamaDatabase db = HaliYikamaDatabase.getInstance(mContext);
        List<Urun> allUrun = db.urunDao().getUrunForMid(data.get(position).getUrunMid());
        if (allUrun != null && allUrun.size() > 0)
            holder.urun_adi_item.setText(allUrun.get(0).getUrunAdi());

        List<Urun> allUrun2 = db.urunDao().getUrunForId(data.get(position).getUrunId());
        if (allUrun2 != null && allUrun2.size() > 0)
            holder.urun_adi_item.setText(allUrun2.get(0).getUrunAdi());

        holder.urun_sil_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SiparisDetayKayitActivity)mContext).urun_sil_button(position);
            }
        });

        holder.urun_adeti_item.setText(String.valueOf(data.get(position).getMiktar().longValue()));
        holder.fiyat_item.setText(String.valueOf(data.get(position).getBirimFiyat() * data.get(position).getMiktar()) + " TL");

        holder.urun_artir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        ((SiparisDetayKayitActivity)mContext).urun_artir_azalt(true, position);

            }
        });

        holder.urun_azalt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SiparisDetayKayitActivity)mContext).urun_artir_azalt(false, position);

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
        public TextView urun_adi_item, miktar_item, fiyat_item, urun_adeti_item;
        ImageView urun_sil_button;
        Button urun_azalt, urun_artir;
        // ImageView isimBasHarfi_item;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.urun_adi_item = (TextView) itemView.findViewById(R.id.siparis_detay_urun_adi_item);
            this.miktar_item = (TextView) itemView.findViewById(R.id.urun_adeti_item);
            this.fiyat_item = (TextView) itemView.findViewById(R.id.siparis_detay_birim_fiyati_item);
            this.urun_sil_button = (ImageView) itemView.findViewById(R.id.urun_sil_button);
            this.urun_adeti_item = (TextView) itemView.findViewById(R.id.urun_adeti_item);
            this.urun_artir = (Button) itemView.findViewById(R.id.urun_artir);
            this.urun_azalt = (Button) itemView.findViewById(R.id.urun_azalt);


            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}
