package ctc.db.form;

/**
 * 任务信息
 */
public class TaskPlan {

	private String stationName;//本站站名
	private String trainName;//车次名称
	private String trainDirection;//车次方向
	private String taskType; //任务类型
	private String time;//到站或离站时间	
	private String trackLine;//股道号
	private String startButton; //起始按钮
	private String endButton; //终端按钮
	
	
	public String getTaskType() {
		return taskType;
	}
	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public String getTrainName() {
		return trainName;
	}
	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}
	public String getTrainDirection() {
		return trainDirection;
	}
	public void setTrainDirection(String trainDirection) {
		this.trainDirection = trainDirection;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTrackLine() {
		return trackLine;
	}
	public void setTrackLine(String trackLine) {
		this.trackLine = trackLine;
	}
	public String getStartButton() {
		return startButton;
	}
	public void setStartButton(String startButton) {
		this.startButton = startButton;
	}
	public String getEndButton() {
		return endButton;
	}
	public void setEndButton(String endButton) {
		this.endButton = endButton;
	}
	
		
}
