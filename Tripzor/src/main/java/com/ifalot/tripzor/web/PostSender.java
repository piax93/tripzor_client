package com.ifalot.tripzor.web;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import com.ifalot.tripzor.utils.Media;
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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.*;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


@SuppressWarnings({"deprecation", "unchecked"})
public class PostSender extends AsyncTask<HashMap<String, String>, String, String> {
	
	public static final String URL_STRING = "http://10.0.2.2:80/tripzor/";
	// public static final String URL_STRING = "http://192.168.1.3:80/tripzor/";
	// public static final String URL_STRING = "https://tripzor.azurewebsites.net";

	protected static HttpContext localContext;
	protected static CookieStore cookieStore;
	
	private boolean multipleLines;
	private boolean getimage;
	private boolean putimage;
	private String image_file;
	private ResultListener listener;
	
	protected PostSender(boolean multipleLines, ResultListener listener){
		this.listener = listener;
		this.multipleLines = multipleLines;
	}

	protected  PostSender(boolean multipleLines, boolean putimage, boolean getimage, String image_file, ResultListener listener){
		this.getimage = getimage;
		this.putimage = putimage;
		this.image_file = image_file;
		this.multipleLines = multipleLines;
		this.listener = listener;
	}
	
	public static void initCookieStore(){
	    cookieStore = new BasicCookieStore();
	    localContext = new BasicHttpContext();
	    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}
	
	public static void sendPost(HashMap<String, String> postData, ResultListener listener){
		PostSender sender = new PostSender(false, listener);
		sender.execute(postData);
	}
	
	public static void sendPostML(HashMap<String, String> postData, ResultListener listener){
		PostSender sender = new PostSender(true, listener);
		sender.execute(postData);
	}

	public static void getMedia(String file_id, String filename, MediaListener listener){
		HashMap<String, String> postData = new HashMap<String, String>();
		postData.put("action", "GetMedia");
		postData.put("file", file_id);
		PostSender sender = new PostSender(false, false, true, filename, listener);
		sender.execute(postData);
	}

	public static void getProfilePicture(String userId, String filefolder, MediaListener listener){
		File folder = new File(filefolder + "/" + Media.PROFILE_PICTURE_DIR);
		if(!folder.exists()) folder.mkdir();
		HashMap<String, String> postData = new HashMap<String, String>();
		postData.put("action", "GetProfilePicture");
		postData.put("userId", userId);
		PostSender sender = new PostSender(false, false, true, folder.getAbsolutePath() + "/" + userId, listener);
		sender.execute(postData);
	}

	public static void putMedia(HashMap<String, String> postData, String filename, ResultListener listener){
		PostSender sender = new PostSender(false, true, false, filename, listener);
		sender.execute(postData);
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(this.getimage){
			((MediaListener)this.listener).onMediaReceived(result);
		} else {
			if (this.multipleLines) {
				String[] tmpArr = result.split("\n");
				List<String> listResult = new ArrayList<String>();
				for (String aTmpArr : tmpArr) {
					if (aTmpArr.length() != 0) listResult.add(aTmpArr);
				}
				listener.onResultsSucceeded(result, listResult);
			} else {
				listener.onResultsSucceeded(result, null);
			}
		}
	}

	@Override
	protected String doInBackground(HashMap<String, String>... params) {
		try {
			HttpClient client = getClient();
			HttpPost post = new HttpPost(URL_STRING);
			HashMap<String, String> postData = params[0];

			if(this.putimage){
				MultipartEntityBuilder meb = MultipartEntityBuilder.create();
				for (Entry<String, String> entry : postData.entrySet()){
					meb.addTextBody(entry.getKey(), entry.getValue());
				}
				File image = new File(this.image_file);
				meb.addBinaryBody(image.getName().split("\\.")[0], image, ContentType.create("image/png"), image.getName());
				post.setEntity(meb.build());
			} else {
				List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
				for (Entry<String, String> entry : postData.entrySet()) {
					urlParameters.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				post.setEntity(new UrlEncodedFormEntity(urlParameters));
			}

			HttpResponse response = client.execute(post, localContext);

			if (this.getimage && response.getEntity().getContentType().getValue().startsWith("image")) {
				this.image_file += "." + response.getEntity().getContentType().getValue().split("/")[1];
				FileOutputStream fos = new FileOutputStream(this.image_file);
				response.getEntity().writeTo(fos);
				fos.close();
				return Codes.DONE;
			} else {
				BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuilder resultstr = new StringBuilder();
				String line;
				while ((line = rd.readLine()) != null) {
					resultstr.append(line);
					if (this.multipleLines) resultstr.append('\n');
				}
				return resultstr.toString().trim();
			}

		} catch (IOException e) {
			Log.d("ciao", e.getMessage());
			return Codes.ERROR;
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
