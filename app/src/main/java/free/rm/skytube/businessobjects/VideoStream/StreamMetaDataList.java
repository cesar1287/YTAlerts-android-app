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

package free.rm.skytube.businessobjects.VideoStream;

import android.util.Log;

import java.util.ArrayList;

import free.rm.skytube.R;
import free.rm.skytube.gui.app.SkyTubeApp;

/**
 * A list of {@link StreamMetaData}.
 */
public class StreamMetaDataList extends ArrayList<StreamMetaData> {

	private String errorMessage = null;

	private static final String TAG = StreamMetaDataList.class.getSimpleName();


	public StreamMetaDataList() {
	}


	public StreamMetaDataList(String errorMessage) {
		this.errorMessage = errorMessage;
	}



	/**
	 * Returns the stream desired by the user (if possible).  The desired stream is defined in the
	 * app preferences.  If the video does NOT contain the desired stream, then it tries to return
	 * a stream with a slighter lower resolution.
	 *
	 * @return The desired {@link StreamMetaData}.
	 */
	public StreamMetaData getDesiredStream() {
		VideoResolution desiredVideoRes = getDesiredVideoResolution();
		Log.d(TAG, "Desired Video Res:  " + desiredVideoRes);
		return getDesiredStream(desiredVideoRes);
	}



	/**
	 * Gets the desired stream recursively.
	 *
	 * @param desiredVideoRes	The desired video resolution (as defined in the app preferences).
	 *
	 * @return The desired {@link StreamMetaData}.
	 */
	private StreamMetaData getDesiredStream(VideoResolution desiredVideoRes) {
		if (desiredVideoRes == VideoResolution.RES_UNKNOWN) {
			Log.w(TAG, "No video with the following res could be found: " + desiredVideoRes);
			return get(0);
		}

		for (StreamMetaData streamMetaData : this) {
			if (streamMetaData.getResolution() == desiredVideoRes)
				return streamMetaData;
		}

		return getDesiredStream(desiredVideoRes.getLowerVideoResolution());
	}



	/**
	 * Gets the desired video resolution as defined by the user in the app preferences.
	 *
	 * @return Desired {@link VideoResolution}.
	 */
	private VideoResolution getDesiredVideoResolution() {
		String resIdValue = SkyTubeApp.getPreferenceManager()
							.getString(SkyTubeApp.getStr(R.string.pref_key_preferred_res),
										Integer.toString(VideoResolution.DEFAULT_VIDEO_RES_ID));

		return VideoResolution.videoResIdToVideoResolution(resIdValue);
	}



	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();

		for (StreamMetaData streamMetaData : this) {
			out.append(streamMetaData.toString());
			out.append('\n');
		}

		return out.toString();
	}


	public String getErrorMessage() {
		return errorMessage;
	}

}
