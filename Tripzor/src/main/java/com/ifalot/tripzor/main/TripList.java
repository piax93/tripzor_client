package com.ifalot.tripzor.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ifalot.tripzor.model.Trip;
import com.ifalot.tripzor.ui.TripListAdapter;
import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.utils.Media;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.MediaListener;
import com.ifalot.tripzor.web.PostSender;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class TripList extends AppCompatActivity implements MediaListener,
		NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

	private NavigationView navigationView;
	private DrawerLayout drawerLayout;
	private ImageView navHeaderFg;
	private int lastItemChecked = 0;
	private ListView tripslv;
	private TripListAdapter tripListAdapter;
	private SwipeRefreshLayout swipeRefreshLayout;
	private Menu actionBarMenu;
	private boolean deleting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_list);

		Toolbar toolbar = (Toolbar) findViewById(R.id.trip_list_toolbar);
		setSupportActionBar(toolbar);
		try { getSupportActionBar().setElevation(18.0f); }
		catch (NullPointerException e) { e.printStackTrace(); }

		deleting = false;
		navigationView = (NavigationView) findViewById(R.id.navigation_view);
		drawerLayout = (DrawerLayout) findViewById(R.id.trip_list_drawer);

		View navHeader = navigationView.inflateHeaderView(R.layout.navigation_header_view);

		navHeaderFg = (ImageView) navHeader.findViewById(R.id.header_view_fgimg);
		String profile_image = Media.getImagePath(this, "profile", "png");
		if (profile_image != null && new File(profile_image).exists()) {
			navHeaderFg.setImageDrawable(Media.getRoundedImage(this, "profile", "png"));
		} else PostSender.getMedia("profile", Media.getFilePath(this, "profile"), this);

		navigationView.setNavigationItemSelectedListener(this);
		ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
				R.string.openDrawer, R.string.closeDrawer){
			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				if(lastItemChecked != 0)
					navigationView.getMenu().findItem(lastItemChecked).setChecked(false);
			}
		};
		drawerLayout.addDrawerListener(drawerToggle);
		drawerToggle.syncState();
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_trips);
		swipeRefreshLayout.setOnRefreshListener(this);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_add_trip);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(tripListAdapter != null) tripListAdapter.deselectAll(tripslv);
				Intent intent = new Intent(TripList.this, AddTrip.class);
				startActivity(intent);
			}
		});

		this.onRefresh();

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		String newtrip = DataManager.selectData("new_trip");
		String update_image = DataManager.selectData("update_image");
		if(newtrip != null && newtrip.equals("true")){
			DataManager.updateValue("new_trip", "false");
			this.onRefresh();
		}
		if(update_image != null && update_image.equals("true")){
			DataManager.updateValue("update_image", "false");
			navHeaderFg.setImageDrawable(Media.getRoundedImage(this, "profile", "png"));
		}
	}

	@Override
	public void onResultsSucceeded(JSONObject res) throws JSONException {
		swipeRefreshLayout.setRefreshing(false);
		String result = res.getString("result");
		if(result.equals(Codes.USER_NOT_FOUND) || result.equals(Codes.ERROR)){
			FastDialog.simpleDialog(this, "ERROR", "An error occurred...",
					"CLOSE", new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
							TripList.this.finish();
							System.exit(RESULT_CANCELED);
						}
					});
		} else {
			if(deleting){
				deleting = false;
				this.onRefresh();
			} else {
				tripslv = (ListView) findViewById(R.id.trip_list);
				JSONArray mytrips = res.getJSONArray("data").getJSONArray(0);
				JSONArray parttrips = res.getJSONArray("data").getJSONArray(1);
				if(mytrips.length() + parttrips.length() == 0) {
					ArrayAdapter<String> adapt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
					adapt.add("No trips linked to your account");
					tripslv.setAdapter(adapt);
				}else{
					tripListAdapter = new TripListAdapter(this, Trip.parseTrips(mytrips, parttrips));
					tripslv.setAdapter(tripListAdapter);
					tripslv.setOnItemClickListener(getListAction());
				}
			}
		}
	}

	@Override
	public void onMediaReceived(JSONObject res) throws JSONException {
		if(res.getString("result").equals(Codes.DONE)) {
			navHeaderFg.setImageDrawable(Media.getRoundedImage(this, "profile", "png"));
		}
	}

	private AdapterView.OnItemClickListener getListAction(){
		return new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Trip t = tripListAdapter.getTrips().get(position);
				Intent intent = new Intent(TripList.this, TripDetail.class);
				intent.putExtra("TripId", t.getId());
				intent.putExtra("TripName", t.toString());
				intent.putExtra("Owned", t.isOwned());
				startActivity(intent);
			}
		};
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		item.setChecked(true);
		lastItemChecked = item.getItemId();
		int id = item.getItemId();
		drawerLayout.closeDrawers();
		Intent intent;
		switch (id) {
			case R.id.password_change:
				intent = new Intent(TripList.this, PasswordChange.class);
				startActivity(intent);
				return true;
			case R.id.profile_edit_item:
				intent = new Intent(TripList.this, EditProfile.class);
				startActivity(intent);
				return true;
			case R.id.logout_item:
				DataManager.deleteData("user");
				startActivity(new Intent(TripList.this, Login.class));
				TripList.this.finish();
				return true;
			case R.id.info_item:
				FastDialog.simpleDialog(TripList.this, "INFO", "App created by Matteo Piano (ifalot93@gmail.com).\n" +
						"Original idea by Andrea Conte", "CLOSE");
				return true;
			default:
				return false;
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU){
			if(drawerLayout.isDrawerOpen(navigationView)) drawerLayout.closeDrawers();
			else drawerLayout.openDrawer(navigationView);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK){
			if(tripslv != null && !tripslv.isClickable()){
				tripListAdapter.deselectAll(tripslv);
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		actionBarMenu = menu;
		getMenuInflater().inflate(R.menu.trip_list_select, menu);
		actionBarMenu.setGroupVisible(R.id.trip_select_option_group, false);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
			case R.id.deselect_all:
				tripListAdapter.deselectAll(tripslv);
				break;
			case R.id.delete_selected:
				this.deleteSelected();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void itemsAreSelected(){
		tripslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
		});
		tripslv.setClickable(false);
		actionBarMenu.setGroupVisible(R.id.trip_select_option_group, true);
	}

	public void noItemsAreSelected(){
		tripslv.setOnItemClickListener(getListAction());
		tripslv.setClickable(true);
		actionBarMenu.setGroupVisible(R.id.trip_select_option_group, false);
	}

	protected void deleteSelected(){
		final List<Trip> selected = tripListAdapter.getSelected();
		FastDialog.yesNoDialog(this, "Delete Trips", "Are you sure you want to delete " + selected.size()
				+ " trip" + (selected.size() > 1 ? "s" : "") + "?",
				new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						swipeRefreshLayout.setRefreshing(true);
						HashMap<String, String> postData = new HashMap<String, String>();
						postData.put("action", "DeleteTrips");
						StringBuilder sb = new StringBuilder();
						for(Trip t : selected) sb.append(t.getId()).append(',');
						sb.deleteCharAt(sb.length()-1);
						postData.put("ids", sb.toString());
						deleting = true;
						PostSender.sendPost(postData, TripList.this);
					}
				}, new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
						tripListAdapter.deselectAll(tripslv);
					}
				});
	}

	@Override
	public void onRefresh() {
		if(tripListAdapter != null) tripListAdapter.deselectAll(tripslv);
		swipeRefreshLayout.setRefreshing(true);
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("action", "ListTrips");
		PostSender.sendPost(data, this);
	}
}
