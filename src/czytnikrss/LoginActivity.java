package czytnikrss;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.czytnikrss.R;
import comunication.Datagram;
import comunication.Method;
import comunication.ObjectCrypter;
import comunication.Parameter;
import comunication.SendToServer;

import czytnikrss.database.MyDatabase;
import czytnikrss.database.User;

public class LoginActivity extends Activity {

	public static MyDatabase db;

	// komponenty
	TextView loginTextBox;
	TextView passwordTextBox;
	CheckBox autologinCheckBox;
	Button buttonLogin;

	ProgressDialog pleaseWaitDialog;
	Handler pleaseWaitHandler;
	Runnable pleaseWaitAction;

	String toastText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ObjectCrypter.init();
		db = new MyDatabase(getApplicationContext());

		// ukrycie paska tytułu
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		loginTextBox = (TextView) findViewById(R.id.loginTextBox);
		passwordTextBox = (TextView) findViewById(R.id.passwordTextBox);
		autologinCheckBox = (CheckBox) findViewById(R.id.remembersMeCheckBox);
		buttonLogin = (Button) findViewById(R.id.loginButton);
		buttonLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				login(loginTextBox.getText().toString(), passwordTextBox
						.getText().toString(), "online");
			}
		});

		if (db.autologin()) {
			String[] userData = db.getUserData("autologin");
			loginTextBox.setText(userData[0]);
			passwordTextBox.setText(userData[1]);
			autologinCheckBox.setChecked(true);
			login(userData[0], userData[1], "online");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, 0, 0, "Utwórz konto").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(0, 1, 0, "Zaloguj offline").setIcon(
				android.R.drawable.ic_menu_mylocation);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				LoginActivity.this);
		LayoutInflater factory = LayoutInflater.from(this);

		switch (item.getItemId()) {
		case 0: // utwórz konto
			alertDialog.setTitle("Utwórz konto");
			alertDialog.setIcon(android.R.drawable.ic_dialog_email);

			View viewNewAccount = factory.inflate(R.layout.new_account, null);
			alertDialog.setView(viewNewAccount);

			final TextView newLogin = (TextView) viewNewAccount
					.findViewById(R.id.newLogin);
			final TextView newPassword = (TextView) viewNewAccount
					.findViewById(R.id.newPassword);
			final TextView newRePassword = (TextView) viewNewAccount
					.findViewById(R.id.newRePassword);
			final TextView newEmail = (TextView) viewNewAccount
					.findViewById(R.id.newEmail);

			alertDialog.setPositiveButton("Zarejestruj", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (newPassword.getText().toString()
							.equals(newRePassword.getText().toString())
							&& newPassword.getText().length() > 5
							&& newLogin.getText().length() > 3) {
						Datagram receivedDatagram = SendToServer
								.registrationDatagram(newLogin.getText()
										.toString(), newPassword.getText()
										.toString(), newEmail.getText()
										.toString());
						if (receivedDatagram.getMethod().equals(Method.ERROR)) {
							Toast.makeText(
									getApplicationContext(),
									receivedDatagram.getParameters()
											.get(Parameter.DATA).toString(),
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(),
									"Rejestracja zakończona sukcesem.",
									Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"Podane dane są nieprawidłowe.",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			alertDialog.setNegativeButton("Anuluj", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			alertDialog.show();
			break;
		case 1: // zaloguj offline
			login(loginTextBox.getText().toString(), passwordTextBox.getText()
					.toString(), "offline");
			break;
		}
		return true;
	}

	private void generateHomeActivity(String userHash) {
		// wyświetlenie HomeActivity
		Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("userHash", userHash);
		startActivity(intent);
		this.finish();
	}

	private void login(final String login, final String password,
			final String type) {
		pleaseWaitDialog = ProgressDialog.show(LoginActivity.this, "",
				"Trwa logowanie...", true);

		pleaseWaitHandler = new Handler();
		pleaseWaitAction = new Runnable() {
			public void run() {
				Toast.makeText(LoginActivity.this, toastText,
						Toast.LENGTH_SHORT).show();
			}
		};

		new Thread() {

			public void run() {
				if (login.length() > 0 && password.length() > 0) {
					if (type.equals("online")) {
						Datagram receivedDatagram = SendToServer.loginDatagram(
								login, password);
						if (receivedDatagram.getMethod().equals(Method.ERROR)) {
							toastText = receivedDatagram.getParameters()
									.get(Parameter.DATA).toString();
							pleaseWaitDialog.dismiss();
							pleaseWaitHandler.post(pleaseWaitAction);
						} else { // logowanie powiodło się
							String userHash = receivedDatagram.getParameters()
									.get(Parameter.HASH);
							if (db.insertIntoUsers(new User(login, password,
									autologinCheckBox.isChecked() ? "1" : "0",
									userHash)) != -1) {
								receivedDatagram = SendToServer
										.getChannels(userHash);
								if (receivedDatagram.getMethod().equals(
										Method.SUBSYN_RESPONSE))
									db.updateChannels(receivedDatagram
											.getParameters()
											.get(Parameter.DATA), userHash);

								pleaseWaitDialog.dismiss();
								generateHomeActivity(userHash);
							} else {
								toastText = "Logowanie nie powiodło się";
								pleaseWaitHandler.post(pleaseWaitAction);
							}
						}
					} else if (type.equals("offline")) {
						pleaseWaitDialog.dismiss();
						if (db.login(login, password)) {
							String userHash = db.getUserHash(login, password);
							if (db.insertIntoUsers(new User(login, password,
									"0", userHash)) != -1)
								generateHomeActivity(userHash);
						} else {
							toastText = "Logowanie nie powiodło się";
							pleaseWaitHandler.post(pleaseWaitAction);
						}
					}
				} else {
					pleaseWaitDialog.dismiss();
					toastText = "Podaj login i hasło";
					pleaseWaitHandler.post(pleaseWaitAction);
				}
			}
		}.start();
	}
}
