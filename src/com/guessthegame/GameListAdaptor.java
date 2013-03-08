package com.guessthegame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
        
        correct = MainActivity.prefs.getInt(o.img, 0);
        
        if(correct == 1) ii.setBackgroundColor(Color.GREEN);
        
        
        new loadImage(context, ii, "images/" + o.img).execute();
        
        return v;
    }
        
}