package com.cloudea.jclient;
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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.cloudea.jclient.handler.DefaultHandler;
import com.cloudea.jclient.handler.StaticHandler;
import com.cloudea.jclient.tool.SessionStorage;
import com.sun.net.httpserver.*;
public class Server {
	private HttpServer server;
	private Map<String, Handler> contexts;
	private int maxCon = 1000;
	private Image image;
	private String toolTip = "��ӭʹ��Java�ͻ���";
	private PopupMenu menu;
	private TrayIcon icon;
	private String root = ".";
	
	public Server(int port) throws IOException {
		contexts = new TreeMap<>();
		server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), maxCon);
		server.createContext("/", new DefaultHandler(this));
		addContext("/static/", new StaticHandler(this));
	}
	
	/*����·��*/
	public void addContext(String path, Handler handler) {
		if(handler != null) {
			contexts.put(path, handler);
		}
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
	
	
	public Map<String, Handler> getContexts() {
		return contexts;
	}

	
	public String getRoot() {
		return root;
	}
	
	
	
	/*��������*/
	public void start(){

		try {
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
		} catch (Exception e) {
			System.out.println("[ERROR]Your device was not support \"X11\"");
		}
		
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
