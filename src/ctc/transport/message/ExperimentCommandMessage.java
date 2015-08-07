package ctc.transport.message;

public class ExperimentCommandMessage extends AbstractMessage {

	private static final long serialVersionUID = -2012284452330859193L;

	int runItem;//运行模式
	int subjectItem; //实验主题
	String districtName; //区段Name
	String time;//虚拟时间
	String timeStep;//时间步长
	
	
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getTimeStep() {
		return timeStep;
	}
	public void setTimeStep(String timeStep) {
		this.timeStep = timeStep;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	public int getRunItem() {
		return runItem;
	}
	public void setRunItem(int runItem) {
		this.runItem = runItem;
	}
	public int getSubjectItem() {
		return subjectItem;
	}
	public void setSubjectItem(int subjectItem) {
		this.subjectItem = subjectItem;
	}

}