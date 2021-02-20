package com.example.haliyikamaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Hesap;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.MusteriDetayKayitActivity;

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

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Hesap myListData = data.get(position);

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
