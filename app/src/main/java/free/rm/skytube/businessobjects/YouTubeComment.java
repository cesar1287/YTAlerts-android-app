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

package free.rm.skytube.businessobjects;

/**
 * A YouTube comment.
 */
public class YouTubeComment {

	private String author;
	private String comment;
	private String datePublished;
	private String likeCount;
	private String thumbnailUrl;

	public YouTubeComment(com.google.api.services.youtube.model.Comment comment) {
		if (comment.getSnippet() != null) {
			this.author = comment.getSnippet().getAuthorDisplayName();
			this.comment = comment.getSnippet().getTextDisplay();
			this.datePublished = new PrettyTimeEx().format(comment.getSnippet().getPublishedAt());
			this.likeCount = comment.getSnippet().getLikeCount().toString();
			this.thumbnailUrl = comment.getSnippet().getAuthorProfileImageUrl();
		}
	}

	public String getAuthor() {
		return author;
	}

	public String getComment() {
		return comment;
	}

	public String getDatePublished() {
		return datePublished;
	}

	public String getLikeCount() {
		return likeCount;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

}
