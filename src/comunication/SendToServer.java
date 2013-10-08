package comunication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class SendToServer {

	static Socket clientSocket = null;
	static BufferedReader in = null;
	static PrintWriter out = null;
	static boolean threadLoop = true;
	static String ip = "192.168.43.198";
	static int port = 6666;
	static String line;

	public SendToServer() {

	}

	private static Datagram readReceivedDatagram() {
		out.flush();
		threadLoop = true;
		while (threadLoop) {
			try {
				if ((line = in.readLine()) != null) {
					line = ObjectCrypter.decrypt(line);
					threadLoop = false;
					return new Datagram(line);
				} else
					return serverOfflineDatagram();
			} catch (IOException e) {
				return serverOfflineDatagram();
			}
		}
		return serverOfflineDatagram();
	}

	private static boolean connectToServer() {
		if (checkServerStatus()) {
			try {
				clientSocket = new Socket(ip, port);

				if (clientSocket != null) {
					in = new BufferedReader(new InputStreamReader(
							clientSocket.getInputStream()));
					out = new PrintWriter(clientSocket.getOutputStream(), true);
					return true;
				} else {
					return false;
				}
			} catch (UnknownHostException e) {
				return false;
			} catch (IOException e) {
			}
			return true;
		} else {
			return false;
		}
	}

	private static boolean checkServerStatus() {
		boolean serverIsOnline = false;
		try {
			URL url = new URL("http://" + ip);
			HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
			urlcon.connect();
			if (urlcon.getResponseCode() == 200) {
				serverIsOnline = true;
			} else {
				serverIsOnline = false;
			}
			urlcon.disconnect();
		} catch (MalformedURLException e1) {
			serverIsOnline = false;
			e1.printStackTrace();
		} catch (IOException e) {
			serverIsOnline = false;
			e.printStackTrace();
		}
		return serverIsOnline;
	}

	private static Datagram serverOfflineDatagram() {
		Map<Parameter, String> parameters = new HashMap<Parameter, String>();
		parameters.put(Parameter.DATA, "Serwer jest niedostępny...");
		return new Datagram(Method.ERROR, parameters);
	}

	public static Datagram registrationDatagram(String username,
			String password, String mail) {
		if (connectToServer()) {
			Map<Parameter, String> parameters = new HashMap<Parameter, String>();
			parameters.put(Parameter.USERNAME, username);
			parameters.put(Parameter.MAIL, mail);
			parameters.put(Parameter.PASSWORD, password);
			out.println(ObjectCrypter.encrypt(new Datagram(Method.REGISTER,
					parameters).toString()));
			// out.println((new Datagram(Method.REGISTER,
			// parameters)).toString());
			return readReceivedDatagram();
		} else {
			return serverOfflineDatagram();
		}
	}

	public static Datagram loginDatagram(String username, String password) {
		if (connectToServer()) {
			Map<Parameter, String> parameters = new HashMap<Parameter, String>();
			parameters.put(Parameter.USERNAME, username);
			parameters.put(Parameter.PASSWORD, password);
			out.println(ObjectCrypter.encrypt(new Datagram(Method.LOGIN,
					parameters).toString()));
			return readReceivedDatagram();
		} else {
			return serverOfflineDatagram();
		}
	}

	public static Datagram logoutDatagram(String userHash) {
		if (connectToServer()) {
			Map<Parameter, String> parameters = new HashMap<Parameter, String>();
			parameters.put(Parameter.HASH, userHash);
			out.println(ObjectCrypter.encrypt(new Datagram(Method.LOGOUT,
					parameters).toString()));
			return readReceivedDatagram();
		} else {
			return serverOfflineDatagram();
		}
	}

	/**
	 * Pobranie wszystkich subskrybowanych kanałów - w celach synchronizacji
	 * 
	 * @param userHash
	 * @return
	 */
	public static Datagram getChannels(String userHash) {
		if (connectToServer()) {
			Map<Parameter, String> parameters = new HashMap<Parameter, String>();
			parameters.put(Parameter.HASH, userHash);
			out.println(ObjectCrypter.encrypt(new Datagram(Method.SUBSYN,
					parameters).toString()));
			return readReceivedDatagram();
		} else {
			return serverOfflineDatagram();
		}
	}

	public static Datagram addNewChannelDatagram(String hash,
			String newChannelURL) {
		if (connectToServer()) {
			Map<Parameter, String> parameters = new HashMap<Parameter, String>();
			parameters.put(Parameter.DATA, newChannelURL);
			parameters.put(Parameter.HASH, hash);
			out.println(ObjectCrypter.encrypt(new Datagram(Method.SUBSCRIBE,
					parameters).toString()));
			return readReceivedDatagram();
		} else {
			return serverOfflineDatagram();
		}
	}

	public static Datagram deleteChannelDatagram(String hash, String cid) {
		if (connectToServer()) {
			Map<Parameter, String> parameters = new HashMap<Parameter, String>();
			parameters.put(Parameter.CHANNEL_ID, cid);
			parameters.put(Parameter.HASH, hash);
			out.println(ObjectCrypter.encrypt(new Datagram(Method.UNSUBSCRIBE,
					parameters).toString()));
			return readReceivedDatagram();
		} else {
			return serverOfflineDatagram();
		}
	}

	public static Datagram userNewsDatagram(String userHash) {
		if (connectToServer()) {
			Map<Parameter, String> parameters = new HashMap<Parameter, String>();
			parameters.put(Parameter.HASH, userHash);
			out.println(ObjectCrypter.encrypt(new Datagram(Method.USER_NEWS,
					parameters).toString()));
			return readReceivedDatagram();
		} else {
			return serverOfflineDatagram();
		}
	}
}
