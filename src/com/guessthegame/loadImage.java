package com.guessthegame;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

class loadImage extends AsyncTask<Object, Void, Bitmap> {

	private ImageView imv;
	private String name;
	private Context context;
	private boolean crop;

	public loadImage(Context context, ImageView imv, String name, boolean crop) {
		this.imv = imv;
		this.name = name;
		this.context = context;
		this.crop = crop;
	}

	@Override
	protected Bitmap doInBackground(Object... params) {
		
		Bitmap bitmap = null;
		
		AssetManager asset = context.getAssets();

		InputStream is;
		try {
			is = asset.open(name);
			bitmap = BitmapFactory.decodeStream(is);
			
			is.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {

		if (result != null && imv != null) {
			if(crop == true) {
				imv.setImageBitmap(MainActivity.getCroppedBitmap(result));
			} else {
				imv.setImageBitmap(result);
			}
			imv.setVisibility(View.VISIBLE);
		} else {
			imv.setImageBitmap(null);
			imv.setVisibility(View.INVISIBLE);
		}
	}

}