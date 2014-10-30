/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.emailservice; 

/** 
 * uility class for the EmailGenerator
 * Copyright:    ORNL 2003
 * @Modified by $Author: cxl $
 * @author : Giri Palanisamy
 * @version $Id: ByteArrayDataSource.java,v 1.3 2005/05/03 19:46:05 cxl Exp $ 
 */ 

import java.io.ByteArrayInputStream;
import java.io.InputStream; 
import java.io.IOException; 
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.activation.DataSource; 

/** 
 * Used to create a DataSource for the mail message.
 * @see MailHelper
 */ 

class ByteArrayDataSource implements DataSource 
{ 
  private byte[] data; // data for mail message 
  private String type; // content type/mime type

  /** 
   * Create a DataSource from a String 
   * @param data is the contents of the mail message 
   * @param type is the mime-type such as text/html
   */ 
  ByteArrayDataSource(String data, String type) 
  { 
    try { 
      // Assumption that the string contains only ascii 
      // characters ! Else just pass in a charset into this 
      // constructor and use it in getBytes() 
      this.data = data.getBytes("iso-8859-1");
    } 
    catch (UnsupportedEncodingException uex)
    { 
    } 
    this.type = type; 
  } 

  //DataSource interface methods
  public InputStream getInputStream() throws IOException
  { 
    if (data == null) throw new IOException("no data"); 
    return new ByteArrayInputStream(data);
  } 

  public OutputStream getOutputStream() throws IOException {
     throw new IOException("cannot do this"); 
  } 

  public String getContentType()
  { 
    return type;
  } 

  public String getName() 
  { 
    return "dummy"; 
  } 

} 

