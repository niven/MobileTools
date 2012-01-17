package org.interdictor.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class Contact {
	private static final String PHONE_URI = "content://com.android.contacts/data/phones";
	public String email = "";
	public String phone = "";
	public String name = "";
	public Address address;

	class Address {
		public String country = "";
		public String street = "";
		public String zipcode = "";
	}

	public static Contact fromCursor(Cursor c, ContentResolver cr) {

		Contact out = new Contact();

		out.name = c.getString(c.getColumnIndex("display_name"));
		out.email = c.getString(c.getColumnIndex("primary_email"));
		out.phone = findPhone(cr, c.getString(c.getColumnIndex("_id")));

		for (String name : c.getColumnNames()) {
			org.interdictor.util.Log.debug( name + ": " + c.getString(c.getColumnIndex(name)));
		}

		return out;
	}

	private static String findPhone(ContentResolver cr, String id) {
		org.interdictor.util.Log.debug( "FIND PHONE+ " + id);
		org.interdictor.util.Log.debug( ContactsContract.CommonDataKinds.Phone.CONTENT_URI.toString());
		org.interdictor.util.Log.debug( ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
//		Cursor pCur = cr.query(Uri.parse(PHONE_URI), null, "contact_id = ?", new String[] { id }, null);

		Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);
		try {
			while (pCur.moveToNext()) {
				for (String s : pCur.getColumnNames()) {
					org.interdictor.util.Log.debug( "PHONE field: " + s + ": " + pCur.getString(pCur.getColumnIndex(s)));
					
				}
				return pCur.getString(pCur.getColumnIndex("data1")); // first one we find is fine, it seems the number is in data1
			}
		} finally {
			pCur.close();
		}
		return null;
	}

	public void dumpContactsFroyo(Context context) {
		ContentResolver cr = context.getContentResolver();
		Class contacts;
		try {
			contacts = Class.forName("android.provider.ContactsContract$Contacts");
			org.interdictor.util.Log.debug(contacts.getField("CONTENT_URI").get(null).toString());
			String id = (String) contacts.getField("_ID").get(new String());
			String name = (String) contacts.getField("DISPLAY_NAME").get(new String());
			Cursor cur = cr.query((Uri) contacts.getField("CONTENT_URI").get(null), null, null, null, null);
			if (cur.getCount() > 0) {
				while (cur.moveToNext()) {
					String idVal = cur.getString(cur.getColumnIndex(id));
					String nameVal = cur.getString(cur.getColumnIndex(name));
					// if
					// (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))
					// > 0) {
					// // Query phone here. Covered next
					// }
					org.interdictor.util.Log.debug( idVal + ": " + nameVal);
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			org.interdictor.util.Log.debug( e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			org.interdictor.util.Log.debug( e);
			e.printStackTrace();
		} catch (SecurityException e) {
			org.interdictor.util.Log.debug( e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			org.interdictor.util.Log.debug( e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			org.interdictor.util.Log.debug( e);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
