package com.example.ezclassapp.Activities;

import android.content.Intent;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


import com.example.ezclassapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.ezclassapp.Models.User;
import com.example.ezclassapp.Fragments.CardFragment;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements CardFragment.onCardSelected {



    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private static ArrayList<String> SUGGESTIONS;
    private SimpleCursorAdapter mAdapter;
    private SearchView searchView;
    // hardcoded needs to be removed
    final String[] arr = {"Chichen Itza", "Christ the Redeemer", "Great Wall of China", "Machu Picchu", "Petra", "Taj Mahal", "Colosseum"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("EZclass");
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);


        final String[] from = new String[]{"cityName"};
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
//            fragment = new CardFragment();
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


    // You must implements your logic to get data using firebase
    /*
            TODO: This populates the adapter for query suggestion
     */
    private void populateAdapter(String query) {


        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "cityName"});
        for (int i = 0; i < SUGGESTIONS.size(); i++) {
            if (SUGGESTIONS.get(i).toLowerCase().startsWith(query.toLowerCase()))
                c.addRow(new Object[]{i, SUGGESTIONS.get(i)});
        }
        mAdapter.changeCursor(c);
    }

    private void updateCardFragment(String query) {
        CardFragment fragment = (CardFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        Log.d("debug", "query is " + query);
        if (fragment != null && fragment.isVisible() && query.length() > 2) {
            Log.d("yeah yeah", "someone has input some text into the query");
            RecyclerView myRecyclerView = (RecyclerView) findViewById(R.id.cardView);
            myRecyclerView.scrollToPosition(0);
            fragment.onNewQuery(query);

        } else if (fragment == null && query.length() > 2) {


            FragmentManager fm = getSupportFragmentManager();
            fragment = new CardFragment();
            fm.beginTransaction().add(R.id.fragmentContainer, fragment).commit();

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
                CardFragment fragment = (CardFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
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
    */
    @Override
    public void onCardSelected(String name) {
        final ReviewListFragment detailsFragment =
                ReviewListFragment.newInstance(name);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, detailsFragment, "reviewListFragment")
                .addToBackStack(null)
                .commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout_btn) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }
        return true;
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }
}
