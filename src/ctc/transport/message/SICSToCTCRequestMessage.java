package ctc.transport.message;

public class SICSToCTCRequestMessage extends AbstractMessage {
	private static final long serialVersionUID = 1L;
	
	private int Team_id;//小组ID 
	private String stationName;  //车站名称
	private String taskName;   //任务名称 接车 发车
	private String directionName;//下行 上行
	private String trackName;//路名1 2  3 
	private String methodName; //方法名称 如operateCTCStation
	private String trainName;//车次名称
	
	private int result;//操作结果 成功或失败

	
	public String getTrainName() {
		return trainName;
	}

	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}
	
	public int getTeam_id() {
		return Team_id;
	}

	public void setTeam_id(int team_id) {
		Team_id = team_id;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	
	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getDirectionName() {
		return directionName;
	}

	public void setDirectionName(String directionName) {
		this.directionName = directionName;
	}

	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}


	
}