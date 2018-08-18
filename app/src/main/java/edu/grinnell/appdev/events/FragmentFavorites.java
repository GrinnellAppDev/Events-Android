package edu.grinnell.appdev.events;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static edu.grinnell.appdev.events.MainActivity.FAVORITES_LIST;
import static edu.grinnell.appdev.events.MainActivity.favoritesList;
import static edu.grinnell.appdev.events.MainActivity.storeEvents;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFavorites extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    private RecyclerView recyclerView;
    private TextView emptyView;
    private FavoritesRecyclerViewAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    /**
     *
     * @param activity Activity in which the recycler view resides
     */
    public void configureRecyclerView(Activity activity){

        recyclerView.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new FavoritesRecyclerViewAdapter();
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), VERTICAL));

        recyclerView.setAdapter(recyclerViewAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


    }

    public static void removeWithID(String id, ArrayList<Event> favorites){
        for (int i =0; i < favorites.size(); i++) {
            if (favorites.get(i).getId().equals(id)) {
                favorites.remove(i);
            }
        }
    }

    public static void addEvent (Event event, ArrayList<Event> favorites){
        for (Event e: favorites){
            if (e.getId().equals(event.getId())){
                return;
            }
        }
        favorites.add(event);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.favorites_recycler);
        emptyView = getView().findViewById(R.id.empty_view);


        if (!MainActivity.favoritesList.isEmpty()) {
            configureRecyclerView(getActivity());
            emptyView.setVisibility(getView().GONE);
            recyclerView.setVisibility(getView().VISIBLE);
        }
        else {
            emptyView.setVisibility(getView().VISIBLE);
            recyclerView.setVisibility(getView().GONE);
        }
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        removeWithID(MainActivity.favoritesList.get(position).getId(), MainActivity.favoritesList);
        storeEvents(favoritesList, getContext(), FAVORITES_LIST);
        recyclerViewAdapter.notifyItemRemoved(position);

        if(favoritesList.isEmpty()){
            emptyView.setVisibility(getView().VISIBLE);
            recyclerView.setVisibility(getView().GONE);
        }
    }
}
