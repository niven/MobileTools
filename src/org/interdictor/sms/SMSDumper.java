package org.interdictor.sms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import au.com.bytecode.opencsv.CSVWriter;

public class SMSDumper {

	private Context context;

	public SMSDumper(Context context) {
		this.context = context;
	}

	public void dump() {

		String folder = "inbox";

		Uri uri = Uri.parse("content://sms/" + folder);
		Cursor c = context.getContentResolver().query(uri, null, null, null, null);

		int colCount = c.getColumnCount();
		String[] line = new String[colCount];

		List<String[]> data = new ArrayList<String[]>();

		for (int i = 0; i < colCount; i++) {
			line[i] = c.getColumnName(i);
		}
		data.add(line); // header

		int count = c.getCount();
		org.interdictor.util.Log.debug("SMSes dumped from " + folder + ": " + count);
		if (c.moveToFirst()) {
			for (int i = 0; i < count; i++) {
				line = new String[line.length];
				for (int j = 0; j < colCount; j++) {
					line[j] = c.getString(j);
				}
				data.add(line);
				c.moveToNext();
			}
		}

		c.close();

		try {
			File root = Environment.getExternalStorageDirectory();
			if (root.canWrite()) {
				File gpxfile = new File(root, "sms_messages_" + folder + ".csv");
				FileWriter gpxwriter = new FileWriter(gpxfile);
				BufferedWriter out = new BufferedWriter(gpxwriter);
				CSVWriter csvOut = new CSVWriter(out);
				csvOut.writeAll(data);

				out.close();
				org.interdictor.util.Log.debug("Wrote " + gpxfile.length() + " bytes to " + gpxfile.getName());
			}
		} catch (IOException e) {
			org.interdictor.util.Log.debug("Could not write file " + e.getMessage());
		}

	}

}