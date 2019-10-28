package com.cloudea.jclient.handler;

import java.io.File;

import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cloudea.jclient.Handler;
import com.cloudea.jclient.Request;
import com.cloudea.jclient.Response;
import com.cloudea.jclient.Server;
import com.cloudea.jclient.tool.MIME;
import com.cloudea.jclient.tool.StringParser;
import com.cloudea.jclient.tool.TemplateRender;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
/*���ؾ�̬�ļ�*/
public class StaticHandler extends Handler {
	private Server server;
	private Map<String, PageCache> htmls;                      //���ݻ���
	private MIME mimes;                                        //MIME����
	private List<String> caches;                               //��������
	
	public StaticHandler(Server server) {
		this.server = server;
		htmls = new HashMap<>();
		mimes = new MIME();
		mimes.addType(".html", "text/html");
		mimes.addType(".htm", "text/html");
		mimes.addType(".css", "text/css");
		mimes.addType(".js", "application/x-javascript");
		caches = new ArrayList<>();
		caches.add("html");
		caches.add("htm");
		caches.add("css");
		caches.add("js");
	}
	
	@Override
	public void handle(Request req, Response resp) {
		try {
			//�ҵ��ļ�
			String path = req.getPath();
			if(path.endsWith("/")) {
				path += "index.html";
			}
			File file = new File(server.getRoot(), path);
			
			//404 or 200
			if(!file.exists()) {
				resp.setNotFound();
				return;
			}
			
			//�ж�����
			String[] typeFragment = path.split("\\.");
			String type = typeFragment[typeFragment.length - 1];
			resp.setHeader("Content-type", mimes.getType("." + type));
			if(caches.contains(type)) {
				PageCache cache = htmls.get(path);
				PrintWriter pw = resp.getWriter();
				//������治���ڻ������µ�
				Date newestModify = new Date(file.lastModified());
				if(cache == null || cache.updateTime.before(newestModify)){
					FileInputStream fis = new FileInputStream(file);
					String content =  new StringParser().parse(fis);
					cache = new PageCache();
					cache.updateTime = newestModify;
					cache.content = content;
					htmls.put(path, cache);
					if(type.equals("html")|| type.equals("htm")) {
						cache.content = TemplateRender.render(file, cache.content);
					}
					fis.close();
				}
				
				pw.print(cache.content);
				pw.close();
				
			}else {
				OutputStream os = resp.getOutputStream();
				FileInputStream fis = new FileInputStream(file);
				byte[] data = new byte[1024];
				int len = 0;
				while((len = fis.read(data)) != -1) {
					os.write(data, 0, len);
				}
				fis.close();
				os.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("����̬���ݷ�������");
		}

	}
	
	private static class PageCache{
		public Date updateTime;
		public String content;
	}
}




