/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ornl.beans.Configuration;
import ornl.database.UserRoles;
import ornl.database.UserRolesDAO;
import ornl.database.Users;
import ornl.database.UsersDAO;
import ornl.emailservice.EmailGenerator;

public class EmailController {
	static ApplicationContext emailsfactory = new ClassPathXmlApplicationContext("applicationContext.xml");
	static Configuration getemails = (Configuration) emailsfactory.getBean("propertiesBean");
	static HashMap email = getemails.getProperties();
	static String basepath = (String) email.get("linkbasepath");
	
	public static String[] getAdminUsers() {
		UserRolesDAO userRolesDAO = new UserRolesDAO();
		List all = userRolesDAO.findAll();
		StringBuffer retKeys = new StringBuffer();
		Iterator ite = all.iterator();

		while (ite.hasNext()) {
			UserRoles ite2 = (UserRoles) ite.next();
			if (ite2.getAuthority().equalsIgnoreCase("ROLE_ADMIN")) {
				retKeys.append(ite2.getUsers().getEmail() + "#");
			}
		}

		return retKeys.toString().split("#");
	}

	public static String getLoggedInUser(String user) {
	String useremail="";
	UsersDAO usesDAO = new UsersDAO();
	
	if(usesDAO.findByUsername(user)!=null){
		List username = usesDAO.findByUsername(user);
		Iterator ite = username.iterator();
		while (ite.hasNext()) {
			Users users = (Users)ite.next();
			useremail =users.getEmail();
		}
		}
	else{
		System.out.println("logged in user not found");
		//useremail = "devarakondar@ornl.gov";
	}

		return useremail;
	}
	
	public static void sendEmail(String filestatus, String title, StringBuffer link, String loggedin_user_email)
			throws MessagingException, ParseException {
		EmailGenerator emailGenerator = new EmailGenerator();
		//String emails = "";
		String[] toAdminemail = null;
		//String toLoggedInUser = null;
		String sender =null;
		Calendar cal = Calendar.getInstance();
		String msgSubstatus ="";
		DateFormat df = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		//System.out.println(loggedin_username + filestatus);
		StringBuffer body = new StringBuffer();
		
		// Getting today's date
		String today = df.format(cal.getTime());
		Date TodayFromSystem = df.parse(today);

		if(email.get("sender")!=null){
		 sender = (String) email.get("sender");
		}
		else{
			sender = "mercury-support@ornl.gov";
		}

		

		
		String fromemail = sender;

		String filelink = link.toString();
		
		
		body.append("<html><body>"+"\n");
		body.append("<a href=\"http://mercury.ornl.gov/OceanOME\"><img src=\"http://mercury.ornl.gov/OceanOME/images/index.gif\"></a>"+"<br>");
		body.append("<b>*** Automated E-Mail - Please do not reply ***</b>");
		body.append("<br>");
		
		if (filestatus.trim().equalsIgnoreCase("submit")){
			
			toAdminemail = getAdminUsers();
			msgSubstatus = "Metadata Editor - your approval requested: ";
			//body.append("<b> Your Approval Requested </b>");		
			body.append("<br>");
			body.append("A new Metadata record "+"<b>'"+title+"'</b> has been submitted. ");
			body.append("<br><br>");
			//body.append(count+") "+"<a href=\"http://www.fluxnet.ornl.gov/fluxnet/sitepage.cfm?SITEID="+key+"\">"+value+"</a><br>");
			body.append("To view this record, please follow this link: <a href=\""+basepath+"show.htm?fileURI="+filelink+"\">"+basepath+"show.htm?fileURI="+filelink+"</a> ");
			body.append("<br><br>");
			body.append("If you have any questions, please contact Alexander Kozyr at kozyra@ornl.gov");
			body.append("<br><br>");
			body.append("<b>*** Automated E-Mail - Please do not reply ***</b>");
			body.append("</body></html>"+"\n");
			
			for (int e = 0; e < toAdminemail.length; e++) {
				//System.out.println("toAdminemail.length "+toAdminemail.length);
								
				emailGenerator.sendHTMLMail(toAdminemail[e],
						msgSubstatus + TodayFromSystem, body.toString(),
						fromemail);
							
			}	
			body = new StringBuffer();
			body.append("<br>");
			body.append("A new Metadata record "+"<b>'"+title+"'</b> has been submitted. ");
			body.append("<br><br>");
			body.append("If you have any questions, please contact Alexander Kozyr at kozyra@ornl.gov");
			body.append("<br><br>");
			body.append("<b>*** Automated E-Mail - Please do not reply ***</b>");
			body.append("</body></html>"+"\n");
			//Also send a email to user
			msgSubstatus = "Metadata Editor - your FGDC record submitted for approval: ";
			//toLoggedInUser = getLoggedInUser(loggedin_username);
							
			
			emailGenerator.sendHTMLMail(loggedin_user_email,
					msgSubstatus + TodayFromSystem, body.toString(),
					fromemail);
			
			
			
		}
		if(filestatus!=""&&filestatus.equalsIgnoreCase("draft")){
			body.append("<br>");
			body.append("A new Metadata record "+"<b>'"+title+"'</b> has been saved as a draft");
			body.append("<br><br>");
			body.append("If you have any questions, please contact United State Geological Survey - CSAS at mercury-support@ornl.gov");
			body.append("<br><br>");
			body.append("<b>*** Automated E-Mail - Please do not reply ***</b>");
			body.append("</body></html>"+"\n");
			
			msgSubstatus = "Metadata Editor - FGDC record saved as draft: ";
			if(loggedin_user_email!=""){
				emailGenerator.sendHTMLMail(loggedin_user_email,
						msgSubstatus + TodayFromSystem, body.toString(),
						fromemail);
			}
		}
		
		if(filestatus!=""&&filestatus.equalsIgnoreCase("approved")){
			msgSubstatus = "Metadata Editor - FGDC record approved: ";
			
			body.append("<br>");
			body.append("Metadata record "+"<b>'"+title+"'</b> has been <b>Approved</b>");
			body.append("<br><br>");
			//body.append(count+") "+"<a href=\"http://www.fluxnet.ornl.gov/fluxnet/sitepage.cfm?SITEID="+key+"\">"+value+"</a><br>");
			body.append("To view this record, please follow this link: <a href=\""+basepath+"show.htm?fileURI="+filelink+"\">"+basepath+"show.htm?fileURI="+filelink+"</a> ");
			body.append("<br><br>");
			body.append("If you have any questions, please contact United State Geological Survey - CSAS at mercury-support@ornl.gov");
			body.append("<br><br>");
			body.append("<b>*** Automated E-Mail - Please do not reply ***</b>");
			body.append("</body></html>"+"\n");
			
			for (int e = 0; e < toAdminemail.length; e++) {
				//System.out.println("toAdminemail.length "+toAdminemail.length);
				emailGenerator.sendHTMLMail(toAdminemail[e],
						msgSubstatus + TodayFromSystem, body.toString(),
						fromemail);
			}		
			
		}
	}

	public static void main(String args[]) throws MessagingException,
			ParseException {
		StringBuffer sb = new StringBuffer();
		sb.append("Links");
		sendEmail("submit","submit", sb, "zzr");
	}
}
