package com.example.haliyikamaapp.AutoCompleteAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.haliyikamaapp.Model.Entity.Musteri;
import com.example.haliyikamaapp.R;

import java.util.ArrayList;
import java.util.List;

public class MusteriAutoCompleteAdapter  extends ArrayAdapter<Musteri> {

    Context context;
    int resource, textViewResourceId;
    List<Musteri> items, tempItems, suggestions;

    public MusteriAutoCompleteAdapter(Context context, int resource, int textViewResourceId, List<Musteri> items) {
        super(context, resource, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<Musteri>(items); // this makes the difference.
        suggestions = new ArrayList<Musteri>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row, parent, false);
        }
        Musteri people = items.get(position);
        if (people != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null)
                lblName.setText(people.getMusteriAdi() + " " + people.getMusteriSoyadi());
        }
        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = (((Musteri) resultValue).getMusteriAdi() + " " + ((Musteri) resultValue).getMusteriSoyadi());
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Musteri musteri : tempItems) {
                    if (musteri.getMusteriAdi().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(musteri);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Musteri> filterList = (ArrayList<Musteri>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (Musteri musteri : filterList) {
                    add(musteri);
                    notifyDataSetChanged();
                }
            }
        }
    };
}
