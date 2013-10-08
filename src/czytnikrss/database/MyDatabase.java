package czytnikrss.database;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import comunication.Article;
import comunication.TopChannel;
import comunication.TopChannels;

public class MyDatabase {

	private DatabaseHelper dbHelper;
	private SQLiteDatabase database;
	private Cursor cursor;
	private Cursor cursor2;
	private ContentValues values;

	private String NEWS = "news";
	private String NEWS_ID = "_id";
	private String NEWS_BLOGID = "blogId";
	private String NEWS_TITLE = "title";
	private String NEWS_DATE = "date";
	private String NEWS_SHORTTEXT = "shortText";
	private String NEWS_URL = "url";
	String[] NEWS_ALL_COLUMNS = new String[] { NEWS_ID, NEWS_BLOGID,
			NEWS_TITLE, NEWS_DATE, NEWS_SHORTTEXT, NEWS_URL };

	private String USERNEWS = "userNews";
	private String USERNEWS_LOGIN = "login";
	private String USERNEWS_NEWSID = "newsId";
	private String USERNEWS_READ = "read";
	private String USERNEWS_FAVORITE = "favorite";
	String[] USERNEWS_ALL_COLUMNS = new String[] { USERNEWS_LOGIN,
			USERNEWS_NEWSID, USERNEWS_READ, USERNEWS_FAVORITE };

	private String USERS = "users";
	private String USERS_LOGIN = "login";
	private String USERS_PASSWORD = "password";
	private String USERS_AUTOLOGIN = "autologin";
	private String USERS_HASH = "hash";
	String[] USERS_ALL_COLUMNS = new String[] { USERS_LOGIN, USERS_PASSWORD,
			USERS_AUTOLOGIN, USERS_HASH };

	private String CHANNELS = "channels";
	private String CHANNELS_ID = "cid";
	private String CHANNELS_NAME = "name";
	private String CHANNELS_URL = "url";
	private String CHANNELS_LOGIN = "login";
	private String CHANNELS_DELETED = "deleted";
	String[] CHANNELS_ALL_COLUMNS = new String[] { CHANNELS_ID, CHANNELS_NAME,
			CHANNELS_URL, CHANNELS_LOGIN, CHANNELS_DELETED };

	public MyDatabase(Context context) {
		super();
		dbHelper = new DatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		//dbHelper.onCreate(database);
	}

	public void close() {
		database.close();
	}

	public long insertIntoUsers(User user) {
		values = new ContentValues();
		cursor = database.query(USERS, USERS_ALL_COLUMNS, USERS_LOGIN + " = '"
				+ user.getLogin() + "'", null, null, null, null);
		if (cursor.getCount() == 1) {
			// użytkownik jest już w bazie
			cursor.close();
			values.put(USERS_HASH, user.getHash());
			values.put(USERS_AUTOLOGIN, user.getAutologin());
			return database.update(USERS, values,
					USERS_LOGIN + " = '" + user.getLogin() + "'", null);
		} else {
			// użytkownik nie jest w bazie danych
			cursor.close();
			values.put(USERS_LOGIN, user.getLogin());
			values.put(USERS_PASSWORD, user.getPassword());
			values.put(USERS_AUTOLOGIN, user.getAutologin());
			values.put(USERS_HASH, user.getHash());
			return database.insert(USERS, null, values);
		}
	}

	public boolean autologin() {
		cursor = database.query(USERS, USERS_ALL_COLUMNS, USERS_AUTOLOGIN
				+ " = 1", null, null, null, null);
		if (cursor.getCount() == 1) {
			// istnieje użytkownik, który loguje się automatycznie
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}

	public boolean login(String login, String password) {
		cursor = database.query(USERS, USERS_ALL_COLUMNS, USERS_LOGIN + " = '"
				+ login + "' AND " + USERS_PASSWORD + " ='" + password + "'",
				null, null, null, null);
		if (cursor.getCount() == 1) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}

	public String getUserHash(String login, String password) {
		String userHash = null;
		cursor = database.query(USERS, USERS_ALL_COLUMNS, USERS_LOGIN + " = '"
				+ login + "' AND " + USERS_PASSWORD + " ='" + password + "'",
				null, null, null, null);
		cursor.moveToFirst();
		if (cursor.getCount() == 1) {
			userHash = cursor.getString(cursor.getColumnIndex(USERS_HASH));
		}
		cursor.close();
		return userHash;
	}

	public String getUserLogin(String userHash) {
		String userLogin = null;
		cursor = database.query(USERS, USERS_ALL_COLUMNS, USERS_HASH + " = '"
				+ userHash + "'", null, null, null, null);
		cursor.moveToFirst();
		if (cursor.getCount() == 1) {
			userLogin = cursor.getString(cursor.getColumnIndex(USERS_LOGIN));
		}
		cursor.close();
		return userLogin;
	}

	public String[] getUserData(String type) {
		String[] userData = null;
		if (type.equals("autologin")) {
			cursor = database.query(USERS, USERS_ALL_COLUMNS, USERS_AUTOLOGIN
					+ " = 1", null, null, null, null);
			cursor.moveToFirst();
			userData = new String[] { cursor.getString(0), cursor.getString(1) };
		}
		cursor.close();
		return userData;
	}

	/**
	 * Dodanie nowego kanału do bazy danych
	 * 
	 * @param channel
	 */
	public long insertIntoChannels(Channel channel, String userHash) {
		values = new ContentValues();
		values.put(CHANNELS_ID, channel.getCid());
		values.put(CHANNELS_NAME, channel.getName());
		values.put(CHANNELS_URL, channel.getUrl());
		values.put(CHANNELS_LOGIN, getUserLogin(userHash));
		values.put(CHANNELS_DELETED, "0");
		return database.insert(CHANNELS, null, values);
	}

	public long deleteFromChannels(String cid, String userHash) {
		return database.delete(CHANNELS, CHANNELS_ID + " = " + cid + " AND "
				+ CHANNELS_LOGIN + " = '" + getUserLogin(userHash) + "'", null);
	}

	/**
	 * Aktualizuje listę kanałów subskrybowanych przez użytkownika na podstawie
	 * danych z serwera
	 * 
	 * @param channelsString
	 */
	public void updateChannels(String channelsString, String userHash) {
		TopChannels channels = TopChannels.fromJson(channelsString);
		String userLogin = getUserLogin(userHash);

		// usunięcie wszystkich subskrybcji
		values = new ContentValues();
		values.put(CHANNELS_DELETED, "1");
		database.update(CHANNELS, values, CHANNELS_LOGIN + " ='" + userLogin
				+ "'", null);

		for (TopChannel c : channels.list) {
			cursor = database.query(CHANNELS, CHANNELS_ALL_COLUMNS, CHANNELS_ID
					+ " = " + c.getCid() + " AND " + CHANNELS_LOGIN + " = '"
					+ userLogin + "'", null, null, null, null);
			if (cursor.getCount() == 0) {
				values = new ContentValues();
				values.put(CHANNELS_ID, c.getCid());
				values.put(CHANNELS_NAME, c.getName());
				values.put(CHANNELS_URL, c.getUrl());
				values.put(CHANNELS_LOGIN, userLogin);
				values.put(CHANNELS_DELETED, "0");
				database.insertOrThrow(CHANNELS, null, values);
			} else if (cursor.getCount() == 1) {
				// ponowna aktywacja subskrybcji
				values = new ContentValues();
				values.put(CHANNELS_DELETED, "0");
				database.update(CHANNELS, values,
						CHANNELS_ID + " = " + c.getCid() + " AND "
								+ CHANNELS_LOGIN + " ='" + userLogin + "'",
						null);
			}
		}
		cursor.close();
	}

	/**
	 * Pobiera listę kanałów subskrybowanych przez użytkownika z bazy
	 * 
	 * @param userHash
	 * @return
	 */
	public List<Channel> getAllChannels(String userHash) {
		cursor = database.query(USERS, USERS_ALL_COLUMNS, USERS_HASH + " = '"
				+ userHash + "'", null, null, null, null);
		cursor.moveToFirst();

		String login = cursor.getString(cursor.getColumnIndex(USERS_LOGIN));

		cursor = database.query(CHANNELS, CHANNELS_ALL_COLUMNS, CHANNELS_LOGIN
				+ " = '" + login + "' AND " + CHANNELS_DELETED + " = 0", null,
				null, null, null);
		cursor.moveToFirst();

		List<Channel> channelsList = new ArrayList<Channel>();

		while (!cursor.isAfterLast()) {
			channelsList.add(new Channel(cursor.getString(cursor
					.getColumnIndex(CHANNELS_ID)), cursor.getString(cursor
					.getColumnIndex(CHANNELS_NAME)), cursor.getString(cursor
					.getColumnIndex(CHANNELS_URL)), cursor.getString(cursor
					.getColumnIndex(CHANNELS_LOGIN))));
			cursor.moveToNext();
		}
		cursor.close();
		return channelsList;
	}

	@SuppressLint("SimpleDateFormat")
	public long insertIntoNews(Article article) {
		values = new ContentValues();
		values.put(NEWS_ID, article.getAid());
		values.put(NEWS_BLOGID, article.getCid());
		values.put(NEWS_TITLE, article.getTitle());
		values.put(NEWS_DATE, article.getDate());
		values.put(NEWS_SHORTTEXT, article.getDescripton());
		values.put(NEWS_URL, article.getUrl());
		return database.insert(NEWS, null, values);
	}

	public long insertIntoUserNews(String login, Article article) {
		values = new ContentValues();
		values.put(USERNEWS_LOGIN, login);
		values.put(USERNEWS_NEWSID, article.getAid());
		values.put(USERNEWS_FAVORITE, "0");
		values.put(USERNEWS_READ, "0");
		return database.insert(USERNEWS, null, values);
	}

	public long deleteFromNews(String userHash, String channelId) {
		String userLogin = getUserLogin(userHash);
		return database.delete(USERNEWS,
				USERNEWS_LOGIN + " = '" + userLogin + "' AND "
						+ USERNEWS_NEWSID
						+ " IN (SELECT _id FROM news WHERE blogId = "
						+ channelId + ")", null);

	}

	public List<UserNews> getAllNews(String userHash) {
		String userLogin = getUserLogin(userHash);
		List<UserNews> news = new ArrayList<UserNews>();

		cursor = database.query(USERNEWS, USERNEWS_ALL_COLUMNS, USERNEWS_LOGIN
				+ " = '" + userLogin + "'", null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			cursor2 = database.query(NEWS, NEWS_ALL_COLUMNS, NEWS_ID + " = "
					+ cursor.getString(cursor.getColumnIndex(USERNEWS_NEWSID)),
					null, null, null, null);
			news.add(new UserNews(cursor, cursor2));
			cursor.moveToNext();
		}
		cursor.close();
		if (cursor2 != null)
			cursor2.close();
		return sort(news);
	}

	public List<UserNews> getUnreadNews(String userHash) {
		String userLogin = getUserLogin(userHash);
		List<UserNews> news = new ArrayList<UserNews>();

		cursor = database.query(USERNEWS, USERNEWS_ALL_COLUMNS, USERNEWS_READ
				+ " = 0 AND " + USERNEWS_LOGIN + " = '" + userLogin + "'",
				null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			cursor2 = database.query(NEWS, NEWS_ALL_COLUMNS, NEWS_ID + " = "
					+ cursor.getString(cursor.getColumnIndex(USERNEWS_NEWSID)),
					null, null, null, null);
			news.add(new UserNews(cursor, cursor2));
			cursor.moveToNext();
		}
		cursor.close();
		if (cursor2 != null)
			cursor2.close();
		return sort(news);
	}

	public boolean MarkNewsAs(int id, boolean read, String userHash) {
		String userLogin = getUserLogin(userHash);
		ContentValues values = new ContentValues();
		if (read)
			values.put("read", 1);
		else
			values.put("read", 0);

		if (database.update(USERNEWS, values, USERNEWS_NEWSID + " = " + id
				+ " AND " + USERNEWS_LOGIN + " = '" + userLogin + "'", null) > 0)
			return true;
		else
			return false;
	}

	public List<UserNews> sort(List<UserNews> news) {
		List<UserNews> sortedNews = new ArrayList<UserNews>();

		while (news.size() > 0) {
			UserNews first = news.get(0);
			for (UserNews userNews : news) {
				if (first.getNews().getDate()
						.compareTo(userNews.getNews().getDate()) < 0) {
					first = userNews;
				}
			}
			sortedNews.add(first);
			news.remove(first);
		}
		return sortedNews;
	}

	public void logout(String userHash) {
		ContentValues values = new ContentValues();
		values.put(USERS_AUTOLOGIN, "0");
		database.update(USERS, values, USERS_HASH + " = '" + userHash + "'",
				null);

	}
}
