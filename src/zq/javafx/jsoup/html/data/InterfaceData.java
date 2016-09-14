package zq.javafx.jsoup.html.data;

import java.io.Serializable;
import java.util.List;

import org.json.JSONObject;
import org.jsoup.Jsoup;

public class InterfaceData implements Serializable{
	/**
	 * 序列化的UID
	 */
	private static final long serialVersionUID = -5257931038321004301L;
	private final String SEPRATOR = "|";

	public String getFuncTitle() {
		return funcTitle;
	}

	public void setFuncTitle(String funcTitle) {
		this.funcTitle = funcTitle;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = rmSpace(url);
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getProtocalNumber() {
		return protocalNumber;
	}

	public void setProtocalNumber(String protocalNumber) {
		this.protocalNumber = protocalNumber;
	}

	public List<JSONObject> getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(List<JSONObject> requestBody) {
		this.requestBody = requestBody;
	}

	public String getRequestExample() {
		return requestExample;
	}

	public void setRequestExample(String requestExample) {
		this.requestExample = requestExample;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = rmSpace(httpMethod);
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = rmSpace(method);
	}

	/**
	 * 获得索引key，用于map的key
	 * @return
	 */
	public String getMapKey(){
		return this.getMethod() + SEPRATOR + this.getDescribe();
	}

	/**
	 * 移除html编码的空格&nbsp
	 * @param str
	 * @return
	 */
	private String rmSpace(String str){
		if(null!=str){
			str = str.replace(Jsoup.parse("&nbsp;").text(), "");
		}
		
		return str;
	}
	
	private String funcTitle;	//接口功能

	private String url;			// 请求地址
	private String httpMethod;	// 请求方式post或get
	private String method;		// 请求方法*.action

	private String requestType;	// 请求类型Content_type
	private String describe;	// 接口描述
	private	String protocalNumber;	//协议号

	private List<JSONObject> requestBody;	//请求参数

	private String requestExample;		//请求示例

	private String response;			//接口返回

	@Override
	public String toString() {
		return "接口功能："+funcTitle+",请求地址："+url+",请求方式："+httpMethod+",请求方法："+method
				+",请求类型："+requestType+",接口描述："+describe+",请求参数："+requestBody;
	}
}
