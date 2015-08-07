package ctc.transport.message;


public class SQLResponseMessage extends AbstractMessage{
    
	private static final long serialVersionUID = -5815398213077033332L;
	
	private int result;//操作结果标记
	private String list; //SQL语句执行结果  数据格式：JSONDE的String格式，在使用前需要进行格式转换
	private int errorCode; //操作失败原因代码 
	
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getList() {
		return list;
	}
	public void setList(String list) {
		this.list = list;
	}
	
	
}