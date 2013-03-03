package com.guessthegame;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

class LevelListAdaptor extends ArrayAdapter<Levels> {
	
    private ArrayList<Levels> levels;
    private final Context context;
    public static int addHeight;
    
    public LevelListAdaptor(Context context, int textViewResourceId, ArrayList<Levels> items) {
         super(context, textViewResourceId, items);
         
         this.levels = items;
         this.context = context;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            
    	View v = convertView;
            
        if (v == null) {
        	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.row, null);
        }
        
        Levels o = levels.get(position);
        
        ImageView ii = (ImageView) v.findViewById(R.id.img);
        TextView tt = (TextView) v.findViewById(R.id.name);
        TextView bt = (TextView) v.findViewById(R.id.desc);
        
        tt.setText(o.name);
        bt.setText(o.desc);

        //ii.setImageResource(o.img);
        
        return v;
    }
    
}