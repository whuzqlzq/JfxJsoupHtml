package zq.javafx.jsoup.html.http;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import zq.javafx.jsoup.html.util.ConfigFileReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class HttpClientFactory {

private static Logger logger = Logger.getLogger(HttpClientFactory.class);
	
	private static ConfigFileReader reader = new ConfigFileReader("conf/httpclient.conf");
	private static int maxConn = reader.getInteger("maxConn");
	private static int maxConnPerHost = reader.getInteger("maxConnPerHost");
	private static int soTimeout = reader.getInteger("soTimeout");
	private static int connTimeout = reader.getInteger("connTimeout");

	private final MultiThreadedHttpConnectionManager multiThreadConnManager;
	private HttpClient client;

	private HttpClientFactory() {
		this(maxConn, maxConnPerHost, soTimeout, connTimeout);
	}

	public HttpClientFactory(int maxConn, int maxConnPerHost, int soTimeout, int connTimeout) {
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		params.setMaxTotalConnections(maxConn);
		params.setDefaultMaxConnectionsPerHost(maxConnPerHost);
		params.setSoTimeout(soTimeout);
		params.setConnectionTimeout(connTimeout);
		multiThreadConnManager = new MultiThreadedHttpConnectionManager();
		multiThreadConnManager.setParams(params);
	}

	private synchronized HttpClient getHttpClient() {
		if (client == null) {
			client = new HttpClient(multiThreadConnManager);
			HttpClientParams params = new HttpClientParams();
			params.setContentCharset("UTF-8");
			client.setParams(params);
		}
		return client;
	}
	
	public static class Factory {
		private static HttpClientFactory client = new HttpClientFactory();
		public static HttpClientFactory getClient() {
			return Factory.client;
		}
	}
	
	private String getResponseBody(HttpMethod http) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(http.getResponseBodyAsStream()));
		StringBuffer stringBuffer = new StringBuffer();
		String str = "";
		try {
			while((str = reader.readLine())!=null){  
			    stringBuffer.append(str);  
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return stringBuffer.toString();
	}

	private JSONObject doRequest(HttpMethod http) {
		JSONObject json = new JSONObject();
		String responseBody = null;
		try {
			int statusCode = getHttpClient().executeMethod(http);
			responseBody = getResponseBody(http);
			if (statusCode != 200) {
				json.put("code", statusCode);
				logger.error("http code:" + statusCode + "&body:" + responseBody);
			} else {
				logger.debug(responseBody);
				json.put("code", 200);
			}
			if (responseBody.startsWith("[")) {
				JSONArray responseJSONArr = new JSONArray(responseBody);
				json.put("body", responseJSONArr);
			} else {
				if (!responseBody.startsWith("{")){
					json.put("body", responseBody);
				}
				else{
					JSONObject responseJSON = new JSONObject(responseBody);
					json.put("body", responseJSON);
				}
			}
		} catch (Exception e) {
			logger.error("do request exception:", e);
		} finally {
			if (null != http) {
				http.releaseConnection();
			}
		}
		return json;
	}
	
	@SuppressWarnings("deprecation")
	private String doRequestString(HttpMethod http) {
		String responseBody = null;
		try {
			int statusCode = getHttpClient().executeMethod(http);			
			responseBody = getResponseBody(http);  
			if (statusCode != 200) {
				logger.error("http code:" + statusCode + "&body:" + responseBody);
			} else {
				logger.debug(responseBody);
			}
		} catch (Exception e) {
			logger.error("do request exception:", e);
		} finally {
			if (null != http) {
				http.releaseConnection();
			}
		}
		return responseBody;
	}
	
	@SuppressWarnings("deprecation")
	public String getString(String url) {
		GetMethod get = new GetMethod(url);
		return doRequestString(get);
	}
	
	public byte[] get(String url) {
		GetMethod get = new GetMethod(url);
		try {
			getHttpClient().executeMethod(get);
			if (get.getStatusCode() == 200) {
				byte[] response = get.getResponseBody();
				return response;
			} else {
				logger.info("res="+new String(get.getResponseBody()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	} 
	
	public JSONObject post(String url, String queryString) {
		PostMethod post = new PostMethod(url);
		post.setQueryString(queryString);
		return doRequest(post);
	}
	
	@SuppressWarnings("deprecation")
	public JSONObject postJson(String url, JSONObject queryJson, String accessToken) {
		PostMethod post = new PostMethod(url);
		post.setRequestHeader("access-token", accessToken); 
		try {
			post.setRequestBody(queryJson.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		post.setRequestHeader("Content-Type", "application/json");
		return doRequest(post);
	}
	
	@SuppressWarnings("deprecation")
	public JSONObject postJsonArray(String url, JSONArray arrayList, String accessToken) {
		PostMethod post = new PostMethod(url);
		post.setRequestHeader("access-token", accessToken); 
		try {
			post.setRequestBody(arrayList.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		post.setRequestHeader("Content-Type", "application/json");
		return doRequest(post);
	}
	
	@SuppressWarnings("deprecation")
	public String post(String url, Map<String,String> addtionalHeader, JSONObject queryJson) {
		PostMethod post = new PostMethod(url);
		if(addtionalHeader != null){
			for (String key : addtionalHeader.keySet()) {
				post.setRequestHeader(key, addtionalHeader.get(key));  
			}
		}
		try {
			post.setRequestBody(queryJson.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		post.setRequestHeader("Content-Type", "application/json;charset=utf-8");
		return doRequestString(post);
	}
	
	
	@SuppressWarnings("deprecation")
	public JSONObject postJson(String url, List<NameValuePair> parametersBody) {
		PostMethod post = new PostMethod(url);
		try {
			post.setRequestBody(parametersBody.toArray(new NameValuePair[parametersBody.size()]));
			} catch (Exception e) {
			e.printStackTrace();
		}
		post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		return doRequest(post);	   
	}	
	
	@SuppressWarnings("deprecation")
	public String post(String url, List<NameValuePair> parametersBody) {
		PostMethod post = new PostMethod(url);
		try {
			post.setRequestBody(parametersBody.toArray(new NameValuePair[parametersBody.size()]));
			} catch (Exception e) {
			e.printStackTrace();
		}
		post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		return doRequestString(post);	   
	}
	
	@SuppressWarnings("deprecation")
	public String postSms(String url, Map<String,String> addtionalHeader, List<NameValuePair> parametersBody) {
		PostMethod post = new PostMethod(url);
		if(addtionalHeader != null){
			for (String key : addtionalHeader.keySet()) {
				post.setRequestHeader(key, addtionalHeader.get(key));  
			}
		}
		try {  
			post.setRequestBody(parametersBody.toArray(new NameValuePair[parametersBody.size()]));
			} catch (Exception e) {
			e.printStackTrace();
		}
		post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		return doRequestString(post);	   
	}
	
	@SuppressWarnings("deprecation")
	public String post(String url, Map<String,String> addtionalHeader, List<NameValuePair> parametersBody) {
		PostMethod post = new PostMethod(url);
		if(addtionalHeader != null){
			for (String key : addtionalHeader.keySet()) {
				post.setRequestHeader(key, addtionalHeader.get(key));  
			}
		}
		try { 
				if (parametersBody != null){
					post.setRequestBody(parametersBody.toArray(new NameValuePair[parametersBody.size()]));
				}
			} catch (Exception e) {
			e.printStackTrace();
		}
		post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		return doRequestString(post);	   
	}
	
	//SMS短信验证码专用
	@SuppressWarnings("deprecation")
	public String postMul(String url, Map<String,String> addtionalHeader, String CheckSum, List<NameValuePair> parametersBody) {
		PostMethod post = new PostMethod(url);
		for (String key : addtionalHeader.keySet()) {
			post.setRequestHeader(key, addtionalHeader.get(key));  
		}
		post.setRequestHeader("CheckSum", CheckSum);  
		try {
			post.setRequestBody(parametersBody.toArray(new NameValuePair[parametersBody.size()]));
			} catch (Exception e) {
			e.printStackTrace();
		}
		post.setRequestHeader("Content-Type", "multipart/form-data");
		return doRequestString(post);	   
	}
}