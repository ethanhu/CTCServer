package ctc.constant;

/*
 * 此类主要用于配置系统用到的一些常量
 */
public class Constants {

	//////////与配置文件有关的常量定义/////////////////////////    
	public final static String PATH_ELOG = "C:\\CTCLogs\\";//日志文件的输出位置
	public static boolean TRACK = true;//设置是否进行日志输出,如果为true,则在程序出现错误时，记录日志
	public static boolean DEBUG = true;//是否开启调试
	public static String DEBUGOF = "console";//调试输出,可选：file/console; file为日志文件，console为控制台输出
	public final static String PATH_LOG4J = System.getProperty("user.dir")+System.getProperty("file.separator")+"resources"+
    System.getProperty("file.separator")  + "config"+System.getProperty("file.separator")+ "log4j.PROPERTIES";

	/////////与数据报文有关常数//////////////////////////
	//
	public static final int SICS_NUMBER = 5;//普通站机SICS的数量
	
	//车次方向. 目前的版本没用 ，  直接使用（上行0，下行1）
	public static final int TDCS_TRAIN_DIRECTION_UP = 0;//上行
	public static final int TDCS_TRAIN_DIRECTION_DOWN = 1;//下行    
	public static final int TIMESTAMP = 60*1000;//定时器延时x秒 
	

	//定义服务器与客户端之间进行通信时之通信模式常量（对应于数据报文中的commandMode域）
	
	/*服务器与客户机之间的同步通信*/
	public static final int MODE_CS_SYN_CLIENT = 0x000001;//发向服务器
	public static final int MODE_CS_SYN_SERVER = 0x000002;//发向客户端
	
	/*服务器与客户机之间的异步通信*/
	public static final int MODE_CS_ASYN_CLIENT = 0x000003;//发向服务器
	public static final int MODE_CS_ASYN_SERVER = 0x000004;//发向客户端
	
	/*服务器与客户机之间的广播通信*/
	public static final int MODE_CS_BROAD_CLIENT = 0x000005;//发向服务器
	public static final int MODE_CS_BROAD_SERVER = 0x000006;//发向客户端
	
	/*客户机之间的同步通信    目前不用。 目前的做法是通过服务器进行转发  用于车站联锁*/
	public static final int MODE_P2P_SYN_UP = 0x000007;//发向上一车站
	public static final int MODE_P2P_SYN_DOWN = 0x000008;//发向下一车站


	/*服务器同监控中心之间的通信 */
	public static final int MODE_SERVER_CTC_SYN = 0x000009;//服务器发向CTC监控中心 同步
	public static final int MODE_SERVER_CTC_ASYN = 0x00000A;//服务器发向CTC监控中心  异步
	public static final int MODE_CTC_SERVER_SYN = 0x00000B;//CTC监控中心发向服务器  同步
	public static final int MODE_CTC_SERVER_ASYN = 0x00000C;//CTC监控中心发向服务器  异步
	
	
	//////////////////////////////////////////////////////////////////////////////////
	//定义服务器与客户端之间进行通信时之通信类别常量（对应于数据报文中的commandType域）
	
	/*与用户登录系统有关的常量*/   
	public static final int TYPE_CLIENT_LOGIN = 0x100001;//发向服务器   
	public static final int TYPE_LOGIN_RESPONSE = 0x100002;//发向客户端 
	
	/*与用户退出系统有关的常量*/
	public static final int TYPE_CLIENT_LOGOUT = 0x100003; //发向服务器  
	public static final int TYPE_LOGOUT_RESPONSE = 0x100004; //发向客户端

	/*与服务器退出运行有关的常量*/
	public static final int TYPE_SERVER_LOGOUT = 0x100005; //发向客户端
	
	/*服务器发送实验开始*/
	public static final int TYPE_SERVER_EXPERIMENT_RUN = 0x100006; //发向客户端
	
	/*客户机发向服务器  用于客户机之间通过服务器进行通信*/
	public static final int TYPE_CLIENT_P2P_SYN_UP = 0x100007;//发向上一车站   同步
	public static final int TYPE_CLIENT_P2P_SYN_DOWN = 0x100008;//发向下一车站 同步
	
	public static final int TYPE_CLIENT_P2P_ASYN_UP = 0x100009;//发向上一车站  异步
	public static final int TYPE_CLIENT_P2P_ASYN_DOWN = 0x10000A;//发向下一车站 异步
	
	/*SICS与CTC之间通信*/	
	public static final int TYPE_CTC_TO_SICS_SYN  = 0x10000B;//CTC发向SICS 同步
	public static final int TYPE_CTC_TO_SICS_ASYN  = 0x10000C;//CTC发向SICS 异步
	public static final int TYPE_SICS_TO_CTC_SYN  = 0x10000D;//SICS发向CTC 同步
	public static final int TYPE_SICS_TO_CTC_ASYN  = 0x10000E;//SICS发向CTC	
	//--hu 2010-7-18--//
	public static final int TYPE_SICS_TO_ZNTDCS_ASYN  = 0x10800E;//SICS发向ZNTDCS
	//hu 2010-11-3
	public static final int TYPE_DDZR_TO_ZNTDCS_ASYN  = 0x109001;//DDZR发向TDCS
	public static final int TYPE_DDZR_TO_ZNCTC_ASYN  = 0x109002;//DDZR发向CTC
	public static final int TYPE_DDZR_TO_DW_ASYN  = 0x109003;//DDZR发向DW
	public static final int TYPE_DDZR_TO_SICS_ASYN  = 0x109004;//DDZR发向SICS
	
	public static final int MESSAGE_TYPE_CTC_TO_SICS_RJC  = 0x10000F;//接发车
	public static final int MESSAGE_TYPE_SICS_TO_CTC_RJC  = 0x100010;//接发车
	public static final int MESSAGE_TYPE_CTC_TO_SICS_DCZDW  = 0x100011;//道岔总定位
	public static final int MESSAGE_TYPE_SICS_TO_CTC_DCZDW  = 0x100012;//道岔总定位
	public static final int MESSAGE_TYPE_CTC_TO_SICS_DCDC  = 0x100013;//道岔单操
	public static final int MESSAGE_TYPE_SICS_TO_CTC_DCDC  = 0x100014;//道岔单操
	
	/*与SQL操作有关的常量*/
	public static final int TYPE_CLIENT_SQLQUERY = 0x100015; //发向服务器  
	public static final int TYPE_SQLQUERY_RESPONSE = 0x100016; //发向客户端
	
	public static final int TYPE_CLIENT_SQLUPDATE = 0x100017; //发向服务器  
	public static final int TYPE_SQLUPDATE_RESPONSE = 0x100018; //发向客户端
	
	public static final int TYPE_CLIENT_SQLDELETE = 0x100019; //发向服务器  对一个库进行操作
	public static final int TYPE_SQLDELETE_RESPONSE = 0x10001A; //发向客户端
	
	public static final int TYPE_CLIENT_SQLINSERT = 0x10001B; //发向服务器  插入一条纪录
	public static final int TYPE_SQLINSERT_RESPONSE = 0x10001C; //发向客户端
	
	public static final int TYPE_CLIENT_SQLBATCHAPPEND = 0x10001D; //发向服务器  利用事务处理   追加多条纪录 （不对库中原有记录进行任何操作）
	public static final int TYPE_CLIENT_SQLBATCHUPDATE = 0x10001E; //发向服务器  利用事务处理  更新多条纪录 （先执行一条删除sql，再执行多条插入sql）
	public static final int TYPE_CLIENT_SQLBATCHINSERTDEELETE = 0x100024; //批量删除和批量插入
	
	public static final int TYPE_CLIENT_SQLBATCHDELETE = 0x10001F; //发向服务器  利用事务处理  删除不同表中的记录
	
	public static final int TYPE_CLIENT_EXPERIMENT_ENV = 0x100020; //发向服务器 设置实验参数
	public static final int TYPE_CLIENT_EXPERIMENT_START = 0x100021; //发向服务器   启动实验
	public static final int TYPE_CLIENT_EXPERIMENT_CLOSE = 0x100022; //发向服务器  关闭实验
	public static final int TYPE_CLIENT_EXPERIMENT_RUN = 0x100023; //发向服务器   运行实验
	
	public static final int TYPE_CLIENT_TDCS_SCHEDULE = 0x100024; //TDCS 调度命令
	public static final int TYPE_CLIENT_TDCS_ERROR = 0x100025; //TDCS 故障注入
	public static final int TYPE_CLIENT_TDCS_CAMERA = 0x100026; //TDCS自动截屏
	public static final int TYPE_CLIENT_TDCS_FILE = 0x100027; //TDCS传送文件
	public static final int TYPE_CLIENT_TDCS_TIMER = 0x100028; //TDCS 定时器
	
	public static final int TYPE_CLIENT_ZNTDCS_DELETE = 0x1001001; //组内删除车次信息
	public static final int TYPE_CLIENT_ZNTDCS_ADD = 0x1001002; //组内添加车次信息
	public static final int TYPE_CLIENT_ZNTDCS_ADJUST = 0x1001003; //组内移动车次信息
	
	public static final int TYPE_CLIENT_SICS_TO_TDCS = 0x1001004; //组内普通站机SICS发送消息到组内TDCS
	public static final int TYPE_CLIENT_TDCS_TO_SICS = 0x1001005; //组内TDCS发送消息到组内普通站机SICS 目前没用
	
	//定义与TDCS通信有关的通信类别命令代码 CommandType
	public static final int TYPE_TEACHER_TDCS_START = 0x100300; //发向服务器   开始进行实验 发送时间信息到服务器
	
    /////////与用户界面有关的常数////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/*教师登录界面*/
	public final static int TEACHER_COUNT_DOWN = 0x010001;//启动倒计时
	public final static int TEACHER_COUNT_UP = 0x010002;//启动计时器
	
	public final static int STUDENT_INFO_INPUT = 0x010003;//学员信息录入
	public final static int STUDENT_INFO_SEARCH = 0x010004;//学员信息查询
	public final static int TEACHER_PASSWORD_UPDATE = 0x010005;//教师信息维护
	
	public final static int TEACHER_STATION_CREATE =  0x010006;//车站概况创建
	public final static int TEACHER_STATION_SEARCH =  0x010007;//车站概况查询
	public final static int TEACHER_STATION_BROWSER = 0x010008;//车站概况浏览
	
	public final static int TEACHER_DISTRICT_CREATE =  0x010009;//区段创建
	public final static int TEACHER_DISTRICT_SEARCH =  0x01000A;//区段查询
	public final static int TEACHER_DISTRICT_BROWSER = 0x01000B;//区段浏览
	
	public final static int TEACHER_TRAIN_CREATE =  0x01000C;//车次创建
	public final static int TEACHER_TRAIN_SEARCH =  0x01000D;//车次查询
	public final static int TEACHER_TRAIN_BROWSER = 0x01000E;//车次浏览
	
	public final static int TEACHER_DISTRICT_STATION_CREATE  = 0x01000F;//车站与区段关系创建
	public final static int TEACHER_DISTRICT_STATION_SEARCH  = 0x010010;//车站与区段关系查询

	public final static int TEACHER_DISTRICT_TRAIN_CREATE  = 0x010011;//车次与区段关系创建
	public final static int TEACHER_DISTRICT_TRAIN_SEARCH  = 0x010012;//车次与区段关系查询
	
	public final static int TEACHER_TRAIN_PLAN_CREATE  = 0x010013;//列车原始计划创建
	public final static int TEACHER_TRAIN_PLAN_SEARCH  = 0x010014;//列车原始计划查询
	
	public final static int CTC_STATION_LAYOUT = 0x010015;//车站站场图绘制
	public final static int CTC_TRAIN_PLAN_LAYOUT = 0x010016;//行车计划
	
	public final static int EXPERIMENT_SET = 0x010017;//设置实验
	public final static int EXPERIMENT_START = 0x010018;//启动实验
	public final static int EXPERIMENT_CLOSE = 0x010019;//关闭实验
	
	/**组内通信标记*/
	public final static int TDCS_START_RSB_CTC = 0x500001;//服务器转向指定组内的RSB区间闭塞员及CTC控制台发送区段内所有车次信息
	
	public final static int TDCS_TEAM_NAME = 0x500002;//从服务器获取组的名称
	public final static int TDCS_TEAM_MEMBER_NAME = 0x500003;//从服务器获取组内成员的名称
	public final static int TDCS_ERROR_COMMAND = 0x500004;//发送故障命令
	public final static int TDCS_SCHEDULE_COMMAND = 0x500005;//发送调度命令
	
	
	/*管理员登录界面*/
	public final static int ADMIN_PASSWORD_UPDATE = 0x01001A;//管理员自身用户名及密码维护
	public final static int ADMIN_TEACHER_UPDATE = 0x01001B;//教师用户名及密码维护
	public final static int ADMIN_TEACHER_INPUT = 0x01001C;//教师信息录入
	public final static int ADMIN_TEACHER_SEARCH = 0x01001D;//教师信息查询
	
   //////////////////////////////////////////////////////////////////////////////////
	//定义服务器与客户端之间进行通信时用到的一些常量（对应于数据报文中的userRole域）
	/*与用户角色有关的常量*/
	public static final int USER_ROLE_NONE = 0x001000;//非法用户
	public static final int USER_ROLE_TEACHER = 0x001001;//教师  4097
	public static final int USER_ROLE_STUDENT = 0x001002;//学员
	public static final int USER_ROLE_SERVER = 0x001003;//服务器
	public static final int USER_ROLE_TUTOR = 0x001004;//教师或管理员, 主要用于登录界面 4100
	public static final int USER_ROLE_ADMIN = 0x001005;//管理员
	public static final int USER_ROLE_CTC = 0x001006;//CTC集中调度用户 
	public static final int USER_ROLE_P2P = 0x001007;//站间显示用户
	public static final int USER_ROLE_SICS = 0x001008;//SICS用户 普通站机
	public static final int USER_ROLE_ZNTDCS = 0x001009;//组内TDCS学员
	
	//hu 2010-11-3
	public static final int USER_ROLE_TEACHER_DDZR = 0x001010; //调度主任
	public static final int USER_ROLE_TEACHER_GZSZ = 0x001011; //故障设置
	
	
	/*与实验模式有关的常量 （对应于数据报文中的commandItem域）*/
	public static final int EXPERIMENT_MODE_NONE = 0x000100;//没有指定
	public static final int EXPERIMENT_MODE_TDCS = 0x000101;//行车调度实验
	public static final int EXPERIMENT_MODE_SICS = 0x000102;//车站联锁
	public static final int EXPERIMENT_MODE_TDSI = 0x000103;//综合实验

	
	/*与系统运行模式有关的常量  （对应于数据报文中的commandItem域）*/
	public static final int RUN_MODE_AUTO = 0x000104;//系统自动演示
	public static final int RUN_MODE_MANUAL = 0x000105;//由学员或教师手动操作
	
	/*分配给该学员的角色类别: 车站连锁,行车计划, CTC*/
	public static final int TERMINAL_TYPE_CTC = 0x000106;//CTC调度中心 发向下一站
	public static final int TERMINAL_TYPE_SICS = 0x000107;//车站联锁
	public static final int TERMINAL_TYPE_TDCS = 0x000108;//行车调度
	public static final int TERMINAL_TYPE_CTC_SWITCH = 0x000109;//CTC调度中心直接转发
	public static final int TERMINAL_TYPE_ZNTDCS = 0x000110;//组内TDCS行车调度员
	public static final int TERMINAL_TYPE_RSB = 0x000111;//组内RSB区间
	//hu 2010-11-3
	public static final int TERMINAL_TYPE_DW = 0x000112;//组内DW区间
	
	/*与服务器对用户请求进行处理结果有关的常量  （对应于数据报文中的result域）*/
	public static final int SERVER_RESULT_OK = 0x808001;//操作 成功   发向客户端功
	public static final int SERVER_RESULT_ERROR = 0x808002;//操作失败   发向客户端
	public static final int SERVER_RESULT_RLOGIN = 0x808003;//重复登录
	public static final int SERVER_ALLOCATE_ERROR = 0x808004;//分配车站信息有错
	public static final int SERVER_ALLOCATE_NOSTART = 0x808005;//实验还没有开始
	public static final int CLIENT_RESULT_OK = 0x808007;//操作 成功   客户机发向服务器或邻近的车站
	public static final int CLIENT_RESULT_ERROR = 0x808008;//操作失败  客户机 发向服务器或邻近的车站
	public static final int SERVER_RESULT_NOPERMISSION  = 0x808009;//非法用户

	public static final int CLIENT_CLOSE_NORMAL = 0x808009;//客户端正常退出，
	public static final int CLIENT_CLOSE_RLOGIN = 0x80800A;//客户端试图二次登陆退出
	public static final int CLIENT_CLOSE_ONEMORE = 0x80800B;//已有教师或集中调度CTC登录到 系统
	
	//行车调度界面车次操作类别
	public static final int TDCS_TRAIN_TYPE_NO = 0x700001;//对原计划(指从数据库获取的，下同)车次没有进行任何操作 33
	public static final int TDCS_TRAIN_TYPE_ADJUST = 0x700002;//对原计划车次进行调整34
	public static final int TDCS_TRAIN_TYPE_DELETE = 0x700003;//对原计划车次进行删除35
	public static final int TDCS_TRAIN_TYPE_SAVE = 0x700004;//对原计划车次进行删除 且已经进行过保存操作
	
	/*对于新添加的车次，情况1：初始状态为：TDCS_TRAIN_TYPE_NEW， 保存后为：TDCS_TRAIN_TYPE_NEW_SAVE  保存后进行删除为：TDCS_TRAIN_TYPE_NEW_DEELETE
	                                                  情况2：初始状态为：TDCS_TRAIN_TYPE_NEW， 进行删除，然后保存 .此情况操作类别trainType始终不变
	*/
	public static final int TDCS_TRAIN_TYPE_NEW = 0x700005;//新添加的车次信息36
	public static final int TDCS_TRAIN_TYPE_NEW_SAVE_DEELETE = 0x700006;//对新添加的车次信息进行删除前，已进行了保存操作
	public static final int TDCS_TRAIN_TYPE_NEW_SAVE = 0x700007;//对新添加的车次信息 已经进行过保存
	public static final int TDCS_TRAIN_TYPE_NEW_ADJUST = 0x700008;//对新添加的线没有进行保存操作前,进行调整
	public static final int TDCS_TRAIN_TYPE_NEW_SAVE_ADJUST = 0x700009;//对新添加,并保存过的线,进行调整
	
	public static final int TDCS_TRAIN_TYPE_RUN = 0x70000A;//实际运行的车次
	
	
	//时间类别 为sortedPlan所用
	public static final int TDCS_TIME_TYPE_NONE = 0x600000; 
	public static final int TDCS_TIME_TYPE_ARRIVEAL = 0x600001;//6291457 到站
	public static final int TDCS_TIME_TYPE_LEAVE = 0x600002;//6291458 离站
	public static final int TDCS_TIME_TYPE_DOUBLE = 0x600003;//到站和离站两种情况

    //新绘制车次时用 
	public static final int TDCS_TRAINLINE_START = 0x600008;
	public static final int TDCS_TRAINLINE_END = 0x600009;
	public static final int TDCS_TRAINLINE_NONE = 0x60000A;
	
	//行车调度界面操作常量 同菜单相关联
	public static final int TDCS_MENU_TOOL_NO = 0x900001;//用户没有选取任何操作
	public static final int TDCS_MENU_TOOL_RECTANGLE_ADJUST = 0x900002;//用户选取表示车次的直线的上端或下端进行时间调整
	public static final int TDCS_MENU_TOOL_LINE_PARALLEL = 0x900003;//用户选取表示车次的直线进行平移操作

}
