package com.guessthegame;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

class GameListAdaptor extends ArrayAdapter<Games> {
	
    private ArrayList<Games> games;
    private final Context context;
    public static int addHeight;
    public ImageView ii;
    static int correct;	
    public RelativeLayout cc;
    
    public GameListAdaptor(Context context, int textViewResourceId, ArrayList<Games> items) {
         super(context, textViewResourceId, items);
         
         this.games = items;
         this.context = context;
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
            
    	//View v = convertView;
    	ViewHolder holder = null;
    	final Games o = games.get(position);
    	correct = MainActivity.prefs.getInt(o.img, 0);
    	
        if (convertView == null) {
        	
        	holder = new ViewHolder();
        	
        	LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        	convertView = inflater.inflate(R.layout.game, null);
            
            holder.ii = (ImageView) convertView.findViewById(R.id.game);
	        holder.cc = (RelativeLayout) convertView.findViewById(R.id.actions);
            
	        convertView.setTag(holder);
	        
        } else {
        	holder = (ViewHolder) convertView.getTag();
        }
        
        if(o != null) {

	        holder.ii.setVisibility(View.INVISIBLE);

	        if(correct > 0) {
	        	holder.cc.setVisibility(View.VISIBLE);
	        } else {
	        	holder.cc.setVisibility(View.INVISIBLE);
	        }
	        
	        new loadImage(context, holder.ii, "images/" + o.img, true).execute();
	        
        }
        
        convertView.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				GamesList.currentPage = GamesList.myPager.getCurrentItem();

				GamesList.editor.putInt(GamesList.file+"_page", GamesList.currentPage);

				GamesList.editor.commit();
				
				Intent intent = new Intent(context, Game.class);
	   			
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				
   				intent.putExtra("FILE", o.file);
   				intent.putExtra("IMG", o.img);
   				intent.putExtra("HINT", o.hint);
   				intent.putExtra("SOUND", o.sound);
   				intent.putExtra("ANSWER", o.answer);
   				intent.putExtra("CLOSE", o.close);
   				
   				context.startActivity(intent);
				
			}});
        
        return convertView;
    }
    
    public static class ViewHolder {
        public ImageView ii;
        public RelativeLayout cc;
    }
        
}