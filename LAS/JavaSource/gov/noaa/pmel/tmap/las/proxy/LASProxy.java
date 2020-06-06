package gov.noaa.pmel.tmap.las.proxy;

import gov.noaa.pmel.tmap.las.util.NameValuePair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.SSLException;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LASProxy {
	public int streamBufferSize = 8196;
	private static Logger log = LoggerFactory.getLogger(LASProxy.class.getName());
	public void executeERDDAPMethodAndSaveResult(String url, File outfile, HttpServletResponse response) throws IOException, HttpException {
		HttpClient client = HttpClientBuilder.create().build();
		RequestConfig config =  RequestConfig.custom().setCircularRedirectsAllowed(true).build();
		HttpGet method = new HttpGet(url);
		method.setConfig(config);
		try {

			HttpResponse httpResponse = client.execute(method);
			int rc = httpResponse.getStatusLine().getStatusCode();

			if (rc != HttpStatus.SC_OK) {

			        String message = EntityUtils.toString(httpResponse.getEntity());
			        if ( message.toLowerCase().contains("query produced no matching")) {
			        	message = "query produced no matching";
			        	throw new IOException(message);
					} else {
						message = message.substring(message.indexOf("<h1>") + 4, message.indexOf("</h1>"));
					}

			        if ( message == null || message.equals("") ) message = "HTTP Error code: "+rc;

				log.error(message);
				if ( response == null ) {
					throw new IOException(message);
				} else {
					response.sendError(rc);
				}
			} 
			InputStream input = httpResponse.getEntity().getContent();
			OutputStream output = new FileOutputStream(outfile);
			stream(input, output);
		} finally {

			method.releaseConnection();
		}
		

	}
	public void executeGetMethodAndSaveResult(String url, File outfile, HttpServletResponse response) throws IOException, HttpException {
		HttpClient client = HttpClientBuilder.create().build();
		RequestConfig config =  RequestConfig.custom().setCircularRedirectsAllowed(true).build();
		HttpGet method = new HttpGet(url);
		method.setConfig(config);
		try {

			HttpResponse httpResponse = client.execute(method);
			int rc = httpResponse.getStatusLine().getStatusCode();

			if (rc != HttpStatus.SC_OK) {

				log.error("HttpGet Error Code: "+rc);
				if ( response == null ) {
					throw new IOException("HttpGet Error Code: "+rc);
				} else {
					response.sendError(rc);
				}
			} 
			InputStream input = httpResponse.getEntity().getContent();
			OutputStream output = new FileOutputStream(outfile);
			stream(input, output);
		} finally {

			method.releaseConnection();
		}
		

	}
	public String executeGetMethodAndReturnResult(String url, HttpServletResponse response) throws IOException, HttpException {
		
		HttpClient client = HttpClientBuilder.create().build();
		RequestConfig config =  RequestConfig.custom().setCircularRedirectsAllowed(true).build();
		HttpGet method = new HttpGet(url);
		method.setConfig(config);

		try {


			HttpResponse httpResponse = client.execute(method);
			int rc = httpResponse.getStatusLine().getStatusCode();

			if (rc != HttpStatus.SC_OK) {


                if ( response == null ) {
                	throw new IOException("Unable to execute method.  RC="+rc);
                } else {
				    response.sendError(rc);
                }

				return null;

			}
			return EntityUtils.toString(httpResponse.getEntity());
		}
		finally {

			method.releaseConnection();
		}
	}
	public String executeGetMethodAndReturnResult(String url) throws HttpException, IOException {
		return executeGetMethodAndReturnResult(url, null);
	}
	   /**
  * Makes HTTP GET request and writes result to response output stream.
  * @param request fully qualified request URL.
  * @param response the response
  * @throws IOException
  * @throws HttpException
  */
	public InputStream executeGetMethodAndReturnStream(String request, HttpServletResponse response, int timeout) throws IOException, HttpException {

		HttpClient client = HttpClientBuilder.create().build();
		RequestConfig config =  RequestConfig.custom().setCircularRedirectsAllowed(true).setConnectionRequestTimeout(timeout*1000).setConnectTimeout(timeout*1000).build();
		HttpGet method = new HttpGet(request);
		method.setConfig(config);
		

	    method.setHeader("Connection", "close");

	    HttpResponse httpResponse = client.execute(method);
		int rc = httpResponse.getStatusLine().getStatusCode();

	    if (rc != HttpStatus.SC_OK) {

	        log.error("HttpGet Error Code: "+rc);

	        //response.sendError(rc);

	        return null;
	    }
	    return httpResponse.getEntity().getContent();

	}
	   /**
     * Makes HTTP GET request and writes result to response output stream.
     * @param request fully qualified request URL.
     * @param response the response
     * @throws IOException
     * @throws HttpException
     */
	public InputStream executeGetMethodAndReturnStream(String request, HttpServletResponse response) throws IOException, HttpException {

		return executeGetMethodAndReturnStream(request, response, -1);
	   
	}
	
	/**
	 * Makes HTTP GET request and writes result to response output stream.
	 * @param request fully qualified request URL.
	 * @param response the response
	 * @throws IOException
	 * @throws HttpException
	 */
	public void executeGetMethodAndStreamResult(String request, HttpServletResponse response) throws IOException, HttpException {
		HttpClient client = HttpClientBuilder.create().build();
		RequestConfig config =  RequestConfig.custom().setCircularRedirectsAllowed(true).build();
		HttpGet method = new HttpGet(request);
		method.setConfig(config);
		method.setHeader("Connection", "close");

		try {

			HttpResponse httpResponse = client.execute(method);
			int rc = httpResponse.getStatusLine().getStatusCode();
			
			if (rc != HttpStatus.SC_OK) {

				log.error("HttpGet Error Code: "+rc);

				response.sendError(rc);

				return;
			}

			streamGetMethodResponse(httpResponse, response.getOutputStream());
		}
		finally {

			method.releaseConnection();
		}
	}
	/**
	 * Makes HTTP GET request and writes result to response output stream.
	 * @param request fully qualified request URL.
	 * @param output OutputStream to write to
	 * @throws IOException
	 * @throws HttpException
	 */
	public void executeGetMethodAndStreamResult(String request, OutputStream output) throws IOException, HttpException {

		HttpClient client = HttpClientBuilder.create().build();
		RequestConfig config =  RequestConfig.custom().setCircularRedirectsAllowed(true).build();
		HttpGet method = new HttpGet(request);
		method.setConfig(config);
		method.setHeader("Connection", "close");

		try {

			HttpResponse httpResponse = client.execute(method);
			int rc = httpResponse.getStatusLine().getStatusCode();

			if (rc != HttpStatus.SC_OK) {

				log.error("HttpGet Error Code: "+rc);

				HttpException exception = new HttpException("HTTP Get returned error: " + rc);

				throw exception;
			}

			streamGetMethodResponse(httpResponse,output);
		}
		finally {

			method.releaseConnection();
		}

	}

	public void streamGetMethodResponse(HttpResponse method, OutputStream output) throws IOException, HttpException {

		InputStream input = method.getEntity().getContent();
		stream(input, output);

	}

	public void stream(InputStream input, OutputStream output) throws IOException {
		try {
			byte[] buffer = new byte[streamBufferSize];
			int count = input.read(buffer);

			while( count != -1 && count <= streamBufferSize ) {

				output.write(buffer,0,count);
				count = input.read(buffer);
			}

			input.close();
			output.close();
		} catch (IOException e) {
			throw e;
		} finally {
			input.close();
			output.close();
		}
	}
	
	HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {

	    public boolean retryRequest(
	            IOException exception,
	            int executionCount,
	            HttpContext context) {
	        if (executionCount >= 5) {
	            // Do not retry if over max retry count
	            return false;
	        }
	        if (exception instanceof InterruptedIOException) {
	            // Timeout
	            return false;
	        }
	        if (exception instanceof UnknownHostException) {
	            // Unknown host
	            return false;
	        }
	        if (exception instanceof ConnectTimeoutException) {
	            // Connection refused
	            return false;
	        }
	        if (exception instanceof SSLException) {
	            // SSL handshake exception
	            return false;
	        }
	        HttpClientContext clientContext = HttpClientContext.adapt(context);
	        HttpRequest request = clientContext.getRequest();
	        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
	        if (idempotent) {
	            // Retry if the request is considered idempotent
	            return true;
	        }
	        return false;
	    }

	};
	public static String makeHTTPostRequestWithHeaders(String connectToURL, List<NameValuePair> keys, String postData) {
		try{
			HttpClient httpClient=new DefaultHttpClient();
			HttpPost httpPost=new HttpPost(connectToURL);
			if(keys!=null){
				
				for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
					NameValuePair nameValuePair = (NameValuePair) iterator.next();			
					httpPost.addHeader(nameValuePair.getName(), nameValuePair.getValue() );
				}
			}
			httpPost.addHeader("Content-Type","application/x-www-form-urlencoded ");
			httpPost.setEntity(new StringEntity(postData));
			HttpResponse httpResponse=httpClient.execute(httpPost);
			return EntityUtils.toString(httpResponse.getEntity(),"utf-8");

		}catch(Exception e){
			log.debug("Post failed: "+e.getMessage());
		}
		return null;

	}
}
