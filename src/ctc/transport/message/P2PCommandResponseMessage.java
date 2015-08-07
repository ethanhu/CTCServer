package ctc.transport.message;

public class P2PCommandResponseMessage extends AbstractMessage {   

	private static final long serialVersionUID = -7514003920439463075L;
    private int terType; //终端类别  用于区分服务器是发向CTC终端 或普通车站终端	

	private int result;//服务器处理结果
	private String userName; //用户账号
	
	private String vrTime; //虚拟时间 由教师设置
	private String timeStep; //时间步长  由教师设置
	private String currentTime;//当前时间 由服务器动态获取
	
	private int runMode; //系统运行方式
	private String districtName; //车站区段名称
	private String trainName; //车次名称
	private int teamID; //组号
	private String stationName; //记录客户端发送此消息的车站名称
	private String trainPlan; //经过该车站的所有车次信息list2json
	private int experimentMode;//本次实验主题
	

	public String getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}
	public int getTerType() {
		return terType;
	}
	public void setTerType(int terType) {
		this.terType = terType;
	}
	
	public int getExperimentMode() {
		return experimentMode;
	}
	public void setExperimentMode(int experimentMode) {
		this.experimentMode = experimentMode;
	}
	public String getTrainPlan() {
		return trainPlan;
	}
	public void setTrainPlan(String trainPlan) {
		this.trainPlan = trainPlan;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTimeStep() {
		return timeStep;
	}
	public void setTimeStep(String timeStep) {
		this.timeStep = timeStep;
	}
	public String getVrTime() {
		return vrTime;
	}
	public int getRunMode() {
		return runMode;
	}
	public void setRunMode(int runMode) {
		this.runMode = runMode;
	}
	
	public int getTeamID() {
		return teamID;
	}
	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}
	
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getTrainName() {
		return trainName;
	}
	public void setTrainName(String trainName) {
		this.trainName = trainName;
	}
	public String getStationName() {
		return stationName;
	}
	public void setStationName(String stationName) {
		this.stationName = stationName;
	}
	public void setVrTime(String vrTime) {
		this.vrTime = vrTime;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	
}  