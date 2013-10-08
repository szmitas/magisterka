package comunication;




public class TopChannel {

	public int cid = 0;
	public String url = null;
	public String name = null;
	public int count=0;

	public TopChannel() {
	}
	
	
	public TopChannel(int cid, String name, String url) {
		this.cid = cid;
		this.url = url;
		this.name = name;
	}



	

	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}


	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return "Channel [cid=" + cid + ", url=" + url + ", name=" + name
				+ ", count=" + count + "]";
	}


	
}
