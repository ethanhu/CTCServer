package ctc.transport.message;

public class P2PCommandMessage extends AbstractMessage {
	private static final long serialVersionUID = 8049743076618793201L;
	
	private int terType; //终端类别  用于区分发向此消息的是CTC终端 或普通车站终端	
	 
	private String userName; //学员账号
	private int runMode; //系统运行方式	
	private int teamID; //本站的组	
	private String timeStep; //时间步长
	private String vrTime; //虚拟时间
	
	private String trainName; //车次名称
	private String direction; //方向
	private String stationName; //本站的车站名称
	private String districtName; //区段名称
	private int result; //本学员操作的结果  
	
	//有关定位到那一步出错的信息以后再确定
	
	
	public String getUserName() {
		return userName;
	}

	public int getTerType() {
		return terType;
	}

	public void setTerType(int terType) {
		this.terType = terType;
	}

	public String getTimeStep() {
		return timeStep;
	}

	public void setTimeStep(String timeStep) {
		this.timeStep = timeStep;
	}

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getRunMode() {
		return runMode;
	}

	public void setRunMode(int runMode) {
		this.runMode = runMode;
	}

	

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
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

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getVrTime() {
		return vrTime;
	}

	public void setVrTime(String vrTime) {
		this.vrTime = vrTime;
	}
	
	
}