package com.cloudea.jclient.tool;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CookieParser implements Parser {
	private Map<String, String> keyvalues = new HashMap<>();
	private List<String> keys = new LinkedList<>();
	
	/*解析cookie*/
	public void parse(String str) {
		String cookieString = str;
		if(cookieString != null) {
			for(String cookieFragment : cookieString.split(";")) {
				String[] keyvalue = cookieFragment.split("=");
				if(keyvalue.length >= 2) {
					keyvalues.put(keyvalue[0].trim(), keyvalue[1].trim());
				}
			}
		}
	}
	
	public  Map<String, String> getCookies() {
		return keyvalues;
	}
	
	public void add(String key,String value) {
		keyvalues.put(key, value);
		keys.add(key);
	}
	
	public void remove(String key) {
		keyvalues.remove(key);
		keys.remove(key);
	}
	
	/*格式化cookie*/
	public String format() {
		int i = 0;
		int size = keyvalues.size();
		StringBuffer sb = new StringBuffer();
		for(String key : keys) {
			sb.append(key);
			sb.append("=");
			sb.append(keyvalues.get(key));
			if(i != size - 1) {
				sb.append(";");
			}
			
			i++;
		}
		return sb.toString();
	}
}
