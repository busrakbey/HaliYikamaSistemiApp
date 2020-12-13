package com.example.haliyikamaapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Model.MyListData;
import com.example.haliyikamaapp.R;

import java.util.ArrayList;

public class MusteriAdapter extends RecyclerView.Adapter<MusteriAdapter.MyViewHolder> {

    private ArrayList<MyListData> data;

    public MusteriAdapter(ArrayList<MyListData> data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.musteri_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final MyListData myListData = data.get(position);
        holder.adiSoyadi_item.setText(data.get(position).getMusteriAdiSoyadi());
        holder.tarih_item.setText(data.get(position).getTarih());
        holder.telefonNo_item.setText(data.get(position).getTelefonNo());
        // holder.imageView.setImageResource(listdata[position].getTarih());
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

    public void restoreItem(MyListData item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public ArrayList<MyListData> getData() {
        return data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView adiSoyadi_item, tarih_item, telefonNo_item;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.adiSoyadi_item = (TextView) itemView.findViewById(R.id.item2);
            this.tarih_item = (TextView) itemView.findViewById(R.id.item1);
            this.telefonNo_item = (TextView) itemView.findViewById(R.id.item3);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}
