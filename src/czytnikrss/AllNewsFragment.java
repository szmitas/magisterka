package czytnikrss;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.czytnikrss.R;

import czytnikrss.database.UserNews;
import czytnikrss.other.AllNewsAdapter;

public class AllNewsFragment extends Fragment {

	static List<UserNews> allNews = new ArrayList<UserNews>();
	int position;

	static ListView listViewAllNews; // przewijana lista
	static AllNewsAdapter adapterAllNews; // adapter listy

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_news_list, container,
				false);
		// pobranie nieprzeczytanych wiadomości z bazy
		allNews = HomeActivity.getNews("all");
		adapterAllNews = new AllNewsAdapter(getActivity(), allNews);

		listViewAllNews = (ListView) view.findViewById(R.id.listViewNewsList);
		listViewAllNews.setAdapter(adapterAllNews);
		listViewAllNews.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parrent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(), NewsActivity.class);
				intent.putExtra("position", position);
				intent.putExtra("type", "all");
				startActivity(intent);
			}
		});
		// przypisanie menu kontekstowego do listy
		registerForContextMenu(listViewAllNews);
		return view;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		position = info.position;

		menu.setHeaderTitle(allNews.get(position).getNews().getTitle());
		// zapisanie ID wybranej wiadomości
		HomeActivity.selectedItemId = allNews.get(position).getNews().get_id();

		if (allNews.get(position).isRead())
			menu.add(0, v.getId(), 0, "Oznacz jako nieprzeczytana");
		else
			menu.add(0, v.getId(), 0, "Oznacz jako przeczytana");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getOrder()) {
		case 0: // oznacz jako przeczytana/nieprzeczytana
			LoginActivity.db.MarkNewsAs(allNews.get(position).getNews()
					.get_id(), !allNews.get(position).isRead(),
					HomeActivity.userHash);
			allNews = HomeActivity.getNews("all");
			UnreadNewsFragment.unreadNews = HomeActivity.getNews("unread");
			UnreadNewsFragment.initiate();
			break;
		case 1: // dodaj/usuń z ulubionych
			break;
		}
		adapterAllNews = new AllNewsAdapter(getActivity(), allNews);
		listViewAllNews.setAdapter(adapterAllNews);
		listViewAllNews.invalidateViews();
		return true;
	}

	public static void initiate() {
		adapterAllNews = new AllNewsAdapter(HomeActivity.activity, allNews);
		listViewAllNews.setAdapter(adapterAllNews);
	}
}
