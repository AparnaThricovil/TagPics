package org.example.helper;

import org.example.tagproject.CameraActivity;
import org.example.tagproject.ImagesTagActivity;
import org.example.tagproject.MapActivity;
import org.example.tagproject.R;
import org.example.tagproject.TagActivity;
import org.example.tagproject.R.drawable;
import org.example.tagproject.R.layout;
import org.example.tagproject.R.menu;
import org.example.tagproject.R.string;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. 
 */
public class NavigationDrawerFragment extends Fragment {
	
	OnDeleteTagSelectedListener deleteCallback;
	/**
	 * Remember the position of the selected item.
	 */
	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	/**
	 * Per the design guidelines, you should show the drawer on launch until the
	 * user manually expands it. This shared preference tracks this.
	 */
	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	@SuppressWarnings("deprecation")
	private ActionBarDrawerToggle drawerToggle;

	private DrawerLayout drawerLayout;
	private ListView drawerListView;
	private View fragmentContainerView;

	private int currentSelectedPosition = 0;
	private boolean fromSavedInstanceState;
	private boolean userLearnedDrawer;
	private String tagofImage;
	public NavigationDrawerFragment() {
	}
	/*
	 * Interface to show alert from which user 
	 * can choose either yes or no depending on 
	 * whether they want to delete a tag. The 
	 * definition of onDeleteSelectedShowAlert() 
	 * is specified in the AddImageto Tags activity.
	 */
	public interface OnDeleteTagSelectedListener {
        public void onDeleteSelectedShowAlert();
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Read in the flag indicating whether or not the user has demonstrated
		// awareness of the
		// drawer. See PREF_USER_LEARNED_DRAWER for details.
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		userLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		if (savedInstanceState != null) {
			currentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
			fromSavedInstanceState = true;
		}

		// Select either the default item (0) or the last selected item.
		selectItem(currentSelectedPosition);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Indicate that this fragment would like to influence the set of
		// actions in the action bar.
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		drawerListView = (ListView) inflater.inflate(
				R.layout.fragment_navigation_drawer, container, false);
		drawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.v("id is :",""+id);
						/*
						 * If "Add Images" option was selected id will be 0
						 */
						if(id==0){
							Intent intentImage = new Intent(getActivity(), ImagesTagActivity.class);
							intentImage.putExtra("tagSelected", tagofImage);
							getActivity().startActivity(intentImage);
						}
						/*
						 * If "Camera" option was selected id will be 1
						 */
						if(id==1){
							Intent intentCamera = new Intent(getActivity(),CameraActivity.class);
							getActivity().startActivity(intentCamera);
						}
						/*
						 * If "Tag List" option was selected id will be 2
						 */
						if(id==2){
							Intent intentList = new Intent(getActivity(),TagActivity.class);
							getActivity().startActivity(intentList);
						}
						/*
						 * If "Map View" option was selected id will be 3
						 */
						if(id==3){
							Intent intentMap = new Intent(getActivity(), MapActivity.class);
							intentMap.putExtra("tagSelected", tagofImage);
							intentMap.putExtra("imagefile", "na");
							getActivity().startActivity(intentMap);
						}
						/*
						 * If "Delete Tag" option was selected id will be 4
						 */
						if(id==4){
							deleteCallback = (OnDeleteTagSelectedListener)getActivity();
							deleteCallback.onDeleteSelectedShowAlert();
						}
						selectItem(position);
					}
				});
		drawerListView.setAdapter(new ArrayAdapter<String>(getActionBar()
				.getThemedContext(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, new String[] {
						getString(R.string.title_section1),
						getString(R.string.title_section2),
						getString(R.string.title_section3),
						getString(R.string.title_section4),
						getString(R.string.title_section5),}));
		drawerListView.setItemChecked(currentSelectedPosition, true);
		return drawerListView;
	}

	public boolean isDrawerOpen() {
		return drawerLayout != null
				&& drawerLayout.isDrawerOpen(fragmentContainerView);
	}

	/**
	 * Users of this fragment must call this method to set up the navigation
	 * drawer interactions.
	 * 
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	@SuppressWarnings("deprecation")
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		fragmentContainerView = getActivity().findViewById(fragmentId);
		drawerLayout = drawerLayout;

		// set a custom shadow that overlays the main content when the drawer
		// opens
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		drawerToggle = new ActionBarDrawerToggle(getActivity(), /* host Activity */
		drawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.navigation_drawer_open, /*
										 * "open drawer" description for
										 * accessibility
										 */
		R.string.navigation_drawer_close /*
										 * "close drawer" description for
										 * accessibility
										 */
		) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				if (!userLearnedDrawer) {
					// The user manually opened the drawer; store this flag to
					// prevent auto-showing
					// the navigation drawer automatically in the future.
					userLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.apply();
				}

				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}
		};

		// If the user hasn't 'learned' about the drawer, open it to introduce
		// them to the drawer,
		// per the navigation drawer design guidelines.
		if (!userLearnedDrawer && !fromSavedInstanceState) {
			drawerLayout.openDrawer(fragmentContainerView);
		}

		// Defer code dependent on restoration of previous instance state.
		drawerLayout.post(new Runnable() {
			@Override
			public void run() {
				drawerToggle.syncState();
			}
		});

		drawerLayout.setDrawerListener(drawerToggle);
	}

	private void selectItem(int position) {
		Log.v("NDF","selectItem");
		currentSelectedPosition = position;
		if (drawerListView != null) {
			drawerListView.setItemChecked(position, true);
		}
		if (drawerLayout != null) {
			drawerLayout.closeDrawer(fragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		Log.v("NDF","onAttach");
		super.onAttach(activity);
		tagofImage = getActivity().getIntent().getStringExtra("tagSelected");
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, currentSelectedPosition);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Forward the new configuration the drawer toggle component.
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.v("NDF","onCreateOptionsMenu");
		// If the drawer is open, show the global app actions in the action bar.
		// See also
		// showGlobalContextActionBar, which controls the top-left area of the
		// action bar.
		if (drawerLayout != null && isDrawerOpen()) {
			inflater.inflate(R.menu.global, menu);
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		/*if (item.getItemId() == R.id.action_example) {
			Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT)
					.show();
			return true;
		}*/

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Per the navigation drawer design guidelines, updates the action bar to
	 * show the global app 'context', rather than just what's in the current
	 * screen.
	 */
	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return getActivity().getActionBar();
	}

	/**
	 * Callbacks interface that all activities using this fragment must
	 * implement.
	 */
	public static interface NavigationDrawerCallbacks {
		/**
		 * Called when an item in the navigation drawer is selected.
		 */
		void onNavigationDrawerItemSelected(int position);
	}
}
