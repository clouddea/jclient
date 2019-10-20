package com.cloudea.jclient.tool;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.naming.spi.DirectoryManager;

import org.omg.SendingContext.RunTime;

public class FormDataParser {
	private File temp = new File(".");
	private int maxByte = 1024;         //超过则会放入文件中
	private int maxSave = 1024 * 1024;  //超过则会拒绝接收
	private InputStream is;
	private Map<String, List<Field>> formdatas;
	
	public FormDataParser(InputStream is) {
		this.is = is;
	}
	
	public Field getField(String key) {
		List<Field> fields = getFields().get(key);
		if(fields != null && fields.size() >= 1) {
			return fields.get(0);
		}
		return null;
	}
	
	
	public Map<String, List<Field>> getFields(){
		if(formdatas == null) {
			String boundText = readLine(is).trim();
			byte[] bound = boundText.getBytes();   //边界
			formdatas = new HashMap<>();
			while (true) {
				try {
					
					//读取其它头部信息
					List<String> headers = new LinkedList<>();
					while(true) {
						String head = readLine(is);
						//System.out.println("head" + head);
						if( head != null && !head.trim().equals("")) {
							headers.add(head);
						}else {
							//不为为空行
							break;
						}
					}
					String[] keyvalues = headers.get(0).split(";");
					Map<String, String> map = new HashMap<>();
					for(String keyvalue : keyvalues) {
						String[] key_value = keyvalue.split("[=:]");
						String key = key_value[0].trim();
						String value = key_value[1].trim();
						value = value.substring(1, value.length() - 1);
						map.put(key, value);
					}
					
					String key = map.get("name");
					String filename = map.get("filename");
					
					//读二进制
					LinkedList<Byte> buf = new LinkedList<>();
					File file = null;
					FileOutputStream fos = null;
					byte[] b = new byte[1];
					while(!checkBound(buf, bound)) {
						if(is.read(b) != -1) {
							buf.add(b[0]);
						}else {
							break;
						}
						
						//溢出
						if(buf.size() > bound.length + maxByte) {
							if(file == null) {
								file = new File(temp, new Date().getTime() + "_" + key);
								fos = new FileOutputStream(file);
							}
							fos.write(buf.removeFirst());
						}
					}
					is.read(b);
					is.read(b);
					
					
					//写最后一点
					if(file != null) {
						for(int i = 0; i < maxByte; i++) {
							fos.write(buf.get(i));
						}
					}
					
					

					//添加
					List<Field> fieldList = formdatas.get(key);
					if(fieldList == null) {
						fieldList = new LinkedList<>();
						formdatas.put(key, fieldList);
					}
					
					Field field = null;
					if(fos != null) {
						fos.close();
						field = new Field(file);
					}else {
						int length = buf.size() - bound.length;
						byte[] data = new byte[length];
						for(int i = 0; i < length; i++) {
							data[i] = buf.get(i);
						}
						field = new Field(data);
						//System.out.println("文本:" + field.getAsString());
					}
					
					fieldList.add(field);
					
				}catch(Exception e) {
					//e.printStackTrace();
					break;
				}		
			}
		}
		
		return formdatas;
	}
	
	
	private String readLine(InputStream is) {
		try {
			LinkedList<Byte> bytes = new LinkedList<>();
			byte[] d = new byte[1];
			while(is.read(d) != -1 && d[0] != 10) {
				bytes.add(d[0]);
			}
			byte[] ret = new byte[bytes.size()];
			for(int i = 0; i < ret.length; i++) {
				ret[i] = bytes.get(i);
			}
			
			return new String(ret);
		} catch (IOException e) {
			return null;
		}
	}
	
	
	private boolean checkBound(LinkedList<Byte> buf, byte[] bound) {
		if(buf.size() >= bound.length) {

			/*byte[] string = new byte[buf.size()];
			for(int i = 0; i< buf.size(); i++) {
				string[i] = buf.get(i);
			}*/
			//System.out.println("string1:" + new String(string));
			//System.out.println("string2:\n" + new String(bound));
			for(int i = 0; i < bound.length; i++) {
				byte byte1 = bound[bound.length - 1 - i];
				byte byte2 = buf.get(buf.size() - 1 - i);
				if(byte1 != byte2) {
					//System.out.println("出错位" + i + "|" + byte1 + "|" + byte2);
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	
	public static class Field{
		private byte[] datas;
		private File file;
		private InputStream is;
		
		protected Field(byte[] datas) {
			this.datas = datas;
		}
		
		protected Field(File file) {
			this.file = file;
		}
		
		public String getAsString() {
			return getAsString("utf-8");
		}
		
		public String getAsString(String encoding) {
			if(datas != null) {
				try {
					return new String(datas, encoding);
				} catch (UnsupportedEncodingException e) {
					return "";
				}
			}else {
				try {
					StringBuffer sb = new StringBuffer();
					Scanner in = new Scanner(file, encoding);
					while(in.hasNextLine()) {
						sb.append(in.nextLine());
						sb.append("\n");
					}
					in.close();
					this.file.delete();
					return sb.toString();
				} catch (FileNotFoundException e) {
					return "";
				}

			}
		}
		
		public boolean save(File file) {
			try {
				FileOutputStream fos = new FileOutputStream(file);
				InputStream iss = null;
				if(datas != null) {
					iss = new ByteArrayInputStream(datas);
				}else {
					iss = new FileInputStream(this.file);
				}
				byte[] buf = new byte[1024];
				int len = 0;
				while((len = iss.read(buf)) != -1) {
					fos.write(buf, 0, len);
				}
				
				iss.close();
				fos.close();
				this.file.delete();
				return true;
			} catch (Exception e) {
				return false;
			}
			
		}
		
	}
}

