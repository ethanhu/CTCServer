package ctc.transport.message;

public class ExperimentCommandResponseMessage extends AbstractMessage {   

	private static final long serialVersionUID = -7514003920439463075L;
	
	private int result;//服务器处理结果    
	
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	
}  