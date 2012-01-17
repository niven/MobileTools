package networkinfo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.interdictor.util.Utils;

import com.booking.contacts.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.ClipboardManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class NetworkInfo {
	
	// bunch of constants
	static final String[] CALL_STATE = new String[] {
			"No activity.",
			"Off-hook. At least one call exists that is dialing, active, or on hold, and no calls are ringing or waiting.",
			"Ringing. A new call arrived and is ringing or waiting. In the latter case, another call is already active." };
	static final String[] DATA_ACTIVITY = new String[] { "No traffic.", "Currently receiving IP PPP traffic.",
			"Currently sending IP PPP traffic.", "Currently both sending and receiving IP PPP traffic.",
			"Data connection is active, but physical link is down." };

	static final String[] DATA_STATE = new String[] {
			"Disconnected. IP traffic not available.",
			"Currently setting up a data connection.",
			"Connected. IP traffic should be available.",
			"Suspended. The connection is up, but IP traffic is temporarily unavailable. For example, in a 2G network, data activity may be suspended when a voice call arrives." };

	static final String[] NETWORK_TYPE = new String[] { "Network type is unknown", "Current network is GPRS",
			"Current network is EDGE", "Current network is UMTS", "Current network is CDMA: Either IS95A or IS95B",
			"Current network is EVDO revision 0", "Current network is EVDO revision A", "Current network is 1xRTT",
			"Current network is HSDPA", "Current network is HSUPA", "Current network is HSPA", "Current network is iDen",
			"Current network is EVDO revision B", "Current network is LTE", "Current network is eHRPD",
			"Current network is HSPA+" };

	static final String[] PHONE_TYPE = new String[] { "No phone radio.", "Phone radio is GSM.", "Phone radio is CDMA.",
			"Phone is via SIP." };

	static final String[] SIM_STATE = new String[] {"Unknown. Signifies that the SIM is in transition between states. For example, when the user inputs the SIM pin under PIN_REQUIRED state, a query for sim status returns this state before turning to SIM_STATE_READY.",
		"No SIM card is available in the device.",
		"Locked: requires the user's SIM PIN to unlock",
		"Locked: requires the user's SIM PUK to unlock",
		"Locked: requries a network PIN to unlock",
		"Ready"};
	
	
	/**
	 * return key/value pairs with info
	 * 
	 * @param context
	 * @return
	 */
	public static Map<String, Object> getInfo(Context context) {
		Map<String, Object> ni = new LinkedHashMap<String, Object>(); // ordered

		TelephonyManager telephonyService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		int phoneType = telephonyService.getPhoneType();

		ni.put("Phone type (ID)", "(" + phoneType + ") " + PHONE_TYPE[phoneType]);
		ni.put("Device ID (IMEI for GSM and the MEID or ESN for CDMA)", telephonyService.getDeviceId()); // requires
		// READ_PHONE_STATE
		ni.put("Software version number (IMEI/SV for GSM)", telephonyService.getDeviceSoftwareVersion()); // requires
		// READ_PHONE_STATE
		ni.put("Phone number string (MSISDN for a GSM)", telephonyService.getLine1Number()); // requires
		// READ_PHONE_STATE
		ni.put("Network type (ID)",
				"(" + telephonyService.getNetworkType() + ") " + NETWORK_TYPE[telephonyService.getNetworkType()]);
		ni.put("Network Country ISO (MCC)", telephonyService.getNetworkCountryIso()
				+ (phoneType == 2 ? " (unreliable phone is CDMA)" : ""));
		ni.put("Network Operator (MCC+MNC)", telephonyService.getNetworkOperator()
				+ (phoneType == 2 ? " (unreliable phone is CDMA)" : ""));
		ni.put("Network Operator Name", telephonyService.getNetworkOperatorName()
				+ (phoneType == 2 ? " (unreliable phone is CDMA)" : ""));
		ni.put("Call state (ID)",
				"(" + telephonyService.getCallState() + ") " + CALL_STATE[telephonyService.getCallState()]);
		ni.put("Data activity (ID)",
				"(" + telephonyService.getDataActivity() + ") " + DATA_ACTIVITY[telephonyService.getDataActivity()]);
		ni.put("Data state (ID)",
				"(" + telephonyService.getDataState() + " )" + DATA_STATE[telephonyService.getDataState()]);

		ni.put("SIM state (ID)", "(" + telephonyService.getSimState() + ") " + SIM_STATE[telephonyService.getSimState()]);
		
		ni.put("SIM Country ISO", telephonyService.getSimCountryIso());
		ni.put("SIM Operator", telephonyService.getSimOperator()); // Availability: SIM state must be SIM_STATE_READY
		ni.put("SIM Operator name", telephonyService.getSimOperatorName()); // Availability: SIM state must be SIM_STATE_READY
		ni.put("SIM Serial number", telephonyService.getSimSerialNumber()); // requires READ_PHONE_STATE
		
		ni.put("Subscriber ID (IMSI for a GSM)", telephonyService.getSubscriberId()); // requires READ_PHONE_STATE
		ni.put("Voicemail Alpha Tag", telephonyService.getVoiceMailAlphaTag()); // requires READ_PHONE_STATE
		ni.put("Voicemail number", telephonyService.getVoiceMailNumber()); // requires READ_PHONE_STATE
		
		ni.put("Has ICC Card: ", telephonyService.hasIccCard());
		ni.put("Is Roaming: ", telephonyService.isNetworkRoaming());
		
		CellLocation cellLocation = telephonyService.getCellLocation();
		if(cellLocation instanceof GsmCellLocation) {
			GsmCellLocation gsm = (GsmCellLocation) cellLocation;
			int cid = gsm.getCid();
			int lac = gsm.getLac();
//			int psc = gsm.getPsc();
			int psc= 0 ;
			ni.put("CID/LAC/PSC", cid + "/" + lac + "/" + psc);
		}
		
		return ni;

	}

	public static Dialog getDialog(final Context context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Network Info");

		Map<String, Object> ni = getInfo(context);

		String[] items = new String[ni.size()];
		int i = 0;
		for (Map.Entry<String, Object> entry : ni.entrySet()) {
			items[i++] = entry.getKey() + ": " + entry.getValue();
		}

		final CharSequence str = Utils.join("\n", items);

		View view = LayoutInflater.from(context).inflate(R.layout.networkinfo, null);
		TextView data = (TextView) view.findViewById(R.id.info);
		data.setText(str);
		
		builder.setView(view);

		
		builder.setNeutralButton("Copy", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				ClipboardManager clipboardService = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
				clipboardService.setText(str); // My phone is API level 10. For >10
															// user
															// clipboardService.setPrimaryClip(ClipData.newPlainText("label",
															// "foo");
				dialog.cancel();
			}
		});

		builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		return builder.create();

	}

}
