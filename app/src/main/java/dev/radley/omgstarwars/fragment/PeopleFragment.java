package dev.radley.omgstarwars.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import dev.radley.omgstarwars.Util.OmgSWUtil;
import dev.radley.omgstarwars.adapter.PeopleAdapter;
import dev.radley.omgstarwars.bundle.DetailIntentUtil;
import dev.radley.omgstarwars.bundle.SearchIntentUtil;
import dev.radley.omgstarwars.listener.OnBottomReachedListener;
import dev.radley.omgstarwars.listener.RecyclerTouchListener;
import dev.radley.omgstarwars.model.sw.People;
import dev.radley.omgstarwars.model.sw.SWModel;
import dev.radley.omgstarwars.model.sw.SWModelList;
import dev.radley.omgstarwars.network.StarWarsApi;
import retrofit2.Call;

public class PeopleFragment extends BaseCategoryFragment {


    protected PeopleAdapter mAdapter;

    protected int mTotalItems;
    protected int mPage = 1;
    protected int mPageSize;
    protected boolean mLoading = false;

    protected ArrayList<People> mList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(SearchIntentUtil.RESULT_LIST)) {

            mList = (ArrayList<People>) arguments.getSerializable(SearchIntentUtil.RESULT_LIST);
        } else {
            mList = new ArrayList<People>();
        }

        StarWarsApi.init();
        initGrid();

        return mView;
    }

    public void updateList(ArrayList<Object> list) {

        mList = new ArrayList<People>();
        for (Object object : list) {
            mList.add(((People) object));
        }

        mAdapter.notifyDataSetChanged();
    }

    public void clear(){
        mList.clear();
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void initGrid() {
        if (mList.size() == 0) {
            getGridItemsByPage(mPage);
            return;
        }

        populateGrid();
    }

    @Override
    protected void populateGrid() {
        mAdapter = new PeopleAdapter(getContext(), mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext()) {

            public void onItemSelected(RecyclerView.ViewHolder holder, int position) {

                startActivity(DetailIntentUtil.getIntent(getActivity(), mList.get(position).getCategoryId(), (SWModel) mList.get(position)));
            }
        });

        mAdapter.setOnBottomReachedListener(new OnBottomReachedListener() {

            @Override
            public void onBottomReached(int position) {
                if (mTotalItems > mList.size() && !mLoading) {
                    mPage++;
                    getGridItemsByPage(mPage);
                }
            }
        });

    }

    protected void getGridItemsByPage(int page) {

        mLoading = true;

        Call<SWModelList<People>> call = StarWarsApi.getApi().getAllPeople(page);
        call.enqueue(new retrofit2.Callback<SWModelList<People>>() {

            @Override
            public void onResponse(Call<SWModelList<People>> call, retrofit2.Response<SWModelList<People>> response) {
                onCallbackSuccess(response.body());
            }

            @Override
            public void onFailure(Call<SWModelList<People>> call, Throwable t) {
                Log.d(OmgSWUtil.tag, "error: " + t.getMessage());
            }
        });
    }

    protected void onCallbackSuccess(SWModelList<People> list) {

        // init new list
        if (mList.size() == 0) {
            mTotalItems = list.count;
            mPageSize = list.results.size();
            //mList.addAll(list.results);

            for (Object object : list.results) {
                mList.add(((People) object));
            }

            populateGrid();

        } else { // update list

            Log.d(OmgSWUtil.tag, "update list");

            int curSize = mAdapter.getItemCount();
            mList.addAll(list.results);
            mAdapter.notifyItemRangeInserted(curSize, list.results.size());
        }

        mLoading = false;
    }



}