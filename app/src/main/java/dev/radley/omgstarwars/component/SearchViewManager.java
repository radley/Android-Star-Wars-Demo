package dev.radley.omgstarwars.component;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;

import dev.radley.omgstarwars.R;
import dev.radley.omgstarwars.Util.OmgSWUtil;
import dev.radley.omgstarwars.model.sw.Film;
import dev.radley.omgstarwars.model.sw.People;
import dev.radley.omgstarwars.model.sw.Planet;
import dev.radley.omgstarwars.model.sw.SWModelList;
import dev.radley.omgstarwars.model.sw.Species;
import dev.radley.omgstarwars.model.sw.Starship;
import dev.radley.omgstarwars.model.sw.Vehicle;
import dev.radley.omgstarwars.network.StarWarsApi;
import retrofit2.Call;

public class SearchViewManager {

    protected Activity mActivity;

    protected ArrayList<String> mResultTitles;
    protected ArrayList<Object> mResultItems;
    protected Call<SWModelList<Film>> mCallFilm;
    protected Call<SWModelList<People>> mCallPeople;
    protected Call<SWModelList<Planet>> mCallPlanets;
    protected Call<SWModelList<Species>> mCallSpecies;
    protected Call<SWModelList<Starship>> mCallStarships;
    protected Call<SWModelList<Vehicle>> mCallVehicles;
    protected Handler mHandler = new Handler();
    protected int mPage;
    protected SearchView mSearchView;

    protected String mCategory;
    protected String mQuery;

    private SearchViewListener listener;


    public SearchViewManager(Activity activity, SearchView searchView) {

        mActivity = activity;
        mSearchView = searchView;

        mResultTitles = new ArrayList<>();
        mResultItems = new ArrayList<>();

        this.listener = null;
    }


    public interface SearchViewListener {

        public void onObjectReady(String title);

        public void onClearSearchResults();

        public void onLoadComplete(ArrayList<Object> data, String query);
    }

    public void addSearchViewListener(SearchViewListener listener) {
        this.listener = listener;
    }

    public void updateCategory(String category) {

        mCategory = category;

        mResultTitles.clear();
        mResultItems.clear();

        //mSearchItem.collapseActionView();
        //mSearchView.setIconified(true);

        // add default hint
        mSearchView.setQueryHint("Search " + mCategory + "...");
        mSearchView.setQuery("", false);
    }

    public void init(String category) {

        mCategory = category;

        mSearchView.setQueryHint("Search " + mCategory + "...");


        mSearchView.setIconified(false);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                mQuery = query;

                mHandler.removeCallbacksAndMessages(null);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startSearchLoop();
                    }
                }, 500);

                return true;

            }
        });
    }


    protected void startSearchLoop() {

        mPage = 1;

        // SW API only searches within categories
        if (mCategory.equals((mActivity.getString(R.string.category_id_films)))) {
            searchFilms();

        } else if (mCategory.equals((mActivity.getString(R.string.category_id_people)))) {
            searchPeople();

        } else if (mCategory.equals((mActivity.getString(R.string.category_id_planets)))) {
            searchPlanets();

        } else if (mCategory.equals((mActivity.getString(R.string.category_id_species)))) {
            searchSpecies();

        } else if (mCategory.equals((mActivity.getString(R.string.category_id_starships)))) {
            searchStarships();

        } else if (mCategory.equals((mActivity.getString(R.string.category_id_vehicles)))) {
            searchVehicles();
        }
    }

    protected void clearSearchResults() {

        mResultTitles.clear();
        mResultItems.clear();

        listener.onClearSearchResults();

    }

    protected void onSearchLoopComplete() {

        listener.onLoadComplete(mResultItems, mQuery);
    }



    protected void searchFilms() {

        if(mCallFilm != null && mCallFilm.isExecuted())
            mCallFilm.cancel();

        if(mQuery.length() < 2){
            clearSearchResults();
            return;
        }

        Log.d(OmgSWUtil.tag, "searchFilms(): " + mQuery);

        mCallFilm = StarWarsApi.getApi().searchFilms(mPage, mQuery);
        mCallFilm.enqueue(new retrofit2.Callback<SWModelList<Film>>() {

            @Override
            public void onResponse(Call<SWModelList<Film>> call, retrofit2.Response<SWModelList<Film>> response) {

                Log.d(OmgSWUtil.tag, "onResponse(): " + mQuery);
                onFilmSearchSuccess(response.body());
            }

            @Override
            public void onFailure(Call<SWModelList<Film>> call, Throwable t) {

                Log.d(OmgSWUtil.tag, "onFailure(): " + mQuery);
                Log.d(OmgSWUtil.tag, "error: " + t.getMessage());
            }
        });
    }

    protected void onFilmSearchSuccess(SWModelList list) {

        // clear if first page
        if(mPage == 1) {
            mResultTitles.clear();
            mResultItems.clear();
        }

        for (Object object : list.results) {
            mResultItems.add(((Film) object));
            mResultTitles.add(((Film) object).title);
        }

        if(list.next != null) {
            mPage++;
            searchFilms();
        } else {
            onSearchLoopComplete();
        }
    }



    protected void searchPeople() {

        if(mCallPeople != null && mCallPeople.isExecuted())
            mCallPeople.cancel();

        if(mQuery.length() < 2){
            clearSearchResults();
            return;
        }


        Log.d(OmgSWUtil.tag, "searchFilms(): " + mQuery);

        mCallPeople = StarWarsApi.getApi().searchPeople(mPage, mQuery);
        mCallPeople.enqueue(new retrofit2.Callback<SWModelList<People>>() {

            @Override
            public void onResponse(Call<SWModelList<People>> call, retrofit2.Response<SWModelList<People>> response) {
                onPeopleSearchSuccess(response.body());
            }

            @Override
            public void onFailure(Call<SWModelList<People>> call, Throwable t) {
                Log.d(OmgSWUtil.tag, "error: " + t.getMessage());
            }
        });
    }

    protected void onPeopleSearchSuccess(SWModelList list) {

        // clear if first page
        if(mPage == 1) {
            mResultTitles.clear();
            mResultItems.clear();
        }

        for (Object object : list.results) {
            mResultItems.add(((People) object));
            mResultTitles.add(((People) object).name);
        }

        if(list.next != null) {
            mPage++;
            searchPeople();
        } else {
            onSearchLoopComplete();
        }
    }

    protected void searchPlanets() {

        if(mCallPlanets != null && mCallPlanets.isExecuted())
            mCallPlanets.cancel();

        if(mQuery.length() < 2){
            clearSearchResults();
            return;
        }

        mCallPlanets = StarWarsApi.getApi().searchPlanets(mPage, mQuery);
        mCallPlanets.enqueue(new retrofit2.Callback<SWModelList<Planet>>() {

            @Override
            public void onResponse(Call<SWModelList<Planet>> call, retrofit2.Response<SWModelList<Planet>> response) {
                onPlanetsSearchSuccess(response.body());
            }

            @Override
            public void onFailure(Call<SWModelList<Planet>> call, Throwable t) {
                Log.d(OmgSWUtil.tag, "error: " + t.getMessage());
            }
        });
    }

    protected void onPlanetsSearchSuccess(SWModelList list) {

        // clear if first page
        if(mPage == 1) {
            mResultTitles.clear();
            mResultItems.clear();
        }

        for (Object object : list.results) {
            mResultItems.add(((Planet) object));
            mResultTitles.add(((Planet) object).name);
        }

        if(list.next != null) {
            mPage++;
            searchPlanets();
        } else {
            onSearchLoopComplete();
        }
    }

    protected void searchSpecies() {

        if(mCallSpecies != null && mCallSpecies.isExecuted())
            mCallSpecies.cancel();

        if(mQuery.length() < 2){
            clearSearchResults();
            return;
        }

        mCallSpecies = StarWarsApi.getApi().searchSpecies(mPage, mQuery);
        mCallSpecies.enqueue(new retrofit2.Callback<SWModelList<Species>>() {

            @Override
            public void onResponse(Call<SWModelList<Species>> call, retrofit2.Response<SWModelList<Species>> response) {
                onSpeciesSearchSuccess(response.body());
            }

            @Override
            public void onFailure(Call<SWModelList<Species>> call, Throwable t) {
                Log.d(OmgSWUtil.tag, "error: " + t.getMessage());
            }
        });
    }

    protected void onSpeciesSearchSuccess(SWModelList list) {

        // clear if first page
        if(mPage == 1) {
            mResultTitles.clear();
            mResultItems.clear();
        }

        for (Object object : list.results) {
            mResultItems.add(((Species) object));
            mResultTitles.add(((Species) object).name);
        }

        if(list.next != null) {
            mPage++;
            searchSpecies();
        } else {
            onSearchLoopComplete();
        }
    }

    protected void searchStarships() {

        if(mCallStarships != null && mCallStarships.isExecuted())
            mCallStarships.cancel();

        if(mQuery.length() < 2){
            clearSearchResults();
            return;
        }

        mCallStarships = StarWarsApi.getApi().searchStarships(mPage, mQuery);
        mCallStarships.enqueue(new retrofit2.Callback<SWModelList<Starship>>() {

            @Override
            public void onResponse(Call<SWModelList<Starship>> call, retrofit2.Response<SWModelList<Starship>> response) {
                onStarshipsSearchSuccess(response.body());
            }

            @Override
            public void onFailure(Call<SWModelList<Starship>> call, Throwable t) {
                Log.d(OmgSWUtil.tag, "error: " + t.getMessage());
            }
        });
    }

    protected void onStarshipsSearchSuccess(SWModelList list) {

        // clear if first page
        if(mPage == 1) {
            mResultTitles.clear();
            mResultItems.clear();
        }

        for (Object object : list.results) {
            mResultItems.add(((Starship) object));
            mResultTitles.add(((Starship) object).name);
        }

        if(list.next != null) {
            mPage++;
            searchStarships();
        } else {
            onSearchLoopComplete();
        }
    }

    protected void searchVehicles() {

        if(mCallVehicles != null && mCallVehicles.isExecuted())
            mCallVehicles.cancel();

        if(mQuery.length() < 2){
            clearSearchResults();
            return;
        }

        mCallVehicles = StarWarsApi.getApi().searchVehicles(mPage, mQuery);
        mCallVehicles.enqueue(new retrofit2.Callback<SWModelList<Vehicle>>() {

            @Override
            public void onResponse(Call<SWModelList<Vehicle>> call, retrofit2.Response<SWModelList<Vehicle>> response) {
                onVehiclesSearchSuccess(response.body());
            }

            @Override
            public void onFailure(Call<SWModelList<Vehicle>> call, Throwable t) {
                Log.d(OmgSWUtil.tag, "error: " + t.getMessage());
            }
        });
    }

    protected void onVehiclesSearchSuccess(SWModelList list) {

        // clear if first page
        if(mPage == 1) {
            mResultTitles.clear();
            mResultItems.clear();
        }

        for (Object object : list.results) {
            mResultItems.add(((Vehicle) object));
            mResultTitles.add(((Vehicle) object).name);
        }

        if(list.next != null) {
            mPage++;
            searchVehicles();
        } else {
            onSearchLoopComplete();
        }
    }


}