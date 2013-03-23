package com.guessthegame;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
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
        
        //Current list position
        Levels o = levels.get(position);
        
        Log.i("img","" + o.img);
        
        //List row elements - res->layout-> row.xml
        ImageView 		ii = (ImageView) v.findViewById(R.id.img);
        TextView 		nn = (TextView) v.findViewById(R.id.name);
        TextView 		dd = (TextView) v.findViewById(R.id.desc);
        RelativeLayout 	aa = (RelativeLayout) v.findViewById(R.id.actions);
        LinearLayout	pp = (LinearLayout) v.findViewById(R.id.progress_holder);
        LinearLayout	pb = (LinearLayout) v.findViewById(R.id.progress_bar);
        
        //Clear Image
        ii.setImageDrawable(null);
        ii.setImageDrawable(null);
        
        if(o.paid.toString().contains("no")) {
        	pp.setVisibility(View.VISIBLE);
        	aa.setBackgroundResource(R.drawable.start);
        	Log.i("paid","" + o.paid);
        } else {
        	pp.setVisibility(View.INVISIBLE);
        	aa.setBackgroundResource(R.drawable.locked);
        }
        
        //Shared preferences for number of games in the level and number of correct answers for the level
        int noGames 	= ((int) MainActivity.prefs.getInt(o.file+"_cnt", 0));
        int correctCnt 	= ((int) MainActivity.prefs.getInt(o.file+"_correct_cnt", 0));
        
        //Level name
        nn.setText(o.name);
        
        //Level description
        dd.setText(o.desc);
        
        //Work out the percentage correct for the level. MUST BE DOUBLE
        double percent = ((double) correctCnt/noGames)*100;
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 2, (float) percent);
        pb.setLayoutParams(params);
        
        //If level is 100% complete style row
        if(percent == 100) {
        	aa.setBackgroundResource(R.drawable.complete);
        	dd.setText("Completed");
        }
                
        //If level has an image
        if(o.img != null && o.img != "" && o.img.length() > 0) {
	        try {
				ii.setImageBitmap(MainActivity.getBitmap(context, "images/" + o.img));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
        	ii.setBackgroundResource(R.drawable.ic_game);
        }
        return v;
    }
    
}