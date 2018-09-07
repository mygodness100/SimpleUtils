package com.wy.http;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * 基于httpclient的http网络编程
 * FIXME
 * @author wanyang
 */
public class HttpClient {
	private static final Logger logger = Logger.getLogger(HttpClient.class);
//	private static CloseableHttpClient  client = HttpClientBuilder.create().build();
	
	public static void testHttp() {
		CloseableHttpClient defaultClient = HttpClients.createDefault();
		try {
			//创建一个uri地址,若有中文,会自动编码
			URI uri =new URIBuilder().setScheme("http").setHost("127.0.0.1:8080").setPath("/JcWeb/User/Login")
					.setParameter("account", "admin").setParameter("password", "123456").build();
			HttpGet httpGet1 = new HttpGet(uri);
			CloseableHttpResponse response = defaultClient.execute(httpGet1);
			System.out.println(response.getEntity().getContentType());
			System.out.println(response.getEntity().getContentEncoding()==null);
			//直接可将返回的流转成字符串,但是转换一次后流就关闭了
			System.out.println(EntityUtils.toString(response.getEntity()));
			//下面的操作,流已经被关闭,将拿不到数据
			System.out.println(response.getEntity().getContent().available());//0
//			byte[] by = new byte[1024];
//			StringBuffer sb = new StringBuffer();
//			while((count = content.read(by)) != -1) {
//				sb.append(new String(by,0,count));
//			}
//			System.out.println(sb.toString());
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void httpGet() {
		CloseableHttpClient defaultClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpGet httpGet = new HttpGet("http://127.0.0.1:8080/JcWeb/Login");
			response = defaultClient.execute(httpGet);
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			if(null != defaultClient) {
				try {
					defaultClient.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.info(e.getMessage());
				}
			}
		}
	}
		
	public static void httpPost() {
		CloseableHttpClient defaultClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost("http://httpbin.org/post");
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("username", "vip"));
            nvps.add(new BasicNameValuePair("password", "secret"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            response = defaultClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
         // do something useful with the response body
            // and ensure it is fully consumed
			EntityUtils.consume(entity);
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			if(null != defaultClient) {
				try {
					defaultClient.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
			if(response != null) {
				try {
					response.close();
				} catch (IOException e) {
					logger.info(e.getMessage());
				}
			}
		}
		
	}
//		instance.execute(new HttpGet("http://www.baidu.com"));
//		String url = "http://www.baidu.com";
//			CloseableHttpResponse response = instance.execute(new HttpGet(url));
//			assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
//			String url = “http://www.baidu.com”;
//				CloseableHttpResponse response = instance.execute(new HttpGet(url));
//				String contentMimeType = ContentType.getOrDefault(response.getEntity()).getMimeType();
//				assertThat(contentMimeType, equalTo(ContentType.TEXT_HTML.getMimeType()))
//				String url = “http://www.baidu.com”;
//					CloseableHttpResponse response = instance.execute(new HttpGet(url));
//					String bodyAsString = EntityUtils.toString(response.getEntity());
//					assertThat(bodyAsString, notNullValue())
//					@Test(expected=SocketTimeoutException.class)
//					public void givenLowTimeout_whenExecutingRequestWithTimeout_thenException() throws ClientProtocolException, IOException{
//					    RequestConfig requestConfig = RequestConfig.custom()
//					    .setConnectionRequestTimeout(50).setConnectTimeout(50)
//					    .setSocketTimeout(50).build();
//					    HttpGet request = new HttpGet(SAMPLE_URL);
//					    request.setConfig(requestConfig);
//					    instance.execute(request);
					
//		instance.execute(new HttpPost(SAMPLE_URL))
////		为HTTP请求配置重定向
//		CloseableHttpClient instance = HttpClientBuilder.create().disableRedirectHandling().build();
//		CloseableHttpResponse response = instance.execute(new HttpGet(SAMPLE_URL));
//		assertThat(reponse.getStatusLine().getStatusCode(), equalTo(301))
////		配置请求的HEADER部
//		HttpGet request = new HttpGet(SAMPLE_URL);
//		request.addHeader(HttpHeaders.ACCEPT, “application/xml”);
//		response = instance.execute(request)
//		//获取响应的HEADER部分
//		CloseableHttpResponse response = instance.execute(new HttpGet(SAMPLE_URL));
//		Header[] headers = response.getHeaders(HttpHeaders.CONTENT_TYPE);
//		assertThat(headers, not(emptyArray()))
//		//关闭或释放资
//		response = instance.execute(new HttpGet(SAMPLE_URL));
//		try{
//		  HttpEntity entity = response.getEntity();
//		  if(entity!=null){
//		InputStream instream = entity.getContent();
//		instream.close();
//		  }
//		} finally{
//		  response.close();
//		}
	
	
}
