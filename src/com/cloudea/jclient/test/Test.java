package com.cloudea.jclient.test;
import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cloudea.jclient.Handler;
import com.cloudea.jclient.Request;
import com.cloudea.jclient.Response;
import com.cloudea.jclient.Server;
import com.cloudea.jclient.tool.CookieParser;
import com.cloudea.jclient.tool.FormDataParser;
import com.cloudea.jclient.tool.FormDataParser.Field;
import com.cloudea.jclient.tool.ParameterParser;
import com.sun.net.httpserver.HttpExchange;

public class Test {
	
	public static void testServer() {
		Server server = null;
		try {
			server = new Server(8080);
			System.out.println("服务在运行中...");
		} catch (IOException e) {
			System.out.println("服务开启失败");
			System.exit(-1);
		}
		
		server.addContext("/", new Handler() {
			@Override
			public void handle(Request req, Response resp) {
				/*resp.setHeader("abc", "123");
				resp.setHeader("abc", "456");
				resp.setHeader("def", "666");
				//resp.redirect("/static/");
				PrintWriter pw = resp.getWriter();
				pw.println(req.getProtocol());
				pw.println(req.getMethod());
				pw.println(req.getPath());
				pw.println(req.getQuery());
				pw.println(req.getHost());
				pw.println(req.getPort());
				pw.println(req.getHeader("123"));
				pw.println(req.getHeader("456"));
				pw.println("666");
				//pw.println("param1 = " + req.getParam("param1"));
				//pw.println("params2 = " + req.getParam("param2"));
				resp.getWriter().print(req.getBody());*/
				
				/*Map<String, List<String>> hs = req.getHeaders();
				for(String key : hs.keySet()) {
					for(String value : hs.get(key)) {
						System.out.println(key + ": " + value);
					}
				}*/

				/*System.out.println("pre");
				FormDataParser fdp = new FormDataParser(req.getInputStream());
				System.out.println("head");
				resp.getWriter().println("head");
				FormDataParser.Field field1 = fdp.getField("abc");
				FormDataParser.Field field2 = fdp.getField("def");
				if(field1 != null) {
					resp.getWriter().println(field1.getAsString());
				}
				if(field2 != null) {
					if(field2.save(new File("picture.jpg"))) {

						resp.getWriter().println("file saved");
					}else {

						resp.getWriter().println("file fail");
					}
					
				}
				resp.getWriter().println("tail");
				System.out.println("tail");*/
				
				
				/*header测试*/
				resp.setHeader("encoding", "中文");
				
				for(String key : req.getCookies().keySet()) {
					resp.getWriter().println(key + ":" + req.getCookie(key));
				}
				
				/*get post测试*/
				resp.getWriter().println("name=" + req.getParam("name"));
				System.out.println("name=" + req.getParam("name"));
				
				
				
			}
		});
		

		server.start();
	}
	
	public static void testTool() {
		System.out.println(new ParameterParser().parse("a=1&b=2&b=3"));
		CookieParser parser = new CookieParser();
		parser.add("abc", "def");
		parser.add("expire", "666");
		System.out.println(parser.format());
	}
	
	public static void main(String[] args) {
		testTool();
		testServer();
	}

}
