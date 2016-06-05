package com.epay.aty;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.epay.aty.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class AtyAddFingerPrint extends Activity implements SurfaceHolder.Callback{

	private Camera camera;
	private SurfaceView surfaceview;
	private SurfaceHolder surfaceHolder;
	private Button next,upload;
	private ImageView image;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addfingerprint);
		ActionBar actionbar =getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		surfaceview = (SurfaceView) findViewById(R.id.surfaceView1);
		next = (Button) findViewById(R.id.bn_takephoto);
		upload = (Button) findViewById(R.id.bn_postfingerphoto);
		image = (ImageView) findViewById(R.id.iv_showfingerphoto);
		surfaceHolder = surfaceview.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);
		
		next.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				camera.takePicture(null, null, new TakePictureCallback());
			}
			
		});
		upload.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AtyAddFingerPrint.this,AtyAddOrDelFingerPrint.class);
				startActivity(intent);
				finish();
				
			}
			
		});
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera = Camera.open();
		try{
			camera.setPreviewDisplay(holder);
			Camera.Parameters parameters = camera.getParameters();
	        if (this.getResources().getConfiguration().orientation   
                    != Configuration.ORIENTATION_LANDSCAPE) {  
            parameters.set("orientation", "portrait");  
            camera.setDisplayOrientation(90);  
            parameters.setRotation(90);  
	        } else {  
            parameters.set("orientation", "landscape");  
            camera.setDisplayOrientation(0);  
            parameters.setRotation(0);  
	        }
	        parameters.setPictureSize(600, 400);
			camera.setParameters(parameters);
			camera.startPreview();
			System.out.println("camera.startpreview");
		}catch(Exception e){
			e.printStackTrace();
			camera.release();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if(camera != null){
			camera.stopPreview();
			
			camera.release();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, AtyAddOrDelFingerPrint.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			camera.autoFocus(null);
				
		}

		return true;
		
	}
	private class TakePictureCallback implements PictureCallback{

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			try {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				File dir = new File(Environment
						.getExternalStorageDirectory().getCanonicalPath(),
						"fingerprintPay/fingerphoto");
				if(!dir.exists()){
					dir.mkdirs();
				}
				File file = new File(dir,System.currentTimeMillis()+".jpg");
				if(!file.exists()){
					file.createNewFile();
				}
				BufferedOutputStream bos  = new BufferedOutputStream( new FileOutputStream(file));
				bitmap.compress(CompressFormat.JPEG, 100, bos);
				image.setImageBitmap(bitmap);
				bos.flush();
				bos.close();
				camera.stopPreview();
				camera.startPreview();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}


}
