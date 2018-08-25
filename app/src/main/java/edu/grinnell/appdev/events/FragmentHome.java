package edu.grinnell.appdev.events;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static edu.grinnell.appdev.events.Constants.FAVORITES_LIST;
import static edu.grinnell.appdev.events.Constants.REFRESH_OFFSET;
import static edu.grinnell.appdev.events.MainActivity.downloadContent;
import static edu.grinnell.appdev.events.MainActivity.favoritesList;
import static edu.grinnell.appdev.events.MainActivity.readData;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public HomeRecyclerViewAdapter recyclerViewAdapter;
    public ArrayList<Event> eventArrayList;
    private Activity activity;
    private RecyclerView.LayoutManager layoutManager;
    private Parcelable parcelable;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        eventArrayList = getArguments().getParcelableArrayList("Event list");
        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        //Set the offset of the swipe animation such that it is below the action bar
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        mSwipeRefreshLayout.setProgressViewOffset(false, actionBarHeight,
                actionBarHeight + REFRESH_OFFSET);
        return rootView;
    }


    /**
     * Set up the recycler view
     * @param activity Activity in which recycler view is set up
     */
    private void configureRecyclerView(Activity activity){
        RecyclerView recyclerView = getView().findViewById(R.id.my_recycler_view);
        recyclerView.hasFixedSize();
        layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        String favStr = readData(FAVORITES_LIST, getActivity(), false);
        if (favStr != null){
            Type type = new TypeToken<ArrayList<Event>>() {}.getType();
            Gson gson = new Gson();
            favoritesList = gson.fromJson(favStr, type); //Restore previous data
        }

        recyclerViewAdapter = new HomeRecyclerViewAdapter(activity, eventArrayList);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), VERTICAL));
        recyclerView.setAdapter(recyclerViewAdapter);
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureRecyclerView(getActivity());

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        this.activity = getActivity();

        // Retrieve list state and list/item positions
        if(savedInstanceState != null)
            parcelable = savedInstanceState.getParcelable("Events list");
    }

    /**
     * Set up search function on the action bar. Sets up a listener as well
     * @param menu Menu item on the action bar
     */
    private void setUpSearch(Menu menu){
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                recyclerViewAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                recyclerViewAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.search_list, menu);
        inflater.inflate(R.menu.overflow, menu);
        setUpSearch(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save list state
        parcelable = layoutManager.onSaveInstanceState();
        state.putParcelable("Events list", parcelable);
    }

    @Override
    public void onResume() {
        super.onResume();

        //Restore the original state of recycler view
        if (parcelable != null) {
            layoutManager.onRestoreInstanceState(parcelable);
        }
    }


    @Override
    public void onRefresh() {
        downloadContent(getActivity());
    }
}
