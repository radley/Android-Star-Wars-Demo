package dev.radley.omgstarwars.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import dev.radley.omgstarwars.R;

public abstract class BaseCategoryFragment extends Fragment {


    protected Context mContext;

    protected GridLayoutManager mLayoutManager;

    protected View mView;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;

    protected int mTotalItems;
    protected int mPage = 1;
    protected int mPageSize;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_category, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.grid);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), getSpanCount()));
        return mView;
    }

    // called from activity to scroll back to top
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
    public abstract void updateList(ArrayList<Object> list);
    public abstract void clear();


    protected int getSpanCount() {
        return getResources().getInteger(R.integer.grid_span_count_tall);
    }

    // To override
    protected abstract void getGridItemsByPage(int page);
    protected abstract void initGrid();
    protected abstract void populateGrid();


}
