package esn.classes;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.location.Location;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.readystatesoftware.maps.TapControlledMapView;

import esn.activities.AddNewEvent;
import esn.activities.R;
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
	public static final String LOG_TAG = "EsnMapView";
	private MapActivity activity;
	public ArrayList<Events> events = new ArrayList<Events>();
	private Maps map;
	private Resources res;
	private boolean isCreateEventByLongPress = true;
	private boolean isLoadEventAround = true;

	public EsnMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public EsnMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * need for mapView
	 * 
	 * @param context
	 * @param apiString
	 */
	public EsnMapView(Context context, String apiString) {
		super(context, apiString);
		init(context);
	}

	/**
	 * initialize
	 * 
	 * @param context
	 */
	private void init(Context context) {
		this.context = context;
		handler = new Handler();
		this.lastMapCenter = new GeoPoint(0, 0);
		this.isTouchEnded = false;
		this.isFirstComputeScroll = true;
		// get resource
		res = context.getResources();
		// instance Maps object
		map = new Maps(context, this);
		// get event around

		lastMapCenter = getMapCenter();
	}

	public void loadEventAround() {
		double radius = calculateRadius();
		new LoadEventsAroundThread(radius).start();
	}

	/**
	 * On long press event (create event o long press)
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		map.hideAllBallon();
		return super.onSingleTapUp(e);
	}

	@Override
	public void onLongPress(MotionEvent e) {
		if (e.getAction() == MotionEvent.ACTION_DOWN
				&& isCreateEventByLongPress) {
			// get current point
			final GeoPoint p = this.getProjection().fromPixels((int) e.getX(),
					(int) e.getY());
			int latitudeE6 = p.getLatitudeE6();
			int longtitudeE6 = p.getLongitudeE6();
			// get current location
			Location cLocation = map.getCurrentLocation();
			if (cLocation != null) {
				// start activity
				Intent addNewEventIntent = new Intent(context,
						AddNewEvent.class);
				addNewEventIntent.putExtra("latitude", latitudeE6 / 1E6);
				addNewEventIntent.putExtra("longtitude", longtitudeE6 / 1E6);
				activity.startActivityForResult(addNewEventIntent,REQUEST_CODE_ADD_NEW_EVENT);
				activity.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			} else {
				Toast.makeText(
						activity.getApplicationContext(),
						res.getString(R.string.esn_global_your_location_not_found),
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	/**
	 * Override super class fires when touch. For ON DRAG MAP EVENT
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			isTouchEnded = false;
		} else if (ev.getAction() == MotionEvent.ACTION_UP) {
			isTouchEnded = true;

			// Log.d("esn", "touch ended");
		} else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			isFirstComputeScroll = true;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * Override super class
	 */
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (!isTouchEnded && this.getMapCenter().equals(lastMapCenter)
				&& isFirstComputeScroll) {
			isFirstComputeScroll = false;
		} else if (isTouchEnded && isFirstComputeScroll) {
			isTouchEnded = false;
			lastMapCenter = this.getMapCenter();
			if (isLoadEventAround) {
				double radius = calculateRadius();
				new LoadEventsAroundThread(radius).start();
			}

		}
	}

	/**
	 * Calculate radius base on current screen size and current zoom level
	 * 
	 * @return radius
	 */
	public double calculateRadius() {
		WindowManager mn = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = mn.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		final GeoPoint p = this.getProjection().fromPixels(0, size.y / 2);

		final GeoPoint p2 = this.getProjection().fromPixels(size.x, size.y / 2);
		// Log.d("esn", p.toString() + "|" + p2.toString());
		return Utils.distanceOfTwoPoint(p, p2);
	}

	public MapActivity getActivity() {
		return activity;
	}

	/**
	 * set activity for mapview
	 * 
	 * @param activity
	 */
	public void setActivity(MapActivity activity) {

		this.activity = activity;
	}

	/**
	 * Load events around thread
	 * 
	 * @author JK
	 * 
	 */
	public class LoadEventsAroundThread extends Thread {
		private double radius;

		public LoadEventsAroundThread(double radius) {
			this.radius = radius;
		}

		@Override
		public void run() {
			if (lastMapCenter != null) {
				Log.d("esn", "Load events around....");
				EventsManager manager = new EventsManager();
				Events[] events;
				try {
					double lat = lastMapCenter.getLatitudeE6() / 1E6;
					double lon = lastMapCenter.getLongitudeE6() / 1E6;
					Log.d("radius", String.valueOf(radius));
					String filter = manager.getFilterString(Sessions
							.getInstance(context));
					Log.d(LOG_TAG, filter);
					events = manager.getEventsAround(lat, lon, this.radius,
							filter);

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
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Load events around hanlder: run on UI
	 * 
	 * @author JK
	 * 
	 */
	private class LoadEventsAroundHandler implements Runnable {
		private Events[] events;

		public LoadEventsAroundHandler(Events[] events) {
			this.events = events;
		}

		@Override
		public void run() {
			for (int i = 0; i < events.length; i++) {
				Events event = events[i];

				EsnMapView.this.events.add(event);
				GeoPoint point = new GeoPoint((int) (event.EventLat * 1E6),
						(int) (event.EventLng * 1E6));
				int icon = EventType.getIconId(event.EventTypeID,
						event.getLevel());
				map.setEventMarker(point, event.Title, event.Description,
						event.EventID, icon);
			}
			invalidate();
			Log.d("esn", "end load events around!");
		}
	}

	public boolean isCreateEventByLongPress() {
		return isCreateEventByLongPress;
	}

	public void setCreateEventByLongPress(boolean isCreateEventByLongPress) {
		this.isCreateEventByLongPress = isCreateEventByLongPress;
	}

	public boolean isLoadEventAround() {
		return isLoadEventAround;
	}

	public void setLoadEventAround(boolean isLoadEventAround) {
		this.isLoadEventAround = isLoadEventAround;
	}

}
