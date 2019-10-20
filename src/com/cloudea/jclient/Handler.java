package com.cloudea.jclient;
import java.io.IOException;

import com.cloudea.jclient.tool.SessionStorage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

//������ģʽ
@SuppressWarnings("restriction")
public abstract class Handler implements HttpHandler{
	
	@Override
	public void handle(HttpExchange arg0) throws IOException {
		Request req = new Request(arg0);
		Response resp = new Response(arg0);
		//����session
		String sessionId = req.getCookie("sessionId");
		SessionStorage sessionStore = SessionStorage.getInstace();
		req.sessions = sessionStore.getStorage(sessionId);
		if(req.sessions == null) {
			sessionId = sessionStore.addStorage();
			resp.setCookie("sessionId", sessionId);
			req.sessions = sessionStore.getStorage(sessionId);
		}
		//�ַ�
		handle(req, resp);
	}

	public abstract void handle(Request req, Response resp);
	
}





