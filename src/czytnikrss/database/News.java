package czytnikrss.database;

import android.database.Cursor;

public class News {
	private int _id;
	private String blogId;
	private String title;
	private String date;
	private String shortText;
	private String url;

	public News(int _id, String blogId, String title, String date,
			String content, String url) {
		super();
		this._id = _id;
		this.blogId = blogId;
		this.title = title;
		this.date = date;
		this.shortText = content;
		this.setUrl(url);
	}

	public News(Cursor cursor) {
		super();
		cursor.moveToFirst();
		this._id = cursor.getInt(cursor.getColumnIndex("_id"));
		this.blogId = cursor.getString(cursor.getColumnIndex("blogId"));
		this.title = cursor.getString(cursor.getColumnIndex("title"));
		this.date = cursor.getString(cursor.getColumnIndex("date"));
		this.shortText = cursor.getString(cursor.getColumnIndex("shortText"));
		this.url = cursor.getString(cursor.getColumnIndex("url"));
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getBlogId() {
		return blogId;
	}

	public void setBlogId(String blogId) {
		this.blogId = blogId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getShortText() {
		return shortText;
	}

	public void setShortext(String shortText) {
		this.shortText = shortText;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
