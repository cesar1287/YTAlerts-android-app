package free.rm.skytube.businessobjects;

import android.util.Log;

import java.util.Random;

import free.rm.skytube.BuildConfig;

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

}
