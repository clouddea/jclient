package com.cloudea.jclient.handler;

import java.io.File;

import java.io.FileInputStream;
import java.io.OutputStream;

import com.cloudea.jclient.Handler;
import com.cloudea.jclient.Request;
import com.cloudea.jclient.Response;
import com.cloudea.jclient.Server;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
/*返回静态文件*/
public class StaticHandler extends Handler {
	private Server server;
	
	public StaticHandler(Server server) {
		this.server = server;
	}
	
	@Override
	public void handle(Request req, Response resp) {
		try {
			//找到文件
			String path = req.getPath();
			if(path.endsWith("/")) {
				path += "index.html";
			}
			File file = new File(server.getRoot(), path);
			
			//404 or 200
			OutputStream os = resp.getOutputStream();
			if(!file.exists()) {
				resp.setNotFound();
				return;
			}
			
			//判断类型
			String[] typeFragment = path.split("\\.");
			String type = typeFragment[typeFragment.length - 1];
			if(type.equals("html") || type.equals("htm")) {
				resp.setHeader("Content-type", "text/html");
			}else {
				resp.setHeader("Content-type", "application/octet-stream");
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

