package ctc.transport.message;

public class TeamTdcsRsbMessage extends AbstractMessage {

	private static final long serialVersionUID = 7933708092631973566L;

	private String districtName; // 区段名称
	private String stationsList; // 某区间内所有车站信息
	private String stationName; // 车站名称
	private String trainName; // 车次名称(车次)
	private String trainPlan;// 所有车次信息或某一车次信息tdcsPlanList的list2json字符串
	private String trainPlan1;// 车站1的计划
	private String trainPlan2;// 车站2的计划
	private String trainPlan3;// 车站3的计划
	private String trainPlan4;// 车站4的计划
	private String trainPlan5;// 车站5的计划

	private int teamID; // 组号
	private int terType; // 终端类别TDCS,SICS,RSB,CTC

	public int getTerType() {
		return terType;
	}

	public void setTerType(int terType) {
		this.terType = terType;
	}

	private int result;// 服务器处理结果

	public TeamTdcsRsbMessage() {

	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getStationsList() {
		return stationsList;
	}

	public void setStationsList(String stationsList) {
		this.stationsList = stationsList;
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

	public String getTrainPlan() {
		return trainPlan;
	}

	public void setTrainPlan(String trainPlan) {
		this.trainPlan = trainPlan;
	}

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public String getTrainPlan1() {
		return trainPlan1;
	}

	public void setTrainPlan1(String trainPlan1) {
		this.trainPlan1 = trainPlan1;
	}

	public String getTrainPlan2() {
		return trainPlan2;
	}

	public void setTrainPlan2(String trainPlan2) {
		this.trainPlan2 = trainPlan2;
	}

	public String getTrainPlan3() {
		return trainPlan3;
	}

	public void setTrainPlan3(String trainPlan3) {
		this.trainPlan3 = trainPlan3;
	}

	public String getTrainPlan4() {
		return trainPlan4;
	}

	public void setTrainPlan4(String trainPlan4) {
		this.trainPlan4 = trainPlan4;
	}

	public String getTrainPlan5() {
		return trainPlan5;
	}

	public void setTrainPlan5(String trainPlan5) {
		this.trainPlan5 = trainPlan5;
	}

}