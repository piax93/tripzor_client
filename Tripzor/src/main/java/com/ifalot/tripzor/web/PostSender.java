package com.ifalot.tripzor.web;

import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


@SuppressWarnings("deprecation")
public class PostSender extends AsyncTask<HashMap<String, String>, String, String> {
	
	public static final String URL_STRING = "http://10.0.2.2:80/tripzor/";
	// public static final String URL_STRING = "https://tripzor.azurewebsites.net";

	protected static HttpContext localContext;
	protected static CookieStore cookieStore;
	
	private boolean multipleLines;
	private ResultListener listener;
	
	protected PostSender(boolean multipleLines, ResultListener listener){
		this.listener = listener;
		this.multipleLines = multipleLines;
	}
	
	public static void initCookieStore(){
	    cookieStore = new BasicCookieStore();
	    localContext = new BasicHttpContext();
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}
	
	@SuppressWarnings("unchecked")
	public static void sendPost(HashMap<String, String> postData, ResultListener listener){
		PostSender sender = new PostSender(false, listener);
		sender.execute(postData);
	}
	
	@SuppressWarnings("unchecked")
	public static void sendPostML(HashMap<String, String> postData, ResultListener listener){
		PostSender sender = new PostSender(true, listener);
		sender.execute(postData);
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(this.multipleLines){
			String[] tmpArr = result.split("\n");
			List<String> listResult = new ArrayList<String>();
			for (String aTmpArr : tmpArr) {
				if (aTmpArr.length() != 0) listResult.add(aTmpArr);
			}
			listener.onResultsSucceeded(result, listResult);
		}else{
			listener.onResultsSucceeded(result, null);
		}
	}

	@Override
	protected String doInBackground(HashMap<String, String>... params) {
		HttpClient client = getClient();		
		HttpPost post = new HttpPost(URL_STRING);
		HashMap<String, String> postData = params[0];
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		for(Entry<String, String> entry : postData.entrySet()){
			urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse response = client.execute(post, localContext);
			BufferedReader rd = new BufferedReader(
	                        new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
				if(this.multipleLines) result.append('\n');
			}
			return result.toString().trim();
		} catch (IOException e) {
			Log.d("ciao", e.getMessage());
			return Codes.FATAL_ERROR;
		} 
		
	}
	
	private static HttpClient getClient(){
		try {
			KeyStore truststore = KeyStore.getInstance(KeyStore.getDefaultType());
			truststore.load(null, null);
			TripzorSSLSocket socketFactory = new TripzorSSLSocket(truststore);
			socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUserAgent(params, "tripzor-app");
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", socketFactory, 443));
	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new DefaultHttpClient();
	}
	
	public static void checkServer(ResultListener listener){
		sendPost(new HashMap<String, String>(), listener);
	}

}
