package com.example.haliyikamaapp.ToolLayer.AccountManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MyServerAuthenticator implements IServerAuthenticator {

	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */
	private static Map<String, String> mCredentialsRepo;
	public static String gelenToken = "";


	static {
		Map<String, String> credentials = new HashMap<String, String>();
		credentials.put("demo@example.com", "demo");
		credentials.put("foo@example.com", "foobar");
		credentials.put("user@example.com", "pass");
		mCredentialsRepo = Collections.unmodifiableMap(credentials);
	}
	
	@Override
	public String signUp(String email, String username, String password) {
		// TODO: register new user on the server and return its auth token
		return null;
	}

	@Override
	public String signIn(String email, String password, Context context) {
		String authToken = null;
		final DateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss");
		
	/*	if (mCredentialsRepo.containsKey(email)) {
			if (password.equals(mCredentialsRepo.get(email))) {
				authToken = email + "-" + df.format(new Date());
			}
		}*/

		//getTtoken(context, email, password, "test");


			authToken = "4c45b8ab-d2bc-4077-9cac-38be5de385ca";

		return authToken;
	}




	@SuppressLint("WrongConstant")
	public static String getTtoken(final Context context, final String username, final String password, final String tenantId) {
		String url = "http://34.91.29.223/hy/" + "oauth/token";

		final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);


		RequestQueue mQueue = Volley.newRequestQueue(context);
		StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.i("VOLLEY", response.toString());

				try {
					JSONObject jsonObject = new JSONObject(response);
					gelenToken = jsonObject.getString("token_type");

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (error instanceof AuthFailureError) {
							//Toast.makeText(context, "Kullanıcı adı veya parola hatalı ! " , Toast.LENGTH_SHORT).show();

							Log.e("TAG", error.getMessage(), error);

						} else {
							// Toast.makeText(context, "Kullanıcı adı veya parola hatalı ! " , Toast.LENGTH_SHORT).show();

							Log.e("TAG", error.getMessage(), error);
						}
					}
				}) {

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				final Map<String, String> headers = new HashMap<>();
				String username = "hy";
				String password = "hy-secret";
				String auth = username + ":" + password;
                /*byte[] data = auth.getBytes();
                String base64 = Base64.encodeToString(data, Base64.DEFAULT);*/

				byte[] data = auth.getBytes(StandardCharsets.UTF_8);
				String base64 = Base64.encodeToString(data, Base64.DEFAULT);


				headers.put("Accept", "application/json");
				headers.put("Content-Type", "application/x-www-form-urlencoded");
				headers.put("tenant-id", tenantId);
				// headers.put("Connection", "keep-alive");
				headers.put("Authorization", "Basic " + base64);

				return headers;

			}


			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("grant_type", "password");
				params.put("username", username);
				params.put("password", password);


				return params;
			}

			@Override
			public String getBodyContentType() {
				return "application/x-www-form-urlencoded";
			}

		};

		request.setRetryPolicy(new RetryPolicy() {
			@Override
			public int getCurrentTimeout() {
				return 50000;
			}

			@Override
			public int getCurrentRetryCount() {
				return 50000;
			}

			@Override
			public void retry(VolleyError error) throws VolleyError {

			}
		});
		mQueue.add(request);
		//dialog.show();

		return  null;

	}


}
