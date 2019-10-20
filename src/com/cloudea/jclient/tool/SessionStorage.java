package com.cloudea.jclient.tool;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/*单例模式*/
public class SessionStorage {
	private static SessionStorage instance;
	private static SessionStorageDaemon daemonThread;
	protected static int timeout = 30;
	
	
	public synchronized static SessionStorage getInstace() {
		if(instance == null) {
			instance = new SessionStorage();
		}
		
		if(daemonThread == null) {
			daemonThread = new SessionStorageDaemon();
			daemonThread.start();
		}
		return instance;
	}
	
	

	private Map<String, SessionStorageStruct> storage;
	
	private SessionStorage() {
		storage = new HashMap<>();
	}
	
	/*设置超时时间*/
	public synchronized void setTimeout(int minutes) {
		if(timeout < 1) {
			timeout = minutes;
		}
	}
	
	
	/*获得sessions*/
	public synchronized Map<String, String> getStorage(String key){
		SessionStorageStruct struct = storage.get(key);
		if(struct != null) {
			struct.createTime = new Date();
			return struct.sessions;
		}
		return null;
	}
	
	//产生新的session
	public synchronized String addStorage() {
		Random rand = new Random();
		String key = (new Date().getTime() + "" + rand.nextInt(10000));
		SessionStorageStruct struct = new SessionStorageStruct();
		storage.put(key, struct);
		System.out.println("create a key");
		return key;
	}
	
	
	public synchronized void refresh() {
		Iterator<String> iterator = storage.keySet().iterator();
		while(iterator.hasNext()) {
			String key = iterator.next();
			SessionStorageStruct struct = storage.get(key);
			Date now = new Date();
			int bound = timeout * 60 * 1000;
			boolean canClear = now.getTime() - struct.createTime.getTime() > bound;
			if(canClear) {
				iterator.remove();
			}
		}

	}
}

//结构体
class SessionStorageStruct{
	protected Date createTime = new Date();
	protected Map<String, String> sessions = new HashMap<>();
}


//定期清除
class  SessionStorageDaemon extends Thread{
	
	@Override
	public void run() {
		while(true) {
			if(this.isInterrupted()) {
				break;
			}
			
			try {
				Thread.sleep(SessionStorage.timeout * 60 * 1000);
			} catch (InterruptedException e) {
				break;
			}
			
			//清除
			SessionStorage.getInstace().refresh();
		}
	}
}
