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

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.MusteriDetayActivity;
import com.example.haliyikamaapp.UI.MusteriKayitActivity;

import java.util.List;

public class MusteriAdapter extends RecyclerView.Adapter<MusteriAdapter.MyViewHolder> {

    private List<Musteri> data;
    private Context mContext;


    public MusteriAdapter(Context mContext, List<Musteri> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.musteri_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Musteri myListData = data.get(position);
        holder.adiSoyadi_item.setText(data.get(position).getMusteriAdi() + " " + data.get(position).getMusteriSoyadi());
        holder.telefonNo_item.setText(data.get(position).getTelefonNumarasi());


        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();
        int color2 = generator.getColor("user@gmail.com");
        int color = generator.getColor(data.get(position));
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(1)
                .endConfig()
                .rect();
       if( !data.get(position).getMusteriAdi().trim().equalsIgnoreCase("")) {
           TextDrawable ic1 = TextDrawable.builder()
                   .buildRound((data.get(position).getMusteriAdi().substring(0, 1).toUpperCase()), color);
           holder.isimBasHarfi_item.setImageDrawable(ic1);
       }


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, MusteriDetayActivity.class);
                musteri.putExtra("musteriMid" , String.valueOf(data.get(position).getMid()));
                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(musteri);            }
        });

       holder.edit_musteri.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent musteri = new Intent(mContext, MusteriKayitActivity.class);
               musteri.putExtra("musteriMid" , String.valueOf(data.get(position).getMid()));
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

    public void restoreItem(Musteri item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<Musteri> getData() {
        return data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView adiSoyadi_item, tarih_item, telefonNo_item;
        ImageView isimBasHarfi_item, edit_musteri;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.isimBasHarfi_item = (ImageView) itemView.findViewById(R.id.imageview);
            this.adiSoyadi_item = (TextView) itemView.findViewById(R.id.adi_soyad_item);
            this.telefonNo_item = (TextView) itemView.findViewById(R.id.telefon_no_item);
            this.edit_musteri = (ImageView) itemView.findViewById(R.id.edit_musteri);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}
