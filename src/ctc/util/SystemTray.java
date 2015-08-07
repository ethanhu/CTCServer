package ctc.util;

/**
* 创建并管理系统托盘
* 在托盘图标上单击鼠标右键弹出托盘菜单，双击显示程序主界面，单击（左键）显示气泡提示文本
*/

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.events.*;

import ctc.transport.MinaServer;

import java.util.Timer;
import java.util.TimerTask;

public class SystemTray {

    private TrayItem trayItem;// 托盘项
    private Image trayicon;// 托盘图标
    private static MinaServer minaServer;
    
	final String IMAGE_PATH = System.getProperty("user.dir")+"/resources/images/tray.png";
    
    //构造方法
    public SystemTray() {   }
   
    /**
    * @param mainshell为主窗口对应的shell对象
    */   
    public void createSysTray(final Shell mainshell, MinaServer minaServer) {
    	this.minaServer = minaServer;
        trayItem = initTrayItem(mainshell);//初始化托盘
        if (trayItem != null) {           
            trayMinimize(mainshell);// 最小化主程序到系统托盘
        }       
    }

	
    /**
    * 初始化托盘项目的文字和图标   
    * @param mainShell 主程序窗口对象
    */       
    private TrayItem initTrayItem(final Shell mainshell) {   
        // 获取系统托盘
        final Tray tray = mainshell.getDisplay().getSystemTray();
        // 在某些平台上，可能不存在或不支持系统托盘。需检查当前的系统是否支持系统托盘。
        if (tray == null) {
            System.out.println("当前系统不支持系统托盘");
            return null;
        }
        //当系统支持系统托盘时
        else {
            trayItem = new TrayItem(tray, SWT.NONE);// 创建托盘项
            Display maindisplay = mainshell.getDisplay();
            trayicon = new Image(maindisplay, IMAGE_PATH);// 创建托盘图像
            trayItem.setImage(trayicon);// 设置托盘图标
            // 添加托盘右键菜单
            fillTrayItem(mainshell);
           
            // 鼠标放在托盘图标上时，显示的提示文本信息
            trayItem.setToolTipText("CTC仿真系统之服务器");
            // 显示托盘气泡提示文本
            final ToolTip tip = new ToolTip(mainshell,SWT.BALLOON | SWT.ICON_INFORMATION);
            //自动隐藏气泡提示文本
            tip.setAutoHide(true);
            //设置提示信息
            tip.setText("欢迎使用");
            trayItem.setToolTip(tip);
            //单击选中托盘图标的时候，显示气泡提示
            trayItem.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    // 显示气泡提示
                    tip.setVisible(true);
                    // 采用定时器，自动关闭气泡提示文本
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        public void run() {
                            // 非用户界面线程不能直接操作用户界面线程
                            // 要想在另外一个线程中尝试修改用户界面,应采用以下方法:
                            mainshell.getDisplay().syncExec(new Runnable() {
                                public void run() {
                                    // 数秒后处理气泡提示文本
                                    // tip.setVisible(false);// 使当前气泡提示文本不可见
                                    tip.setAutoHide(true);// 自动隐藏
                                    // System.out.println("气泡提示文本消失了！");
                                }
                            });
                            timer.cancel();
                        }
                    }, 2 * 3 * 100);
               
                    //单击托盘图标，图标不断闪烁                                           
                  //  trayIconFlicker(mainshell);
                   
                }
            });                       
            // 双击托盘，显示程序主界面并恢复为正常大小
            trayItem.addListener(SWT.DefaultSelection, new Listener() {
                public void handleEvent(Event event) {                       
                    mainshell.setVisible(true);
                    mainshell.setMinimized(false);                   
                }
            });
           
            return trayItem;
        }
    }

   
    /**
    * 接收到新消息时，托盘图标闪烁
    * @param minute 执行一次操作的时间间隔
    * TODO 单击系统托盘图标，都会执行这个方法，导致图标的闪烁不断加快
    * 考虑采用全局变量来实现计时器类final Timer timer
    */
    private void trayIconFlicker(final Shell mainshell) {
        int minute=1;//执行一次操作的时间间隔
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                // 非用户界面线程不能直接操作用户界面线程
                // 要想在另外一个线程中尝试修改用户界面,应采用以下方法:                   
                    mainshell.getDisplay().syncExec(new Runnable() {
                        public void run() {
                            // 每个200ms间隔显示托盘图标，以实现图标闪烁的效果
                            trayItem.setImage(null);//设置托盘图标为空                       
                            try {
                                Thread.sleep(200);                               
                            } catch (InterruptedException e) {
                            }
                            trayItem.setImage(trayicon);//设置托盘图标
                        }
                    });                   
            }
        },0,minute * 6 * 100);
       
    }
   
       // 3. 接收到新消息的时候，自动弹出没有图标的消息提示框，26ms之后消失
     
   
    /**
    * 最小化程序到托盘
    * @param mainShell 主程序窗口对象
    */
    private void trayMinimize(final Shell mainShell) {
        mainShell.addShellListener(new ShellAdapter() {
            // 最小化时不显示在任务栏
            public void shellIconified(ShellEvent e) {
                // 主程序窗口不可见
                mainShell.setVisible(false);
            }
        });
       
        //用户双击托盘图标，显示主程序窗口并恢复为正常大小
//        trayItem.addListener(SWT.Selection, new Listener() {           
//            public void handleEvent(Event event) {               
//                if (mainShell.isVisible()==false) {
//                    mainShell.setVisible(true);
//                    mainShell.setMinimized(false);
//                }
//            }
//        });
    }   
   
    /**
    * 构造托盘菜单项   
    * @param mainShell 主程序窗口对象
    */
    private Menu fillTrayItem(final Shell mainshell) {       
        final Menu menu = new Menu(mainshell, SWT.POP_UP);
        //主程序退出菜单项
        final MenuItem exitItem = new MenuItem(menu, SWT.PUSH);//主程序退出菜单
        exitItem.setText("退出程序");
        exitItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                MessageBox box = new MessageBox(mainshell, SWT.YES | SWT.NO
                        | SWT.ICON_QUESTION);
                box.setMessage("退出CTC仿真系统之服务器？");
                int response = box.open();//打开消息对话框
                if (response == SWT.YES) {
                	minaServer.close();
                    trayDispose();//释放托盘相关的资源
                    System.exit(1);//退出主程序
                }
            }
        });
        new MenuItem(menu, SWT.SEPARATOR);// 分割条
        //平台开发者信息
      /*  final MenuItem authorItem = new MenuItem(menu, SWT.PUSH);// 开发者信息菜单
        authorItem.setText("平台开发者");
        authorItem.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                MessageBox box = new MessageBox(mainshell, SWT.OK);
                box.setMessage("");
                box.open();
            }
        });
        */
        
        MenuItem menuItemMaximize = new MenuItem(menu, SWT.PUSH);// 窗口恢复菜单
        menuItemMaximize.setText("恢复主窗口");
        menuItemMaximize.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (mainshell.isVisible() == false) {
                    mainshell.setVisible(true);
                    mainshell.setMinimized(false);
                }            
            }
        });

      /*  MenuItem menuItemMinimize = new MenuItem(menu, SWT.PUSH);// 最小化菜单
        menuItemMinimize.setText("最小化窗口到托盘");
        menuItemMinimize.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                mainshell.setMinimized(true);
            }
        });
        */
        // 当前菜单项不可用时，显示为灰色
        trayItem.addListener(SWT.MenuDetect, new Listener() {
            public void handleEvent(Event event) {      
                menu.setVisible(true);
                if (mainshell.isVisible()) {
                    //menu.getItem(3).setEnabled(false);//最小化菜单用
                  //  menu.getItem(4).setEnabled(true);
                }
                else if(mainshell.getMinimized()==true){
                 //   menu.getItem(3).setEnabled(true);
                    //menu.getItem(4).setEnabled(false);//含平台开发者信息时用
                }
            }
        });
        // 选中托盘图标并单击鼠标右键，显示托盘菜单项
//        trayItem.addSelectionListener(new SelectionAdapter() {
//            public void widgetSelected(Event e) { // 使托盘菜单可见
//                if ((menu.getVisible() == false) && (e.button == 3)) {
//                    menu.setVisible(true);
//                }
//            }
//        });
           
        return menu;
    }   

    /**
    * 释放托盘及其相关资源
    */
    public void trayDispose() {
        // 释放系统托盘
        if (trayItem != null)
            trayItem.dispose();
        // 释放创建的图像资源（系统托盘图标）
        if (trayicon != null) {
            trayicon.dispose();
        }
    }   

   
}