package com.example.haliyikamaapp.Adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.haliyikamaapp.Database.HaliYikamaDatabase;
import com.example.haliyikamaapp.R;
import com.example.haliyikamaapp.UI.DialogConfigFragment;
import com.example.haliyikamaapp.UI.TanimlamalarActivity;

import java.util.List;

public class AyarlarAdapter extends RecyclerView.Adapter<AyarlarAdapter.MyViewHolder>  {

    private List<String> data;
    private Context mContext;



    public AyarlarAdapter(Context mContext, List<String> data) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public AyarlarAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ayarlar_item, parent, false);
        return new AyarlarAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AyarlarAdapter.MyViewHolder holder, final int position) {
        final String myListData = data.get(position);
        HaliYikamaDatabase db = HaliYikamaDatabase.getInstance(mContext);
        holder.ayarlar_item.setText(data.get(position));

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.get(position).toString().equalsIgnoreCase("Sistem Url Adresi")){

                    FragmentManager manager =((AppCompatActivity)mContext).getFragmentManager();
                    DialogConfigFragment kd = new DialogConfigFragment(mContext);
                    kd.show(manager,"");
                }
            }
        });


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.get(position).toString().equalsIgnoreCase("Tanımlamalar")){

                   Intent i = new Intent(mContext, TanimlamalarActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.getApplicationContext().startActivity(i);
                }
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

    public void restoreItem(String item, int position) {
        data.add(position, item);
        notifyItemInserted(position);
    }

    public List<String> getData() {
        return data;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        public TextView ayarlar_item, olcu_birimi_item, miktar_item, fiyat_item;
        // ImageView isimBasHarfi_item;


        public MyViewHolder(View itemView) {
            super(itemView);
            this.ayarlar_item = (TextView) itemView.findViewById(R.id.ayarlar_item_text);

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

}
