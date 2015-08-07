package ctc.transport.message;

/**如果普通站机对某趟车次进行收发后发消息到TDCS的话,需要设置rTime和sTime,同时设置timeType =Constants.TDCS_TIME_TYPE_DOUBLE 
 * 
 * 如果普通站机对某趟车次进行收和发操作后分别发消息到TDCS的话,对于接车消息需要设置:
 * timeType = constants.TDCS_TIME_TYPE_ARRIVEAL;//到站
 * 和rTime  prestationName;//前站站名  stationName; 本站站名
 * 
 * 或对于发车消息需要设置
 * timeType = constants.TDCS_TIME_TYPE_LEAVE;//离站
 * sTime stationName;本站站名  sucstationName;//后站站名(需要编写代码来确定 也可以不填写,目前TDCS动态获取):将来可以考虑在plan表存储 
 * */
public class TrainLineAnchorMessage extends AbstractMessage {   

	private static final long serialVersionUID = 7933708092631973566L;
	private int teamID; //组号 必须项
	private String prestationName;//前站站名 必须项
	private String stationName;//本站站名 必须项
	private String sucstationName;//后站站名  
	private String trainName; //车次名称(车次)  必须项 
	private String rTime;//接车时间  实际时间值 等价于 从数据库得到的的车次到站或离站的时间
	private String sTime;//发车时间
	
	private int timeType;//时间类别 到站/离站等 Constants.TDCS_TIME_TYPE_ARRIVEAL到站 ；  Constants.TDCS_TIME_TYPE_LEAVE离站
	
	private int trainDirection;//车次方向（上行0和下行1）

	private int result;//服务器处理结果

	public String getSucstationName() {
		return sucstationName;
	}

	public void setSucstationName(String sucstationName) {
		this.sucstationName = sucstationName;
	}

	public TrainLineAnchorMessage(){	}

	public int getTeamID() {
		return teamID;
	}

	public void setTeamID(int teamID) {
		this.teamID = teamID;
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

	public int getTrainDirection() {
		return trainDirection;
	}

	public void setTrainDirection(int trainDirection) {
		this.trainDirection = trainDirection;
	}

	public int getTimeType() {
		return timeType;
	}

	public void setTimeType(int timeType) {
		this.timeType = timeType;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}



	public String getRTime() {
		return rTime;
	}

	public void setRTime(String time) {
		rTime = time;
	}

	public String getSTime() {
		return sTime;
	}

	public void setSTime(String time) {
		sTime = time;
	}

	public String getPrestationName() {
		return prestationName;
	}

	public void setPrestationName(String prestationName) {
		this.prestationName = prestationName;
	}
	
	
}  