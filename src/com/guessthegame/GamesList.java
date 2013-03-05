package com.guessthegame;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class GamesList extends Activity {

	static String file, name, desc, img = "";
	GameListAdaptor adaptor;
	GridView listViewGames;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games_list);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Bundle extras = getIntent().getExtras();
		
		if(extras != null) {
			file 	= extras.getString("FILE");
			name	= extras.getString("NAME");
			desc	= extras.getString("DESC");
			img		= extras.getString("IMG");
		}
		
		if(file != "") {
			
			final TextView Tname = (TextView) findViewById(R.id.name);
			final TextView Tdesc = (TextView) findViewById(R.id.desc);
			
			Tname.setText(name);
			Tdesc.setText(desc);
			
			adaptor = new GameListAdaptor(this,R.layout.game, loadGames());
			
			listViewGames = (GridView) findViewById(R.id.games);
			
			listViewGames.setClickable(true);
			
			listViewGames.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					
					Games games = adaptor.getItem(position);
					
					Intent intent = new Intent(getBaseContext(), Game.class);
					
					intent.putExtra("IMG", games.img);
					intent.putExtra("HINT", games.hint);
					intent.putExtra("SOUND", games.sound);
					intent.putExtra("ANSWER", games.answer);
					intent.putExtra("CLOSE", games.close);
					
					startActivity(intent);
					
				}
				
			});
			
			listViewGames.setAdapter(adaptor);
			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_games_list, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() { 
		// After a pause OR at startup
		super.onResume();
		adaptor.notifyDataSetChanged();
		//Refresh your stuff here
	}
	
	private ArrayList<Games> loadGames(){
        ArrayList<Games> games = new ArrayList<Games>();
        		
		String jsonString = null;
		
		InputStream in;
		try {
			in = this.getAssets().open(file);
			
			int size = in.available();
			
			byte[] buffer = new byte[size];
			in.read(buffer);
			in.close();
			
			jsonString = new String(buffer);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		try {
			
			JSONObject jsonObj = new JSONObject(jsonString);
			
			JSONArray gamesArr = jsonObj.getJSONArray("games");
			
			for (int i = 0; i < gamesArr.length(); i++) { 

            	JSONObject j = gamesArr.getJSONObject(i);
			            	
				Games game= new Games();
				
				game.img 	= (j.getString("img") != "" ? j.getString("img") : "" );
		        game.hint 	= (j.getString("hint") != "" ? j.getString("hint") : "" );
		        game.sound 	= (j.getString("sound") != "" ? j.getString("sound") : "" );
		        game.answer	= (j.getString("answer") != "" ? j.getString("answer") : "" );
		        game.close	= (j.getString("close") != "" ? j.getString("close") : "" );
		        
		        Log.i("games","" + game);
		        
		        games.add(game);
		       
			}
	        
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return games;
		
	}

}
