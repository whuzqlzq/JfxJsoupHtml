package zq.javafx.jsoup.html.config;

import zq.javafx.jsoup.html.util.ConfigFileReader;

public class Config {
	private static ConfigFileReader reader = new ConfigFileReader("conf/param.conf");
	
	public static String AppKey = reader.getString("APPKEY");
	public static String AppSecret = reader.getString("APPSECRET");
}
