package com.example.haliyikamaapp.Adapter;

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

import java.util.ArrayList;
import java.util.List;

public class MusteriAdapter extends RecyclerView.Adapter<MusteriAdapter.MyViewHolder> {

    private List<Musteri> data;

    public MusteriAdapter(List<Musteri> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.musteri_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Musteri myListData = data.get(position);
        holder.adiSoyadi_item.setText(data.get(position).getMusteriAdi());
        holder.tarih_item.setText(data.get(position).getMusteriSoyadi());
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
        TextDrawable ic1 =   TextDrawable.builder()
                .buildRound((data.get(position).getMusteriAdi().substring(0,1)), color);
        holder.isimBasHarfi_item.setImageDrawable(ic1);


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(view.getContext(),"click on item: "+myListData.getDescription(),Toast.LENGTH_LONG).show();
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
        ImageView isimBasHarfi_item;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.adiSoyadi_item = (TextView) itemView.findViewById(R.id.item2);
            this.tarih_item = (TextView) itemView.findViewById(R.id.item1);
            this.telefonNo_item = (TextView) itemView.findViewById(R.id.item3);
            this.isimBasHarfi_item = (ImageView) itemView.findViewById(R.id.imageview);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}
