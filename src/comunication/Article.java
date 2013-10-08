package comunication;


public class Article {

	public int aid = 0;
	public int cid = 0;
	public String title;
	public String url;
	public String descripton;
	public String date;
	public String author;

	public Article() {
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescripton() {
		return descripton;
	}

	public void setDescripton(String descripton) {
		this.descripton = descripton;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getAid() {
		return aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	@Override
	public String toString() {
		return "Article [aid=" + aid + ", cid=" + cid + ", title=" + title
				+ ", url=" + url + ", descripton=" + descripton + ", date="
				+ date + ", author=" + author + "]";
	}
}
