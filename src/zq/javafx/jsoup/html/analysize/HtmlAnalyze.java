package zq.javafx.jsoup.html.analysize;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import zq.javafx.jsoup.html.data.InterfaceData;
import zq.javafx.jsoup.html.data.RequestParam;
import zq.javafx.jsoup.html.http.HttpExecute;

public class HtmlAnalyze extends Application{
	private static Logger logger = Logger.getLogger(HtmlAnalyze.class);
	private TextField searchKey = null;	//接口搜索关键字
	private TextArea responseShow = null;	//接口响应
	private static final ObservableList<String> actionData = FXCollections.observableArrayList();		// 接口功能列表
	private ObservableList<RequestParam> interfaceParam = FXCollections.observableArrayList();	// 接口参数
	private HashMap<String, InterfaceData> maps = new HashMap<>();
	private String interfaceKey = null;
	private int WWIDTH = 1080;
	private int HHEIGHT = 600;
	private int WWEIGHT[] = {2,3,1};
	private int HWEIGHT[] = {1,2,1};
	private int PARSE_TIMEOUT = 3000;

	public static void main(String []args){
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane borderPane = new BorderPane();
		VBox vBox = new VBox(10);
		HBox hBox = new HBox();
		TableView<RequestParam> tvParam = new TableView<>(interfaceParam);
		Button button = new Button("搜索接口");
		Button btnOnline = new Button("网上获取接口");
		Button btnHttp = new Button("执行http请求");
		ListView<String> actions = new ListView<>(actionData);
		searchKey = new TextField("");
		responseShow = new TextArea("response");

		// 初始化
		createTableViewForParam(tvParam);
		tvParam.setEditable(true);	//列元素可编辑，tableview编辑属性需要设置为true

//		// 接口列表初始化
		readHtmlForRequest(null, 1);
        //填充列表显示的内容,自动刷新
        mapIterateForList(maps, null);
        logger.info("总共有"+maps.size()+"个接口");
		
		// 事件处理
		// 获取接口列表按钮事件的处理
		button.setOnAction(event -> {
			mapIterateForList(maps, searchKey.getText());
        });
		// 列表选择事件
		actions.getSelectionModel().selectedItemProperty().addListener(
				(ObservableValue<? extends String> ov, String old_val, String new_val)->{
					createInterfaceParam(new_val);
					interfaceKey = new_val;
				}
		);
		// 输入框Enter执行搜索
		searchKey.setOnKeyPressed( new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER){
					mapIterateForList(maps, searchKey.getText());
				}
			}
			
		});
		// 从网上获取接口
		btnOnline.setOnAction(event->{
			try {
				if(!maps.isEmpty()){
					maps.clear();
				}
				String url = searchKey.getText();
				if(url.contains("http://")){
					readHtmlForRequest(new URL(url), 1);
				}else{
					readHtmlForRequest(new URL("http://www.baidu.com"), 1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	        //填充列表显示的内容,自动刷新
	        mapIterateForList(maps, null);
	        logger.info("总共有"+maps.size()+"个接口");
		});
		
		// 执行http请求
		btnHttp.setOnAction(event -> {
			if(null==interfaceKey || interfaceKey.isEmpty()){
				responseShow.setText("未选中任何接口！");
			}else{
				responseShow.setText( HttpExecute.httpPost(getParamAll(), maps.get(interfaceKey).getUrl()) );
			}
		});
		
		searchKey.setPrefWidth(WWIDTH*9/10 - button.getPrefWidth());
		hBox.getChildren().addAll(searchKey, button);
		borderPane.setTop(hBox);
		borderPane.setLeft(actions);		//接口说明
		actions.setPrefWidth(WWIDTH*WWEIGHT[0]/getSumOfW());
		borderPane.setCenter(tvParam);		//接口参数
		tvParam.setPrefWidth(WWIDTH*WWEIGHT[1]/getSumOfW());
		vBox.getChildren().add(btnOnline);	//线上获取网址
		vBox.getChildren().add(btnHttp);	//提交请求
		vBox.setPrefWidth(WWIDTH*WWEIGHT[2]/getSumOfW());
		vBox.setAlignment(Pos.TOP_CENTER);
		vBox.setPadding(new Insets(10,0,0,0));;
		borderPane.setRight(vBox);
		responseShow.setPrefHeight(HHEIGHT*HWEIGHT[2]/getSumOfH());
		responseShow.setFont(Font.font(16));
		borderPane.setBottom(responseShow);
		
		// 布局属性设定
		Scene scene = new Scene(borderPane, WWIDTH, HHEIGHT, Color.WHITE);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * 初始化用于显示接口参数的tableview
	 * @param tvParam
     */
	private void createTableViewForParam(TableView<RequestParam> tvParam){
		TableColumn<RequestParam, String> nameColume = new TableColumn<>("参数");
		TableColumn<RequestParam, String> typeColume = new TableColumn<>("参数类型");
		TableColumn<RequestParam, String> needColume = new TableColumn<>("是否必须");
		TableColumn<RequestParam, String> decColume = new TableColumn<>("说明");
		TableColumn<RequestParam, String> inColume = new TableColumn<>("输入");

		double TableWidth = WWIDTH*WWEIGHT[1]/getSumOfW();
		nameColume.setPrefWidth( TableWidth*1.5/10 );
		typeColume.setPrefWidth( TableWidth/10 );
		needColume.setPrefWidth( TableWidth/10 );
		decColume.setPrefWidth( TableWidth*4.5/10 );
		inColume.setPrefWidth( TableWidth/5 );
		
		nameColume.setCellValueFactory(new PropertyValueFactory<>("paramName"));
		typeColume.setCellValueFactory(new PropertyValueFactory<>("paramType"));
		needColume.setCellValueFactory(new PropertyValueFactory<>("paramNeeded"));
		decColume.setCellValueFactory(new PropertyValueFactory<>("paramDec"));
		inColume.setCellValueFactory(new PropertyValueFactory<>("paramIn"));

		// 输入参数获取
		inColume.setCellFactory(TextFieldTableCell.forTableColumn());

		tvParam.getColumns().add(nameColume);
		tvParam.getColumns().add(typeColume);
		tvParam.getColumns().add(needColume);
		tvParam.getColumns().add(decColume);
		tvParam.getColumns().add(inColume);
	}

	/**
	 * 获取参数列表及其值
	 * @return	列表参数及值
     */
	public List<NameValuePair> getParamAll(){
		List<NameValuePair> params = new ArrayList<>();
		for (RequestParam requestParam : interfaceParam) {
			params.add(new NameValuePair(requestParam.getParamName(), requestParam.getParamIn()));
		}

		return params;
	}

	/**
	 * 初始化接口参数
	 */
	public void createInterfaceParam(String mapKey) {
		if( null!= maps.get(mapKey)){
			//response窗口显示接口详细信息
			responseShow.setText("接口请求地址URL:"+maps.get(mapKey).getUrl());
			responseShow.appendText("\r\n接口名称:"+maps.get(mapKey).getMethod());
			responseShow.appendText("\r\n接口说明:"+maps.get(mapKey).getDescribe());
			
			interfaceParam.clear();
			List<JSONObject> datas = maps.get(mapKey).getRequestBody();
			for(JSONObject data : datas){
				RequestParam requestParam = null;
				try {
					requestParam = new RequestParam(
                            data.getString("参数"),
                            data.getString("参数类型"),
                            data.getString("是否必须"),
                            data.getString("说明")
                            );
				} catch (JSONException e) {
					logger.warn("接口参数解析错误:" + data.toString());
					e.printStackTrace();
				}
				interfaceParam.add(requestParam);
			}
		}
	}

	/**
	 * hash map遍历
	 * @param maps
	 */
	public String mapIterate(HashMap<String, InterfaceData> maps){
		String ret = "";
		Iterator iterator = maps.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String, InterfaceData> entry = (Entry<String, InterfaceData>) iterator.next();
			String key = entry.getKey();
			InterfaceData requestData = entry.getValue();
			ret += requestData + "\n";
		}
		return ret;
	}

	/**
	 * hash map遍历，列表框显示内容
	 * @param maps
	 */
	public void mapIterateForList(HashMap<String, InterfaceData> maps, String keyword){
		//清空数据
		if(!actionData.isEmpty()){
			actionData.clear();
		}
		Iterator iterator = maps.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String, InterfaceData> entry = (Entry<String, InterfaceData>) iterator.next();
			String key = entry.getKey();
			InterfaceData requestData = entry.getValue();
			if(null==keyword || keyword.equals("")){
				actionData.add( requestData.getMapKey() );
			}else{
				if(key.contains(keyword)){
					actionData.add( requestData.getMapKey() );
				}
			}
		}
	}

	/**
	 * 获取宽度权重
	 * @return
	 */
	private int getSumOfW(){
		int sum = 0;
		for(int s : WWEIGHT){
			sum += s;
		}
		
		return sum;
	}
	
	/**
	 * 获取高度权重
	 * @return
	 */
	private int getSumOfH(){
		int sum = 0;
		for(int s:HWEIGHT){
			sum += s;
		}
		return sum;
	}
	
	/**
	 * 解析html，获得接口请求的描述、请求参数和响应示例
	 * @param data		文档html内容
	 * @param start		解析起始索引
	 * @return
	 * @throws JSONException
	 */
	public void readHtmlForRequest(URL url, int start) throws JSONException{
		
		File in = null;
		Document document = null;
		if(null==url){
			in = new File("file/HTTP API 接口.html");
			try {
				document = Jsoup.parse(in, "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try {
				document = Jsoup.parse(url, PARSE_TIMEOUT);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Elements elements = document.getElementsByClass("table-wrap");
		for(int loop=start; loop<elements.size(); loop++){
			//判断是否有效接口
			Elements trs = elements.get(loop).select("table").select("tr");
			if(trs.size()>0){
				Elements ths = trs.get(0).select("th");
				if(ths.size()>0){
					if( ths.get(0).text().contains("请求说明") ){
					}else{
						continue;
					}
				}else{
					continue;
				}
			}else{
				continue;
			}
			InterfaceData requestData = readHtmlForRequestData(elements, loop);
			maps.put(requestData.getMapKey() , requestData);	// 得到有效接口的文档
			loop += 2;	// 偏移到下一个可能有效的接口
		}
	}

	/**
	 * 从指定的元素集中读取索引从start开始，连续4个元素作为请求的描述、参数和响应
	 * @param elements	元素集
	 * @param start		起始索引
	 * @return
	 * @throws JSONException
	 */
	public InterfaceData readHtmlForRequestData(Elements elements, int start) throws JSONException{
		InterfaceData requestData = new InterfaceData();
		Element element = null;
		Elements trs;
		Elements tds;
		Elements ths;
		for(int index=start; index<start+3; index++){
			//取出element
			element = elements.get(index);
			//取出请求相关的内容
			switch( (index-start) % 3 ){
				case 0:	// 描述
					trs = element.select("table").select("tr");
					int decid=0;
					for(Element tr : trs){
						ths = tr.select("th");
						tds = tr.select("td");
						switch (decid++) {
							case 0:	// 请求说明

								break;
							case 1:	// url
								if(tds.size()>0){
									Elements temp = tds.get(0).select("code[class=java plain]");
									if(temp.size()>0){
										String []phs = temp.get(0).text().split(" +");
										requestData.setHttpMethod(phs[0]);
										String []url = tds.get(0).select("code[class=java comments]").get(0).text().split(" +");
										requestData.setUrl(phs[1]+url[0]);
										url = url[0].split("/");
										requestData.setMethod(url[url.length-1]);	// 取出方法名
									}
									if(temp.size()>1){
										requestData.setRequestType( temp.get(1).text() + tds.get(0).select("code[class=java value]").get(0).text() );
									}
								}else{
									logger.error("接口未找到请求地址");
								}

								break;
							case 2:	// 接口描述
								if(ths.get(0).text().contains("接口描述")){
									requestData.setDescribe(tds.get(0).text());
								}else{
									logger.warn("接口描述部分描述出现问题" + element.toString());
								}
								break;
							case 3:// 协议号
								if(ths.get(0).text().contains("协议号")){
									requestData.setProtocalNumber(tds.get(0).text());
								}else{
									logger.warn("接口描述部分协议号出现问题" + element.toString());
								}
								break;
							default:
								break;
						}
					}
					break;
				case 1:	// 请求参数
					List<String> key = null;
					List<JSONObject> jsonList = new ArrayList<>();
					JSONObject json = null;
					trs = element.select("table").select("tr");
					for(Element tr : trs){
						if(null==key){
							ths = tr.select("th");
							key = new ArrayList<>();
							for(Element th:ths){
								key.add(th.text());
							}
						}else{
							tds = tr.select("td");
							if(tds.size()>=key.size()){		//忽略option示例
								int tdid=0;
								json = new JSONObject();
								for(Element td:tds){
									if(tdid<key.size()){
										json.put(key.get(tdid++), td.text());
									}
								}
								jsonList.add(json);
							}
						}
					}
					requestData.setRequestBody(jsonList);
					break;
				case 2:	// 响应
					break;
				default:
					logger.warn("请求描述、参数或响应整理时出现位置类型...");
					break;
			}
		}

		return requestData;
	}
}
