package czytnikrss.database;

public class Channel {
	private String cid;
	private String name;
	private String url;
	private String login;
	private String deleted;

	public Channel(String cid, String name, String url, String login) {
		super();
		this.cid = cid;
		this.name = name;
		this.url = url;
		this.login = login;
	}

	public Channel(String cid, String name, String url) {
		super();
		this.cid = cid;
		this.name = name;
		this.url = url;
		this.setDeleted("0");
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

}
