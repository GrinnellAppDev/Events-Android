package edu.grinnell.appdev.events;


import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentHome extends Fragment {

    public HomeRecyclerViewAdapter recyclerViewAdapter;
    public ArrayList<Event> eventArrayList;
    private Activity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        eventArrayList = getArguments().getParcelableArrayList("Event list");
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    /**
     * Set up the recycler view
     * @param activity Activity in which recycler view is set up
     */
    public void configureRecyclerView(Activity activity){
        RecyclerView recyclerView = getView().findViewById(R.id.my_recycler_view);
        recyclerView.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
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
    }

    /**
     * Set up search function on the action bar. Sets up a listener as well
     * @param menu Menu item on the action bar
     */
    public void setUpSearch(Menu menu){
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



}
