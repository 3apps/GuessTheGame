package com.guessthegame;
	
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.viewpagerindicator.LinePageIndicator;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
	
public class GamesList extends Activity {
	
	static String file, name, desc, img = "";
	GameListAdaptor adaptor;
	GridView listViewGames;
	static TextView Tname;
	static TextView Tdesc;
	static TextView Tcount;
	static ViewPager myPager;
	int perPage;

	int gamesCnt = 0;
	int pagesNo = 1;
	static int currentPage = 0;
	Boolean redirect = false;
	static SharedPreferences.Editor editor;
	
	private class MyPagerAdapter extends PagerAdapter {
        public int getCount() {
        	
        	pagesNo = (int) Math.ceil(((float) gamesCnt / (float) perPage));

            return (pagesNo > 1 ? pagesNo  : 1 );
        }
        
        public Object instantiateItem(View collection, int position) {

            LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(getBaseContext().LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.activity_games_list, null);
            ((ViewPager) collection).addView(view, 0);

            adaptor = new GameListAdaptor(getBaseContext(),R.layout.game, loadGames(position+1));
					
			listViewGames = (GridView) findViewById(R.id.games);
			   
			listViewGames.setClickable(true);

 	   		listViewGames.setAdapter(adaptor);
 	   		
 	   		adaptor.setNotifyOnChange(true);
 	   	
			return view;
	            
	        }
	        @Override
	        public void destroyItem(View arg0, int arg1, Object arg2) {
	            ((ViewPager) arg0).removeView((View) arg2);
	        }
	        @Override
	        public boolean isViewFromObject(View arg0, Object arg1) {
	            return arg0 == ((View) arg1);
	        }
	        @Override
	        public Parcelable saveState() {
	            return null;
	        }
	        public int getItemPosition(Object object){
	            return POSITION_NONE;
	       }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_games_page);

	}
	
	@Override
	public void onResume() { 
		// After a pause OR at startup
		super.onResume();
		
		if (MainActivity.landscape) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
		perPage = Integer.parseInt(getResources().getString(R.string.gameGridSize));
		
		editor = MainActivity.prefs.edit();
		
		Bundle extras = getIntent().getExtras();
		
		if(extras != null) {
			file 	= extras.getString("FILE");
			name	= extras.getString("NAME");
			desc	= extras.getString("DESC");
			img		= extras.getString("IMG");
			redirect= extras.getBoolean("redirect");
		}

		if(file != "") {

			gamesCnt = MainActivity.prefs.getInt(file+"_cnt", 0);
			Long totalTimeF = MainActivity.prefs.getLong(file+"_totalTime", 0);
			
			//Shared preferences for number of games in the level and number of correct answers for the level
	        int noGames 	= ((int) MainActivity.prefs.getInt(file+"_cnt", 0));
	        int correctCnt 	= ((int) MainActivity.prefs.getInt(file+"_correct_cnt", 0));
			currentPage		= ((int) MainActivity.prefs.getInt(file+"_page", 0));
	        
			//Work out the percentage correct for the level. MUST BE DOUBLE
	        double percent = ((double) correctCnt/noGames)*100;
			
			TextView timeText =  (TextView) findViewById(R.id.time);
			TextView progress = (TextView) findViewById(R.id.progress);
			LinearLayout	pb = (LinearLayout) findViewById(R.id.progress_bar);
			
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 6, (float) percent);
	        pb.setLayoutParams(params);
	        
			timeText.setText(MainActivity.getTime(totalTimeF));
			
			progress.setText( correctCnt + " of " + noGames );
			
			MyPagerAdapter adapterP = new MyPagerAdapter();
		    myPager = (ViewPager) findViewById(R.id.myfivepanelpager);
		    myPager.setAdapter(adapterP);
		    
			
		    //Bind the title indicator to the adapter
		    LinePageIndicator titleIndicator = (LinePageIndicator)findViewById(R.id.indicator);
		    titleIndicator.setViewPager(myPager);
		    
		    titleIndicator.setCurrentItem(currentPage);
		    
		    myPager.setCurrentItem(currentPage);

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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private ArrayList<Games> loadGames(int position) {
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
			
			int gamesCnt = gamesArr.length();

			int startNo = (position == 1 ? 0 : (position-1) * perPage);
			int endNo	= ((startNo+perPage) > gamesCnt ? gamesCnt : (startNo+perPage));

			for(int currentNo = startNo; currentNo < endNo; currentNo++) {

				JSONObject j = gamesArr.getJSONObject(currentNo);
				
				Games game= new Games();
				
				game.file	= file;
				game.img 	= (j.getString("img") != "" ? j.getString("img") : "" );
		        game.hint 	= (j.getString("hint") != "" ? j.getString("hint") : "" );
		        game.sound 	= (j.getString("sound") != "" ? j.getString("sound") : "" );
		        game.answer	= (j.getString("answer") != "" ? j.getString("answer") : "" );
		        game.close	= (j.getString("close") != "" ? j.getString("close") : "" );

		        games.add(game);

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return games;
		
	}
	
	@Override
	public void onBackPressed() {

		editor.putInt(file+"_page", myPager.getCurrentItem());
		editor.commit();
		
		GamesList.this.finish();

	}

}
