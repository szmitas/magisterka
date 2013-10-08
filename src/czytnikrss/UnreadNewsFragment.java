package czytnikrss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.czytnikrss.R;

import czytnikrss.database.UserNews;
import czytnikrss.other.AllNewsAdapter;

public class UnreadNewsFragment extends Fragment {

	static List<UserNews> unreadNews = new ArrayList<UserNews>();
	private static int position = 0;

	private static TextView title;
	private static TextView description;
	private static TextView shortText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// pobranie nieprzeczytanych wiadomości z bazy
		unreadNews = HomeActivity.getNews("unread");

		View view = inflater.inflate(R.layout.layout_unread_news_item,
				container, false);
		title = (TextView) view.findViewById(R.id.title);
		title.setTypeface(HomeActivity.font);
		description = (TextView) view.findViewById(R.id.description);
		description.setTypeface(HomeActivity.font);
		shortText = (TextView) view.findViewById(R.id.shortText);
		shortText.setTypeface(HomeActivity.font);
		shortText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				readMore();
			}
		});

		initiate();

		view.setOnTouchListener((new OnTouchListener() {

			float historicX = Float.NaN;
			float historicY = Float.NaN;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					historicX = event.getX();
					historicY = event.getY();
					return true;
				}
				if (event.getAction() == MotionEvent.ACTION_CANCEL) {
					if (historicX < event.getX()
							&& (event.getX() - historicX > 30)) {
						markAsRead();
					}
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (historicY < event.getY()
							&& (event.getY() - historicY > 150)) {
						if (position > 0) {
							position--;
							showNews();
						}
					} else if (historicY > event.getY()
							&& (event.getY() - historicY < 150)) {
						if (position < unreadNews.size() - 1) {
							position++;
							showNews();
						} else {
							if (unreadNews.size() != 0)
								Toast.makeText(getActivity(),
										"To już ostatnia wiadomość",
										Toast.LENGTH_SHORT).show();
						}
					}
					return true;
				}
				return false;
			}
		}));

		return view;
	}

	public static void showNews() {
		title.setText(unreadNews.get(position).getNews().getTitle());
		description
				.setText(BlogsFragment.getBlogName(unreadNews.get(position)
						.getNews().getBlogId())
						+ ", "
						+ userFriendlyDate(unreadNews.get(position).getNews()
								.getDate()));
		if (unreadNews.get(position).getNews().getShortText().length() > 200)
			shortText.setText(unreadNews.get(position).getNews().getShortText()
					.substring(0, 200)
					+ "...");
		else
			shortText.setText(unreadNews.get(position).getNews().getShortText()
					+ "...");
		if (shortText.getVisibility() == View.GONE)
			shortText.setVisibility(View.VISIBLE);
	}

	private void readMore() {
		Intent intent = new Intent(getActivity(), NewsActivity.class);
		intent.putExtra("position", position);
		intent.putExtra("type", "unread");
		startActivity(intent);
	}

	private void markAsRead() {
		int unreadNewsCount = unreadNews.size();

		if (unreadNewsCount == 0) {
		} else if (unreadNewsCount == 1) {
			if (LoginActivity.db.MarkNewsAs(unreadNews.get(position).getNews()
					.get_id(), true, HomeActivity.userHash)) {
				unreadNews.remove(position);
				noMoreNews();
			}
		} else if (position == unreadNewsCount - 1) {
			// oznaczenie ostatniej wiadomości na liście
			if (LoginActivity.db.MarkNewsAs(unreadNews.get(position).getNews()
					.get_id(), true, HomeActivity.userHash)) {
				unreadNews.remove(position);
				position--;
				showNews();
			}
		} else {
			if (LoginActivity.db.MarkNewsAs(unreadNews.get(position).getNews()
					.get_id(), true, HomeActivity.userHash)) {
				unreadNews.remove(position);
				showNews();
			}
		}
		AllNewsFragment.allNews = HomeActivity.getNews("all");
		AllNewsFragment.adapterAllNews = new AllNewsAdapter(getActivity(),
				AllNewsFragment.allNews);
		AllNewsFragment.listViewAllNews
				.setAdapter(AllNewsFragment.adapterAllNews);
	}

	private static void noMoreNews() {
		title.setText("Brak nowych wiadomości");
		description.setText("");
		shortText.setText("");
		shortText.setVisibility(View.GONE);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		position = info.position;

		menu.setHeaderTitle(unreadNews.get(position).getNews().getTitle());
		HomeActivity.selectedItemId = unreadNews.get(position).getNews()
				.get_id();

		menu.add(0, v.getId(), 0, "Oznacz jako przeczytana");
		menu.add(0, v.getId(), 2, "Skopiuj link");
		menu.add(0, v.getId(), 3, "Otwórz link");
	}

	public static void initiate() {
		if (unreadNews.size() == 0) {
			noMoreNews();
		} else {
			showNews();
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static String userFriendlyDate(String publishDate) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = dateFormat.format(new Date());
		int[] diff = {
				Integer.valueOf(currentDate.substring(0, 4))
						- Integer.valueOf(publishDate.substring(0, 4)),
				Integer.valueOf(currentDate.substring(5, 7))
						- Integer.valueOf(publishDate.substring(5, 7)),
				Integer.valueOf(currentDate.substring(8, 10))
						- Integer.valueOf(publishDate.substring(8, 10)),
				Integer.valueOf(currentDate.substring(11, 13))
						- Integer.valueOf(publishDate.substring(11, 13)),
				Integer.valueOf(currentDate.substring(14, 16))
						- Integer.valueOf(publishDate.substring(15, 16)) };

		if (diff[0] == 0) {
			if (diff[1] == 0 || (Math.abs(diff[1]) == 1)) {
				if (diff[2] == 0) {
					if (diff[3] == 0) {
						return (diff[4]) + " min. temu";
					} else
						return diff[3] + " godz. temu";
				} else if (diff[2] == 1 || Math.abs(diff[2]) - 30 == 1)
					return "wczoraj, " + publishDate.substring(11, 16);
				else if ((diff[2] > 1 && diff[2] <= 7))
					return diff[2] + " dni temu, "
							+ publishDate.substring(11, 16);
				else if (Math.abs(Math.abs(diff[2]) - 30) > 1
						&& Math.abs(Math.abs(diff[2]) - 30) <= 7)
					return Math.abs(Math.abs(diff[2]) - 30) + " dni temu, ";
				else if (Math.abs(Math.abs(diff[2]) - 30) > 7)
					return Math.abs(Math.abs(diff[2]) - 30) / 7
							+ " tyg. temu, " + publishDate.substring(11, 16);
			} else {
				return diff[1] + " mies. temu, "
						+ publishDate.substring(11, 16);
			}
		} else {
			return diff[0] + " rok temu";
		}
		return publishDate;
	}
}
