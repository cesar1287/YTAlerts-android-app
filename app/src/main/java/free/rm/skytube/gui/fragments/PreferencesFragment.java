package free.rm.skytube.gui.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import free.rm.skytube.BuildConfig;
import free.rm.skytube.R;
import free.rm.skytube.businessobjects.VideoStream.VideoResolution;

/**
 * A fragment that allows the user to change the settings of this app.  This fragment is called by
 * {@link free.rm.skytube.gui.activities.PreferencesActivity}
 */
public class PreferencesFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		ListPreference resolutionPref = (ListPreference) findPreference(getString(R.string.pref_key_preferred_res));
		resolutionPref.setEntries(VideoResolution.getAllVideoResolutionsNames());
		resolutionPref.setEntryValues(VideoResolution.getAllVideoResolutionsIds());

		// remove the 'use official player' checkbox if we are running an OSS version
		if (BuildConfig.FLAVOR.equals("oss")) {
			PreferenceCategory videoPlayerCategory = (PreferenceCategory) findPreference(getString(R.string.pref_key_video_player_category));
			Preference useOfficialPlayer = findPreference(getString(R.string.pref_key_use_offical_player));
			videoPlayerCategory.removePreference(useOfficialPlayer);
		}

		// set the app's version number
		Preference versionPref = findPreference(getString(R.string.pref_key_version));
		versionPref.setSummary(BuildConfig.VERSION_NAME);
	}
}
