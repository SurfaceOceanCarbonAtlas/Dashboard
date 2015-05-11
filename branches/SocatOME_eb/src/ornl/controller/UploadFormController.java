/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.controller;

import java.io.File;

import java.io.FileOutputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import ornl.beans.Configuration;
import ornl.beans.Editor;
import ornl.beans.Metadata_Editor;
import ornl.beans.UploadForm;
import ornl.beans.Statistics;
import ornl.client.UserInfo;
import ornl.database.Files;
import ornl.database.FilesDAO;
import ornl.service.EditorService;
import ornl.validator.EditorValidator;

@Controller
@RequestMapping(value = "/editor.htm")
public class UploadFormController extends BaseController implements
		HandlerExceptionResolver {
	User user;
	String name;
	ArrayList<String> al1 = new ArrayList<String>();
	EditorValidator customerValidator;

	@RequestMapping(method = RequestMethod.GET)
	public String showForm(ModelMap model, HttpServletRequest request) {
		UploadForm form = new UploadForm();
		Editor editor = new Editor();
		model.addAttribute("FORM", form);
		model.addAttribute("editor", editor);
		model.addAttribute("username", name);
		boolean b = false;
		String doc_base = "";
		String fs = System.getProperty("file.separator");
		ApplicationContext factory = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		Configuration cv = (Configuration) factory.getBean("propertiesBean");
		HashMap hmProps = cv.getProperties();
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {

			doc_base = (String) hmProps.get("win_base");

		}

		else {

			doc_base = (String) hmProps.get("lin_base");

		}

		try {
			if (isLoggedIn()) {
				user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				name = user.getUsername();

				// System.out.println("initForm:loginName : " +name );
			} else {
				return "index";
			}
		} catch (Exception exc) {

		}
		String users[] = { "admin","guest"};
		String guest = name ;
		// System.outout.println(guest);

		String dir = doc_base ;
		editor.setHomePath(dir);

		String fileTypeFolder = doc_base + guest + fs;
		File d = new File(fileTypeFolder);
		
		String[] entries = (d.list() != null) ? d.list() : new String[0];
		if (entries.length > 0 && entries != null)
			Arrays.sort(entries, 1, entries.length,
					String.CASE_INSENSITIVE_ORDER);
		model.addAttribute("files", entries);
		model.addAttribute("users", users);
		// need change here, since this may return an object which is not a User
		// type.
		// User user = (User)
		// SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		b = request.isUserInRole("ROLE_ADMIN");
		if (null == model.get("editor")) {
			editor.setMdFile("fgdc_xml_test2.xml");

		} else {
			editor.setMdFile(((Editor) (model.get("editor"))).getMdFile());
			
		}

		if (b) {
			// System.outout.println("admineditform");
			return "AdminEditorForm";
		}

		return "EditorForm";
	}

	@RequestMapping(value = "/editor", params = "loader", method = RequestMethod.POST)
	public String processEdits(
			@RequestParam(value = "fileURI", required = false) String fileURI,
			@ModelAttribute("editor") Editor editor, BindingResult result,
			SessionStatus status, ModelMap model) {
		// String s1 = null;
		// String s2 = null;
		//System.out.println("editor2");
		String fs = System.getProperty("file.separator");
		ApplicationContext factory = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		Configuration cv = (Configuration) factory.getBean("propertiesBean");
		HashMap hmProps = cv.getProperties();
		EditorService esd = new EditorService();
		Metadata_Editor testMe = new Metadata_Editor();
		UserInfo userInfo = new UserInfo();
//		try {
//			if (isLoggedIn()) {
//				user = (User) SecurityContextHolder.getContext()
//						.getAuthentication().getPrincipal();
//				name = user.getUsername();
//				editor.setaUser(name);
//				// System.out.println("initForm:loginName : " +name );
//			} else {
//				return ("index");
//			}
//		} catch (Exception exc) {
//
//		}
//		try {
//			if (userInfo.isAdmin(name)) {
//				editor.setadminUser("true");
//			} else {
//				editor.setadminUser("false");
//			}
//		} catch (Exception e) {
//		}
		String doc_base = "";
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			doc_base = (String) hmProps.get("win_base");
		} else {
			doc_base = (String) hmProps.get("lin_base");
		}

		try {

			// s1 = editor.getHomePath();
			// s2 = editor.getMdFile();
			testMe = esd.readFGDC(doc_base+editor.getProfile()+fs+editor.getMdFile());
			testMe.setField_filename(editor.getMdFile());
			editor.setMed(testMe);		
			model.addAttribute("fgdcMap", testMe);
			model.addAttribute("editor", editor);
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
		}
		// customerValidator.validate(editor, result);
		if (result.hasErrors()) {
			return "AdminEditorForm";
		} else {
			status.setComplete();			
				return "form";
		}

	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processForm(
			@ModelAttribute(value = "FORM") UploadForm form,
			BindingResult result, ModelMap model) {
		
		
		Editor editor = new Editor();
		UserInfo userInfo = new UserInfo();
		ApplicationContext factory = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		Configuration cv = (Configuration) factory.getBean("propertiesBean");
		HashMap hmProps = cv.getProperties();
		
		String doc_base = "";
		String fs = System.getProperty("file.separator");
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {

			doc_base = (String) hmProps.get("win_base");

		}

		else {

			doc_base = (String) hmProps.get("lin_base");

		}
		
//		try {
//			if (isLoggedIn()) {
//				user = (User) SecurityContextHolder.getContext()
//						.getAuthentication().getPrincipal();
//				name = user.getUsername();
//				editor.setaUser(name);
//				// System.out.println("initForm:Name : " +name );
//			} else {
//				return new ModelAndView("index");
//			}
//		} catch (Exception exc) {
//
//		}
		String users[] = { "guest","admin" };
		String dir = doc_base+name+fs;
		editor.setHomePath(dir);
		File d = new File(dir);
		String[] entries = d.list();
		if (entries.length > 0 && entries != null)
		Arrays.sort(entries, 1, entries.length, String.CASE_INSENSITIVE_ORDER);
		model.addAttribute("files", entries);
		model.addAttribute("users", users);
		model.addAttribute("editor", editor);

		

		try {
			if (userInfo.isAdmin(name)) {
				editor.setadminUser("true");
			} else {
				editor.setadminUser("false");
			}
		} catch (Exception e) {
		}
		
		if (!result.hasErrors()) {

			
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			System.out.println(form.getFile().getOriginalFilename());
			String mimeType = fileNameMap.getContentTypeFor(form.getFile()
					.getOriginalFilename());
			if (mimeType != null && mimeType.length() > 0) {
				if (!mimeType.equalsIgnoreCase("application/xml")) {
					model.addAttribute("error", "true");

					if (userInfo.isAdmin(name))

						return new ModelAndView("AdminEditorForm");
					else
						return new ModelAndView("EditorForm");
				}
			} else {
				if (userInfo.isAdmin(name))

					return new ModelAndView("AdminEditorForm");
				else
					return new ModelAndView("EditorForm");
			}
			FileOutputStream outputStream = null;
			String filePath = System.getProperty("java.io.tmpdir") + "/"
					+ form.getFile().getOriginalFilename();
			
			try {
				outputStream = new FileOutputStream(new File(filePath));
				outputStream.write(form.getFile().getFileItem().get());

				EditorService esd = new EditorService();

				Metadata_Editor testMe = new Metadata_Editor();

				try {
					testMe = esd.readFGDC(filePath);

					editor.setMed(testMe);
					model.addAttribute("fgdcMap", testMe);

					// model.addAttribute("FORM", form);
				} catch (Exception exc) {
					exc.printStackTrace();
				} finally {
					// System.out.println("Trying to parse " + filePath);
				}
				outputStream.close();

			} catch (Exception e) {
				model.addAttribute("error", "true");
				// System.outout.println("admineditform3");
				if (userInfo.isAdmin(name))
					return new ModelAndView("AdminEditorForm");
				else
					return new ModelAndView("EditorForm");
			} finally {
				// System.out.println("filePath => " + filePath);
			}
			
				return new ModelAndView("form", "editor", editor);
		}

		else {
			model.addAttribute("error", "true");
			// return "FileUploadForm";
			return null;
		}
	}

	@RequestMapping(value = "/openUser", params = "openUser", method = RequestMethod.POST)
	public String opwnUser(@ModelAttribute("editor") Editor editor,
			BindingResult result, SessionStatus status, ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		File d = null;
		boolean b = false;
		
		UploadForm form = new UploadForm();
		model.addAttribute("FORM", form);
		
		
		String fs = System.getProperty("file.separator");
		User user = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		String name = user.getUsername();

		ApplicationContext factory = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		Configuration cv = (Configuration) factory.getBean("propertiesBean");
		HashMap hmProps = cv.getProperties();
		String doc_base = "";
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			doc_base = (String) hmProps.get("win_base");
		} else {
			doc_base = (String) hmProps.get("lin_base");
		}
		// String doc_base = (String) param_map.get("doc_base");
		String dir = doc_base + editor.getProfile() + fs;
		editor.setHomePath(dir);

		try {
			d = new File(dir);
			if (!d.isDirectory()) {
				throw new IllegalArgumentException("Not a directory:  " + dir);
			} else {
				String[] entries = d.list();

				b = request.isUserInRole("ROLE_ADMIN");
				String adminUser = new Boolean(b).toString();

				// System.out.println("ROLE_ADMIN=" + b);
				// System.out.println("ROLE_USER=" + c);

				String users[] = { "guest","admin" };
				

				
				model.addAttribute("files", entries);
				model.addAttribute("users", users);
				
				// cleanup neded here !!!
				// Editor editor = new Editor();
				
				
				// below to take care of distinguishing action taken in first
				// versus subsequent visits to this method.
				// if the editor object already exists, use it.
				if (null == model.get("editor")) {
					editor.setMdFile("fgdc_xml_test2.xml");
					editor.setaUser(name);
					editor.setadminUser(adminUser);
				} else {
					editor.setFiles(entries);
					
				
				}
				model.addAttribute("editor", editor);
				
			}
		} catch (Exception exc) {

		}
		if (b) {
			return "AdminEditorForm";
		}
		return "EditorForm";


	}
	

	@RequestMapping(value = "/editor", params = "statistics", method = RequestMethod.POST)
	public ModelAndView processStats(@ModelAttribute("editor") Editor editor,
			BindingResult result, SessionStatus status, ModelMap model,
			HttpServletRequest request, HttpServletResponse response) {
		Statistics s = new Statistics();
		HashMap<String, String> f = new HashMap<String, String>();
		Files files = new Files();
		FilesDAO filesDAO = new FilesDAO();
		List all = filesDAO.findAll();
		Iterator ite = all.iterator();
		while (ite.hasNext()) {

			Files ite2 = (Files) ite.next();
			String filename = ite2.getFileLocation();
			String stat = ite2.getFileStatus();
			f.put(filename, stat);
		}

		s.setStats(f);
		return new ModelAndView("statistics", "s", s);
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object arg2, Exception exception) {

		Map<Object, Object> model = new HashMap<Object, Object>();
		if (exception instanceof MaxUploadSizeExceededException) {
			model.put(
					"errors",
					"File size should be less then "
							+ ((MaxUploadSizeExceededException) exception)
									.getMaxUploadSize() + " byte.");

		} else {
			model.put("errors", "Unexpected error: " + exception.getMessage());
		}

		return new ModelAndView("redirect:editor.htm");

		// return new ModelAndView("/FileUploadForm", (Map) model);
		// return new ModelAndView("login", (Map) model);
	}
}
