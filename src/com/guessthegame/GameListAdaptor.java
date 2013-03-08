package com.guessthegame;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

class GameListAdaptor extends ArrayAdapter<Games> {
	
    private ArrayList<Games> games;
    private final Context context;
    public static int addHeight;
    public ImageView ii;
    static int correct;	
    
    public GameListAdaptor(Context context, int textViewResourceId, ArrayList<Games> items) {
         super(context, textViewResourceId, items);
         
         this.games = items;
         this.context = context;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            
    	View v = convertView;
            
        if (v == null) {
        	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.game, null);
        }
        
        Games o = games.get(position);
		
        ii = (ImageView) v.findViewById(R.id.game);
        
        ii.setVisibility(View.VISIBLE);
        
        correct = MainActivity.prefs.getInt(o.img, 0);
        
        Log.i("correct",o.img + " - " + correct);
        
        if(correct > 0) {
        	ii.setBackgroundColor(Color.GREEN);
        } else {
        	ii.setBackgroundColor(Color.TRANSPARENT);
        }
        
        new loadImage(context, ii, "images/" + o.img).execute();
        
        return v;
    }
        
}