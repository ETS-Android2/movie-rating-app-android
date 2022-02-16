package com.example.movieapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Comparator;

public class ListFrag extends Fragment {
    private static final int SORT_BY_NONE = 0;
    private static final int SORT_BY_NAME = 1;
    private static final int SORT_BY_RELEASE_DATE = 2;
    private static final int SORT_BY_RATING = 3;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter<ApplicationAdapter.ViewHolder> myAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private View view;

    public ListFrag() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);

        ((MainActivity) requireActivity()).setFragmentRefreshListener((sort, movies) -> {
            if(sort == SORT_BY_NONE) {
                movies.sort(Comparator.comparing(Movie::getId));
                myAdapter = new ApplicationAdapter(this.getActivity(), movies);
                recyclerView.setAdapter(myAdapter);
            } else if(sort == SORT_BY_NAME) {
                movies.sort(Comparator.comparing(m -> m.getMovieName().toLowerCase()));
                myAdapter = new ApplicationAdapter(getActivity(), movies);
                recyclerView.setAdapter(myAdapter);
            } else if(sort == SORT_BY_RELEASE_DATE) {
                movies.sort(Comparator.comparing(m -> m.getYearOfApparition().toLowerCase()));
                myAdapter = new ApplicationAdapter(getActivity(), movies);
                recyclerView.setAdapter(myAdapter);
            } else if(sort == SORT_BY_RATING) {
                movies.sort(Comparator.comparing(Movie::getMovieRating).reversed());
                myAdapter = new ApplicationAdapter(getActivity(), movies);
                recyclerView.setAdapter(myAdapter);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView = view.findViewById(R.id.list);
        recyclerView.setHasFixedSize(true);

        layoutManager = new WrapContentGridLayoutManager(this.getActivity(), 3, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        myAdapter = new ApplicationAdapter(this.getActivity(), new ArrayList<>());
        recyclerView.setAdapter(myAdapter);

    }

}