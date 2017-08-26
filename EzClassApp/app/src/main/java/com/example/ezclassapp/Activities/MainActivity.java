package com.example.ezclassapp.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ezclassapp.Fragments.ClassesCardFragment;
import com.example.ezclassapp.Fragments.ReviewListFragment;
import com.example.ezclassapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements ClassesCardFragment.onCardSelected {


    private static ArrayList<String> SUGGESTIONS;
    // hardcoded needs to be removed
    final String[] arr = {"Chichen Itza", "Christ the Redeemer", "Great Wall of China", "Machu Picchu", "Petra", "Taj Mahal", "Colosseum"};
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private SimpleCursorAdapter mAdapter;
    private SearchView searchView;
    private String APPNAME = "EZclass";
    /* Used for navigation drawer */
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavmenu;
    private String mActivityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        // Set up the toolbar
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(APPNAME);

        // Initialize the drawer layout and navigation view
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavmenu = (NavigationView) findViewById(R.id.navigation_view);
        mActivityTitle = getTitle().toString();

        setupDrawer();
        setupNavigationMenu();
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        final String[] from = new String[]{"className"};
        SUGGESTIONS = new ArrayList<>();
        /*
            Hardcoded populating adapters
            TODO: should populate with the classes
        */

        for (String str : arr) {
            SUGGESTIONS.add(str);
        }
        final int[] to = new int[]{android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

//        if (fragment == null) {
//            fragment = new ClassesCardFragment();
//            fm.beginTransaction()
//                    .add(R.id.fragmentContainer, fragment)
//                    .commit();
//        }

    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        } else {
            //mUserRef.child("online").setValue("true");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // the search bar for searching classes, this will be very important
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // this basically sets up the search view listeners
        setUpSearchView();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        // Menu is the one inflated in the current activity, not the navigation drawer menu
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mNavmenu);
        hideMenuItems(menu, !drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    // You must implements your logic to get data using firebase
    /*
            TODO: This populates the adapter for query suggestion
            need to use firebase Database to populate it!
     */
    private void populateAdapter(String query) {
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "className"});
        for (int i = 0; i < SUGGESTIONS.size(); i++) {
            if (SUGGESTIONS.get(i).toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[]{i, SUGGESTIONS.get(i)});
        }
        mAdapter.changeCursor(c);
    }

    private void updateCardFragment(String query) {
        ClassesCardFragment classesCardFragment = (ClassesCardFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        Log.d("debug", "query is " + query);
        if (classesCardFragment != null && classesCardFragment.isVisible() && query.length() > 2) {
            Log.d("yeah yeah", "someone has input some text into the query");
            RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.cardView);
            myRecyclerView.scrollToPosition(0);
            classesCardFragment.onNewQuery(query);
        } else if (classesCardFragment == null && query.length() > 2) {
            FragmentManager fm = getSupportFragmentManager();
            classesCardFragment = ClassesCardFragment.newInstance(query);
            fm.beginTransaction().add(R.id.fragmentContainer, classesCardFragment).commit();
        }
    }

    public void setUpSearchView() {
        final EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.material_white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.material_white));
        searchEditText.setHint(R.string.searchbar_hint);
        searchEditText.setSingleLine();
        searchView.setSuggestionsAdapter(mAdapter);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                ClassesCardFragment fragment = (ClassesCardFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                if (fragment != null && fragment.isVisible()) {
                    fragment.clearItems();
                }
                Log.d("Debug", "closing the search");
                return false;
            }
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionClick(int position) {
                // Your code here
                Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion = cursor.getString(1);
                Log.d("debug", "u click suggestion " + suggestion);
                searchEditText.setText(suggestion);
                searchEditText.setSelection(suggestion.length());
                searchView.setQuery(suggestion, true);//setting suggestion
                updateCardFragment(suggestion.trim());
                return true;
            }

            @Override
            public boolean onSuggestionSelect(int position) {
                // Your code here
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                populateAdapter(newText);
                updateCardFragment(newText);
                // whenever the you type something into the search Bar
                Log.d("clicked action search", "changing text");
                return true;
            }
        });

    }

    /*
        When the class is selected
        - Interface is from card Fragment
        - when the user clicks on the class Card
    */
    @Override
    public void onCardSelected(String name) {
        final ReviewListFragment reviewListFragment = ReviewListFragment.newInstance(name);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, reviewListFragment, "reviewListFragment")
                .addToBackStack(null)
                .commit();

    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation");
                invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
            }
        };
        // Set drawer indicator
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private void setupNavigationMenu() {
        mNavmenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId(); // Gets the id of the item pressed
                switch (id) {
                    case R.id.menu_item_settings:
                        sendToSettings();
                        return true;
                    case R.id.menu_item_onSignout:
                        FirebaseAuth.getInstance().signOut();
                        sendToStart();
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    private void hideMenuItems(Menu menu, boolean visible) {
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(visible);
        }
    }

    private void sendToSettings() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
}
