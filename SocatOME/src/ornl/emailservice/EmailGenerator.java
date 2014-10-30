/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.emailservice;

/**
 * This class is for generating and sending text and HTML emails.
 *
 */

import java.io.File;

import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Date;
import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

public class EmailGenerator {
    public static final String SMTP="mail.smtp.host";
    public static final String MAIL_HOST="smtp.ornl.gov";

    public static final String TEXT_EMAIL="text/plain";
    public static final String HTML_EMAIL="text/html";

    public static final int TEXT_TYPE=0;
    public static final int HTML_TYPE=1;

    public static final String DATE="date";
    public static final String FROM="from";
    public static final String TO="to";
    public static final String CC="cc";
    public static final String SUBJECT="subject";
    public static final String BODY="body";
    public static final String EMAIL_LOG="Email log";

    public static final int PROJECT_ID = 0 ;
  
    public EmailGenerator() {
    }

    /**
    * Send Text email to an individual
    *
    * @param from The sender's email
    * @param recipient The intended recipient of the message
    * @param subject The subject of the message
    * @param message The body of the message
    */
    public static void sendTextMail(String recipient, String subject, String message,
                             String from) throws MessagingException {
        //System.out.println("Inside EmailGenerator 1");
        sendMail(recipient, subject, message, from, TEXT_TYPE);
    }

    /**
    *  Send text email to an individual also log the email info in a
    *  log file with a properties format.
    *
    * @param from        The sender's email
    * @param recipient   The intended recipient of the message
    * @param subject     The subject of the message
    * @param message     The body of the message(the HTML part)
    * @param LogFile     Property file where the email will be logged
    */
    public static void sendTextMailWithLog(String recipient, String subject,
                                String message, String from, String logFile)
                                                throws MessagingException {
        sendMailWithLog(recipient, subject, message, from, logFile, TEXT_TYPE);
    }

    /**
    * Send HTML email to an individual
    *
    * @param from The sender's email
    * @param recipient The intended recipient of the message
    * @param subject The subject of the message
    * @param message The body of the message
    */
    public static void sendHTMLMail(String recipient, String subject, String message,
                             String from) throws MessagingException {
        sendMail(recipient, subject, message, from, HTML_TYPE);
    }

    /**
    *  Send text email to an individual also log the email info in a
    *  log file with a properties format.
    *
    * @param from        The sender's email
    * @param recipient   The intended recipient of the message
    * @param subject     The subject of the message
    * @param message     The body of the message(the HTML part)
    * @param LogFile     Property file where the email will be logged
    */
    public static void sendHTMLMailWithLog(String recipient, String subject,
                                String message, String from, String logFile)
                                                throws MessagingException {
        sendMailWithLog(recipient, subject, message, from, logFile, HTML_TYPE);
    }

    /*
    * Send email to an
    * The type of the message, for Text email type is 0 and for HTML Email
    * type is 1
    */
    private static void sendMail(String recipient, String subject, String message,
                             String from, int type) throws MessagingException {
        //System.out.println("Inside EmailGenerator sendMail and recipient="+recipient+" AND subject="+subject+
        				//	" AND message="+message+" AND from="+from+" AND type="+type);
        
        try{        	
   
        
        //set the host smtp address
        Properties props = new Properties();
        props.put (SMTP, MAIL_HOST);

        //create some proerties and get the default session
        Session session = Session.getDefaultInstance(props, null);
	
        //create a message
        Message msg = new MimeMessage(session);

        //set the from and to address
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);

        InternetAddress addressTo = new InternetAddress(recipient);
        msg.setRecipient(Message.RecipientType.TO, addressTo);
        msg.setSentDate(new Date());
        
        //set the subject and content type
        msg.setSubject(subject);

        // send a text email
        if (type == TEXT_TYPE) {
            msg.setContent(message, TEXT_EMAIL);
        }
        else if (type == HTML_TYPE) {
            msg.setDataHandler(new DataHandler(
		           new ByteArrayDataSource(message, HTML_EMAIL)));
        }
        else {
            // send error this part is not finished
        }
        Transport.send(msg);
        }
        catch(Exception e)
        {
          //System.out.println("error is here in sendmail()="+e.getMessage());
          throw new MessagingException();
        }
    }


    /* Send email to an individual also this method provides the function
    *  to log the email info in a specified log file with a properties format.
    * The type of the message, for Text email type is 0 and for HTML Email
    * type is 1
    */
    private static void sendMailWithLog(String recipient, String subject, String message,
                                    String from, String logFile, int type) throws MessagingException {
        try {
            File filetodelete = new File(logFile);

            filetodelete.delete();

            Properties faxlog = new Properties();
            faxlog.setProperty(DATE,(new Date()).toString());
            faxlog.setProperty(FROM,from);
            faxlog.setProperty(TO,recipient);
            faxlog.setProperty(CC,"NULL");
            faxlog.setProperty(SUBJECT,subject);
            faxlog.setProperty(BODY,message);
            faxlog.store(new FileOutputStream(logFile, true),EMAIL_LOG);
        } catch(Exception e) {
            //errorLogger.writeToLog(e.getMessage(), "EmailGenerator", PROJECT_ID);
            //System.out.println("problem in sending mail"+e.getMessage());
        }
        sendMail(recipient, subject, message, from, type);
    }
 
}
