package czytnikrss.other;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.czytnikrss.R;

import czytnikrss.BlogsFragment;
import czytnikrss.UnreadNewsFragment;
import czytnikrss.database.UserNews;

public class AllNewsAdapter extends BaseAdapter {

	private List<UserNews> data = new ArrayList<UserNews>();
	private Activity context;

	public AllNewsAdapter(Activity context, List<UserNews> allNews) {
		this.context = context;
		this.data = allNews;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return data.get(position).getNews().get_id();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		System.out.println("sprawdzamy gita");
	
	
		View view = null;
		if (convertView == null) {
			view = (View) context.getLayoutInflater().inflate(
					R.layout.layout_news_item, null);
		} else {
			view = convertView;
		}

		TextView title = (TextView) view.findViewById(R.id.titleAllNews);
		title.setText(data.get(position).getNews().getTitle());

		TextView description = (TextView) view
				.findViewById(R.id.descriptionAllNews);
		description.setText(BlogsFragment.getBlogName(data.get(position)
				.getNews().getBlogId())
				+ ", "
				+ UnreadNewsFragment.userFriendlyDate(data.get(position)
						.getNews().getDate()));

		if (data.get(position).isRead()) {
			title.setTextColor(Color.GRAY);
		} else {
			title.setTextColor(Color.BLACK);
		}
		return view;
	}
}
