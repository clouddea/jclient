package com.cloudea.jclient.tool;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class StringParser implements Parser {
	public String parse(InputStream is) {
		return parse(is, "utf-8");
	}
	
	public String parse(InputStream is, String charset) {
		StringBuffer sb = new StringBuffer();
	    Scanner in = new Scanner(is, charset);
	    while(in.hasNextLine()) {
	    	sb.append(in.nextLine());
	    	sb.append("\n");
	    }
	    return sb.toString();
		
	}
	
}
