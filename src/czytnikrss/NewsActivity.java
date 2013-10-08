package czytnikrss;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.czytnikrss.R;

import czytnikrss.database.News;

public class NewsActivity extends Activity {

	News news;
	String type;

	boolean color = true;

	RelativeLayout relativeLayoutNews;
	TextView textViewNewstTitle;
	Button buttonReadAll;
	Button buttonNext;
	Button buttonPrevious;
	WebView webView;
	WebSettings webSettings;

	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	// The 3 states (events) which the user is trying to perform
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;

	// these PointF objects are used to record the point(s) the user is touching
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		type = getIntent().getExtras().getString("type");
		if (type.equals("unread"))
			news = UnreadNewsFragment.unreadNews.get(
					getIntent().getExtras().getInt("position")).getNews();
		else
			news = AllNewsFragment.allNews.get(
					getIntent().getExtras().getInt("position")).getNews();

		relativeLayoutNews = (RelativeLayout) findViewById(R.id.relativeLayoutNews);
		textViewNewstTitle = (TextView) findViewById(R.id.textViewNewsTitle);
		textViewNewstTitle.setText(news.getTitle());

		buttonReadAll = (Button) findViewById(R.id.buttonReadAll);
		buttonReadAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(news.getUrl()));
				startActivity(browserIntent);
			}
		});

		webView = (WebView) findViewById(R.id.webview);
		webView.setBackgroundColor(0x00000000);

		webSettings = webView.getSettings();
		webSettings.setDefaultTextEncodingName("UTF-8");
		webView.setVerticalScrollBarEnabled(true);

		webView.loadData(
				"<p style=\"text-align: justify;\">" + news.getShortText()
						+ "</p>", "text/html", "UTF-8");

		webView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				float scale;

				switch (event.getAction() & 255) {
				case MotionEvent.ACTION_DOWN: // pierwszy palec dotyka
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					mode = DRAG;
					break;

				case MotionEvent.ACTION_UP: // pierwszy palec puszcza
				case 6: // drugi palec pszcza
					mode = NONE;
					break;

				case 5: // first and second finger down
					oldDist = spacing(event);
					if (oldDist > 5f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
					}
					break;

				case MotionEvent.ACTION_MOVE:
					if (mode == ZOOM) {
						// pinch zooming
						float newDist = spacing(event);
						if (newDist > 5f) {
							matrix.set(savedMatrix);
							scale = newDist / oldDist;

							if (scale > 1) {
								webSettings.setDefaultFontSize(webSettings
										.getDefaultFontSize() + 1);
							} else
								webSettings.setDefaultFontSize(webSettings
										.getDefaultFontSize() - 1);
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
				}

				return false; // indicate event was handled
			}

			@SuppressLint("FloatMath")
			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}

			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add(0, 0, 0, "Odwróć kolory").setIcon(
				android.R.drawable.ic_menu_revert);
		menu.add(0, 1, 0, "Skopiuj link").setIcon(
				android.R.drawable.ic_menu_agenda);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0: // odwróć kolory
			if (color) {
				textViewNewstTitle.setTextColor(Color.WHITE);
				relativeLayoutNews.setBackgroundColor(Color.BLACK);
				webView.loadData(
						"<p style=\"text-align: justify; color: #ffffff;\">"
								+ news.getShortText() + "</p>", "text/html",
						"UTF-8");
				color = !color;
			} else {
				textViewNewstTitle.setTextColor(Color.BLACK);
				relativeLayoutNews.setBackgroundColor(Color.WHITE);
				webView.loadData(
						"<p style=\"text-align: justify; color: black;\">"
								+ news.getShortText() + "</p>", "text/html",
						"UTF-8");
				color = !color;
			}
			break;
		case 1:
			// TODO skopiowanie linka do schowka;
		}
		return true;
	}

}
