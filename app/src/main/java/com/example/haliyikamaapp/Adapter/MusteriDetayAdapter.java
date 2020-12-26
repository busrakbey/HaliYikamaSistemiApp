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
import com.example.haliyikamaapp.Model.Entity.MusteriIletisim;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.UI.MusteriDetayActivity;
import com.example.haliyikamaapp.UI.MusteriDetayKayitActivity;

import java.util.List;

public class MusteriDetayAdapter extends RecyclerView.Adapter<MusteriDetayAdapter.MyViewHolder> {

    private List<MusteriIletisim> data;
    private Context mContext;
    HaliYikamaDatabase db;


    public MusteriDetayAdapter(Context mContext, List<MusteriIletisim> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.musteri_detay_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final MusteriIletisim myListData = data.get(position);
        holder.adres_item.setText(data.get(position).getAdres() != null ? data.get(position).getAdres() : "");
        holder.il_ilce_item.setText("Ankara/Pursaklar");
      //  holder.il_ilce_item.setText((data.get(position).getIlceId() != null ? data.get(position).getIlceId() : "") + "/" + (data.get(position).getIlId() != null ? data.get(position).getIlId() : ""));
        holder.iletisim_adi_item.setText(data.get(position).getIletisimAdi() != null ? data.get(position).getIletisimAdi() : "");
        holder.telefonNo_item.setText(data.get(position).getTelefonNo() != null ? data.get(position).getTelefonNo() : "");

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, MusteriDetayKayitActivity.class);
                musteri.putExtra("musteriMid", data.get(position).getMustId().toString());
                musteri.putExtra("musteriDetayMid", data.get(position).getMid().toString());
                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(musteri);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void removeItem(final int position, final List<MusteriIletisim> data) {
        data.remove(position);
        notifyItemRemoved(position);

        db = HaliYikamaDatabase.getInstance(mContext);

    }

    public void restoreItem(MusteriIletisim item, final int position) {
        data.add(position, item);
        notifyItemInserted(position);



    }

    public List<MusteriIletisim> getData() {
        return data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView iletisim_adi_item, il_ilce_item, adres_item, telefonNo_item;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.iletisim_adi_item = (TextView) itemView.findViewById(R.id.iletisim_adi_item);
            this.il_ilce_item = (TextView) itemView.findViewById(R.id.ilce_il_item);
            this.telefonNo_item = (TextView) itemView.findViewById(R.id.telefon_no_item);
            this.adres_item = (TextView) itemView.findViewById(R.id.adres_item);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}
