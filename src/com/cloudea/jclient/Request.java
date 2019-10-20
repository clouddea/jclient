package com.cloudea.jclient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;

import com.cloudea.jclient.tool.CookieParser;
import com.cloudea.jclient.tool.ParameterParser;
import com.cloudea.jclient.tool.StringParser;
import com.sun.net.httpserver.HttpExchange;

public class Request {
	protected String protocol;
	protected String method;
	protected String query;
	protected String path;
	protected String host;
	protected String port;
	protected Map<String, List<String>> getParams;
	protected Map<String, List<String>> postParams;
	protected Map<String, List<String>> formParams;
	protected Map<String, List<String>> headers;
	protected Map<String, String> cookies;
	protected Map<String, String> sessions;
	protected Map<String, String> attributes;
	protected String body;
	private HttpExchange args;
	protected Request(HttpExchange args) {
		this.args = args;
		protocol = args.getProtocol();
		method = args.getRequestMethod();
		query = args.getRequestURI().getQuery();
		path = args.getRequestURI().getPath();
		host = args.getRemoteAddress().getHostString();
		port = args.getRemoteAddress().getPort() + "";
		
	}
	public String getProtocol() {
		return protocol;
	}
	public String getMethod() {
		return method;
	}
	public String getQuery() {
		return query;
	}
	public String getPath() {
		return path;
	}
	public String getHost() {
		return host;
	}
	public String getPort() {
		return port;
	}
	
	
	/*参数*/
	public String getParam(String key) {
		List<String> values = getParams().get(key);
		if(values != null) {
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < values.size(); i++) {
				sb.append(values.get(i));
				if(i != values.size() - 1) {
					sb.append(",");
				}
			}
			return sb.toString();
		}
		return null;
	}
	
	public Map<String, List<String>> getParams() {
		if(getMethod().equals("GET")) {
			if(getParams == null) {
				getParams = new ParameterParser().parse(getQuery());
			}
			return getParams;
		}else {
			if(postParams == null) {
				String s;
				try {
					s = java.net.URLDecoder.decode(getBody(), "utf-8");
				} catch (UnsupportedEncodingException e) {
					s = "";
				}
				postParams = new ParameterParser().parse(s);
			}
			return postParams;
		}
	}
	
	
	/*头部*/
	public String getHeader(String key) {
		List<String> values = getHeaders().get(key);
		if(values != null) {
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < values.size(); i++) {
				sb.append(values.get(i));
				if(i != values.size() - 1) {
					sb.append(",");
				}
			}
			return sb.toString();
		}
		return null;
	}
	
	public Map<String, List<String>> getHeaders() {
		if(headers == null) {
			headers = args.getRequestHeaders();
		}
		return headers;
	}
	
	
	/*属性*/
	public Map<String, String> getAttributes() {
		if(attributes == null) {
			attributes = new HashMap<>();
		}
		return attributes;
	}
	
	/*请求体*/
	public String getBody() {
		if(body == null) {
		    body = new StringParser().parse(getInputStream());
		}
	    return body;
	}
	
	public InputStream getInputStream() {
		return args.getRequestBody();
	}
	
	/*cookie and session*/
	public String getCookie(String key) {
		return getCookies().get(key);
	}
	
	
	public Map<String, String> getCookies(){
		if(cookies == null) {
			CookieParser parser = new CookieParser();
			parser.parse(getHeader("cookie"));
			cookies = parser.getCookies();
		}
		
		return cookies;
	}
	
	public String getSession(String key) {
		return sessions.get(key);
	}
	
	public void setSession(String key, String value) {
		sessions.put(key, value);
	}
}
