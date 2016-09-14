package zq.javafx.jsoup.html.data;

import javafx.beans.property.SimpleStringProperty;

public final class RequestParam {
    // 参数名称
	private final SimpleStringProperty paramName = new SimpleStringProperty();
	// 参数类型
    private final SimpleStringProperty paramType = new SimpleStringProperty();
	// 是否必须
    private final SimpleStringProperty paramNeeded = new SimpleStringProperty();
	// 说明
    private final SimpleStringProperty paramDec = new SimpleStringProperty();
    // 参数输入
    private final SimpleStringProperty paramIn = new SimpleStringProperty();

    public String getParamIn() {
        return paramIn.get();
    }

    public SimpleStringProperty paramInProperty() {
        return paramIn;
    }

    public void setParamIn(String paramIn) {
        this.paramIn.set(paramIn);
    }

    public String getParamName() {
        return paramName.get();
    }

    public SimpleStringProperty paramNameProperty() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName.set(paramName);
    }

    public String getParamType() {
        return paramType.get();
    }

    public SimpleStringProperty paramTypeProperty() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType.set(paramType);
    }

    public String getParamNeeded() {
        return paramNeeded.get();
    }

    public SimpleStringProperty paramNeededProperty() {
        return paramNeeded;
    }

    public void setParamNeeded(String paramNeeded) {
        this.paramNeeded.set(paramNeeded);
    }

    public String getParamDec() {
        return paramDec.get();
    }

    public SimpleStringProperty paramDecProperty() {
        return paramDec;
    }

    public void setParamDec(String paramDec) {
        this.paramDec.set(paramDec);
    }

    /**
     * 构造对象
     * @param name      参数名称
     * @param type      参数类型
     * @param needed    参数是否必须
     * @param dec       说明
     */
    public RequestParam(String name, String type, String needed, String dec){
        this.paramName.set(name);
        this.paramType.set(type);
        this.paramNeeded.set(needed);
        this.paramDec.set(dec);

        //默认值
//        this.paramIn.set("0");
    }


}
