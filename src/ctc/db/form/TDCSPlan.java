package ctc.db.form;

import ctc.constant.*;

public class TDCSPlan {

	public String District_name;//区段名称
	public String Plan_arrivestationtime;//到站时间
	public String Plan_leavestationtime;//离站时间
	public String Prestation_name;//前站名称
	public String Station_name;//本站名称
	public String Train_name;//列车车次
	
	//以上信息同库表Plan的结构完全一致
	
	public int TimeType;//时间类别 到站/离站等 TDCS_TIME_TYPE_ARRIVEAL = 0x600001;//6291457 到站 ； TDCS_TIME_TYPE_LEAVE = 0x600002;//6291458 离站

	public int TrainDirection;//车次方向  上行0和下行1
	public int TrainLine;//股道号

	public TDCSPlan(){}
	
	//用于调整车次
	public TDCSPlan(String station_name,String train_name,int timeType,String time) {
		Station_name = station_name;
		Train_name = train_name;
		TimeType = timeType;
		if(timeType == Constants.TDCS_TIME_TYPE_ARRIVEAL)
			Plan_arrivestationtime = time;
		else
			Plan_leavestationtime = time;
	}
	
	//用于删除车次
	public TDCSPlan(String prestation_name,String station_name,String train_name) {
		Prestation_name = prestation_name;
		Station_name = station_name;
		Train_name = train_name;
	}
	
	//用于新添加新车次
	public TDCSPlan(String plan_arrivestationtime, String plan_leavestationtime,
			String district_name, String prestation_name, String station_name,
			String train_name,int timeType) {
		Plan_arrivestationtime = plan_arrivestationtime;
		Plan_leavestationtime = plan_leavestationtime;
		District_name = district_name;
		Prestation_name = prestation_name;
		Station_name = station_name;
		Train_name = train_name;
		TimeType = timeType;
	}
	
	
	public TDCSPlan(String plan_arrivestationtime, String plan_leavestationtime,
			String district_name, String prestation_name, String station_name,
			String train_name,int trainDirection,int trainLine) {
		Plan_arrivestationtime = plan_arrivestationtime;
		Plan_leavestationtime = plan_leavestationtime;
		District_name = district_name;
		Prestation_name = prestation_name;
		Station_name = station_name;
		Train_name = train_name;
		TrainDirection = trainDirection;
		TrainLine = trainLine;;
	}
	

	public int getTimeType() {
		return TimeType;
	}

	public void setTimeType(int timeType) {
		TimeType = timeType;
	}

	public int getTrainDirection() {
		return TrainDirection;
	}

	public void setTrainDirection(int TrainDirection) {
		this.TrainDirection = TrainDirection;
	}

	public int getTrainLine() {
		return TrainLine;
	}

	public void setTrainLine(int TrainLine) {
		this.TrainLine = TrainLine;
	}
	
	public String getPlan_arrivestationtime() {
		return Plan_arrivestationtime;
	}
	public void setPlan_arrivestationtime(String plan_arrivestationtime) {
		Plan_arrivestationtime = plan_arrivestationtime;
	}
	public String getPlan_leavestationtime() {
		return Plan_leavestationtime;
	}
	public void setPlan_leavestationtime(String plan_leavestationtime) {
		Plan_leavestationtime = plan_leavestationtime;
	}
	public String getDistrict_name() {
		return District_name;
	}
	public void setDistrict_name(String district_name) {
		District_name = district_name;
	}
	public String getPrestation_name() {
		return Prestation_name;
	}
	public void setPrestation_name(String prestation_name) {
		Prestation_name = prestation_name;
	}
	public String getStation_name() {
		return Station_name;
	}
	public void setStation_name(String station_name) {
		Station_name = station_name;
	}
	public String getTrain_name() {
		return Train_name;
	}
	public void setTrain_name(String train_name) {
		Train_name = train_name;
	}

	

}