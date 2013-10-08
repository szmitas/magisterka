package comunication;

import java.util.ArrayList;

import com.google.gson.Gson;

public class RssNews {

	public ArrayList<Article> list = null;

	public RssNews(ArrayList<Article> l) {
		this.list = l;
	}

	public ArrayList<Article> getList() {
		return list;
	}

	public void setList(ArrayList<Article> list) {
		this.list = list;
	}

	public static RssNews fromJson(String json) {
		try {
			return (new Gson()).fromJson(json, RssNews.class);
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	public String toString() {
		return "RssNews [list=" + list + "]";
	}

}
