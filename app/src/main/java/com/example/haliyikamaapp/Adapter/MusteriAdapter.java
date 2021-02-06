package com.example.haliyikamaapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
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
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.OrtakFunction;
import com.example.haliyikamaapp.UI.MainActivity;
import com.example.haliyikamaapp.UI.MusteriDetayActivity;
import com.example.haliyikamaapp.UI.MusteriKayitActivity;
import com.example.haliyikamaapp.UI.SiparisKayitActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusteriAdapter extends RecyclerView.Adapter<MusteriAdapter.MyViewHolder>  implements Filterable {

    private List<Musteri> data;
    private List<Musteri> itemsFiltered;

    private Context mContext;


    public MusteriAdapter(Context mContext, List<Musteri> data) {
        this.data = data;
        this.itemsFiltered = data;
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
        holder.adiSoyadi_item.setText(itemsFiltered.get(position).getMusteriAdi() + " " + itemsFiltered.get(position).getMusteriSoyadi());
        holder.telefonNo_item.setText(itemsFiltered.get(position).getTelefonNumarasi());


        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();
        int color2 = generator.getColor("user@gmail.com");
        int color = generator.getColor(data.get(position));
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(1)
                .endConfig()
                .rect();
        if (!data.get(position).getMusteriAdi().trim().equalsIgnoreCase("")) {
            TextDrawable ic1 = TextDrawable.builder()
                    .buildRound((data.get(position).getMusteriAdi().substring(0, 1).toUpperCase()), color);
            holder.isimBasHarfi_item.setImageDrawable(ic1);
        }


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, MusteriKayitActivity.class);
                musteri.putExtra("musteriMid", String.valueOf(data.get(position).getMid()));
                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(musteri);
            }
        });

        holder.edit_musteri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, MusteriKayitActivity.class);
                musteri.putExtra("musteriMid", String.valueOf(data.get(position).getMid()));
                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(musteri);
            }
        });

        holder.new_siparis_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, SiparisKayitActivity.class);
                musteri.putExtra("musteriMid", String.valueOf(data.get(position).getMid()));
                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(musteri);
            }
        });
        holder.telefon_et_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!data.get(position).getTelefonNumarasi().equalsIgnoreCase(""))
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel: " + data.get(position).getTelefonNumarasi()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.getApplicationContext().startActivity(intent);
                }else {
                    Cursor telefonun_rehberi = mContext.getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                    while (telefonun_rehberi.moveToNext()) {
                        String isim = telefonun_rehberi.getString(telefonun_rehberi.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String numara = telefonun_rehberi.getString(telefonun_rehberi.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        if (isim.equalsIgnoreCase(data.get(position).getMusteriAdi() + " " + data.get(position).getMusteriSoyadi())) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_DIAL); // Action for what intent called for
                            intent.setData(Uri.parse("tel: " + numara)); // Data with intent respective action on intent
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.getApplicationContext().startActivity(intent);
                            telefonun_rehberi.close();

                        }
                    }
                }

            }
        });

        holder.senkron_et_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Musteri musteri = new Musteri();
                musteri.tcKimlikNo = data.get(position).getTcKimlikNo();
                musteri.vergiKimlikNo = data.get(position).getVergiKimlikNo();
                musteri.telefonNumarasi = data.get(position).getTelefonNumarasi();
                musteri.musteriTuru = data.get(position).getMusteriTuru();
                musteri.musteriAdi = data.get(position).getMusteriAdi();
                musteri.musteriSoyadi = data.get(position).getMusteriSoyadi();
             //   ((MainActivity)mContext).postMusteriListFromService(musteri);
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

    public void restoreItem(Musteri item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<Musteri> getData() {
        return itemsFiltered;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

                List<Musteri> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = itemsFiltered;
                } else {
                    for (Musteri u : data) {
                        if (u.getMusteriAdi().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(u);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemsFiltered = (ArrayList<Musteri>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView adiSoyadi_item, tarih_item, telefonNo_item;
        ImageView isimBasHarfi_item, edit_musteri, new_siparis_button, telefon_et_button,senkron_et_button;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.isimBasHarfi_item = (ImageView) itemView.findViewById(R.id.imageview);
            this.adiSoyadi_item = (TextView) itemView.findViewById(R.id.adi_soyad_item);
            this.telefonNo_item = (TextView) itemView.findViewById(R.id.telefon_no_item);
            this.edit_musteri = (ImageView) itemView.findViewById(R.id.edit_musteri);
            this.new_siparis_button = (ImageView) itemView.findViewById(R.id.new_siparis);
            this.telefon_et_button = (ImageView) itemView.findViewById(R.id.telefon_button);
            this.senkron_et_button =  (ImageView) itemView.findViewById(R.id.senkron_musteri);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}
