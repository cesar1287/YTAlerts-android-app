/*
 * SkyTube
 * Copyright (C) 2017  Ramon Mifsud
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

package free.rm.skytube.businessobjects;

import android.util.Log;

import java.util.Random;

import free.rm.skytube.BuildConfig;
import free.rm.skytube.R;
import free.rm.skytube.gui.app.SkyTubeApp;

/**
 * Represents a YouTube API key.
 */
public class YouTubeAPIKey {

	/** User's YouTube API key which is inputted via the
	 * {@link free.rm.skytube.gui.fragments.PreferencesFragment}. **/
	private String userAPIKey;
	private Random random = new Random();

	private static YouTubeAPIKey youTubeAPIKey = null;
	private static final String TAG = YouTubeAPIKey.class.getSimpleName();



	private YouTubeAPIKey() {
		userAPIKey = getUserApiKey();
	}


	/**
	 * @return An instance of {@link YouTubeAPIKey}.
	 */
	public static YouTubeAPIKey get() {
		if (youTubeAPIKey == null) {
			youTubeAPIKey = new YouTubeAPIKey();
		}

		return youTubeAPIKey;
	}



	/**
	 * @return Return YouTube API key.
	 */
	public String getYouTubeAPIKey() {
		String key;

		if (isUserApiKeySet()) {
			// if the user has not set his own API key, then use the default SkyTube key
			key = userAPIKey;
		} else {
			// else we are going to choose one of the defaults keys at random
			int i = random.nextInt( BuildConfig.YOUTUBE_API_KEYS.length );
			key = BuildConfig.YOUTUBE_API_KEYS[i];
		}

		Log.d(TAG, "Key = " + key);
		return key;
	}


	////////////////////////////////////////


	/**
	 * @return True if the user has set his own YouTube API key (via the
	 * {@link free.rm.skytube.gui.fragments.PreferencesFragment}); false otherwise.
	 */
	private boolean isUserApiKeySet() {
		return (userAPIKey != null && !userAPIKey.isEmpty());
	}


	/**
	 * @return User's YouTube API key (if set).
	 */
	private String getUserApiKey() {
		String userApiKey = SkyTubeApp.getPreferenceManager().getString(SkyTubeApp.getStr(R.string.pref_youtube_api_key), null);

		if (userApiKey != null)
			userApiKey = userApiKey.trim();

		return userApiKey;
	}

}
