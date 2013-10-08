package czytnikrss;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.example.czytnikrss.R;
import comunication.Datagram;
import comunication.Method;
import comunication.Parameter;
import comunication.SendToServer;

import czytnikrss.database.Channel;
import czytnikrss.other.BlogsAdapter;

public class BlogsFragment extends Fragment {

	ProgressDialog pleaseWaitDialog;
	Handler pleaseWaitHandler;
	Runnable pleaseWaitAction;

	ListView listViewBlogs;
	BlogsAdapter blogsAdapter;

	String toastText;

	static List<Channel> listChannels = new ArrayList<Channel>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		listChannels = LoginActivity.db.getAllChannels(HomeActivity.userHash);

		View view = inflater.inflate(R.layout.activity_news_list, container,
				false);

		listViewBlogs = (ListView) view.findViewById(R.id.listViewNewsList);

		blogsAdapter = new BlogsAdapter(getActivity(), listChannels);
		listViewBlogs.setAdapter(blogsAdapter);
		listViewBlogs.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parrent, View view,
					final int position, long id) {
				AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
				adb.setTitle("Rezygnacja z subskrybcji");
				adb.setMessage("Czy na pewno chcesz zrezygnować z subskrybcji bloga "
						+ listChannels.get(position).getName() + "?");
				adb.setPositiveButton("Tak, rezygnuję", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO rezygnacja z subskrybcji
						pleaseWaitDialog = ProgressDialog.show(getActivity(),
								"", "Rezygnacja z subskrybcji "
										+ listChannels.get(position).getName()
										+ "...", true);

						pleaseWaitHandler = new Handler();
						pleaseWaitAction = new Runnable() {
							public void run() {
								Toast.makeText(getActivity(), toastText,
										Toast.LENGTH_SHORT).show();
								blogsAdapter = new BlogsAdapter(getActivity(),
										listChannels);
								listViewBlogs.setAdapter(blogsAdapter);
								UnreadNewsFragment.unreadNews = HomeActivity
										.getNews("unread");
								UnreadNewsFragment.initiate();

								AllNewsFragment.allNews = HomeActivity
										.getNews("all");
								AllNewsFragment.initiate();
							}
						};

						new Thread() {

							public void run() {
								Datagram receivedDatagram = SendToServer
										.deleteChannelDatagram(
												HomeActivity.userHash,
												listChannels.get(position)
														.getCid());
								if (receivedDatagram.getMethod().equals(
										Method.ERROR)) {
									toastText = receivedDatagram
											.getParameters()
											.get(Parameter.DATA).toString();
									pleaseWaitDialog.dismiss();
									pleaseWaitHandler.post(pleaseWaitAction);
								} else { // usunięto subskrybcję
									if (LoginActivity.db
											.deleteFromChannels(listChannels
													.get(position).getCid(),
													HomeActivity.userHash) > 0) {
										// usunięto z bazy danych
										LoginActivity.db.deleteFromNews(
												HomeActivity.userHash,
												listChannels.get(position)
														.getCid());
										listChannels = LoginActivity.db
												.getAllChannels(HomeActivity.userHash);
										toastText = "Subskrybcja zakończona";
										pleaseWaitDialog.dismiss();
										pleaseWaitHandler
												.post(pleaseWaitAction);
									}
								}
							}
						}.start();
					}
				});
				adb.setNegativeButton("Nie", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				adb.show();
				return false;
			}
		});

		return view;
	}

	public static String getBlogName(String id) {
		for (Channel channel : listChannels) {
			if (channel.getCid().equals(id))
				return channel.getName();
		}
		return "NULL";
	}
}
