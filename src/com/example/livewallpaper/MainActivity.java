package com.example.livewallpaper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public class MainActivity extends WallpaperService {
	private final Handler handler = new Handler();

	@Override
	public Engine onCreateEngine() {
		return new UhrzeitLiveWallpaperEngine();
	}
	private class UhrzeitLiveWallpaperEngine extends Engine {
		private final Paint paint;
		//Runnable und Handler für Thread
		private final Runnable runnable;
		private int color;
		private float x,y;
		private boolean visible;
		private String text = "DevelAPPer";
		
		public UhrzeitLiveWallpaperEngine() {
			paint = new Paint();
			runnable = new Runnable() {
				
				@Override
				public void run() {
					draw();
				}
			};
		}
		@Override
		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
		}
		@Override
		public void onDestroy() {
			super.onDestroy();
			handler.removeCallbacks(runnable);
		}
		
		@Override
		public void onVisibilityChanged(boolean v) {
			visible = v;
			if (v) {
				draw();
			} else {
				handler.removeCallbacks(runnable);
			}
		}
		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			color = Color.WHITE;
			//Breite und Höhe des Bildschirms / 2
			x = width / 2.0f;
			y = height / 2.0f;
			draw();
		}
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			visible = false;
			// Callback = Methoden
			handler.removeCallbacks(runnable);
		}
		private void draw() {
			final SurfaceHolder holder = getSurfaceHolder();
			Canvas c = null;
			try {
				// Schutz des kritischen Abschnittes
				// sorgt dafür, dass zu einem Zeitpunkt immer nur einer den Code zwischen lock und unlock 
				// benutzt
				// Canvas != null = Zugriff erlaubt
				// Canvas == null: Jemand anders benutzt die Leinwand (canvas)
				c = holder.lockCanvas();
				if (c!= null) {
					int w = c.getWidth();
					int h = c.getHeight();
					paint.setColor(Color.BLACK);
					//Rechteck gemäß Handy Bildschirm
					c.drawRect(0, 0,w - 1,h -1,paint);
					paint.setColor(color);
					paint.setTextSize(64);
					paint.setTextAlign(Align.RIGHT);
					c.drawText(text, x, y, paint);
					color = getNextColor(color);
					paint.setColor(color);
				}
			} finally {
				if (c!=null)
					holder.unlockCanvasAndPost(c);
			}
			handler.removeCallbacks(runnable);
			if (visible) {
				handler.postDelayed(runnable, 1000);
			}
			}
			private int getNextColor(int color) {
				if (color == Color.GREEN) {
					return Color.BLUE;
				} 
				return Color.GREEN;
			}
		}
}
