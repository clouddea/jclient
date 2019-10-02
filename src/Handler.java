import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

//������ģʽ
@SuppressWarnings("restriction")
public abstract class Handler implements HttpHandler {

	private HttpHandler nextHandler;
	
	public Handler(HttpHandler nextHandler) {
		this.nextHandler = nextHandler;
	}
	
	@Override
	public abstract void handle(HttpExchange arg0);
	
	//ʹ����һ��������
	public void handleNext(HttpExchange arg0) throws IOException {
		if(nextHandler != null) {
			nextHandler.handle(arg0);
		}
	}
}





