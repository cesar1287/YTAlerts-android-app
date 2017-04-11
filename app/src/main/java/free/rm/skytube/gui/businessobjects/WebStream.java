/*
 * SkyTube
 * Copyright (C) 2016  Ramon Mifsud
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

package free.rm.skytube.gui.businessobjects;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Given a {@link URL}, it will extract the stream ({@link InputStream} and the stream size.
 */
public class WebStream {

	/** Stream of the remote file */
	private InputStream stream = null;
	/** Stream size in bytes */
	private	int streamSize = 0;

	private static final String TAG = WebStream.class.getSimpleName();


	public WebStream(URL remoteFileUrl) throws IOException {
		HttpURLConnection urlConnection = (HttpURLConnection) remoteFileUrl.openConnection();
		int					responseCode = urlConnection.getResponseCode();

		if (responseCode < 0) {
			Log.e(TAG, "Cannot establish connection with the update server.  Response code = " + responseCode);
		} else {
			this.stream = urlConnection.getInputStream();
			this.streamSize = urlConnection.getContentLength();
		}
	}


	public WebStream(String remoteFileUrl) throws Exception {
		this(new URL(remoteFileUrl));
	}

	public InputStream getStream() {
		return stream;
	}

	public int getStreamSize() {
		return streamSize;
	}

}