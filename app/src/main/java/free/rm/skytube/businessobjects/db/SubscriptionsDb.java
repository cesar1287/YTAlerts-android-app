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

package free.rm.skytube.businessobjects.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import free.rm.skytube.businessobjects.YouTubeChannel;
import free.rm.skytube.businessobjects.YouTubeVideo;
import free.rm.skytube.gui.app.SkyTubeApp;

/**
 * A database (DB) that stores user subscriptions (with respect to YouTube channels).
 */
public class SubscriptionsDb extends SQLiteOpenHelper {

	private static volatile SubscriptionsDb subscriptionsDb = null;

	private static final int DATABASE_VERSION = 2;
	private static final String DATABASE_NAME = "subs.db";


	private SubscriptionsDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	public static synchronized SubscriptionsDb getSubscriptionsDb() {
		if (subscriptionsDb == null) {
			subscriptionsDb = new SubscriptionsDb(SkyTubeApp.getContext());
		}

		return subscriptionsDb;
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SubscriptionsTable.getCreateStatement());
		db.execSQL(SubscriptionsVideosTable.getCreateStatement());
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Version 2 of the database introduces the SubscriptionsVideosTable, which stores videos found in each subscribed channel
		if(oldVersion == 1 && newVersion == 2) {
			db.execSQL(SubscriptionsVideosTable.getCreateStatement());
		}
	}


	/**
	 * Saves the given channel into the subscriptions DB.
	 *
	 * @param channel Channel the user wants to subscribe to.
	 *
	 * @return True if the operation was successful; false otherwise.
	 */
	public boolean subscribe(YouTubeChannel channel) {
		ContentValues values = new ContentValues();
		values.put(SubscriptionsTable.COL_CHANNEL_ID, channel.getId());
		values.put(SubscriptionsTable.COL_LAST_VISIT_TIME, System.currentTimeMillis());

		saveChannelVideos(channel);
		return getWritableDatabase().insert(SubscriptionsTable.TABLE_NAME, null, values) != -1;
	}


	/**
	 * Removes the given channel from the subscriptions DB.
	 *
	 * @param channel Channel the user wants to unsubscribe to.
	 *
	 * @return True if the operation was successful; false otherwise.
	 */
	public boolean unsubscribe(YouTubeChannel channel) {
		getWritableDatabase().delete(SubscriptionsVideosTable.TABLE_NAME,
						SubscriptionsVideosTable.COL_CHANNEL_ID + " = ?",
						new String[]{channel.getId()});

		int rowsDeleted = getWritableDatabase().delete(SubscriptionsTable.TABLE_NAME,
				SubscriptionsTable.COL_CHANNEL_ID + " = ?",
				new String[]{channel.getId()});

		return (rowsDeleted >= 0);
	}


	/**
	 * @return A list of channels that the user subscribed to.
	 *
	 * @throws IOException
	 */
	public List<YouTubeChannel> getSubscribedChannels() throws IOException {
		return getSubscribedChannels(true);
	}


	/**
	 * Returns A list of channels that the user subscribed to.
	 *
	 * @param shouldCheckForNewVideos  If true it will check if the channel has new videos since last channel visit.
	 *
	 * @return A list of channels that the user subscribed to.
	 * @throws IOException
	 */
	public List<YouTubeChannel> getSubscribedChannels(boolean shouldCheckForNewVideos) throws IOException {
		ArrayList<YouTubeChannel> subsChannels = new ArrayList<>();
		Cursor cursor = getReadableDatabase().query(SubscriptionsTable.TABLE_NAME, new String[]{SubscriptionsTable.COL_CHANNEL_ID}, null, null, null, null, SubscriptionsTable.COL_ID + " ASC");

		if (cursor.moveToNext()) {
			int colChannelIdNum = cursor.getColumnIndexOrThrow(SubscriptionsTable.COL_CHANNEL_ID);
			String channelId = null;
			YouTubeChannel channel = null;

			do {
				channelId = cursor.getString(colChannelIdNum);
				channel = new YouTubeChannel();
				channel.init(channelId, true /* = user is subscribed to this channel*/, shouldCheckForNewVideos);
				subsChannels.add(channel);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return subsChannels;
	}


	/**
	 * @return The total number of subscribed channels.
	 */
	public int getTotalSubscribedChannels() {
		String	query  = String.format("SELECT COUNT(*) FROM %s", SubscriptionsTable.TABLE_NAME);
		Cursor	cursor = SubscriptionsDb.getSubscriptionsDb().getReadableDatabase().rawQuery(query, null);
		int		totalSubbedChannels = 0;

		if (cursor.moveToFirst())
			totalSubbedChannels = cursor.getInt(0);

		cursor.close();
		return totalSubbedChannels;
	}


	/**
	 * Checks if the user is subscribed to the given channel.
	 *
	 * @param channelId	Channel ID
	 * @return True if the user is subscribed; false otherwise.
	 * @throws IOException
	 */
	public boolean isUserSubscribedToChannel(String channelId) throws IOException {
		Cursor cursor = getReadableDatabase().query(
				SubscriptionsTable.TABLE_NAME,
				new String[]{SubscriptionsTable.COL_ID},
				SubscriptionsTable.COL_CHANNEL_ID + " = ?",
				new String[]{channelId}, null, null, null);
		boolean	isUserSubbed = cursor.moveToNext();

		cursor.close();
		return isUserSubbed;
	}


	/**
	 * Updates the given channel's last visit time.
	 *
	 * @param channelId	Channel ID
	 *
	 * @return	last visit time, if the update was successful;  -1 otherwise.
	 */
	public long updateLastVisitTime(String channelId) {
		SQLiteDatabase	db = getWritableDatabase();
		long			currentTime = System.currentTimeMillis();

		ContentValues values = new ContentValues();
		values.put(SubscriptionsTable.COL_LAST_VISIT_TIME, currentTime);

		int count = db.update(
				SubscriptionsTable.TABLE_NAME,
				values,
				SubscriptionsTable.COL_CHANNEL_ID + " = ?",
				new String[]{channelId});

		return (count > 0 ? currentTime : -1);
	}


	/**
	 * Returns the last time the user has visited this channel.
	 *
	 * @param channel
	 *
	 * @return	last visit time, if the update was successful;  -1 otherwise.
	 * @throws IOException
	 */
	public long getLastVisitTime(YouTubeChannel channel) {
		Cursor	cursor = getReadableDatabase().query(
							SubscriptionsTable.TABLE_NAME,
							new String[]{SubscriptionsTable.COL_LAST_VISIT_TIME},
							SubscriptionsTable.COL_CHANNEL_ID + " = ?",
							new String[]{channel.getId()}, null, null, null);
		long	lastVisitTime = -1;

		if (cursor.moveToNext()) {
			int colLastVisitTIme = cursor.getColumnIndexOrThrow(SubscriptionsTable.COL_LAST_VISIT_TIME);
			lastVisitTime = cursor.getLong(colLastVisitTIme);
		}

		cursor.close();
		return lastVisitTime;
	}


	private boolean hasVideo(YouTubeVideo video) {
		String query = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", SubscriptionsVideosTable.TABLE_NAME, SubscriptionsVideosTable.COL_YOUTUBE_VIDEO_ID);
		Cursor cursor = SubscriptionsDb.getSubscriptionsDb().getReadableDatabase().rawQuery(query, new String[]{video.getId()});
		if(cursor.moveToFirst()) {
			return cursor.getInt(0) > 0;
		}
		return false;
	}


	/**
	 * Check if the given channel has new videos (by looking into the {@link SubscriptionsVideosTable}
	 * [i.e. video cache table]).
	 *
	 * @param channel Channel to check.
	 *
	 * @return True if the user hasn't visited the channel and new videos have been uploaded in the
	 * meantime; false otherwise.
	 */
	public boolean channelHasNewVideos(YouTubeChannel channel) {
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		String query = String.format("SELECT COUNT(*) FROM %s WHERE %s = ? AND %s > ?", SubscriptionsVideosTable.TABLE_NAME, SubscriptionsVideosTable.COL_CHANNEL_ID, SubscriptionsVideosTable.COL_YOUTUBE_VIDEO_DATE);
		Cursor cursor = SubscriptionsDb.getSubscriptionsDb().getReadableDatabase().rawQuery(
							query,
							new String[]{channel.getId(), fmt.parseDateTime(new DateTime(new Date(channel.getLastVisitTime())).toString()).toString()});
		boolean channelHasNewVideos = false;

		if (cursor.moveToFirst())
			channelHasNewVideos = cursor.getInt(0) > 0;

		cursor.close();
		return channelHasNewVideos;
	}

	/**
	 * Loop through each video saved in the passed {@link YouTubeChannel} and save it into the database, if it's not already been saved
	 * @param channel
	 */
	public void saveChannelVideos(YouTubeChannel channel) {
		Gson gson = new Gson();
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		for (YouTubeVideo video : channel.getYouTubeVideos()) {
			if(!hasVideo(video)) {
				ContentValues values = new ContentValues();
				values.put(SubscriptionsVideosTable.COL_CHANNEL_ID, channel.getId());
				values.put(SubscriptionsVideosTable.COL_YOUTUBE_VIDEO_ID, video.getId());
				values.put(SubscriptionsVideosTable.COL_YOUTUBE_VIDEO, gson.toJson(video).getBytes());
				values.put(SubscriptionsVideosTable.COL_YOUTUBE_VIDEO_DATE, fmt.parseDateTime(video.getPublishDate().toStringRfc3339()).toString());

				getWritableDatabase().insert(SubscriptionsVideosTable.TABLE_NAME, null, values);
			}
		}
	}


	public boolean trimSubscriptionVideos() {
		int result = getWritableDatabase().delete(SubscriptionsVideosTable.TABLE_NAME, String.format("%s < DATETIME('now', '-1 month')", SubscriptionsVideosTable.COL_YOUTUBE_VIDEO_DATE), null);
		return result > 0;
	}


	public List<YouTubeVideo> getSubscriptionVideos() {
		Cursor	cursor = getReadableDatabase().query(
							SubscriptionsVideosTable.TABLE_NAME,
							new String[]{SubscriptionsVideosTable.COL_YOUTUBE_VIDEO},
							null, null, null, null,
							SubscriptionsVideosTable.COL_YOUTUBE_VIDEO_DATE + " DESC");
		List<YouTubeVideo> videos = new ArrayList<>();

		if (cursor.moveToNext()) {
			do {
				byte[] blob = cursor.getBlob(cursor.getColumnIndex(SubscriptionsVideosTable.COL_YOUTUBE_VIDEO));
				YouTubeVideo video = new Gson().fromJson(new String(blob), new TypeToken<YouTubeVideo>(){}.getType());
				videos.add(video);
			} while(cursor.moveToNext());
		}

		cursor.close();
		return videos;
	}

}
