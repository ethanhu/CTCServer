package ctc.transport.message;

public class LoginResponseMessage extends AbstractMessage {   

	private static final long serialVersionUID = 7933708092631973566L;
	
	private int result;//服务器处理结果    
	
	private int runMode; //系统运行模式
	private int experimentMode;//本次实验主题
	private String districtName; //区段名称
	private String trainPlan; //经过该车站(对普通站机)或所有车站(对TDCS CTC RSB)的所有车次信息list2json
	private String stationsList; //某区间内所有车站信息(对CTC RSB)
	private String stationName; //分配给某学员的车站名称
	private int teamID; //分配给某学员的车站名称所在组号
	private int terType;//分配给该学员的角色类别: 车站连锁,行车计划, CTC
	
	public String getStationsList() {
		return stationsList;
	}
	public void setStationsList(String stationsList) {
		this.stationsList = stationsList;
	}
	public int getTerType() {
		return terType;
	}
	public void setTerType(int terType) {
		this.terType = terType;
	}
	public int getTeamID() {
		return teamID;
	}
	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}
	public String getTrainPlan() {
		return trainPlan;
	}
	public void setTrainPlan(String trainPlan) {
		this.trainPlan = trainPlan;
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
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public int getRunMode() {
		return runMode;
	}
	public void setRunMode(int runMode) {
		this.runMode = runMode;
	}
	public int getExperimentMode() {
		return experimentMode;
	}
	public void setExperimentMode(int experimentMode) {
		this.experimentMode = experimentMode;
	}
	


}  