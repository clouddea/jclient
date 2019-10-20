package com.cloudea.jclient;
import java.io.IOException;

import com.cloudea.jclient.tool.SessionStorage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

//责任链模式
@SuppressWarnings("restriction")
public abstract class Handler implements HttpHandler{
	
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		Request req = new Request(arg0);
		Response resp = new Response(arg0);
		//处理session
		String sessionId = req.getCookie("sessionId");
		SessionStorage sessionStore = SessionStorage.getInstace();
		req.sessions = sessionStore.getStorage(sessionId);
		if(req.sessions == null) {
			sessionId = sessionStore.addStorage();
			resp.setCookie("sessionId", sessionId);
			req.sessions = sessionStore.getStorage(sessionId);
		}
		//分发
		handle(req, resp);
	}

	public abstract void handle(Request req, Response resp);
	
}





