package czytnikrss.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "rssdatabase.db";
	private static final int DATABASE_VERSION = 1;

	private static final String CREATE_NEWS_TABLE = "create table news (_id integer primary key, blogId integer, title text, date text, shortText text, url text);";
	private static final String CREATE_USER_NEWS_TABLE = "create table userNews (login text, newsId integer, read integer, favorite integer);";
	private static final String CREATE_USERS_TABLE = "create table users (login text primary key, password text, autoLogin integer, hash text);";
	private static final String CREATE_CHANNELS_TABLE = "create table channels (cid integer, name text, url text, login text, deleted integer);";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// utworzenie tabeli z wiadomościami
		database.execSQL("DROP TABLE IF EXISTS news");
		database.execSQL(CREATE_NEWS_TABLE);
		database.execSQL("DROP TABLE IF EXISTS users");
		database.execSQL(CREATE_USERS_TABLE);
		database.execSQL("DROP TABLE IF EXISTS userNews");
		database.execSQL(CREATE_USER_NEWS_TABLE);
		database.execSQL("DROP TABLE IF EXISTS channels");
		database.execSQL(CREATE_CHANNELS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*
		 * Log.w(MySQLiteHelper.class.getName(),
		 * "Upgrading database from version " + oldVersion + " to " + newVersion
		 * + ", which will destroy all old data");
		 */
		// db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
		// onCreate(db);
	}
}
