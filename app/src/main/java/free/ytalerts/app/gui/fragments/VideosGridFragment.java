/*
 * SkyTube
 * Copyright (C) 2015  Ramon Mifsud
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation (version 3 of the License).
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package free.ytalerts.app.gui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import free.ytalerts.app.R;
import free.ytalerts.app.businessobjects.MainActivityListener;
import free.ytalerts.app.businessobjects.VideoCategory;
import free.ytalerts.app.gui.activities.MainActivity;
import free.ytalerts.app.gui.businessobjects.firebase.FirebaseHelper;
import free.ytalerts.app.gui.businessobjects.VideoGridAdapter;

/**
 * A fragment that will hold a {@link GridView} full of YouTube videos.
 */
public abstract class VideosGridFragment extends BaseVideosGridFragment {

	protected RecyclerView	gridView;
	private View			progressBar = null;
	private int 			layoutResource = 0;
    DatabaseReference mDatabase;


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setLayoutResource(R.layout.videos_gridview);

        mDatabase = FirebaseDatabase.getInstance().getReference();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// inflate the layout for this fragment
		View view = inflater.inflate(layoutResource, container, false);

		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ad_firebase);
		TextView channelNameTextview = (TextView) view.findViewById(R.id.channel_name_text_view);
        TextView channelDescriptionTextView = (TextView) view.findViewById(R.id.channel_description_text_view);
        TextView labelAdFirebase = (TextView) view.findViewById(R.id.text_label_ad);
        ImageView channelImageView = (ImageView) view.findViewById(R.id.channel_image_view);
        if (channelNameTextview!=null && channelDescriptionTextView!=null && channelImageView!=null && labelAdFirebase!=null){

            channelNameTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
					if(FirebaseHelper.LINK.equals("")) {
						Intent i = new Intent(getActivity(), MainActivity.class);
						i.setAction(MainActivity.ACTION_VIEW_CHANNEL);
						i.putExtra(ChannelBrowserFragment.CHANNEL_ID, FirebaseHelper.ID_CHANNEL);
						startActivity(i);
					}else{
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(FirebaseHelper.LINK));
						startActivity(i);
					}

                    mDatabase.child(FirebaseHelper.FIREBASE_DATABASE_AD)
                            .child(FirebaseHelper.CHILD)
                            .child(FirebaseHelper.FIREBASE_DATABASE_CLICKS)
                            .setValue(FirebaseHelper.CLICKS+1);
                }
            });

            channelDescriptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
					if(FirebaseHelper.LINK.equals("")) {
						Intent i = new Intent(getActivity(), MainActivity.class);
						i.setAction(MainActivity.ACTION_VIEW_CHANNEL);
						i.putExtra(ChannelBrowserFragment.CHANNEL_ID, FirebaseHelper.ID_CHANNEL);
						startActivity(i);
					}else{
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(FirebaseHelper.LINK));
						startActivity(i);
					}

                    mDatabase.child(FirebaseHelper.FIREBASE_DATABASE_AD)
                            .child(FirebaseHelper.CHILD)
                            .child(FirebaseHelper.FIREBASE_DATABASE_CLICKS)
                            .setValue(FirebaseHelper.CLICKS+1);
                }
            });

            channelImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
					if(FirebaseHelper.LINK.equals("")) {
						Intent i = new Intent(getActivity(), MainActivity.class);
						i.setAction(MainActivity.ACTION_VIEW_CHANNEL);
						i.putExtra(ChannelBrowserFragment.CHANNEL_ID, FirebaseHelper.ID_CHANNEL);
						startActivity(i);
					}else{
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(FirebaseHelper.LINK));
						startActivity(i);
					}

                    mDatabase.child(FirebaseHelper.FIREBASE_DATABASE_AD)
                            .child(FirebaseHelper.CHILD)
                            .child(FirebaseHelper.FIREBASE_DATABASE_CLICKS)
                            .setValue(FirebaseHelper.CLICKS+1);
                }
            });

            labelAdFirebase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
					if(FirebaseHelper.LINK.equals("")) {
						Intent i = new Intent(getActivity(), MainActivity.class);
						i.setAction(MainActivity.ACTION_VIEW_CHANNEL);
						i.putExtra(ChannelBrowserFragment.CHANNEL_ID, FirebaseHelper.ID_CHANNEL);
						startActivity(i);
					}else{
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(FirebaseHelper.LINK));
						startActivity(i);
					}

                    mDatabase.child(FirebaseHelper.FIREBASE_DATABASE_AD)
                            .child(FirebaseHelper.CHILD)
                            .child(FirebaseHelper.FIREBASE_DATABASE_CLICKS)
                            .setValue(FirebaseHelper.CLICKS+1);
                }
            });

			if(!FirebaseHelper.NAME_CHANNEL.equals("")) {
				channelNameTextview.setText(FirebaseHelper.NAME_CHANNEL);
				channelDescriptionTextView.setText(FirebaseHelper.DESCRIPTION);
				Glide.with(getActivity())
						.load(FirebaseHelper.BANNER)
						.centerCrop()
						.placeholder(R.drawable.ic_icon)
						.into(channelImageView);
			}else{
				linearLayout.setVisibility(View.GONE);
			}
        }

		// set up the loading progress bar
		progressBar = view.findViewById(R.id.loading_progress_bar);

		// setup the video grid view
		gridView = (RecyclerView) view.findViewById(R.id.grid_view);
		if (videoGridAdapter == null) {
			videoGridAdapter = new VideoGridAdapter(getActivity());
		} else {
			videoGridAdapter.setContext(getActivity());
		}
		videoGridAdapter.setProgressBar(progressBar);

		if (getVideoCategory() != null)
			videoGridAdapter.setVideoCategory(getVideoCategory());

		videoGridAdapter.setListener((MainActivityListener)getActivity());

		gridView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.video_grid_num_columns)));
		gridView.setAdapter(videoGridAdapter);

		return view;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		Glide.get(getActivity()).clearMemory();
	}

	/**
	 * In case a subclass of VideosGridFragment wants to use an alternate layout resource (e.g. Subscriptions).
 	 */
	protected void setLayoutResource(int layoutResource) {
		this.layoutResource = layoutResource;
	}


	/**
	 * @return Returns the category of videos being displayed by this fragment.
	 */
	protected abstract VideoCategory getVideoCategory();


	/**
	 * @return The fragment/tab name/title.
	 */
	protected abstract String getFragmentName();


	/**
	 * Will be called when the user selects this fragment/tab.
	 */
	protected abstract void onFragmentSelected();

}
