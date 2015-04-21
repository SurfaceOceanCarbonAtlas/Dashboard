/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.controller;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.jdom.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ornl.beans.Configuration;
import ornl.beans.MapBean;

@Controller
public class LoginController extends BaseController{

	private ApplicationContext ctx = null;

	private MapBean xmlBean = new MapBean();
	public LinkedHashMap<String, String> param_map = null;
	private HashMap<String, String> fieldMap = new HashMap<String, String>();
	private Document xmlDoc = null;

	
	/*
	 * Method no longer used. part of prototype development
	 */
	@RequestMapping(value = "/welcome", method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {

		File d = null;

		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		String name = user.getUsername();

		
		model.addAttribute("username", name);
		model.addAttribute("message",
				"Spring Security login + database example");

		//ctx = new ClassPathXmlApplicationContext("mergeConfig.xml");
		//xmlBean = (MapBean) ctx.getBean("param_map");
		//param_map = xmlBean.getFgdc();
		ApplicationContext factory = new ClassPathXmlApplicationContext("applicationContext.xml");
		Configuration cv = (Configuration) factory.getBean("propertiesBean");
		HashMap hmProps = cv.getProperties();
		String doc_base = (String) hmProps.get("win_base");
		//String doc_base = (String) param_map.get("doc_base");
		String dir = doc_base + user.getUsername();

		try {

			d = new File(dir);

			if (!d.isDirectory()) {

			throw new IllegalArgumentException("Not a directory:  " + dir);

			} else {

			String[] entries = d.list();


			model.addAttribute("files", entries);

			}

			}

			catch(Exception e){}

			// return "hello";

			return "index";

			// return "tagger";


	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(ModelMap model) {
		System.out.println("************************************* Here is the user from login controller /login => "+
		model.get("username"));
		return "login";

	}

	@RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
	public String loginerror(ModelMap model) {

		model.addAttribute("error", "true");

		// System.out.println("Here is the error from login controller  =>"
		// +model.get("error"));

		return "login";

	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(ModelMap model) {

		return "index";

	}

}
