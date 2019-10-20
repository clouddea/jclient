package com.cloudea.jclient.tool;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ParameterParser implements Parser {
	public Map<String, List<String>> parse(String text){
		Map<String, List<String>> ret = new HashMap<>();
		if(text != null && !text.equals("")) {
			String[] keyvalues = text.split("&");
			for(String keyvalue : keyvalues) {
				try {
					String[] key_value = keyvalue.split("=");
					String key = key_value[0].trim();
					String value = key_value[1].trim();
					List<String> values = ret.get(key);
					if(values == null) {
						values = new LinkedList<>();
						ret.put(key, values);
					}
					values.add(value);
				}catch(Exception e) {
					continue;
				}
			}
		}
		return ret;
	}
}
