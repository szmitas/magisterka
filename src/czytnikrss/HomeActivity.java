package czytnikrss;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.czytnikrss.R;
import comunication.Article;
import comunication.Datagram;
import comunication.Method;
import comunication.Parameter;
import comunication.RssNews;
import comunication.SendToServer;

import czytnikrss.database.Channel;
import czytnikrss.database.UserNews;

public class HomeActivity extends FragmentActivity {

	public static int selectedItemId = 0;
	public static Activity activity;
	public static Typeface font;

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	ProgressDialog pleaseWaitDialog;
	Handler pleaseWaitHandler;
	Runnable pleaseWaitAction;

	public static String userHash = null;
	String toastText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ukrycie paska tytułu
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);

		userHash = getIntent().getExtras().getString("userHash");
		BlogsFragment.listChannels = LoginActivity.db.getAllChannels(userHash);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		activity = this;
		font = Typeface.createFromAsset(getAssets(), "Roboto-Condensed.ttf"); 
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setMessage("Na pewno chcesz wyjść?");
		adb.setTitle("Zamknij");
		adb.setIcon(android.R.drawable.ic_dialog_alert);
		adb.setPositiveButton("Tak", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				LoginActivity.db.close();
				HomeActivity.this.finish();
			}
		});
		adb.setNegativeButton("Nie", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialogQuit = adb.create();
		alertDialogQuit.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, 0, 0, "Odśwież").setIcon(android.R.drawable.ic_menu_rotate);
		menu.add(0, 1, 0, "Dodaj kanał")
				.setIcon(android.R.drawable.ic_menu_add);
//		menu.add(0, 2, 0, "Ustawienia konta").setIcon(
//				android.R.drawable.ic_menu_manage);
		menu.add(0, 3, 0, "Wyloguj się").setIcon(
				android.R.drawable.ic_menu_directions);
//		menu.add(0, 4, 0, "O programie").setIcon(
//				android.R.drawable.ic_menu_info_details);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0: // Odśwież
			getNews();
			break;
		case 1: // Dodaj kanał
			AlertDialog.Builder addChannelDialog = new AlertDialog.Builder(this);
			LayoutInflater factory = LayoutInflater.from(this);
			addChannelDialog.setTitle("Dodaj kanał");
			addChannelDialog.setIcon(android.R.drawable.ic_dialog_info);

			final View viewNewAccount = factory.inflate(R.layout.add_channel,
					null);
			addChannelDialog.setView(viewNewAccount);

			addChannelDialog.setPositiveButton("Dodaj kanał",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							TextView newChannelURL = (TextView) viewNewAccount
									.findViewById(R.id.newChannelURL);
							addNewChannel(prepareURL(newChannelURL.getText()
									.toString()));
						}
					});
			addChannelDialog.setNegativeButton("Anuluj",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alertDialogQuit = addChannelDialog.create();
			alertDialogQuit.show();
			break;
		case 2: // ustawienia konta
			break;
		case 3: // wyloguj się
			logout(userHash);
			break;
		}

		return true;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = new UnreadNewsFragment();
				break;
			case 1:
				fragment = new AllNewsFragment();
				break;
			case 2:
				fragment = new BlogsFragment();
				break;
			default:
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "NIEPRZECZYTANE";
			case 1:
				return "WSZYSTKIE";
			case 2:
				return "BLOGI";
			}
			return null;
		}
	}

	public static List<UserNews> getNews(String type) {
		if (type.equals("unread")) {
			return LoginActivity.db.getUnreadNews(userHash);
		} else {
			return LoginActivity.db.getAllNews(userHash);
		}
	}

	private void logout(final String userHash) {
		pleaseWaitDialog = ProgressDialog.show(HomeActivity.this, "",
				"Trwa wylogowywanie...", true);

		pleaseWaitHandler = new Handler();
		pleaseWaitAction = new Runnable() {
			public void run() {
				Toast.makeText(HomeActivity.this, toastText, Toast.LENGTH_LONG)
						.show();
			}
		};

		new Thread() {

			public void run() {
				Datagram receivedDatagram = SendToServer
						.logoutDatagram(userHash);
				if (receivedDatagram.getMethod().equals(Method.ERROR)) {
					pleaseWaitDialog.dismiss();
					toastText = receivedDatagram.getParameters()
							.get(Parameter.DATA).toString();
					pleaseWaitHandler.post(pleaseWaitAction);
				} else { // wylogowanie pomyślne
					LoginActivity.db.logout(userHash);
					LoginActivity.db.close();
					pleaseWaitDialog.dismiss();
					HomeActivity.this.finish();
				}
			}
		}.start();

	}

	private void addNewChannel(final String newChannelURL) {
		pleaseWaitDialog = ProgressDialog.show(HomeActivity.this, "",
				"Trwa dodawanie kanału...", true);

		pleaseWaitHandler = new Handler();
		pleaseWaitAction = new Runnable() {
			public void run() {
				Toast.makeText(HomeActivity.this, toastText, Toast.LENGTH_LONG)
						.show();
				BlogsFragment.listChannels = LoginActivity.db.getAllChannels(userHash);
			}
		};

		new Thread() {

			public void run() {
				if (newChannelURL.length() > 0) {
					Datagram receivedDatagram = SendToServer
							.addNewChannelDatagram(userHash, newChannelURL);
					if (receivedDatagram.getMethod().equals(Method.ERROR)) {
						pleaseWaitDialog.dismiss();
						toastText = receivedDatagram.getParameters()
								.get(Parameter.DATA).toString();
						pleaseWaitHandler.post(pleaseWaitAction);
					} else { // kanał dodany
						pleaseWaitDialog.dismiss();
						if (LoginActivity.db.insertIntoChannels(
								new Channel(receivedDatagram.getParameters()
										.get(Parameter.ID), receivedDatagram
										.getParameters().get(Parameter.DATA),
										newChannelURL), userHash) != -1) {
							toastText = "Kanał został dodany";
						} else {
							toastText = "Dodanie kanału niepowiodło się. Spróbuj ponownie.";
						}
						pleaseWaitHandler.post(pleaseWaitAction);
					}
				} else {
					pleaseWaitDialog.dismiss();
					toastText = "Podaj adres URL kanału";
					pleaseWaitHandler.post(pleaseWaitAction);
				}
			}
		}.start();
	}

	private void getNews() {
		pleaseWaitDialog = ProgressDialog.show(HomeActivity.this, "",
				"Trwa pobieranie zawartości kanałów...", true);

		pleaseWaitHandler = new Handler();
		pleaseWaitAction = new Runnable() {
			public void run() {
				Toast.makeText(HomeActivity.this, toastText, Toast.LENGTH_SHORT)
						.show();

				UnreadNewsFragment.unreadNews = getNews("unread");
				UnreadNewsFragment.initiate();
				AllNewsFragment.allNews = getNews("all");
				AllNewsFragment.initiate();
			}
		};

		new Thread() {

			public void run() {
				Datagram receivedDatagram = SendToServer
						.userNewsDatagram(userHash);
				if (receivedDatagram.getMethod().equals(Method.ERROR)) {
					pleaseWaitDialog.dismiss();
					toastText = receivedDatagram.getParameters()
							.get(Parameter.DATA).toString();
					pleaseWaitHandler.post(pleaseWaitAction);
				} else { // pobranie zakończone, dodanie wpisów do bazy
					RssNews newsList = RssNews.fromJson(receivedDatagram
							.getParameters().get(Parameter.DATA));
					if (newsList.list.size() == 0) {
						toastText = "Brak nowych wiadomości";
					} else {
						for (Article article : newsList.list) {
							LoginActivity.db.insertIntoNews(article);
							LoginActivity.db.insertIntoUserNews(
									LoginActivity.db.getUserLogin(userHash),
									article);
							toastText = "Miłej lektury";
						}
					}
					pleaseWaitHandler.post(pleaseWaitAction);
					pleaseWaitDialog.dismiss();
				}
			}
		}.start();
	}

	@SuppressLint("DefaultLocale")
	private String prepareURL(String url) {
		if (url.substring(0, 7).toLowerCase().equals("http://"))
			url = url.substring(0, 7);
		if (url.substring(0, 5).toLowerCase().equals("www."))
			url = url.substring(0, 5);
		return "http://" + url;
	}
}
