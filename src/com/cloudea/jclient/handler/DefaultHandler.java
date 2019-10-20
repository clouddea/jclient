package com.cloudea.jclient.handler;

import java.util.Map;

import com.cloudea.jclient.Handler;
import com.cloudea.jclient.Request;
import com.cloudea.jclient.Response;
import com.cloudea.jclient.Server;
import com.cloudea.jclient.tool.SessionStorage;

public class DefaultHandler extends Handler {
	private Server server;
	
	public DefaultHandler(Server server) {
		this.server = server;
	}
	
	@Override
	public void handle(Request req, Response resp) {
		Handler handler = null;
		for(String key : server.getContexts().keySet()) {
			Handler h = server.getContexts().get(key);
			if(h.getClass() == StaticHandler.class) {
				if(req.getPath().startsWith(key)) {
					handler = h;
				}
			}else {
				if(req.getPath().equals(key)) {
					handler = h;
				}
			}
		}
		
		if(handler != null) {
			try {
				//分发处理
				handler.handle(req, resp);
			} catch (Exception e) {
				System.out.println("错误:" + e.getMessage());
				System.out.println("错误:" + e.getCause());
			}
		}else {
			resp.setNotFound();
			resp.getWriter().println("<h1>404 not found!</h1>");
			resp.getWriter().close();
		}
		
		//收尾处理
		try {
			resp.getWriter().flush();
			resp.getWriter().close();
			resp.getOutputStream().flush();
			resp.getOutputStream().close();
		}catch(Exception e) {
			//TODO:nothing
		}
	}

}
