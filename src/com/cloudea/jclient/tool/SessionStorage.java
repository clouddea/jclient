package com.cloudea.jclient.tool;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/*����ģʽ*/
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
	
	/*���ó�ʱʱ��*/
	public synchronized void setTimeout(int minutes) {
		if(timeout < 1) {
			timeout = minutes;
		}
	}
	
	
	/*���sessions*/
	public synchronized Map<String, String> getStorage(String key){
		SessionStorageStruct struct = storage.get(key);
		if(struct != null) {
			struct.createTime = new Date();
			return struct.sessions;
		}
		return null;
	}
	
	//�����µ�session
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

//�ṹ��
class SessionStorageStruct{
	protected Date createTime = new Date();
	protected Map<String, String> sessions = new HashMap<>();
}


//�������
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
			
			//���
			SessionStorage.getInstace().refresh();
		}
	}
}
