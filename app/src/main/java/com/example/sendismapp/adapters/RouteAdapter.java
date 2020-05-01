package com.example.sendismapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sendismapp.R;
import com.example.sendismapp.logic.Ruta;

import java.util.List;

public class RouteAdapter extends BaseAdapter {

    List<Ruta> arrayList;
    Context context;
    public RouteAdapter(Context context, List<Ruta> arrayList) {
        this.arrayList=arrayList;
        this.context=context;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.adapter_search_reult, parent, false);
        }
        TextView route_name = (TextView) convertView.findViewById(R.id.text_view_route_name);
        TextView route_distance=(TextView) convertView.findViewById(R.id.text_view_distance_adapter);
        TextView route_difficulty=(TextView)convertView.findViewById(R.id.text_view_difficulty_adapter);
        Ruta route=arrayList.get(position);
        route_name.setText(route.getNombre());
        route_distance.setText( String.valueOf( route.getDistancia() ) );
        route_difficulty.setText(String.valueOf(route.getDificultad()));
        //TODO: programmatically set imageview:src to the route image
        return convertView;
    }
}
