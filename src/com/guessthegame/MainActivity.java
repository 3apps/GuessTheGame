package com.guessthegame;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		final LevelListAdaptor adaptor = new LevelListAdaptor(this,R.layout.row, loadList());
			
		final ListView listViewLevels = (ListView) findViewById(R.id.levels);
		
		listViewLevels.setClickable(true);
		
		listViewLevels.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
				Levels level = adaptor.getItem(position);
				
				Intent intent = new Intent(getBaseContext(), GamesList.class);
				
				intent.putExtra("FILE", level.file);
				intent.putExtra("NAME", level.name);
				intent.putExtra("DESC", level.desc);
				intent.putExtra("IMG", level.img);
				
				startActivity(intent);
				
			}
			
		});
		
		listViewLevels.setAdapter(adaptor);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
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
		
		for(String filename : files) {

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
                    levels.add(level);
					
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
	
}
