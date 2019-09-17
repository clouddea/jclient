import java.awt.AWTException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.sun.net.httpserver.HttpExchange;

public class Test {

	public static void main(String[] args) {
		
		Server server = null;
		try {
			server = new Server(8080);
			System.out.println("服务在运行中...");
		} catch (IOException e) {
			System.out.println("服务开启失败");
			System.exit(-1);
		}
		/*server.addContext("/", new Handler(null) {
			
			@Override
			public void handle(HttpExchange arg0) {
				try {
					arg0.getResponseBody().write("欢迎使用".getBytes("utf-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});*/
		try {
			server.start();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
