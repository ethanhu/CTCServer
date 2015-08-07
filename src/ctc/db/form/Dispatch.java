package ctc.db.form;

/**
 * 存储行车调度员调整后的原始计划表plan的车次等信息
 */
public class Dispatch {

	public String Dispatch_arrivestationtime;//到站时间
	public String Dispatch_leavestationtime;//离站时间
	public String District_name;//区段名称
	public String Operator_name; //操作者姓名
	public String Prestation_name;//前站站名
	public String Station_name;//本站站名
	public String Train_name;//列车名称
	public int  Train_Type;//操作类别
	
	
	public int getTrain_Type() {
		return Train_Type;
	}
	public void setTrain_Type(int train_Type) {
		Train_Type = train_Type;
	}
	public String getDispatch_arrivestationtime() {
		return Dispatch_arrivestationtime;
	}
	public void setDispatch_arrivestationtime(String dispatch_arrivestationtime) {
		Dispatch_arrivestationtime = dispatch_arrivestationtime;
	}
	public String getDispatch_leavestationtime() {
		return Dispatch_leavestationtime;
	}
	public void setDispatch_leavestationtime(String dispatch_leavestationtime) {
		Dispatch_leavestationtime = dispatch_leavestationtime;
	}
	public String getDistrict_name() {
		return District_name;
	}
	public void setDistrict_name(String district_name) {
		District_name = district_name;
	}
	public String getOperator_name() {
		return Operator_name;
	}
	public void setOperator_name(String operator_name) {
		Operator_name = operator_name;
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