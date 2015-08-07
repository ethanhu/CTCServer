package ctc.ui;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;

import ctc.CTCServer;
import ctc.transport.MinaServer;
import ctc.util.DateUtil;
import ctc.util.SystemTray;

//import data.*;
//import handlers.*;

/**
 * This class represents the main window of the application
 */
public class CTCServerMainWindow {
  

  private Shell shell;
  private Text text;
  private Text statusLine;
  
  private static SystemTray sysTray = new SystemTray();
  private static CTCServerToolbarFactory ctcServerToolbarFactory = new CTCServerToolbarFactory();
  private static CTCServerMenu ctcServerMenu;
  
  private static MinaServer minaServer;

  /**
   * Constructs a PasswordMainWindow
   * @param newShell the parent shell
   */
  public CTCServerMainWindow(Shell newShell,MinaServer minaServer) {
	this.minaServer = minaServer;
    shell = newShell;
    //shell.setText("CTC仿真系统之服务器");
    shell.setText("分散自律调度集中仿真软件-服务器");
     
    createContents();
    

    //监听关闭窗口事件,对应窗口右上角的关闭按钮
	shell.addShellListener(new org.eclipse.swt.events.ShellAdapter() {
        public void shellClosed(org.eclipse.swt.events.ShellEvent e) {
         	CTCServer.getApp().closeWindow();    	
        }
    });
  }

  /**
   * Gets the shell (main window) of the application
   * @return Shell
   */
  public Shell getShell() {
    return shell;
  }
  public SystemTray getSystemTray() {
	    return sysTray;
  }
  
  public CTCServerToolbarFactory getCTCServerToolbarFactory() {
	    return ctcServerToolbarFactory;
  }
  public CTCServerMenu getCTCServerMenu() {
	    return ctcServerMenu;
  }
  
  //向Text追加信息
  public void setText(String content) {
    String buffer = DateUtil.getNowDateMedium() + " : " + content + "\n\r";
    text.append(buffer);
  }
  public void setStatus(String content) {
	    
	  statusLine.setText(content);
}

  /**
   * Creates the window contents
   */
  private void createContents() {
    //shell.setLayout(new FormLayout());
	//对应此布局的代码已注释掉，见下

	//创建系统托盘
	sysTray.createSysTray(shell,minaServer);
    
	//构造菜单 
	ctcServerMenu = new CTCServerMenu(shell);
    shell.setMenuBar(ctcServerMenu.getMenu());

   //构造工具栏
  //  ToolBar toolbar = CTCServerToolbarFactory.create(shell);
    ToolBar toolbar = ctcServerToolbarFactory.create(shell);
   /* FormData data = new FormData();
    data.top = new FormAttachment(0, 0);
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, 0);
    toolbar.setLayoutData(data);
  */  toolbar.pack();
    
    //创建多行Text只写组件，包含边框，自动换行，包括垂直滚动条.用于纪录用户操作信息
	text = new Text(shell, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.WRAP| SWT.V_SCROLL);
	text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WHITE));//设置背景色
    text.setBounds(20,30, 450, 385);
  /*  data = new FormData();
    data.top = new FormAttachment(toolbar, 0);
    data.bottom = new FormAttachment(100, 0);
    data.left = new FormAttachment(0, 0);
    data.right = new FormAttachment(100, 0);
    text.setLayoutData(data);
*/
    
	//状态栏的实现,SWT中没有状态栏，只好模仿
	//statusLine = new Label(shell, SWT.BORDER);
    statusLine = new Text(shell, SWT.READ_ONLY|SWT.BORDER);
	statusLine.setBackground(statusLine.getDisplay().getSystemColor(SWT.COLOR_WHITE));//设置背景色
    statusLine.setBounds(10, 420, 470, 20);
    statusLine.setText("操作:准备好");
    
    
	
  }

}
