package zq.javafx.jsoup.html.http;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;

import zq.javafx.jsoup.html.config.Config;
import zq.javafx.jsoup.html.util.Encode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpExecute{
	private static Logger logger = Logger.getLogger(HttpExecute.class);
	private static HttpClientFactory client = HttpClientFactory.Factory.getClient();
	private static String AppKey = Config.AppKey;
	private static String AppSecret = Config.AppSecret;

	/**
	 * 执行post方法
	 * @param params	参数
	 * @param url		请求地址
     * @return
     */
	public static String httpPost(List<NameValuePair> params, String url){
		String ret = "";
		try {
			ret = commonRequest(params, url);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 *  公用接口测试实现
	 * @param params
	 */
	private static String commonRequest(List<NameValuePair> params, String url) throws Exception {
		String Nonce = "12345";
		String CurTime = System.currentTimeMillis()/1000+"";
		Map<String,String> headerList = new HashMap<String,String>() ;
		headerList.put("AppKey", AppKey);
		headerList.put("Nonce", Nonce);
		headerList.put("CurTime", CurTime);
		String CheckSum = Encode.SHA1(AppSecret + Nonce + CurTime);
		headerList.put("CheckSum", CheckSum);
		logger.info("url=" +  url + ",参数=" + params.toString() + ",请求头="+headerList.toString());
		long stime = System.currentTimeMillis();
		String result = client.post(url, headerList, params);
		long timeCost = System.currentTimeMillis() - stime;
		logger.info("http post success, result=" +  new String(result.getBytes(),"utf-8") + ",url=" + url + ",time cost=" + timeCost);

		return result;
	}
}
