package com.guessthegame;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	public static final String PREFS_NAME = "GTGPrefs";
	static SharedPreferences.Editor editor;
	static SharedPreferences prefs;
	static ListView listViewLevels;
	static LevelListAdaptor adaptor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		prefs = getSharedPreferences(PREFS_NAME, 0);
		editor = MainActivity.prefs.edit();
		
		LinearLayout header = (LinearLayout) findViewById(R.id.header);
		
		header.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//REMOVE WHEN LIVE AS THIS RESETS SAVED DATA WHEN APP IS RE OPEND - APP WILL BE SLOW TO OPEN DUE TO DELETING ALL PREFS
				editor.clear();
				editor.commit();
				
				new LoadJsonTask().execute();
				
				Toast.makeText(getBaseContext(), "SAVED DATA DELETED", Toast.LENGTH_LONG).show();
				/////////////////////////////////////////////////////////////////
			}
			
		});
		
	}
	
	@Override
	public void onResume() { 
		// After a pause OR at startup
		super.onResume();
		
		TextView timeText =  (TextView) findViewById(R.id.time);
		
		long totalTime = MainActivity.prefs.getLong("totalTime", 0);
		
		timeText.setText(MainActivity.getTime(totalTime));
		
		new LoadJsonTask().execute();
			
	}
	
	public static String getTime(Long totalTimes) {
		
		long seconds = (long) (totalTimes / 1000) % 60 ;
		long minutes = (long) ((totalTimes / (1000*60)) % 60);
		long hours   = (long) ((totalTimes / (1000*60*60)) % 24);
		
		return String.format("%02d:%02d:%02d",(int) hours,(int) minutes,(int) seconds);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.fragment:
				//Intent intent = new Intent(getBaseContext(), Main.class);
					
				//startActivity(intent);
				return true;
			case R.id.clear:
				
				//REMOVE WHEN LIVE AS THIS RESETS SAVED DATA WHEN APP IS RE OPEND - APP WILL BE SLOW TO OPEN DUE TO DELETING ALL PREFS
				editor.clear();
				editor.commit();
				
				new LoadJsonTask().execute();
				
				Toast.makeText(this, "SAVED DATA DELETED", Toast.LENGTH_LONG).show();
				/////////////////////////////////////////////////////////////////
				
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class LoadJsonTask extends AsyncTask<String, Void, ArrayList<Levels> > {
	       
       protected ArrayList<Levels> doInBackground (String... params){
           return loadList();
       }
       protected void onPostExecute(ArrayList<Levels> mylist){

	   	ListView listViewLevels = (ListView) findViewById(R.id.levels);
	
		adaptor = new LevelListAdaptor(MainActivity.this,R.layout.row, mylist);
	
		listViewLevels.setAdapter(adaptor);
		
		listViewLevels.setClickable(true);
		
		listViewLevels.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
				Levels level = adaptor.getItem(position);
				
				if(level.paid.toString().contains("no")) {
				
   					Intent intent = new Intent(getBaseContext(), GamesList.class);
   					
   					intent.putExtra("FILE", level.file);
   					intent.putExtra("NAME", level.name);
   					intent.putExtra("DESC", level.desc);
   					intent.putExtra("IMG", level.img);
   					
   					startActivity(intent);
   					
				}
			}
			
		});
           
       }
    }
	
	
	private ArrayList<Levels> loadList(){
        ArrayList<Levels> levels = new ArrayList<Levels>();
        
		AssetManager assetManager = getAssets();
		String[] files = null;
		
		try {
			
			files = assetManager.list("");
			
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}
		int cnt = 0;
		for(final String filename : files) {

			try {
				
				if(filename.contains(".json")) {

					InputStream in = this.getAssets().open(filename);
					
					int size = in.available();
					
					byte[] buffer = new byte[size];
					in.read(buffer);
					in.close();
					
					String jsonString = new String(buffer);
					
					JSONObject jsonObj 	= new JSONObject(jsonString);
					
					Levels level= new Levels();
					level.file 	= filename;
                    level.name 	= jsonObj.getString("name");
                    level.desc 	= jsonObj.getString("desc");
                    level.img	= jsonObj.getString("img");
                    level.paid	= jsonObj.getString("paid");
                    
                    levels.add(level);
                    
                    final JSONArray gamesArr = jsonObj.getJSONArray("games");

                    editor.putInt(filename+"_cnt", gamesArr.length());
                    
                    if(MainActivity.prefs.getInt(filename+"_correct_cnt", 0) == 0) {
                    	editor.putInt(filename+"_correct_cnt", 0);
                    }
                    
        			editor.commit();
                    
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return levels;
		
	}
	
	public static Bitmap getBitmap(Context context, String name) throws IOException {
        AssetManager asset = context.getAssets();

        InputStream is = asset.open(name);
        Bitmap bitmap = BitmapFactory.decodeStream(is);

        is.close();
        
        return bitmap;
    }
	
	//crops image to circle
	public static Bitmap getCroppedBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	            bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
	            bitmap.getWidth() / 2, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
	    //return _bmp;
	    return output;
	}
	
}
