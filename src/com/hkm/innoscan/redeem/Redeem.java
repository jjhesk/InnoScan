package com.hkm.innoscan.redeem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hkm.innoscan.MainActivity;
import com.hkm.innoscan.R;
import com.hkm.innoscan.MainActivity.redeem_call_back;
import com.hkm.innoscan.R.string;

public class Redeem extends AsyncTask<String, Void, JSONObject> {
	// http://www.innoactor.com/apigateway/?action=redeem&h=03cd1160e9492ff9b06949d5f9ab7467
	public HttpParams httpParams;
	private Context c;
	private redeem_call_back cb;
	private String getMac, PATH;
	public static final String TAG = "Redeem Class";

	public Redeem(Context workingbase, redeem_call_back mredeem_call_back) {
		c = workingbase;
		cb = mredeem_call_back;
		ini_start();
	}

	public Redeem(MainActivity workingbase, redeem_call_back mredeem_call_back) {
		// TODO Auto-generated constructor stub
		c = workingbase;
		cb = mredeem_call_back;
		ini_start();
	}

	private void ini_start() {
		Log.d(TAG, "start");
		httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		HttpConnectionParams.setSoTimeout(httpParams, 5000);
		getMac = Tool.get_mac_address(c);
		PATH = c.getResources().getString(R.string.server_testing);
	}

	public static String convertStreamToString(InputStream is) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		is.close();
		return sb.toString();
	}

	@Override
	protected JSONObject doInBackground(String... data) {
		// TODO Auto-generated method stub

		String reg = data[0];
		String action = data[1];

		String resultString = "";
		JSONObject resultStringo = new JSONObject();
		try {
			Log.d(TAG, "DefaultHttpClient");
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

			HttpPost httpPostRequest = new HttpPost(PATH);
			Log.d(TAG, "HttpPost code " + reg);
			// Add data
			final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					2);
			nameValuePairs.add(new BasicNameValuePair("action", action));
			nameValuePairs.add(new BasicNameValuePair("h", reg));
			nameValuePairs.add(new BasicNameValuePair("mac", getMac));

			if (new String(action).equals("redeem")) {
				nameValuePairs
						.add(new BasicNameValuePair("ver_method", data[2]));
				Log.d(TAG,
						"set new String(action).equals(\"redeem\") Entity &&& data2: + "
								+ data[2]);
			}

			httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			Log.d(TAG, "setEntity");
			HttpResponse response = httpClient.execute(httpPostRequest);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				resultString = convertStreamToString(instream);
				// now you have the string representation of the HTML request
				System.out.println("RESPONSE: " + resultString);
				instream.close();
				if (response.getStatusLine().getStatusCode() == 200) {
					Log.d(TAG, "work just fine!");
					final JSONObject i = new JSONObject(resultString);
					resultStringo = i;
				}
			}
		} catch (Exception e) {
			Log.d("work ERROR", e.toString());
			resultStringo = new JSONObject();
			try {
				resultStringo.put("result", 1021);
				resultStringo.put("msg", e.toString());
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return resultStringo;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		Log.d(TAG, "onPostExecute result == " + result.toString());
		// TODO Auto-generated method stub
		try {
			decodejson(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		if (Tool.isOnline(c)) {
			super.onPreExecute();
		} else {
			Tool.trace(c, R.string.warning_online_alert);
		}
	}

	protected void decodejson(JSONObject i) throws JSONException {
		int result = i.getInt("result");
		if (result == 1) {
			JSONObject data = i.getJSONObject("content");
			if (data != null) {
				StoreStatus.CurrentRedeemProduct = data;
				cb.redeem_success(data);
			}
		} else if (result == 1021) {
			cb.redeem_error("timeout from the server connection. Please try again");
		} else {
			cb.redeem_error(i.getString("msg"));
		}

	}

}
