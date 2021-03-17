package com.example.haliyikamaapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.Model.Entity.Sube;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.UI.MainActivity;
import com.example.haliyikamaapp.UI.SiparisDetayActivity;
import com.example.haliyikamaapp.UI.SiparisKayitActivity;

import java.util.ArrayList;
import java.util.List;

import static androidx.annotation.InspectableProperty.ValueType.COLOR;


public class SiparisAdapter extends RecyclerView.Adapter<SiparisAdapter.MyViewHolder> implements Filterable {

    private List<Siparis> data;
    private List<Siparis> itemsFiltered;
    private Context mContext;
    private Boolean gorevEkraniMi;
    HaliYikamaDatabase db;


    public SiparisAdapter(Context mContext, List<Siparis> data, Boolean gorevEkraniMi) {
        this.data = data;
        this.itemsFiltered = data;
        this.mContext = mContext;
        this.gorevEkraniMi = gorevEkraniMi;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.siparis_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        db = HaliYikamaDatabase.getInstance(mContext);
        final Siparis myListData = data.get(position);
        holder.tarih_item.setText(itemsFiltered.get(position).getSiparisTarihi() != null ? itemsFiltered.get(position).getSiparisTarihi() : "");
        // holder.sipariş_durumu_item.setText(itemsFiltered.get(position).getSiparisDurumu() != null ? itemsFiltered.get(position).getSiparisDurumu() : "" );

        if (itemsFiltered.get(position).getSiparisDurumu() != null) {
            if (itemsFiltered.get(position).getSiparisDurumu().equalsIgnoreCase("Teslim Alınacak"))
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#FF9800"));

            if (itemsFiltered.get(position).getSiparisDurumu().equalsIgnoreCase("Teslime Hazır"))
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#32C24D"));

            if (itemsFiltered.get(position).getSiparisDurumu().equalsIgnoreCase("Teslim Edildi"))
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#0000FF"));

            if (itemsFiltered.get(position).getSiparisDurumu().equalsIgnoreCase("Teslime Çıktı"))
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#50B050"));

            if (itemsFiltered.get(position).getSiparisDurumu().equalsIgnoreCase("Yıkamada"))
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#FF0000"));

            if (itemsFiltered.get(position).getSiparisDurumu().equalsIgnoreCase("Yıkanacak"))
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#2E8B57"));
        }


        holder.sipariş_durumu_item.setText(itemsFiltered.get(position).getSiparisDurumu());
        holder.siparis_tutari_item.setText(String.valueOf(0));
        List<SiparisDetay> siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisMid(itemsFiltered.get(position).getMid());
        List<SiparisDetay> siparisDetayList2 = db.siparisDetayDao().getSiparisDetayForSiparisId(itemsFiltered.get(position).getId());
        for (SiparisDetay item : siparisDetayList2)
            if (holder.siparis_tutari_item.getText().toString() != null && item.getBirimFiyat()!= null && item.getMiktar() != null)
                holder.siparis_tutari_item.setText(String.valueOf(Double.valueOf(holder.siparis_tutari_item.getText().toString()) + item.getBirimFiyat() * item.getMiktar()));
            else
                holder.siparis_tutari_item.setText(holder.siparis_tutari_item.getText().toString());
        if (siparisDetayList2.size() == 0) {
            for (SiparisDetay item : siparisDetayList)
                if (holder.siparis_tutari_item.getText().toString() != null && item.getBirimFiyat()!= null && item.getMiktar() != null)
                    holder.siparis_tutari_item.setText(String.valueOf(Double.valueOf(holder.siparis_tutari_item.getText().toString()) + item.getBirimFiyat() * item.getMiktar()));
                else
                    holder.siparis_tutari_item.setText(holder.siparis_tutari_item.getText().toString());


        }
        List<Sube> sube = db.subeDao().getSubeForId(itemsFiltered.get(position).getSubeId());
        List<Sube> sube2 = db.subeDao().getSubeForMid(itemsFiltered.get(position).getSubeMid());
        if (sube.size() > 0)
            holder.sube_item.setText(sube.get(0).getSubeAdi());
        if (sube2.size() > 0)
            holder.sube_item.setText(sube.get(0).getSubeAdi());


        List<Musteri> musteriListId = db.musteriDao().getMusteriForId(itemsFiltered.get(position).getMusteriId());
        if (musteriListId.size() > 0) {
            holder.adres_item.setText((musteriListId.get(0).getAdres() != null ? musteriListId.get(0).getAdres() : "") + " / " +
                    (musteriListId.get(0).getBolge() != null ? musteriListId.get(0).getBolge() : ""));
            holder.musteri_adi.setText(musteriListId.get(0).getMusteriAdi() );
        } else {
            musteriListId = db.musteriDao().getMusteriForMid(itemsFiltered.get(position).getMusteriMid());
            holder.adres_item.setText((musteriListId.get(0).getAdres() != null ? musteriListId.get(0).getAdres() : "") + " / " +
                    (musteriListId.get(0).getBolge() != null ? musteriListId.get(0).getBolge() : ""));
            holder.musteri_adi.setText(musteriListId.get(0).getMusteriAdi());

        }

        holder.aciklama_item.setText((itemsFiltered.get(position).getAciklama() != null && !itemsFiltered.get(position).getAciklama().equalsIgnoreCase("")) ? itemsFiltered.get(position).getAciklama() : "Sipariş Notu Yok.");


        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        int color1 = generator.getRandomColor();
        int color2 = generator.getColor("user@gmail.com");
        final int color = generator.getColor(itemsFiltered.get(position));
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(1)
                .endConfig()
                .rect();


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, SiparisDetayActivity.class);
                musteri.putExtra("siparisMid", String.valueOf(itemsFiltered.get(position).getMid()));
                musteri.putExtra("subeId", String.valueOf(itemsFiltered.get(position).getSubeId()));
                musteri.putExtra("subeMid", String.valueOf(itemsFiltered.get(position).getSubeMid()));
                musteri.putExtra("siparisId", String.valueOf(itemsFiltered.get(position).getId()));
                musteri.putExtra("musteriId", String.valueOf(itemsFiltered.get(position).getMusteriId()));
                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(musteri);
            }
        });

        holder.edit_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, SiparisKayitActivity.class);
                musteri.putExtra("siparisMid", String.valueOf(itemsFiltered.get(position).getMid()));
                musteri.putExtra("siparisId", String.valueOf(itemsFiltered.get(position).getId()));
                musteri.putExtra("musteriMid", String.valueOf(itemsFiltered.get(position).getMusteriMid()));
                musteri.putExtra("siparisId", String.valueOf(itemsFiltered.get(position).getId()));
                musteri.putExtra("musteriId", String.valueOf(itemsFiltered.get(position).getMusteriId()));

                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(musteri);
            }
        });

        holder.senkron_siparis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemsFiltered.get(position).getProcessInstanceId() != null)
                    MessageBox.showAlert(mContext, "Süreç zaten başlatılmıştır.\n", false);
                else
                    ((MainActivity) mContext).postSiparisSureciBaslatService(itemsFiltered.get(position));
            }
        });

        if (gorevEkraniMi != null && gorevEkraniMi == true) {
            holder.gorev_siparis_layout.setVisibility(View.VISIBLE);
            holder.gorev_siparis_layout2.setVisibility(View.GONE);
        }


        if (data.get(position).getSiparisDurumu() != null) {

            if (data.get(position).getSiparisDurumu().equalsIgnoreCase("Teslim Edildi")) {
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#5cee36"));
                holder.siparis_no_item.setBackgroundColor(Color.parseColor("#5cee36"));

            }

        }

        if(data.get(position).getId() != null)
        holder.siparis_no_item.setText("\n000" + data.get(position).getId().toString() + "\n");

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

                List<Musteri> filteredMusteri = new ArrayList<>();

                for (Siparis u : data) {
                    if (u.getMusteriId() != null)
                        filteredMusteri.add(db.musteriDao().getMusteriForId(u.getMusteriId()).get(0));
                    else
                        filteredMusteri.add(db.musteriDao().getMusteriForMid(u.getMusteriMid()).get(0));


                }

                if (query.isEmpty()) {
                    filtered = itemsFiltered;
                } else {
                    for (Musteri m : filteredMusteri) {
                        if (m.getMusteriAdi().toLowerCase().contains(query.toLowerCase())) {
                            if (db.siparisDao().getSiparisForMusterIid(m.getId()).size() > 0)
                                filtered.add(db.siparisDao().getSiparisForMusterIid(m.getId()).get(0));
                            if (db.siparisDao().getSiparisForMusteriMid(m.getId()).size() > 0)
                                filtered.add(db.siparisDao().getSiparisForMusteriMid(m.getMid()).get(0));

                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults
                    filterResults) {
                itemsFiltered = (ArrayList<Siparis>) filterResults.values;
                notifyDataSetChanged();
            }
        }

                ;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView tarih_item, sipariş_durumu_item, siparis_tutari_item, sube_item, adres_item, musteri_adi, aciklama_item, siparis_no_item;
        ImageView edit_item, senkron_siparis;
        LinearLayout gorev_siparis_layout, gorev_siparis_layout2;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.tarih_item = (TextView) itemView.findViewById(R.id.siparis_tarih_item);
            this.siparis_tutari_item = (TextView) itemView.findViewById(R.id.siparis_tutari_item);
            this.sipariş_durumu_item = (TextView) itemView.findViewById(R.id.siparis_durum_item);
            this.sube_item = (TextView) itemView.findViewById(R.id.siparis_sube_item);
            this.edit_item = (ImageView) itemView.findViewById(R.id.edit_imageview);
            this.senkron_siparis = (ImageView) itemView.findViewById(R.id.senkron_siparis);
            this.adres_item = (TextView) itemView.findViewById(R.id.siparis_adres_item);
            this.musteri_adi = (TextView) itemView.findViewById(R.id.siparis_musteri_adi);
            this.aciklama_item = (TextView) itemView.findViewById(R.id.siparis_aciklama_item);
            this.gorev_siparis_layout = (LinearLayout) itemView.findViewById(R.id.gorev_siparis_layout);
            this.siparis_no_item = (TextView) itemView.findViewById(R.id.siparis_no_item);
            this.gorev_siparis_layout2 = (LinearLayout) itemView.findViewById(R.id.gorev_siparis_layout2);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}

