package com.example.haliyikamaapp.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.GorevFomBilgileri;
import com.example.haliyikamaapp.Model.Entity.Gorevler;
import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.Model.Entity.Siparis;
import com.example.haliyikamaapp.Model.Entity.SiparisDetay;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.UI.BluetoothActivity;
import com.example.haliyikamaapp.UI.MusteriGorevlerimDetayActivity;
import com.example.haliyikamaapp.UI.SiparisDetayActivity;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GorevlerAdapter extends RecyclerView.Adapter<GorevlerAdapter.MyViewHolder> {

    private List<Gorevler> data;
    private Context mContext;
    HaliYikamaDatabase db;

    public GorevlerAdapter(Context mContext, List<Gorevler> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent == null) {
            return null;

        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gorev_item, parent, false);

            return new MyViewHolder(itemView);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        db = HaliYikamaDatabase.getInstance(mContext);

        final Gorevler myListData = data.get(position);
        if (data.get(position).getSiparisTarihi() != null) {
            Timestamp stamp = new Timestamp(Long.valueOf(data.get(position).getSiparisTarihi()));
            DateFormat f = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date date = new Date(stamp.getTime());
            //  holder.tarih_item.setText(f.format(date));
        }

        holder.siparis_sube_adi.setText((data.get(position).getSubeAdi() != null ? data.get(position).getSubeAdi() : "" )+ " / " +
                (data.get(position).getKaynakAdi() != null ? data.get(position).getKaynakAdi() : ""));

        holder.siparis_tutari_item.setText(data.get(position).getTaskDescription());
        holder.sipariş_durumu_item.setText(data.get(position).getTaskName());
        holder.siparis_no_item.setText("\n000" + data.get(position).getSiparisId().toString() + "\n");

        if (data.get(position).getSiparisDurumu() != null) {
            if (data.get(position).getTaskName().equalsIgnoreCase("TeslimAl")) {
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#c056d9"));
                holder.siparis_no_item.setBackgroundColor(Color.parseColor("#c056d9"));

            }

            if (data.get(position).getTaskName().equalsIgnoreCase("Araca Yükle")) {
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#5cee36"));
                holder.siparis_no_item.setBackgroundColor(Color.parseColor("#5cee36"));

            }


            if (data.get(position).getTaskName().equalsIgnoreCase("TeslimEt")) {
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#ea3732"));
                holder.siparis_no_item.setBackgroundColor(Color.parseColor("#ea3732"));

            }


            if (data.get(position).getTaskName().equalsIgnoreCase("Yikama") ||
                    data.get(position).getSiparisDurumu().equalsIgnoreCase("Yıkamada")) {
                holder.sipariş_durumu_item.setTextColor(Color.parseColor("#f48024"));
                holder.siparis_no_item.setBackgroundColor(Color.parseColor("#f48024"));

            }
        }

        List<Musteri> musteriList = db.musteriDao().getMusteriForId(data.get(position).getMusteriId());
        holder.adres_item.setText(musteriList.size() > 0 ? ((musteriList.get(0).getAdres() != null ? musteriList.get(0).getAdres() : "") + " / "
                + (musteriList.get(0).getBolge() != null ? musteriList.get(0).getBolge() : "")) : "");

        List<Siparis> siparisList = db.siparisDao().getSiparisForSiparisId(data.get(position).getSiparisId());

        holder.adres_item2.setText(siparisList.size() > 0 ? siparisList.get(0).getAciklama() : "Sipariş Notu Yok");

        holder.musteri_adi.setText(data.get(position).getMusteriAdi() + " " + (data.get(position).getMusteriSoyadi() != null ? data.get(position).getMusteriSoyadi() : ""));
        holder.siparis_tutari_item.setText(data.get(position).getSiparisToplamTutar() != null ? String.valueOf(data.get(position).getSiparisToplamTutar()) : null);


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent musteri = new Intent(mContext, MusteriGorevlerimDetayActivity.class);
                musteri.putExtra("gorevMid" , String.valueOf(data.get(position).getMid()));
                musteri.putExtra("gorevId" , String.valueOf(data.get(position).getTaskId()));
                musteri.putExtra("siparisId" , String.valueOf(data.get(position).getSiparisId()));
                musteri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(musteri);*/





              /*  if (data.get(position).getTaskName().equalsIgnoreCase("TeslimAl")) {
                    List<Siparis> siparisList = null;
                    List<SiparisDetay> siparisDetayList = null;
                    List<Gorevler> gorevlerList = db.gorevlerDao().getGorevForId(Long.valueOf(data.get(position).getTaskId()));
                    if (gorevlerList.size() > 0) {
                        siparisList = db.siparisDao().getSiparisForSiparisId(gorevlerList.get(0).getSiparisId());
                        if (siparisList.size() > 0)
                            siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisId(siparisList.get(0).getId());
                        if (siparisDetayList.size() == 0)
                            siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisMid(siparisList.get(0).getMid());

                    }
                    Intent i = new Intent(mContext, SiparisDetayActivity.class);
                    // i.putExtra("gelenPage", "sipariş");
                    i.putExtra("siparisId", String.valueOf(data.get(position).getSiparisId()));
                    i.putExtra("subeMid", siparisList.get(0).getSubeMid() != null ? siparisList.get(0).getSubeMid().toString() : null);
                    i.putExtra("subeId", siparisList.get(0).getSubeId() != null ? siparisList.get(0).getSubeId().toString() : null);
                    i.putExtra("siparisMid", siparisList != null ? siparisList.get(0).getMid().toString() : null);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.getApplicationContext().startActivity(i);

                }else{*/

                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mView = inflater.inflate(R.layout.alert_dialog_gorev, null);

                final CardView ara_tamamla = (CardView) mView.findViewById(R.id.ara_tamamla);
                final CardView sms_tamamla = (CardView) mView.findViewById(R.id.sms_tamamla);
                final CardView yazdir_tamamla = (CardView) mView.findViewById(R.id.yazdir_tamamla);
                final CardView tamamla_tamamla = (CardView) mView.findViewById(R.id.tamamla_Tamamla);
                final CardView konum_tamamla = (CardView) mView.findViewById(R.id.konum_tamamla);
                final CardView whatsapp_tamamla = (CardView) mView.findViewById(R.id.whatsapp_tamamla);


                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                dialog.show();

                ara_tamamla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!data.get(position).getTelefonNumarasi().equalsIgnoreCase("")) {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel: " + data.get(position).getTelefonNumarasi()));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.getApplicationContext().startActivity(intent);
                        } else {
                            Cursor telefonun_rehberi = mContext.getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                            while (telefonun_rehberi.moveToNext()) {
                                String isim = telefonun_rehberi.getString(telefonun_rehberi.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                String numara = telefonun_rehberi.getString(telefonun_rehberi.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                if (isim.equalsIgnoreCase(data.get(position).getMusteriAdi() + " " +
                                        data.get(position).getMusteriSoyadi())) {
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


                sms_tamamla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("smsto:" + data.get(position).getTelefonNumarasi())); // This ensures only SMS apps respond
                        intent.putExtra("sms_body", "Merhabalar..");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        mContext.getApplicationContext().startActivity(intent);

                    }
                });


                yazdir_tamamla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (data.get(position).getSiparisId() != null) {
                            Intent bluetooth = new Intent(mContext, BluetoothActivity.class);
                            bluetooth.putExtra("siparisId", data.get(position).getSiparisId().toString());
                            bluetooth.putExtra("subeAdi", data.get(position).getSubeAdi());
                            bluetooth.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            mContext.getApplicationContext().startActivity(bluetooth);
                        }

                    }
                });


                tamamla_tamamla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<Siparis> siparisList = null;
                        List<SiparisDetay> siparisDetayList = null;
                        List<Gorevler> gorevlerList = db.gorevlerDao().getGorevForId(Long.valueOf(data.get(position).getTaskId()));
                        if (gorevlerList.size() > 0) {
                            siparisList = db.siparisDao().getSiparisForSiparisId(gorevlerList.get(0).getSiparisId());
                            if (siparisList.size() > 0)
                                siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisId(siparisList.get(0).getId());
                            if (siparisDetayList.size() == 0)
                                siparisDetayList = db.siparisDetayDao().getSiparisDetayForSiparisMid(siparisList.get(0).getMid());

                        }
                        Intent i = new Intent(mContext, SiparisDetayActivity.class);
                        // i.putExtra("gelenPage", "sipariş");
                        i.putExtra("siparisId", String.valueOf(data.get(position).getSiparisId()));
                        i.putExtra("subeMid", siparisList.get(0).getSubeMid() != null ? siparisList.get(0).getSubeMid().toString() : null);
                        i.putExtra("subeId", siparisList.get(0).getSubeId() != null ? siparisList.get(0).getSubeId().toString() : null);
                        i.putExtra("siparisMid", siparisList != null ? siparisList.get(0).getMid().toString() : null);
                        i.putExtra("gorevId", data.get(position).getTaskId());

                        // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(i);


                    }
                });


                konum_tamamla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        List<Musteri> musteriList = null;
                        musteriList = db.musteriDao().getMusteriForId(data.get(position).getMusteriId());
                        if (musteriList.size() > 0) {

                            if (musteriList.get(0).getxKoor() != null && musteriList.get(0).getyKoor() != null
                                    && Double.valueOf(musteriList.get(0).getxKoor()) > 0 && Double.valueOf(musteriList.get(0).getyKoor()) > 0) {
                                String geoUri = "http://maps.google.com/maps?q=loc:" + Double.valueOf(musteriList.get(0).getyKoor()) + "," + Double.valueOf(musteriList.get(0).getxKoor()) + " (" + "" + ")";
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(geoUri));
                                mContext.getApplicationContext().startActivity(intent);
                            } else if (!musteriList.get(0).getAdres().equalsIgnoreCase("")) {
                                String url = "http://maps.google.com/maps?daddr=" + musteriList.get(0).getAdres();
                                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.getApplicationContext().startActivity(intent);
                            }
                        }
                    }
                });


                whatsapp_tamamla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        List<Musteri> musteriList = null;
                        musteriList = db.musteriDao().getMusteriForId(data.get(position).getMusteriId());

                        String url = "https://api.whatsapp.com/send?phone=+90" + musteriList.get(0).getTelefonNumarasi() + "&text=Merhabalar! ...";
                        try {
                            PackageManager pm = mContext.getApplicationContext().getPackageManager();
                            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.getApplicationContext().startActivity(i);
                        } catch (PackageManager.NameNotFoundException e) {
                            Toast.makeText(mContext, "Lütfen cihazınıza Whatsapp uygulamasını yükleyiniz..", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }


                    }
                });


            }

            //  }
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

    public void restoreItem(Gorevler item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<Gorevler> getData() {
        return data;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView sipariş_durumu_item, siparis_tutari_item, sube_item, adres_item, musteri_adi, adres_item2, siparis_sube_adi, siparis_no_item;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.siparis_tutari_item = (TextView) itemView.findViewById(R.id.siparis_tutari_item);
            this.sipariş_durumu_item = (TextView) itemView.findViewById(R.id.siparis_durum_item);

            this.adres_item = (TextView) itemView.findViewById(R.id.siparis_adres_item);
            this.adres_item2 = (TextView) itemView.findViewById(R.id.siparis_adres_item2);
            this.musteri_adi = (TextView) itemView.findViewById(R.id.siparis_musteri_adi);
            this.siparis_sube_adi = (TextView) itemView.findViewById(R.id.siparis_sube_adi);
            this.siparis_no_item = (TextView) itemView.findViewById(R.id.siparis_no_item);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}

