package com.cloudea.jclient;

import java.awt.HeadlessException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cloudea.jclient.tool.CookieParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class Response {
	protected int status = 200;
	protected PrintWriter writer;
	protected OutputStream os;
	private HttpExchange args;
	
	protected Response(HttpExchange args) {
		this.args = args;
	}
	
	/*写内容*/
	public PrintWriter getWriter() {
		return getWriter("utf-8");
	}
	
	public PrintWriter getWriter(String charset) {
		if(writer == null) {
			try {
				writer = new PrintWriter(new OutputStreamWriter(getOutputStream(), charset));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return writer;
	}
	
	public OutputStream getOutputStream() throws IOException {
		if(os == null) {
			args.sendResponseHeaders(status, 0);
			os = args.getResponseBody();
		}
		return os;
	}
	
	/*写头*/
	public void setHeader(String key, String value) {
		args.getResponseHeaders().add(key, value);
	}
	
	/*重定向*/
	public void redirect(String path) {
		status = 301;
		setHeader("Location", path);
	}
	
	/*404*/
	public void setNotFound() {
		status = 404;
	}
	
	/*写cookie*/
	public void setCookie(String key, String value) {
		setHeader("Set-Cookie", key + "=" + value);
	}
	
	public void setCookie(String key, String value, String path, int age) {
		CookieParser parser = new CookieParser();
		parser.add(key, value);
		parser.add("path", path);
		parser.add("max-age", age + "");
		setHeader("Set-cookie", parser.format());
	}
}
