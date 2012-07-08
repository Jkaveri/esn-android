package esn.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockMapActivity;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.maps.MapView;
import esn.classes.Maps;
import esn.classes.VoiceModeHelper;

public class VoiceModeActivity extends SherlockMapActivity implements OnNavigationListener{
	private Maps map;
	private MapView mapView;
	private VoiceModeHelper voidModeHeplper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.esn_voice_mode);
		
		voidModeHeplper = new VoiceModeHelper();
		
		setupActionBar();
		setupMap();
		setupListNavigate();
	}

	private void setupListNavigate() {
		getSupportActionBar().setNavigationMode(ActionBar.DISPLAY_SHOW_TITLE);
		getSupportActionBar().setTitle(getString(R.string.esn_voicemode_title));
	}

	private void setupActionBar() {
		/** setup action bar **/
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
	}

	private void setupMap() {

		/** setup map **/
		mapView = (MapView) findViewById(R.id.esn_google_maps_state);
		map = new Maps(this, mapView);
		// set zoom level to 14
		map.setZoom(14);
		map.setCurrMarkerIcon(R.drawable.ic_current_location);
		map.displayCurrentLocation();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem startRecord = menu.add("Start");
		startRecord.setIcon(R.drawable.ic_mic_start).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		voidModeHeplper.setMenuItemRecord(startRecord);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		String itemTitle = item.getTitle().toString();
		if (itemTitle.equals("Start")) {
			voidModeHeplper.startRecording();
			item.setTitle("Stop");
			return true;
		}
		else if(itemTitle.equals("Stop")){
			voidModeHeplper.stopRecording();
			item.setTitle("Start");
			return true;
		}
		else {
			return super.onMenuItemSelected(featureId, item);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Toast.makeText(this, getString(R.string.esn_voicemode_title), Toast.LENGTH_SHORT).show();
		return true;
	}
}
