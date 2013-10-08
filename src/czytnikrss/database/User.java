package czytnikrss.database;

public class User {
	private String login;
	private String password;
	private String autologin;
	private String hash;

	public User(String login, String password, String autologin, String hash) {
		super();
		this.login = login;
		this.password = password;
		this.autologin = autologin;
		this.hash = hash;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAutologin() {
		return autologin;
	}

	public void setAutologin(String autologin) {
		this.autologin = autologin;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
