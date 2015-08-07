package ctc.transport.message;

public class TDCSCommandMessage extends AbstractMessage {

	private static final long serialVersionUID = 1L;
	
	private int runMode;//运行模式 自动 人工等 
	private int subjectName;//实验主题
	
	private String districtName;//区段名称
	private String stationName; //JSON格式的字符串 车站名称， 可能含有很多车站
	private int teamID; //车站所在组
	private int direction;//车次方向 上行0，下行1
	private String operatedName; //JSON格式的字符串  可以是 车次名称，道岔名称 轨道， 信号灯等 
	private int operationType;//对operatedName进行 的操作类别  支持故障注入 一次支持一种故障类别，否则的话处理起来比较复杂
	
	//对道岔， 轨道， 信号灯等进行故障注入时的定位坐标
	private int originX;//参考坐标中心位置X
	private int originY;//参考坐标中心位置Y
	private int offsetLeftX;//左    相对于中心位置，下面的类同
	private int offsetLeftY;
	private int offsetRightX; //右
	private int offsetRightY;
	private int offsetUpX; //上
	private int offsetUpY;
	private int offsetDownX;//下
	private int offsetDownY;
	
	private String vrTime;//虚拟时间   由教师设定
	private String timeStep;//时间步长 由教师设定
	private String currentTime;//当前时间 由服务器动态获取
	
	private int result;//处理结果 true 或 false    
	
	
	public int getOffsetRightX() {
		return offsetRightX;
	}
	public void setOffsetRightX(int offsetRightX) {
		this.offsetRightX = offsetRightX;
	}
	public int getOffsetRightY() {
		return offsetRightY;
	}
	public void setOffsetRightY(int offsetRightY) {
		this.offsetRightY = offsetRightY;
	}
	public int getOriginX() {
		return originX;
	}
	public void setOriginX(int originX) {
		this.originX = originX;
	}
	public int getOriginY() {
		return originY;
	}
	public void setOriginY(int originY) {
		this.originY = originY;
	}
	public int getOffsetLeftX() {
		return offsetLeftX;
	}
	public void setOffsetLeftX(int offsetLeftX) {
		this.offsetLeftX = offsetLeftX;
	}
	public int getOffsetLeftY() {
		return offsetLeftY;
	}
	public void setOffsetLeftY(int offsetLeftY) {
		this.offsetLeftY = offsetLeftY;
	}
	public int getOffsetUpX() {
		return offsetUpX;
	}
	public void setOffsetUpX(int offsetUpX) {
		this.offsetUpX = offsetUpX;
	}
	public int getOffsetUpY() {
		return offsetUpY;
	}
	public void setOffsetUpY(int offsetUpY) {
		this.offsetUpY = offsetUpY;
	}
	public int getOffsetDownX() {
		return offsetDownX;
	}
	public void setOffsetDownX(int offsetDownX) {
		this.offsetDownX = offsetDownX;
	}
	public int getOffsetDownY() {
		return offsetDownY;
	}
	public void setOffsetDownY(int offsetDownY) {
		this.offsetDownY = offsetDownY;
	}
	public String getCurrentTime() {
		return currentTime;
	}
	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
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
	public int getSubjectName() {
		return subjectName;
	}
	public void setSubjectName(int subjectName) {
		this.subjectName = subjectName;
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
	public int getTeamID() {
		return teamID;
	}
	public void setTeamID(int teamID) {
		this.teamID = teamID;
	}
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public String getOperatedName() {
		return operatedName;
	}
	public void setOperatedName(String operatedName) {
		this.operatedName = operatedName;
	}
	public int getOperationType() {
		return operationType;
	}
	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}
	public String getVrTime() {
		return vrTime;
	}
	public void setVrTime(String vrTime) {
		this.vrTime = vrTime;
	}
	public String getTimeStep() {
		return timeStep;
	}
	public void setTimeStep(String timeStep) {
		this.timeStep = timeStep;
	}
	
	

}