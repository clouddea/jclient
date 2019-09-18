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
	private String toolTip = "��ӭʹ��Java�ͻ���";
	private PopupMenu menu;
	private TrayIcon icon;
	String root;
	
	public Server(int port) throws IOException {
		server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 1);
		server.createContext("/static/", new StaticHandler(this));
	}
	
	/*����·��*/
	public void addContext(String path, Handler handler) {
		server.createContext(path, new OKHandler(handler));
	}
	
	/*����������ʾ��ͼƬ*/
	public void setIcon(Image image) {
		this.image = image;
	}
	
	/*��������*/
	public void setTray(TrayIcon icon) {
		this.icon = icon;
	}
	
	/*����������ʾ*/
	public void setToolTip(String tip) {
		this.toolTip = tip;
	}
	
	/*�������̲˵�*/
	public void setMenu(PopupMenu menu) {
		this.menu = menu;
	}
	
	/*���ø�Ŀ¼-ĩβ�����б��*/
	public void setRoot(String path) {
		this.root = path;
	}
	
	/*��������*/
	public void start() throws AWTException {
		//����Ĭ��ͼ���Ĭ�ϲ˵�
		if(image == null) {
			URL url = this.getClass().getClassLoader().getResource("icon.jpg");
			if(url != null) {
				image = Toolkit.getDefaultToolkit().getImage(url);
			}else {
				System.out.println("�Ҳ���Ĭ��ͼ��");
			}
		}
		
		if(menu == null) {
			menu = new PopupMenu();
			menu.add(new MenuItem("��������"));
			menu.add(new MenuItem("�˳�"));
			menu.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(arg0.getActionCommand().equals("�˳�")){
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
		
		//��������
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
		
		//��������
		server.start();
	}
	
	/*�˳�����*/
	public void close(){
		server.stop(0);
		SystemTray.getSystemTray().remove(icon);
	}
	
	/*��ʾ����*/
	public void show() throws IOException {
		try {
			URI uri = new URI("http://localhost:" + server.getAddress().getPort() + "/static/");
			Desktop.getDesktop().browse(uri);
		}catch(Exception e) {
			System.out.println("�޷��������");
		}
	}
}

/*�ڲ���������*/
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
			//���û���ļ�����
			if(pathFragments.length < 3) { 
				filename = "index.html";
			}
			
			//�ҵ��ļ�
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
			
			//�ж�����
			String[] typeFragment = filename.split("\\.");
			String type = typeFragment[typeFragment.length - 1];
			Headers headers = arg0.getResponseHeaders();
			if(type.equals("html") || type.equals("htm")) {
				headers.add("Content-type", "text/html");
			}else {
				headers.add("Content-type", "application/octet-stream");
			}
			
			
			//��ȡ
			FileInputStream fis = new FileInputStream(file);
			byte[] data = new byte[1024];
			int len = 0;
			while((len = fis.read(data)) != -1) {
				os.write(data, 0, len);
			}
			fis.close();
			os.close();
			
		} catch (Exception e) {
			System.out.println("����̬���ݷ�������");
		}
	}
	
}