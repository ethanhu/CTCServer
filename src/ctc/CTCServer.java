package ctc;

/*
 * 功能：启动/关闭服务器，参数（服务器的IP地址和端口）设置
 * 用到的类：创建并管理系统托盘SystemTray
 */

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctc.transport.MinaServer;
import ctc.ui.*;
import ctc.util.ErrorLog;
import ctc.constant.Constants;
import ctc.data.*;

public class CTCServer {

  public final static Logger LOGGER = LoggerFactory.getLogger("CTCServer");
	
  final String IMAGE_PATH = System.getProperty("user.dir")+"/resources/images/tray.png";
  private static final CTCServer app = new CTCServer();
  private static final ConfigureFile configureFile = new ConfigureFile();
  
 // Retain a reference to the main window of the application
  private CTCServerMainWindow mainWindow;  
  private static MinaServer minaServer;
  private Shell shell;
  
  public static void main(String[] args) {
    CTCServer.getApp().run();
  }
  
  public static CTCServer getApp() {
    return app;
  }
  
  /**
   * 获取配制文件信息
   */
  public static ConfigureFile getConfigureFile() {
	  return configureFile;
  }

  public void run() {
      
	  //并没有真正用
	  
 	  PropertyConfigurator.configure(Constants.PATH_LOG4J);
	  //LOGGER.info("启动服务器");
	  	  
	  minaServer = new MinaServer();

	  Display display = new Display();//display 控制事件的循环

	  //定制窗口右上角的按钮
	  shell = new Shell(display,SWT.TITLE|SWT.CLOSE|SWT.MIN|SWT.ON_TOP);

	  mainWindow = new CTCServerMainWindow(shell,minaServer);//应用主窗口


	  //Class c = CTCServer.getApp().getClass();
	  Image TRAY = new Image(shell.getDisplay(),IMAGE_PATH);
	  shell.setImage(TRAY); 

	  shell.setSize(500, 500);   

	  //使窗口处于屏幕中间
	  Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
	  Rectangle shellBounds = shell.getBounds();// 获取屏幕高度和宽度
	  int x = displayBounds.x + (displayBounds.width - shellBounds.width)>>1;
	  int y = displayBounds.y + (displayBounds.height - shellBounds.height)>>1;
	  shell.setLocation(x, y);//定位窗口坐标

	  //显示主窗口
	  shell.open();
	  //当窗体未被关闭时执行循环体内的代码
	  while (!shell.isDisposed()) {
		  //如果未发生事件，通过sleep方法进行监视事件队列
		  /*readAndDispatch() 方法从平台的事件队列中读取事件，并分配他们到合适的处理程序(接收者)。
		只要队列中一直有事件可以处理，这个方法一直返回true，当事件队列为空时，则返回false(因此
		允许用户界面UI线程出于sleep状态直到事件队列不为空)。*/
		  if (!display.readAndDispatch())
			  display.sleep();//If no more entries in event queue
	  }
	  display.dispose();
  }

  /**
   * Gets the main window
   * 
   * @return PasswordMainWindow
   */
  public CTCServerMainWindow getMainWindow() {
	  return mainWindow;
  }


  public void paramSet() {
	  mainWindow.setStatus("当前状态: 设置参数");
	  configureFile.init();
	  //Pop up the paramentry dialog
	  ParamEntryDialog dlg = new ParamEntryDialog(mainWindow.getShell());

	  ParamEntry entry = dlg.open();

	  if (entry != null) {//保存用户新输入的参数
		  configureFile.save(entry);
	  }

  }

  public void startServer(){
	  mainWindow.setStatus("当前状态: 启动服务器");

	  configureFile.init();
	  //启动服务器
	  try {
		  minaServer.start();
		  mainWindow.getCTCServerToolbarFactory().setStartServer(false);
		  mainWindow.getCTCServerToolbarFactory().setCloseServer(true);
		  mainWindow.getCTCServerMenu().setStartServer(false);
		  mainWindow.getCTCServerMenu().setCloseServer(true);
		  mainWindow.setText("服务器已启动! 侦听端口:"+ getConfigureFile().getCtcServerPort());
	  } catch (Exception e) {
		  minaServer.close();
		  mainWindow.setText("启动服务器失败!"+e);
	  }
  }

  public void closeServer(){
	  mainWindow.getCTCServerToolbarFactory().setStartServer(true);
	  mainWindow.getCTCServerToolbarFactory().setCloseServer(false);
	  mainWindow.getCTCServerMenu().setStartServer(true);
	  mainWindow.getCTCServerMenu().setCloseServer(false);
	  minaServer.close();
	  mainWindow.setText("服务器已关闭");
	  mainWindow.setStatus("当前状态: 关闭服务器");

  }

  public void about() {
	  // Display the message box
	  /*MessageBox mb = new MessageBox(mainWindow.getShell(), SWT.ABORT |  SWT.ICON_INFORMATION);
	  mb.setText("关于CTC仿真系统");//消息框的标题
	  mb.setMessage("CTC仿真系统V1.0\n\r" + //消息框的提示文字
	  "发行时间:2009-12-1");
	  mb.open();*/
	  mainWindow.setStatus("操作: 关于");
	  AboutDialog dialog = new AboutDialog(shell);
	  dialog.open();
  }

  public void closeWindow(){

	  if (minaServer != null)
		  minaServer.close();
	  mainWindow.getSystemTray().trayDispose();//释放托盘及其相关资源

	  shell.dispose();
	  System.exit(1);//退出主程序

  }
  //目前没用
  public void closeWindow2(){
	  Listener exitListener = new Listener() {
		  public void handleEvent(Event e) {
			  System.out.println("shell:" + shell);
			  if(shell != null){
				  MessageBox dialog = new MessageBox(shell, SWT.OK | SWT.CANCEL | SWT.ICON_QUESTION);
				  dialog.setText("提示信息");
				  dialog.setMessage("确定退出吗?");
				  if (e.type == SWT.Close)
					  e.doit = false;
				  if (dialog.open() != SWT.OK)
					  return;
			  }
			  minaServer.close();
			  mainWindow.getSystemTray().trayDispose();// 释放托盘及其相关资源
			  //System.exit(1);//退出主程序
			  shell.dispose();
		  }
	  };
	  shell.addListener(SWT.Close, exitListener);
  }

}


