package org.interdictor;

import java.io.IOException;
import java.io.InputStream;

import networkinfo.NetworkInfo;

import org.interdictor.contact.Contact;
import org.interdictor.sms.SMSDumper;
import org.interdictor.util.Utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.booking.contacts.R;

public class Dashboard extends Activity {

	private static final int PICK_CONTACT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// set a textview logger so we see everything in app that we log
		org.interdictor.util.Log.setTextViewLog((TextView) findViewById(R.id.logdump));
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		org.interdictor.util.Log.debug("API: " + currentapiVersion);
		if (currentapiVersion <= android.os.Build.VERSION_CODES.DONUT) {
			// disable contact import
			findViewById(R.id.select_contact_button).setVisibility(View.GONE);
		}

	}

	public void selectContact(View view) {
		org.interdictor.util.Log.debug( "selecting contact");
		Intent intent = new Intent(Intent.ACTION_PICK, People.CONTENT_URI);
		startActivityForResult(intent, PICK_CONTACT);

	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		switch (reqCode) {
		case (PICK_CONTACT):
			if (resultCode == Activity.RESULT_OK) {
				Uri contactData = data.getData();
				org.interdictor.util.Log.debug( contactData.toString());

				Cursor c = managedQuery(contactData, null, null, null, null);
				if (c.moveToFirst()) {
					ContentResolver cr = getContentResolver();
					Contact cont = Contact.fromCursor(c, cr);
					org.interdictor.util.Log.debug( "Selected: " + Utils.dump(cont));
					TextView dt = (TextView) findViewById(R.id.data);
					dt.setText(Utils.dump(cont));
				}
			}
			break;
		}
	}

	public void dumpSMS(View v) {

		SMSDumper dumper = new SMSDumper(this);
		dumper.dump();

	}

	public void showNetworkInfo(View v) {
		NetworkInfo.getDialog(this).show();
	}
	
}
