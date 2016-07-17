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
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TripList extends AppCompatActivity implements ResultListener,
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
	private boolean loading_image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_list);

		deleting = false;
		navigationView = (NavigationView) findViewById(R.id.navigation_view);
		drawerLayout = (DrawerLayout) findViewById(R.id.trip_list_drawer);
		View navHeader = navigationView.inflateHeaderView(R.layout.navigation_header_view);

		navHeaderFg = (ImageView) navHeader.findViewById(R.id.header_view_fgimg);
		String profile_image = Media.getImagePath(this, "profile", "png");
		if (profile_image != null && new File(profile_image).exists()) {
			navHeaderFg.setImageDrawable(Media.getRoundedImage(this, "profile", "png"));
			loading_image = false;
		} else loading_image = true;

		navigationView.setNavigationItemSelectedListener(this);
		ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.string.openDrawer, R.string.closeDrawer){
			@Override
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				if(lastItemChecked != 0)
					navigationView.getMenu().findItem(lastItemChecked).setChecked(false);
			}
		};
		drawerLayout.addDrawerListener(drawerToggle);
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
	public void onResultsSucceeded(String result, List<String> listResult) {
		swipeRefreshLayout.setRefreshing(false);
		if(result.equals(Codes.USER_NOT_FOUND) || result.equals(Codes.ERROR)){
			if(loading_image) loading_image = false;
			else FastDialog.simpleDialog(this, "ERROR", "An error occurred...",
					"CLOSE", new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
							TripList.this.finish();
							System.exit(RESULT_CANCELED);
						}
					});
		}else{
			if(deleting){
				deleting = false;
				String res = listResult.get(listResult.size()-1);
				if(res.equals(Codes.DONE)){
					// Now server removes the user from partecipants
//					if (result.startsWith("_")) {
//						StringBuffer sb = new StringBuffer();
//						for (int i = 0; i < listResult.size() - 1; i++) {
//							sb.append("- ").append(listResult.get(i).substring(6)).append("\n");
//						}
//						FastDialog.simpleDialog(this, "Warning", "The following trips cannot be deleted " +
//								"because you are not the creator:\n " + sb.toString(), "CLOSE");
//					}
				}else if (res.equals(Codes.ERROR)){
					FastDialog.simpleErrorDialog(this, "Database error occurred :(");
				}
				this.onRefresh();
			}else if(loading_image && result.equals(Codes.DONE)) {
				navHeaderFg.setImageDrawable(Media.getRoundedImage(this, "profile", "png"));
				loading_image = false;
			}else{
				tripslv = (ListView) findViewById(R.id.trip_list);
				if(listResult.size() == 0) {
					listResult.add("No trips linked to your account");
					tripslv.setAdapter(new ArrayAdapter<String>(this,
							android.R.layout.simple_list_item_1, listResult));
				}else{
					tripListAdapter = new TripListAdapter(this, parseTrips(listResult));
					tripslv.setAdapter(tripListAdapter);
					tripslv.setOnItemClickListener(getListAction());
				}
				if(loading_image) {
					swipeRefreshLayout.setRefreshing(true);
					PostSender.getMedia("profile", Media.getFilePath(this, "profile"), this);
				}
			}
		}
	}

	private ArrayList<Trip> parseTrips(List<String> listResult) {
		ArrayList<Trip> trips = new ArrayList<Trip>();
		for(String line : listResult){
			boolean owned = false;
			if(line.startsWith("*")){
				owned = true;
				line = line.substring(1);
			}
			String[] tmp = line.split(":", 2);
			trips.add(new Trip(Integer.parseInt(tmp[0]), owned, tmp[1]));
		}
		return trips;
	}

	private AdapterView.OnItemClickListener getListAction(){
		return new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Trip t = tripListAdapter.getTrips().get(position);
				Intent intent = new Intent(TripList.this, TripDetail.class);
				intent.putExtra("TripId", t.getId());
				intent.putExtra("TripName", t.toString());
				startActivity(intent);
			}
		};
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
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
			if(!tripslv.isClickable()){
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
						StringBuffer sb = new StringBuffer();
						for(Trip t : selected) sb.append(t.getId()).append(',');
						sb.deleteCharAt(sb.length()-1);
						postData.put("ids", sb.toString());
						deleting = true;
						PostSender.sendPostML(postData, TripList.this);
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
		PostSender.sendPostML(data, this);
	}
}
