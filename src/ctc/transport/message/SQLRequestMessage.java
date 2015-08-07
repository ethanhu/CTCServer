package ctc.transport.message;

public class SQLRequestMessage extends AbstractMessage {
    
	private static final long serialVersionUID = -7641476032160324590L;
	
	private String dataBean;//进行反射时所用javabean的名称
	private String sql;//SQL语句
	private String params;//SQL语句的参数。 JSON格式的字符串，原始数据的格式：Object [] params或 null（表示上面的SQL是无参数SQL语句）保存insert update命令
	private String sql_1;
	private String Params_1;
	
	public String getSql_1() {
		return sql_1;
	}
	public void setSql_1(String sql_1) {
		this.sql_1 = sql_1;
	}
	public String getParams_1() {
		return Params_1;
	}
	public void setParams_1(String params_1) {
		Params_1 = params_1;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getDataBean() {
		return dataBean;
	}
	public void setDataBean(String dataBean) {
		this.dataBean = dataBean;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}

   
}