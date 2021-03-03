package com.example.haliyikamaapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.Model.Entity.Kaynak;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.ToolLayer.MessageBox;
import com.example.haliyikamaapp.UI.KaynakActivity;
import com.example.haliyikamaapp.UI.KaynakKayitActivity;
import com.example.haliyikamaapp.UI.MainActivity;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class KaynakAdapter extends RecyclerView.Adapter<KaynakAdapter.MyViewHolder> {

    private List<Kaynak> data;
    private Context mContext;
    HaliYikamaDatabase db;


    public KaynakAdapter(Context mContext, List<Kaynak> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.kaynak_item, parent, false);
        return new MyViewHolder(itemView);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Kaynak myListData = data.get(position);
        db = HaliYikamaDatabase.getInstance(mContext);


        holder.kaynak_marka_item.setText(data.get(position).getMarka() != null ? data.get(position).getMarka() : null);
        holder.kaynak_adi_item.setText(data.get(position).getKaynakTuru() != null ? data.get(position).getKaynakTuru() : null);
        holder.kaynak_plaka_item.setText(data.get(position).getKaynakAdi() != null ? data.get(position).getKaynakAdi() : null);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent musteri = new Intent(mContext, KaynakKayitActivity.class);
                musteri.putExtra("kaynakId", String.valueOf(data.get(position).getMid()));
                musteri.putExtra("kaynakMid", String.valueOf(data.get(position).getId()));
                mContext.getApplicationContext().startActivity(musteri);
            }
        });

        if(data.get(position).getSecilenKaynakMi() != null && data.get(position).getSecilenKaynakMi() == true) {
            holder.relativeLayout.setBackgroundResource(R.drawable.para_giris_shape_bk);
        }


        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {


                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Sistem");
                builder.setMessage("Kaynak seçilecektir. Devam etmek istiyor musunuz?");
                builder.setNegativeButton("Hayır", null);
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int xx = db.kaynakDao().updateSecilenKaynakMiAll();
                        int yy = db.kaynakDao().updateSecilenKaynakMi(data.get(position).getMid(), true);
                        if (xx > 0 && yy > 0) {
                            MessageBox.showAlert(mContext, "Kaynak seçimi başarılı bir şekilde gerçekleşmiştir.", false);
                            ((KaynakActivity) mContext).get_list();
                        }


                    }
                });
                builder.show();


                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void removeItem(final int position, final List<Kaynak> data) {
        data.remove(position);
        notifyItemRemoved(position);

        db = HaliYikamaDatabase.getInstance(mContext);

    }

    public void restoreItem(Kaynak item, final int position) {
        data.add(position, item);
        notifyItemInserted(position);


    }

    public List<Kaynak> getData() {
        return data;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView kaynak_plaka_item, kaynak_adi_item, kaynak_marka_item;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.kaynak_plaka_item = (TextView) itemView.findViewById(R.id.kaynak_plaka_item);
            this.kaynak_adi_item = (TextView) itemView.findViewById(R.id.kaynak_adi_item);
            this.kaynak_marka_item = (TextView) itemView.findViewById(R.id.kaynak_marka_item);


            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}
