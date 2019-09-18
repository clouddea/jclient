import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.sun.net.httpserver.*;
public class Server {
	private HttpServer server;
	private Image image;
	private String toolTip = "欢迎使用Java客户端";
	private PopupMenu menu;
	private TrayIcon icon;
	String root;
	
	public Server(int port) throws IOException {
		server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 1);
		server.createContext("/static/", new StaticHandler(this));
	}
	
	/*增加路径*/
	public void addContext(String path, Handler handler) {
		server.createContext(path, new OKHandler(handler));
	}
	
	/*设置托盘显示的图片*/
	public void setIcon(Image image) {
		this.image = image;
	}
	
	/*设置托盘*/
	public void setTray(TrayIcon icon) {
		this.icon = icon;
	}
	
	/*设置托盘提示*/
	public void setToolTip(String tip) {
		this.toolTip = tip;
	}
	
	/*设置托盘菜单*/
	public void setMenu(PopupMenu menu) {
		this.menu = menu;
	}
	
	/*设置根目录-末尾必须带斜杠*/
	public void setRoot(String path) {
		this.root = path;
	}
	
	/*启动服务*/
	public void start() throws AWTException {
		//设置默认图标和默认菜单
		if(image == null) {
			URL url = this.getClass().getClassLoader().getResource("icon.jpg");
			if(url != null) {
				image = Toolkit.getDefaultToolkit().getImage(url);
			}else {
				System.out.println("找不到默认图标");
			}
		}
		
		if(menu == null) {
			menu = new PopupMenu();
			menu.add(new MenuItem("打开主界面"));
			menu.add(new MenuItem("退出"));
			menu.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(arg0.getActionCommand().equals("退出")){
						close();
						System.exit(0);
					}else {
						try {
							show();
						} catch (IOException e) {
							
						}
					}
				}
			});
		}
		
		//设置托盘
		SystemTray tray = SystemTray.getSystemTray();
		if(icon == null) {
			icon = new TrayIcon(image, toolTip, menu);
			icon.setImageAutoSize(true);
			icon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON1) {
						try {
							show();
						} catch (IOException e1) {
							
						}
					}
				}
				
			});
		}
		tray.add(icon);
		
		//开启服务
		server.start();
	}
	
	/*退出程序*/
	public void close(){
		server.stop(0);
		SystemTray.getSystemTray().remove(icon);
	}
	
	/*显示窗口*/
	public void show() throws IOException {
		try {
			URI uri = new URI("http://localhost:" + server.getAddress().getPort() + "/static/");
			Desktop.getDesktop().browse(uri);
		}catch(Exception e) {
			System.out.println("无法打开浏览器");
		}
	}
}

/*内部处理请求*/
class OKHandler extends Handler{

	public OKHandler(HttpHandler nextHandler) {
		super(nextHandler);
	}

	@Override
	public void handle(HttpExchange arg0){
		try {
			arg0.sendResponseHeaders(200, 0);
			this.handleNext(arg0);
			arg0.getResponseBody().close();;
		} catch (IOException e) {
			return;
		}
	}
}

class StaticHandler extends Handler{
	private Server server;
	
	public StaticHandler(Server server) {
		super(null);
		this.server = server;
	}

	@Override
	public void handle(HttpExchange arg0) {
		try {
			String[] pathFragments = arg0.getRequestURI().getPath().split("/"); 
			String filename = pathFragments[pathFragments.length - 1];
			//如果没有文件参数
			if(pathFragments.length < 3) { 
				filename = "index.html";
			}
			
			//找到文件
			String root = server.root == null ? "" : server.root;
			File file = new File(root + "static/" + filename);
			
			//404 or 200
			OutputStream os = arg0.getResponseBody();
			if(!file.exists()) {
				arg0.sendResponseHeaders(404, 0);
				os.close();
				return;
			}else {
				arg0.sendResponseHeaders(200, 0);
			}
			
			//判断类型
			String[] typeFragment = filename.split("\\.");
			String type = typeFragment[typeFragment.length - 1];
			Headers headers = arg0.getResponseHeaders();
			if(type.equals("html") || type.equals("htm")) {
				headers.add("Content-type", "text/html");
			}else {
				headers.add("Content-type", "application/octet-stream");
			}
			
			
			//读取
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[1024];
			int len = 0;
			while((len = fis.read(data)) != -1) {
				os.write(data, 0, len);
			}
			fis.close();
			os.close();
			
		} catch (Exception e) {
			System.out.println("处理静态内容发生错误");
		}
	}
	
}