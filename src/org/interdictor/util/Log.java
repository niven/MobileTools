package org.interdictor.util;

import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class Log {

	private static TextView tvlog;

	public static void setTextViewLog(TextView tv) {
		tvlog = tv;
		tvlog.setMovementMethod(new ScrollingMovementMethod());
	}

	public static void debug(String msg) {
		logToTextView(msg);
		android.util.Log.d("interdictor", msg);
	}

	private static void logToTextView(String msg) {
		if (tvlog == null) {
			return;
		}

		tvlog.setText(tvlog.getText().toString() + "\n" + msg);

	}

	public static void debug(Exception e) {
		debug(e.getClass().getName() + ": " + e.getMessage());
	}

}
