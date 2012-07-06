package esn.classes;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.readystatesoftware.maps.TapControlledMapView;

import esn.activities.R;
import esn.activities.SelectEventLabel;
import esn.models.EventType;
import esn.models.Events;
import esn.models.EventsManager;

public class EsnMapView extends TapControlledMapView {

	private GeoPoint lastMapCenter;
	private boolean isTouchEnded;
	private boolean isFirstComputeScroll;
	private Context context;
	public Handler handler;
	public static final int REQUEST_CODE_ADD_NEW_EVENT = 1;
	private MapActivity activity;
	public ArrayList<Events> events = new ArrayList<Events>();
	public EsnMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
       init(context);
    }

    public EsnMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }
	public EsnMapView(Context context, String apiString) {
		super(context, apiString);
		init(context);
	}
	private void init(Context context){
		this.context = context;
		handler = new Handler();
		this.lastMapCenter = new GeoPoint(0, 0);
		this.isTouchEnded = false;
		this.isFirstComputeScroll = true;
		lastMapCenter = getMapCenter();
		double radius = calculateRadius();
		new LoadEventsAroundThread(radius).start();
	}
	@Override
	public void onLongPress(MotionEvent e) {
		if(e.getAction() ==MotionEvent.ACTION_DOWN){
			// get current point
			final GeoPoint p = this.getProjection()
					.fromPixels((int) e.getX(), (int) e.getY());

			new Thread(new Runnable() {
				public void run() {
					Looper.prepare();
					int latitudeE6 = p.getLatitudeE6();
					int longtitudeE6 = p.getLongitudeE6();
					Intent addNewEventIntent = new Intent(context,
							SelectEventLabel.class);
					addNewEventIntent
							.putExtra("latitude", latitudeE6 / 1E6);
					addNewEventIntent.putExtra("longtitude",
							longtitudeE6 / 1E6);
					activity.startActivityForResult(addNewEventIntent, REQUEST_CODE_ADD_NEW_EVENT);
				}
			}).start();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			isTouchEnded = false;
			Drawable drawable = context.getResources().getDrawable(R.drawable.ic_event_type_0_1);
			EsnItemizedOverlay marker = new EsnItemizedOverlay(drawable, this);
			marker.hideAllBalloons();
		} else if (ev.getAction() == MotionEvent.ACTION_UP) {
			isTouchEnded = true;
			
		//	Log.d("esn", "touch ended");
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			isFirstComputeScroll = true;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		if (!isTouchEnded && this.getMapCenter().equals(lastMapCenter)
				&& isFirstComputeScroll) {
			isFirstComputeScroll = false;
		} else if(isTouchEnded && isFirstComputeScroll) {	
			isTouchEnded = false;
			lastMapCenter = this.getMapCenter();
			
		//	Log.d("esn","lastMapCenter: "+lastMapCenter.toString());
			double radius = calculateRadius();
			new LoadEventsAroundThread(radius).start();
		}
	}

	
	public class LoadEventsAroundThread extends Thread {
		private double radius;
		public LoadEventsAroundThread(double radius) {
			this.radius = radius;
		}
		@Override
		public void run() {
			if(lastMapCenter!=null){
				Log.d("esn","Load events around....");
				EventsManager manager = new EventsManager();
				Events[] events;
				try {
					double lat = lastMapCenter.getLatitudeE6() / 1E6;
					double lon = lastMapCenter.getLongitudeE6() / 1E6;
					Log.d("radius",String.valueOf(radius));
					String filter = manager.getFilterString(Sessions.getInstance(context));
					events = manager.getEventsAround(lat, lon, this.radius,filter);
					
					handler.post(new LoadEventsAroundHandler(events));

				} catch (IllegalArgumentException e) {
					Log.d("esn", e.getMessage());
					e.printStackTrace();
				} catch (JSONException e) {
					Log.d("esn", e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					Log.d("esn", e.getMessage());
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					Log.d("esn", e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private class LoadEventsAroundHandler implements Runnable {
		private Events[] events;

		public LoadEventsAroundHandler(Events[] events) {
			this.events = events;
		}

		@Override
		public void run() {
			getOverlays().clear();
			for (int i = 0; i < events.length; i++) {
				Events event = events[i];
				EsnMapView.this.events.add(event);
				GeoPoint point = new GeoPoint((int) (event.EventLat * 1E6),
						(int) (event.EventLng * 1E6));
				int icon = EventType.getIconId(event.EventTypeID,
						event.getLevel());
				setMarker(point, event.Title, event.Description, event.EventID,
						icon);
			}
			invalidate();
			Log.d("esn","end load events around!");
		}
	}

	public void setMarker(GeoPoint point, String title, String subtitle,
			int eventId, int drawable) {
		// create overlay item
		EventOverlayItem item = new EventOverlayItem(point, title, subtitle,
				eventId);
		// get pointer image
		Drawable markerIcon = context.getResources().getDrawable(drawable);
		// instance HelloItemizedOverlay with image
		EsnItemizedOverlay marker = new EsnItemizedOverlay(markerIcon, this);
		// add itemOVerlay to itemizedOverlay
		marker.addOverlay(item);
		// set marker
		getOverlays().add(marker);
		//invalidate();
	}
	public double calculateRadius(){
		WindowManager mn = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = mn.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		final GeoPoint p = this.getProjection()
				.fromPixels(0, size.y/2);
		
		final GeoPoint p2 = this.getProjection().fromPixels(size.x, size.y/2);
		Log.d("esn", p.toString()+"|"+p2.toString());
		return Math.floor(Utils.distanceOfTwoPoint(p, p2));
	}
	public MapActivity getActivity() {
		return activity;
	}

	public void setActivity(MapActivity activity) {
		
		
		this.activity = activity;
	}
	
	//getter & setter
	
}
