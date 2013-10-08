package comunication;


import java.util.ArrayList;
import java.util.Map;

import com.google.gson.Gson;

public class TopChannels {

	public ArrayList<TopChannel> list=null; 
	
	
	public TopChannels(ArrayList<TopChannel> l){
		this.list=l;
	}


	public static TopChannels fromJson(String json) {
		return (new Gson()).fromJson(json, TopChannels.class);

	}
	
	
	
	
	@Override
	public String toString() {
		return "TopChannels [map=" + list + "]";
	}



}
