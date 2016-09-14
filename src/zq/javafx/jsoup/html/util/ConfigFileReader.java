package zq.javafx.jsoup.html.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class ConfigFileReader {
	
	private String filename ;
	private Properties properties;
	
	public ConfigFileReader(String filename) {
		this.filename = filename;
		properties  = imported();
	}
	
	private String getProperty(String property) {
		return properties.getProperty(property);
	}
	
	public String getString(String property) {
		return getProperty(property);
	}
	
	public int getInteger(String property) {
		return Integer.parseInt(getProperty(property));
	}
	
	public long getLong(String property) {
		return Long.parseLong(getProperty(property));
	}
	
	private Properties imported() {
		Properties properties = new Properties();
		try {
			InputStream in = new FileInputStream(filename);
			try {
				properties.load(new InputStreamReader(in, "UTF-8"));
			} finally {
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}
	
}