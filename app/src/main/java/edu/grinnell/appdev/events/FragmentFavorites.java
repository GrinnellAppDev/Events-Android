package edu.grinnell.appdev.events;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFavorites extends Fragment {

    public FavoritesRecyclerViewAdapter recyclerViewAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    public void configureRecyclerView(Activity activity){
        RecyclerView recyclerView = getView().findViewById(R.id.favorites_recycler);
        recyclerView.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new FavoritesRecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configureRecyclerView(getActivity());
        Log.d("recycler configured", "onViewCreated: ");
    }


}
