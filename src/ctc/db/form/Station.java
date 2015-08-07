package ctc.db.form;

/**
 * 车站表
 */
public class Station {

	public String Station_name;// 车站名称
	public int Station_downnumber;// 车站下行可用车道
	public int Station_upnumber;// 车站上行可用车道
	public String Station_graph;// 站场图
	

	public Station(){}
	
	public String getStation_name() {
		return Station_name;
	}

	public void setStation_name(String station_name) {
		Station_name = station_name;
	}

	public int getStation_downnumber() {
		return Station_downnumber;
	}

	public void setStation_downnumber(int station_downnumber) {
		Station_downnumber = station_downnumber;
	}

	public int getStation_upnumber() {
		return Station_upnumber;
	}

	public void setStation_upnumber(int station_upnumber) {
		Station_upnumber = station_upnumber;
	}

	public String getStation_graph() {
		return Station_graph;
	}

	public void setStation_graph(String station_graph) {
		Station_graph = station_graph;
	}

}