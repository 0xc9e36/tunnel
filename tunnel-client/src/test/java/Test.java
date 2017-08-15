import com.tunnel.common.HttpData;
import com.tunnel.common.HttpUtil;

public class Test {

	public static void main(String[] args) {
		String post = "POST /operation-api/op/common/unsafe_upload_picture HTTP/1.1\r\n"+
						"Host: test.tunnel.com\r\n"+
						"Connection: keep-alive\r\n"+
						"Content-Length: 11087\r\n"+
						"Origin: http://f.tunnel.com\r\n"+
						"User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36\r\n"+
						"X-1008-Session-Token: 7e8ff13f3e674c44581192873fe15ab1\r\n"+
						"Content-Type: multipart/form-data; boundary=----WebKitFormBoundaryWBkMpDZZRL0TOgaT\r\n"+
						"Accept: */*\r\n"+
						"Referer: http://f.tunnel.com/index.html\r\n"+
						"Accept-Encoding: gzip, deflate\r\n"+
						"Accept-Language: zh-CN,zh;q=0.8\r\n"+
						"\r\n"+
						"------WebKitFormBoundaryWBkMpDZZRL0TOgaT\r\n"+
						"Content-Disposition: form-data; name=\"file\"; filename=\"20170725_114906_007.jpg\"\r\n"+
						"Content-Type: image/jpeg\r\n";
		HttpData analyzeHttpData = HttpUtil.analyzeHttpData(post.getBytes());
		System.out.println(analyzeHttpData.getHeader());
	}
}
