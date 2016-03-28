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
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ifalot.tripzor.model.Trip;
import com.ifalot.tripzor.ui.TripListAdapter;
import com.ifalot.tripzor.utils.DataManager;
import com.ifalot.tripzor.utils.FastDialog;
import com.ifalot.tripzor.web.Codes;
import com.ifalot.tripzor.web.PostSender;
import com.ifalot.tripzor.web.ResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TripList extends AppCompatActivity implements ResultListener, NavigationView.OnNavigationItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {

	private NavigationView navigationView;
	private DrawerLayout drawerLayout;
	private int lastItemChecked = 0;
	private ListView tripslv;
	private TripListAdapter tripListAdapter;
	private SwipeRefreshLayout swipeRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trip_list);

		navigationView = (NavigationView) findViewById(R.id.navigation_view);
		drawerLayout = (DrawerLayout) findViewById(R.id.trip_list_drawer);
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

		HashMap<String, String> data = new HashMap<String, String>();
		data.put("action", "ListTrips");
		PostSender.sendPostML(data, this);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.button_add_trip);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(tripListAdapter != null) tripListAdapter.deselectAll(tripslv);
				Intent intent = new Intent(TripList.this, AddTrip.class);
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		String newtrip = DataManager.selectData("new_trip");
		if(newtrip.equals("true")){
			DataManager.insertData("new_trip", "false");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("action", "ListTrips");
			PostSender.sendPostML(data, this);
		}
	}

	@Override
	public void onResultsSucceeded(String result, List<String> listResult) {
		swipeRefreshLayout.setRefreshing(false);
		if(result.equals(Codes.USER_NOT_FOUND)){
			FastDialog.simpleDialog(this, "ERROR", "An error occurred...",
					"CLOSE", new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
							TripList.this.finish();
							System.exit(RESULT_CANCELED);
						}
					});
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

				tripslv.setOnScrollListener(new AbsListView.OnScrollListener() {
					@Override public void onScrollStateChanged(AbsListView view, int scrollState) {}

					@Override
					public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
						if(firstVisibleItem == 0){

						}
					}
				});

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
		switch (id) {
			case R.id.password_change:
				Intent intent = new Intent(TripList.this, PasswordChange.class);
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
		}
		return super.onKeyUp(keyCode, event);
	}

	public void itemsAreSelected(){
		tripslv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}
		});
		tripslv.setClickable(false);
	}

	public void noItemsAreSelected(){
		tripslv.setOnItemClickListener(getListAction());
		tripslv.setClickable(true);
	}

	@Override
	public void onRefresh() {
		tripListAdapter.deselectAll(tripslv);
		swipeRefreshLayout.setRefreshing(true);
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("action", "ListTrips");
		PostSender.sendPostML(data, this);
	}
}
