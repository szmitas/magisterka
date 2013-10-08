package czytnikrss.database;

import android.database.Cursor;

public class UserNews {
	private boolean read;
	private boolean favorite;
	private News news;

	public UserNews(boolean read, boolean favorite, News news) {
		super();
		this.read = read;
		this.favorite = favorite;
		this.setNews(news);
	}

	public UserNews(Cursor cursor, Cursor cursor2) {
		if (cursor.getString(cursor.getColumnIndex("read")).equals("1"))
			this.read = true;
		else
			this.read = false;
		if (cursor.getString(cursor.getColumnIndex("favorite")).equals("1"))
			this.favorite = true;
		else
			this.favorite = false;
		this.setNews(new News(cursor2));
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}
}
