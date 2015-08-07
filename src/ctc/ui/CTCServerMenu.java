package ctc.ui;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

import ctc.CTCServer;

/**
 * This class contains the menu for the Password application
 */
public class CTCServerMenu {
  Menu menu = null;

  /**
   * Constructs a PasswordMenu
   * 
   * @param shell the parent shell
   */
  
  //用于控制启动/关闭服务器的两个菜单项之间的逻辑互联关系
  private static MenuItem startServer,closeServer; 
  public static void setStartServer(boolean value){
	  startServer.setEnabled(value);
  }
  public static void setCloseServer(boolean value){
	  closeServer.setEnabled(value);
  }

  public CTCServerMenu(final Shell shell) {
    // Create the menu
    menu = new Menu(shell, SWT.BAR);//BAR用于主菜单

    // Create the File top-level menu
    MenuItem item = new MenuItem(menu, SWT.CASCADE);//CASCADE表示有子菜单
    item.setText("文件");
    Menu dropMenu = new Menu(shell, SWT.DROP_DOWN);
    item.setMenu(dropMenu);

    // Create File->paramset
    item = new MenuItem(dropMenu, SWT.NULL);//RADIO选择后，前面会显示一个圆点
    item.setText("参数设置\tCtrl+N");
    item.setAccelerator(SWT.CTRL + 'N');
    item.addSelectionListener(new SelectionAdapter() {//添加事件监听器
      public void widgetSelected(SelectionEvent event) {
        CTCServer.getApp().paramSet();
      }
    });
    
      // Create File->启动服务器
    startServer = new MenuItem(dropMenu, SWT.NULL);
    startServer.setText("启动服务器\tCtrl+S");
    startServer.setAccelerator(SWT.CTRL + 'S');
    startServer.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCServer.getApp().startServer();
      }
    });

    // Create File->Save
    closeServer = new MenuItem(dropMenu, SWT.NULL);
    closeServer.setText("关闭服务器\tCtrl+C");
    closeServer.setAccelerator(SWT.CTRL + 'C');
    closeServer.setEnabled(false);
    closeServer.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCServer.getApp().closeServer();
      }
    });

    
    new MenuItem(dropMenu, SWT.SEPARATOR);//SEPARATOR分隔符 显示一个横线，把几个选项隔开

    // Create File->Exit
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("退出\tAlt+F4");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
    	  CTCServer.getApp().closeWindow();
    	//  shell.close();
      }
    });

    // Create Help
    item = new MenuItem(menu, SWT.CASCADE);
    item.setText("帮助");
    dropMenu = new Menu(shell, SWT.DROP_DOWN);
    item.setMenu(dropMenu);

    // Create Help->About
    item = new MenuItem(dropMenu, SWT.NULL);
    item.setText("关于\tCtrl+A");
    item.setAccelerator(SWT.CTRL + 'A');
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
        CTCServer.getApp().about();
      }
    });
  }

  /**
   * Gets the underlying menu
   * 
   * @return Menu
   */
  public Menu getMenu() {
    return menu;
  }
  
}
