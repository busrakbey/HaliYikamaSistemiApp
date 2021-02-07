package com.example.haliyikamaapp.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.MainActivity;
import com.example.haliyikamaapp.UI.SiparisDetayActivity;
import com.example.haliyikamaapp.UI.SiparisKayitActivity;

import java.util.ArrayList;
import java.util.List;

public class SiparisAdapter extends RecyclerView.Adapter<SiparisAdapter.MyViewHolder> implements Filterable {

    private List<Siparis> data;
    private List<Siparis> itemsFiltered;
    private Context mContext;


    public SiparisAdapter(Context mContext, List<Siparis> data) {
        this.data = data;
        this.itemsFiltered = data;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.siparis_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Siparis myListData = data.get(position);
        holder.tarih_item.setText(data.get(position).getSiparisTarihi() != null ? data.get(position).getSiparisTarihi() : "" );
       // holder.sipariş_durumu_item.setText(data.get(position).getSiparisDurumu() != null ? data.get(position).getSiparisDurumu() : "" );
        holder.sipariş_durumu_item.setText("SİPARİŞ ALINDI");
        holder.siparis_tutari_item.setText(data.get(position).getSiparisTutar() != null ? data.get(position).getSiparisTutar().toString() + " TL": ""  );
        holder.sube_item.setText("Merkez Şube" );



        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();
        int color2 = generator.getColor("user@gmail.com");
        int color = generator.getColor(data.get(position));
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(1)
                .endConfig()
                .rect();



        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, SiparisDetayActivity.class);
                musteri.putExtra("siparisMid" , String.valueOf(data.get(position).getMid()));
                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(musteri);            }
        });

        holder.edit_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, SiparisKayitActivity.class);
                musteri.putExtra("siparisMid" , String.valueOf(data.get(position).getMid()));
                musteri.putExtra("musteriMid" , String.valueOf(data.get(position).getMusteriMid()));
                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(musteri);
            }
        });

        holder.senkron_siparis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Siparis siparis = new Siparis();
                siparis.musteriId = data.get(position).getMusteriId();
                siparis.siparisTarihi= data.get(position).getSiparisTarihi();
                siparis.subeId = data.get(position).getSubeId();
                Long siparisMid = data.get(position).getMid();
                ((MainActivity)mContext).postSiparisListFromService(siparis, siparisMid);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemsFiltered.size();
    }


    public void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Siparis item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<Siparis> getData() {
        return itemsFiltered;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                List<Siparis> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = itemsFiltered;
                } else {
                    for (Siparis u : data) {
                      /*  if (u.g get.toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(u);
                        }*/
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemsFiltered = (ArrayList<Siparis>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView tarih_item, sipariş_durumu_item, siparis_tutari_item, sube_item;
        ImageView edit_item, senkron_siparis;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.tarih_item = (TextView) itemView.findViewById(R.id.siparis_tarih_item);
            this.siparis_tutari_item = (TextView) itemView.findViewById(R.id.siparis_tutari_item);
            this.sipariş_durumu_item = (TextView) itemView.findViewById(R.id.siparis_durum_item);
            this.sube_item = (TextView) itemView.findViewById(R.id.siparis_sube_item);
            this.edit_item = (ImageView) itemView.findViewById(R.id.edit_imageview);
            this.senkron_siparis = (ImageView) itemView.findViewById(R.id.senkron_siparis);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}

