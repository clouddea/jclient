package com.cloudea.jclient.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateRender {
	public static String render(File base , String origin) {
		if(origin != null) {
			StringBuilder sb = new StringBuilder();
			Pattern p = Pattern.compile("<include\\s.*?/.*?>", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(origin);
			int start = 0;
			int end = 0;
			while(m.find()) {
				sb.append(origin.substring(end, m.start()));
				start = m.start();
				end = m.end();
				Pattern p2 = Pattern.compile("file=\"?([\\w\\./_]+)\"?");
				Matcher m2 = p2.matcher(m.group());
				if(m2.find()) {
					String relativeFilename = m2.group(1);
					File file = new File(base.getParent(), relativeFilename);
					if(file.exists()) {
						FileInputStream fis;
						try {
							fis = new FileInputStream(file);
							String content = new StringParser().parse(fis);
							fis.close();
							sb.append(render(file, content));
						} catch (Exception e) {
							//TODO: nothing
						}
					}
				}
			}
			sb.append(origin.substring(end));
			return sb.toString();
		}else {
			return "";
		}
	}
}
