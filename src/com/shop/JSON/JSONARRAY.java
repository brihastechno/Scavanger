package com.shop.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONARRAY {
	static InputStream is = null;
	static JSONArray jObj = null;
	static String json = "";

	// constructor
	public JSONARRAY() {

	}

	public JSONArray getJSONFromUrl(String url,
			ArrayList<NameValuePair> namevaluepairs) {

		// Making HTTP request
		try {
			// defaultHttpClient

			HttpClient httpclient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(namevaluepairs));
			HttpResponse response = null;
			response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "");
			}
			is.close();
			Log.d("value response", sb.toString());
			json = sb.toString();
			Log.d("String Response", sb.toString());
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}
		try {
			jObj = new JSONArray(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// try parse the string to a JSON object

		// return JSON String
		return jObj;

	}
}