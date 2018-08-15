package edu.grinnell.appdev.events;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentFavorites extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyView;

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
        FavoritesRecyclerViewAdapter recyclerViewAdapter = new FavoritesRecyclerViewAdapter();
        recyclerViewAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.favorites_recycler);
        emptyView = getView().findViewById(R.id.empty_view);
        if (!MainActivity.favoritesList.isEmpty()) {
            configureRecyclerView(getActivity());
            recyclerView.setVisibility(getView().VISIBLE);
            emptyView.setVisibility(getView().GONE);
        }
        else {
            recyclerView.setVisibility(getView().GONE);
            emptyView.setVisibility(getView().VISIBLE);
        }
    }


}
