package dev.radley.omgstarwars.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import dev.radley.omgstarwars.R;
import dev.radley.omgstarwars.Util.OmgSWUtil;
import dev.radley.omgstarwars.component.AutoCompleteSearchViewManager;
import dev.radley.omgstarwars.fragment.BaseCategoryFragment;
import dev.radley.omgstarwars.bundle.DetailIntentUtil;
import dev.radley.omgstarwars.model.CategoryView;
import dev.radley.omgstarwars.model.CategoryViewList;
import dev.radley.omgstarwars.model.sw.SWModel;

public class MainActivity extends AppCompatActivity {

    protected Activity mActivity;
    protected AppBarLayout mAppBarLayout;

    protected ArrayList<String> mSearchResultTitles;
    protected ArrayList<Object> mSearchResultItems;
    protected Handler mHandler = new Handler();
    protected int mPage;

    protected PagerAdapter mPagerAdapter;
    //protected SearchView mSearchView;
    protected AutoCompleteSearchViewManager mSearchViewManager;
    protected String mCurrentCategory;
    protected TabLayout mTabLayout;
    protected ViewPager mPager;

    protected static ArrayList<CategoryView> sCategories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        setContentView(R.layout.activity_categories);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mActivity = this;

        initCategories();
        initLayout();
        updateHeroImage(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_categories, menu);
        mSearchViewManager = new AutoCompleteSearchViewManager(this, (SearchView) menu.findItem(R.id.action_search).getActionView());
        mSearchViewManager.init(mCurrentCategory);
        return true;
    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {

        private Context mContext;

        public MyPagerAdapter(Context context, FragmentManager fragmentManager) {
            super(fragmentManager);
            mContext = context;
        }

        @Override
        public int getCount() {
            return sCategories.size();
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(OmgSWUtil.tag, "sCategories.get(position) = " + sCategories.get(position));
            return sCategories.get(position).getFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return sCategories.get(position).getName();
        }
    }


    protected void initCategories() {
        sCategories = CategoryViewList.getCategories(this);
        mCurrentCategory = getString(R.string.category_id_films);
    }

    protected void initLayout() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(sCategories.size());

        mPagerAdapter = new MyPagerAdapter(this, getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                updateHeroImage(position);

                mCurrentCategory = sCategories.get(position).getId();

                if(mSearchViewManager != null)
                    mSearchViewManager.updateCategory(mCurrentCategory);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        // Give the TabLayout the ViewPager
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mPager) {

            // scroll to top
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                BaseCategoryFragment fragment = (BaseCategoryFragment) mPager.getAdapter()
                        .instantiateItem(mPager, mPager.getCurrentItem());

                fragment.getRecyclerView().smoothScrollToPosition(0);
            }
        });

        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                // only show Tabbar background color when extended (or close)
                if (Math.abs(verticalOffset) < 24) {
                    mTabLayout.setBackgroundColor(getApplicationContext().getColor(R.color.transparentPrimaryDark));

                } else {
                    mTabLayout.setBackground(null);
                }
            }
        });
    }

    public void updateHeroImage(int position) {

        String id = sCategories.get(position).getId();

        DrawableCrossFadeFactory factory =
                new DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(false).build();

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.hero_placeholder);

        ImageView imageView = (ImageView) findViewById(R.id.hero_image);
        Glide.with(this)
                .setDefaultRequestOptions(requestOptions)
                .load(Uri.parse("file:///android_asset/categories/" + id + ".jpg"))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    protected void initSearch() {

    }

    protected Intent getDetailIntent(int index) {

        return DetailIntentUtil.getIntent(this, mCurrentCategory, (SWModel) mSearchResultItems.get(index));
    }
}