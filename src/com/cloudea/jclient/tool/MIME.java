package com.cloudea.jclient.tool;

import java.util.HashMap;
import java.util.Map;

public class MIME {
	private Map<String, String> mimes;
	
	public MIME() {
		mimes = new HashMap<>();
	}
	
	public void addType(String suffix, String type) {
		if(suffix != null && suffix.startsWith(".") && type != null) {
			mimes.put(suffix, type);
		}
	}
	
	public String getType(String filename) {
		String type = mimes.get(filename);
		if(type == null) {
			type = "application/octet-stream";
		}
		return type;
	}
}
