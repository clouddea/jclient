import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

//责任链模式
@SuppressWarnings("restriction")
public abstract class Handler implements HttpHandler {

	private HttpHandler nextHandler;
	
	public Handler(HttpHandler nextHandler) {
		this.nextHandler = nextHandler;
	}
	
	@Override
	public abstract void handle(HttpExchange arg0);
	
	//使用下一个拦截器
	public void handleNext(HttpExchange arg0) throws IOException {
		if(nextHandler != null) {
			nextHandler.handle(arg0);
		}
	}
}





