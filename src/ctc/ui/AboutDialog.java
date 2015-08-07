package ctc.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.*;
import com.swtdesigner.SWTResourceManager;



public class AboutDialog extends Dialog {

	private Label labelHeader_2;
	private Label labelHeader_1;
	private static final String IMAGE_PATH = System.getProperty("user.dir")+"/resources/images/";
	private Button btnOK = null;
	private Composite composite = null;
	private Label labelLogo = null;
	private Label labelHeader = null;
	private Label labelCopyRight = null;
	private Link linkWeb = null;
	private Link emailLink = null;
	private Shell shell = null;
	private Image logo;

	public AboutDialog(Shell shell) {
		super(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		setText("关于CTC仿真系统"); 
	}


	public void open() {
		shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		logo = new Image(shell.getDisplay(),IMAGE_PATH+ "app.gif");
		shell.setImage(logo);
		Display display = getParent().getDisplay();

		initialize();

		shell.pack();
		
		//使对话框窗口处于屏幕中间
		Rectangle displayBounds = display.getPrimaryMonitor().getBounds();
	    Rectangle shellBounds = shell.getBounds();// 获取屏幕高度和宽度
	    int x = displayBounds.x + (displayBounds.width - shellBounds.width)>>1;
	    int y = displayBounds.y + (displayBounds.height - shellBounds.height)>>1;
	    shell.setLocation(x, y);//定位窗口坐标
	    
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void initialize() {
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(null);
		composite.setSize(new Point(360, 240));
		
		btnOK = new Button(composite, SWT.NONE);
		btnOK.setText("确定");
		btnOK.setBounds(new Rectangle(143, 213, 91, 23));
		btnOK.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		createBody(composite);
	}

	/**
	 * This method initializes composite	
	 *
	 */
	private void createBody(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setBackground(Display.getCurrent().getSystemColor(
				SWT.COLOR_WHITE));
		composite.setLayout(null);
		composite.setBounds(new Rectangle(0, 0, 360, 207));


		Label msLogo = new Label(composite, SWT.NO_BACKGROUND);
		logo = new Image(shell.getDisplay(),IMAGE_PATH + "logo_up.gif");
		msLogo.setImage(logo);
		msLogo.setBounds(new Rectangle(20, 55, 96, 96));
		
		/*labelLogo = new Label(composite, SWT.NO_BACKGROUND);
		logo = new Image(shell.getDisplay(),IMAGE_PATH+ "logo_down.png");
		labelLogo.setImage(logo);
		labelLogo.setBounds(new Rectangle(5, 5 + 96 + 5, 96, 96));
*/


		labelHeader = new Label(composite, SWT.NONE);
		labelHeader.setForeground(SWTResourceManager.getColor(255, 128, 128));
		labelHeader.setBounds(new Rectangle(130, 15, 168, 30));
		labelHeader.setFont(new Font(Display.getDefault(), "Tahoma", 14,SWT.NORMAL));
		labelHeader.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		labelHeader.setText("关于CTC仿真系统");

		linkWeb = new Link(composite, SWT.NONE);
		linkWeb.setBounds(new Rectangle(175, 140, 63, 18));
		linkWeb.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		linkWeb.setText("<a>详细信息</a>");
		linkWeb.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Program.launch("http://www.bjtu.edu.cn/");
			}
		});
		
		
		emailLink = new Link(composite, SWT.NONE);
		emailLink.setBounds(new Rectangle(145, 180, 153, 18));
		emailLink.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		emailLink.setText("<a>版权所有© CTC开发小组</a>");
		emailLink.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Program.launch("mailto:bmxu@bjtu.edu.cn");
			}
		});

		labelHeader_1 = new Label(composite, SWT.NONE);
		labelHeader_1.setBounds(130, 55, 113, 30);
		labelHeader_1.setFont(SWTResourceManager.getFont("Tahoma", 14, SWT.NONE));
		labelHeader_1.setBackground(SWTResourceManager.getColor(255, 255, 255));
		labelHeader_1.setText("版本:V1.0");

		labelHeader_2 = new Label(composite, SWT.NONE);
		labelHeader_2.setBounds(130, 100, 208, 30);
		labelHeader_2.setFont(SWTResourceManager.getFont("Tahoma", 14, SWT.NONE));
		labelHeader_2.setBackground(SWTResourceManager.getColor(255, 255, 255));
		labelHeader_2.setText("开发者:CTC开发小组");
	}
}
