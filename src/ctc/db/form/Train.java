package ctc.db.form;

/**
 * 列车表
 */
public class Train {
	
	public int Train_direction;//车次方向（上行0和下行1）
	public String Train_name;// 车次名称
	public int Train_maxspeed;//车最大速度
	public String Train_startstationname;//始发站名称
	public String Train_endstationname; //到达站名称
	
	
	public int getTrain_direction() {
		return Train_direction;
	}
	public void setTrain_direction(int train_direction) {
		Train_direction = train_direction;
	}
	public String getTrain_name() {
		return Train_name;
	}
	public void setTrain_name(String train_name) {
		Train_name = train_name;
	}
	public int getTrain_maxspeed() {
		return Train_maxspeed;
	}
	public void setTrain_maxspeed(int train_maxspeed) {
		Train_maxspeed = train_maxspeed;
	}
	public String getTrain_startstationname() {
		return Train_startstationname;
	}
	public void setTrain_startstationname(String train_startstationname) {
		Train_startstationname = train_startstationname;
	}
	public String getTrain_endstationname() {
		return Train_endstationname;
	}
	public void setTrain_endstationname(String train_endstationname) {
		Train_endstationname = train_endstationname;
	}

	

}