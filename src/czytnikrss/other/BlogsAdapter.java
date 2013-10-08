package czytnikrss.other;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import czytnikrss.database.Channel;

public class BlogsAdapter extends BaseAdapter {

	private List<Channel> data = new ArrayList<Channel>();
	private Activity context;

	public BlogsAdapter(Activity context, List<Channel> blogsList) {
		this.context = context;
		this.data = blogsList;
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
		return Integer.valueOf(data.get(position).getCid());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;
		if (convertView == null) {
			view = (View) context.getLayoutInflater().inflate(
					android.R.layout.simple_list_item_2, null);
		} else {
			view = convertView;
		}

		TextView title = (TextView) view.findViewById(android.R.id.text1);
		title.setText(data.get(position).getName());

		TextView description = (TextView) view.findViewById(android.R.id.text2);
		description.setText(data.get(position).getUrl());

		return view;
	}
}
