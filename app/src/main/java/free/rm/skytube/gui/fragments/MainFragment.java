package free.rm.skytube.gui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import free.rm.skytube.R;
import free.rm.skytube.businessobjects.MainActivityListener;
import free.rm.skytube.businessobjects.db.BookmarksDb;
import free.rm.skytube.gui.businessobjects.domain.AdFirebase;
import free.rm.skytube.gui.businessobjects.FirebaseHelper;
import free.rm.skytube.gui.businessobjects.FragmentEx;
import free.rm.skytube.gui.businessobjects.SubsAdapter;

public class MainFragment extends FragmentEx {

	Query mAd;
	ValueEventListener valueEventListener;
	ValueEventListener singleValueEventListener;
	AdFirebase adFirebase;

	private RecyclerView				subsListView = null;
	private SubsAdapter					subsAdapter  = null;
	private ActionBarDrawerToggle		subsDrawerToggle;

	/** List of fragments that will be displayed as tabs. */
	private List<VideosGridFragment>	videoGridFragmentsList = new ArrayList<>();
	private FeaturedVideosFragment		featuredVideosFragment = null;
	private MostPopularVideosFragment	mostPopularVideosFragment = null;
	private SubscriptionsFeedFragment   subscriptionsFeedFragment = null;
	private BookmarksFragment			bookmarksFragment = null;

	private VideosPagerAdapter			videosPagerAdapter = null;
	private ViewPager					viewPager;
    TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_flash,
            R.drawable.ic_account,
			R.drawable.ic_heart
    };

    private int[] tabIconsWhite = {
            R.drawable.ic_home_white,
            R.drawable.ic_flash_white,
            R.drawable.ic_account_white,
			R.drawable.ic_heart_white
    };


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		setupFirebaseAd();

		AdView mAdView = (AdView) view.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				.build();
		mAdView.loadAd(adRequest);

		// setup the toolbar / actionbar
		Toolbar toolbar = (Toolbar) view.findViewById(R.id.activity_main_toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setElevation(0);

		// indicate that this fragment has an action bar menu
		setHasOptionsMenu(true);

		DrawerLayout subsDrawerLayout = (DrawerLayout) view.findViewById(R.id.subs_drawer_layout);
		subsDrawerToggle = new ActionBarDrawerToggle(
						getActivity(),
						subsDrawerLayout,
						R.string.app_name,
						R.string.app_name
		);
		subsDrawerToggle.setDrawerIndicatorEnabled(true);
		final ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setHomeButtonEnabled(true);
		}

		subsListView = (RecyclerView) view.findViewById(R.id.subs_drawer);
		if (subsAdapter == null) {
			subsAdapter = SubsAdapter.get(getActivity(), view.findViewById(R.id.subs_drawer_progress_bar));
		} else {
			subsAdapter.setContext(getActivity());
		}
		subsAdapter.setListener((MainActivityListener)getActivity());

		subsListView.setLayoutManager(new LinearLayoutManager(getActivity()));
		subsListView.setAdapter(subsAdapter);

		videosPagerAdapter = new VideosPagerAdapter(getChildFragmentManager());
		viewPager = (ViewPager) view.findViewById(R.id.pager);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(videosPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //TODO
            }

            @Override
            public void onPageSelected(int position) {
                getActivity().setTitle(videoGridFragmentsList.get(position).getFragmentName());
                if(position==0){
                    tabLayout.getTabAt(0).setIcon(tabIconsWhite[0]);
                    tabLayout.getTabAt(1).setIcon(tabIcons[1]);
                    tabLayout.getTabAt(2).setIcon(tabIcons[2]);
					tabLayout.getTabAt(3).setIcon(tabIcons[3]);
                }else if(position==1){
                    tabLayout.getTabAt(0).setIcon(tabIcons[0]);
                    tabLayout.getTabAt(1).setIcon(tabIconsWhite[1]);
                    tabLayout.getTabAt(2).setIcon(tabIcons[2]);
					tabLayout.getTabAt(3).setIcon(tabIcons[3]);
                }else if(position==2){
                    tabLayout.getTabAt(0).setIcon(tabIcons[0]);
                    tabLayout.getTabAt(1).setIcon(tabIcons[1]);
                    tabLayout.getTabAt(2).setIcon(tabIconsWhite[2]);
					tabLayout.getTabAt(3).setIcon(tabIcons[3]);
                }else if(position==3){
					tabLayout.getTabAt(0).setIcon(tabIcons[0]);
					tabLayout.getTabAt(1).setIcon(tabIcons[1]);
					tabLayout.getTabAt(2).setIcon(tabIcons[2]);
					tabLayout.getTabAt(3).setIcon(tabIconsWhite[3]);
				}
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //TODO
            }
        });

		tabLayout = (TabLayout)view.findViewById(R.id.tab_layout);
		tabLayout.setupWithViewPager(viewPager);

        setupTabIcons();

		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				viewPager.setCurrentItem(tab.getPosition());
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {
			}
		});

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				videoGridFragmentsList.get(position).onFragmentSelected();
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mAd.removeEventListener(valueEventListener);
		mAd.removeEventListener(singleValueEventListener);
	}

	public void setupFirebaseAd(){

		DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mAd = mDatabase.child(FirebaseHelper.FIREBASE_DATABASE_AD);

		valueEventListener = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
					adFirebase = new AdFirebase();
					adFirebase.setBanner((String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_BANNER).getValue());
					adFirebase.setChannel((String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_CHANNEL).getValue());
					adFirebase.setClicks((Long) postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_CLICKS).getValue());
					adFirebase.setId_channel((String)postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_ID_CHANNEL).getValue());
					adFirebase.setImpressions((Long) postSnapshot.child(FirebaseHelper.FIREBASE_DATABASE_IMPRESSIONS).getValue());
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Toast.makeText(getActivity(), R.string.error_loading_ad, Toast.LENGTH_LONG).show();
			}
		};

		singleValueEventListener = new ValueEventListener() {
			public void onDataChange(DataSnapshot dataSnapshot) {

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Toast.makeText(getActivity(), R.string.error_loading_ad, Toast.LENGTH_LONG).show();
			}
		};

		mAd.addValueEventListener(valueEventListener);

		mAd.addListenerForSingleValueEvent(singleValueEventListener);
	}

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIconsWhite[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
		tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		subsDrawerToggle.syncState();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app
		// icon touch event
		if (subsDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		// Handle your other action bar items...
		return super.onOptionsItemSelected(item);
	}



	private class VideosPagerAdapter extends FragmentPagerAdapter {

		public VideosPagerAdapter(FragmentManager fm) {
			super(fm);

			// initialise fragments
			if (featuredVideosFragment == null)
				featuredVideosFragment = new FeaturedVideosFragment();

			if (mostPopularVideosFragment == null)
				mostPopularVideosFragment = new MostPopularVideosFragment();

			if (subscriptionsFeedFragment == null)
				subscriptionsFeedFragment = new SubscriptionsFeedFragment();

			if (bookmarksFragment == null) {
				bookmarksFragment = new BookmarksFragment();
				BookmarksDb.getBookmarksDb().addListener(bookmarksFragment);
			}

			// add fragments to list:  do NOT forget to ***UPDATE*** @string/default_tab and @string/default_tab_values
			videoGridFragmentsList.clear();
			videoGridFragmentsList.add(featuredVideosFragment);
			videoGridFragmentsList.add(mostPopularVideosFragment);
			videoGridFragmentsList.add(subscriptionsFeedFragment);
			videoGridFragmentsList.add(bookmarksFragment);

            getActivity().setTitle(videoGridFragmentsList.get(0).getFragmentName());
		}

		@Override
		public int getCount() {
			return videoGridFragmentsList.size();
		}

		@Override
		public Fragment getItem(int position) {
			return videoGridFragmentsList.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			//return videoGridFragmentsList.get(position).getFragmentName();
            return null;
		}

	}

}
