package ctc.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

import ctc.CTCServer;

/**
 * This class contains the toolbar for the Password application
 */
public class CTCServerToolbarFactory {

//private static final String IMAGE_PATH = "/images/";

  private static final String IMAGE_PATH = System.getProperty("user.dir")+"/resources/images/";
  // These contain the images for the toolbar buttons
  private static Image PARAMSET;
  private static Image STARTSERVER;
  private static Image CLOSESERVER;
  
  public CTCServerToolbarFactory(){}
  /**
   * Factory create method
   * 
   * @param composite the parent composite
   * @return ToolBar
   */
  public static ToolBar create(Composite composite) {
    createImages(composite);//create images for coolbar buttons
  
     //创建工具栏
    ToolBar toolbar = new ToolBar(composite, SWT.HORIZONTAL);
    createItems(toolbar);
    return toolbar;
  }

  /**
   * Creates the toolbar items
   * 
   * @param toolbar the parent toolbar
   */
  //用于控制启动/关闭服务器的两个按钮之间的逻辑互联关系
  private static ToolItem startServer,closeServer; 
  public static void setStartServer(boolean value){
	  startServer.setEnabled(value);
  }
  public static void setCloseServer(boolean value){
	  closeServer.setEnabled(value);
  }
  
  private static void createItems(ToolBar toolbar) {
    // Create the New item
    ToolItem item = createItemHelper(toolbar,PARAMSET, "参数设置");
    item.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
    	  CTCServer.getApp().paramSet();
      }
    });

    // Create the Open item
    startServer = createItemHelper(toolbar, STARTSERVER, "启动服务器");
    startServer.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
    	  CTCServer.getApp().startServer();
      }
    });

    // Create the Save item
    closeServer = createItemHelper(toolbar, CLOSESERVER, "关闭服务器");
    closeServer.setEnabled(false);
    closeServer.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent event) {
    	  CTCServer.getApp().closeServer();
      }
    });
  
  }

  /**
   * Helper method to create a toolbar item
   * 
   * @param toolbar the parent toolbar
   * @param image the image to use
   * @param text the text to use
   * @return ToolItem
   */
  private static ToolItem createItemHelper(ToolBar toolbar, Image image,String text) {
    ToolItem item = new ToolItem(toolbar, SWT.PUSH);
    if (image == null) {
      item.setText(text);
    } else {
      item.setImage(image);
      item.setToolTipText(text);
    }
    return item;
  }
  
  //create images for coolbar buttons
  private static void createImages(Composite composite) {
    if (PARAMSET == null) {
      Display display = composite.getDisplay();
      Class c = CTCServer.getApp().getClass();
      
    //PARAMSET = new Image(display, c.getResourceAsStream(IMAGE_PATH + "save.png"));
      PARAMSET = new Image(display, (IMAGE_PATH + "save.png"));
      STARTSERVER = new Image(display, (IMAGE_PATH + "startserver.png"));
      CLOSESERVER = new Image(display, (IMAGE_PATH + "closeserver.png"));
    }
  }
}
