/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import ornl.beans.Configuration;
import ornl.beans.Editor;
import ornl.beans.EmailLinks;
import ornl.beans.FormElements;
import ornl.beans.Metadata_Editor;
import ornl.beans.TransactionDetail;
import ornl.client.UserInfo;
import ornl.service.EditorService;
import ornl.service.SimpleFTPClient;
import ornl.validator.EditorValidator;

@Controller
public class EditorController extends BaseController {

	User user;
	String name;
	ModelMap myModel = new ModelMap();

	EditorValidator customerValidator;
	ArrayList<String> al1 = new ArrayList<String>();

	@Autowired
	public EditorController(EditorValidator customerValidator) {
		this.customerValidator = customerValidator;
	}

	
	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public String initForm(
			@RequestParam(value = "fileURI", required = false) String fileURI,
			ModelMap model, HttpServletRequest request) {
		File d = null;
		boolean b = false;
		String fs = System.getProperty("file.separator");
		UserInfo userInfo = new UserInfo();
		ApplicationContext factory = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		Configuration cv = (Configuration) factory.getBean("propertiesBean");
		HashMap hmProps = cv.getProperties();
		String discrete = (String) hmProps.get("discrete");
		String underway = (String) hmProps.get("underway");

		try {
			if (isLoggedIn()) {
				user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				name = user.getUsername();

				// System.out.println("initForm:loginName : " +name );
			} else {
				name = "guest";

			}
		} catch (Exception exc) {

		}
		// this is a decision point for returning to login with a model in tow
		// restructure here to detect and handle multiple new fields ( file ids
		// and alternate logins and ? )
		// for now, this will open remote file in the form.
		// saving is not handled yet, but opens & parses ok.
		String profile = "";

		model.addAttribute("username", name);
		model.addAttribute("message",
				"Spring Security login + database example");

		String[] regusers = {"admin","guest"};
		Editor editor = new Editor();
		try {
			if (isLoggedIn())
				b = request.isUserInRole("ROLE_ADMIN");

			String adminUser = new Boolean(b).toString();

			editor.setUserName(name);
			editor.setadminUser(adminUser);
			// System.out.println("just admin user" +adminUser);
			// System.out.println("get admin user" +editor.getAdminUser());
			
			// editor.setUsers();
			model.addAttribute("users", regusers);
			// below to take care of distinguishing action taken in first
			// versus subsequent visits to this method.
			// if the editor object already exists, use it.
			if (null == model.get("editor")) {
				editor.setMdFile("fgdc_xml_test2.xml");
				editor.setaUser(name);
				editor.setadminUser(adminUser);
			} else {
				editor.setFiles(((Editor) (model.get("files"))).getFiles());				
				editor.setMdFile(((Editor) (model.get("editor"))).getMdFile());
				editor.setaUser(((Editor) (model.get("editor"))).getUserName());
				editor.setadminUser(((Editor) (model.get("editor")))
						.getadminUser());

			}

			model.addAttribute("editor", editor);
			myModel = model;

		} catch (Exception exc) {
		}

		if ((null != fileURI) && (fileURI.trim().length() > 0)) {

			model.addAttribute("fileURI", fileURI);
			EditorService esd = new EditorService();

			Metadata_Editor testMe = new Metadata_Editor();
			try {
				testMe = esd.readURI(fileURI);
				editor.setMed(testMe);
				profile = testMe.getField_form_type();
				model.addAttribute("fgdcMap", testMe);

			} catch (Exception exc) {
				exc.printStackTrace();
				return "error";
			}
			if (discrete.equals(profile))
				return ("formDis");
			if (underway.equals(profile))
				return ("form");
			else
				return ("form");
		}

		if (b) {
			// System.out.println("Hree");
			return "AdminEditorForm";
		}

		return "EditorForm";

	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));

	}	

	@RequestMapping(value = "/newForm")
	public String newForm(@ModelAttribute("editor") Editor editor,
			BindingResult result, SessionStatus status, ModelMap model) {

		UserInfo userInfo = new UserInfo();
		try {
			if (isLoggedIn()) {
				user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				name = user.getUsername();
				editor.setaUser(name);
				// System.out.println("initForm:loginName : " +name );
			} else {
				return "index";
			}
		} catch (Exception exc) {

		}
		try {
			if (userInfo.isAdmin(name)) {
				// System.out.println("Inside isadmin");
				editor.setadminUser("true");
			} else {
				editor.setadminUser("false");
				// System.out.println("ouside isadmin");
			}
		} catch (Exception e) {
		}

		model.addAttribute("editor", editor);

		EditorService esd = new EditorService();
		Metadata_Editor testMe = new Metadata_Editor();

		model.addAttribute("fgdcMap", testMe);
		model.addAttribute("editor", editor);
		if (result.hasErrors()) {
			return "EditorForm";
		} else {
			status.setComplete();
			return "form";
		}
	}

	@RequestMapping(value = "/newFormDis")
	public String newFormDis(@ModelAttribute("editor") Editor editor,
			BindingResult result, SessionStatus status, ModelMap model) {
		UserInfo userInfo = new UserInfo();
		try {
			if (isLoggedIn()) {
				user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				name = user.getUsername();
				editor.setaUser(name);
				// System.out.println("initForm:indexName : " +name );
			} else {
				return "index";
			}
		} catch (Exception exc) {

		}
		try {
			if (userInfo.isAdmin(name)) {
				// System.out.println("Inside isadmin");
				editor.setadminUser("true");
			} else {
				editor.setadminUser("false");
				// System.out.println("ouside isadmin");
			}
		} catch (Exception e) {
		}

		model.addAttribute("editor", editor);

		EditorService esd = new EditorService();
		Metadata_Editor testMe = new Metadata_Editor();

		model.addAttribute("fgdcMap", testMe);
		model.addAttribute("editor", editor);
		if (result.hasErrors()) {
			return "EditorForm";
		} else {
			status.setComplete();
			return "formDis";
		}
	}

	@RequestMapping(value = "/serverForm")
	public String serverForm(@ModelAttribute("editor") Editor editor,
			BindingResult result, SessionStatus status, ModelMap model) {

		UserInfo userInfo = new UserInfo();
		try {
			if (isLoggedIn()) {
				user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				name = user.getUsername();
				editor.setaUser(name);
				// System.out.println("initForm:loginName : " +name );
			} else {
				return "index";
			}
		} catch (Exception exc) {

		}
		try {
			if (userInfo.isAdmin(name)) {
				// System.out.println("Inside isadmin");
				editor.setadminUser("true");
			} else {
				editor.setadminUser("false");
				// System.out.println("Ouside isadmin");
			}
		} catch (Exception e) {
		}

		EditorService esd = new EditorService();
		Metadata_Editor testMe = new Metadata_Editor();
		model.addAttribute("fgdcMap", testMe);
		model.addAttribute("editor", editor);
		if (result.hasErrors()) {
			return "EditorForm";
		} else {
			status.setComplete();
			return "form";
		}
	}

	@RequestMapping(value = "/saveForm", params = "submit", method = RequestMethod.POST)
	public String saveAndSubmit(HttpServletRequest request,
			@ModelAttribute("editor") Editor editor, BindingResult result,
			HttpServletResponse response, ModelMap modelMap) throws Exception {

		UserInfo userInfo = new UserInfo();
		try {
			if (isLoggedIn()) {
				user = (User) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				name = user.getUsername();
				editor.setaUser(name);
				// System.out.println("initForm:loginName : " +name );
			} else {
				return "index";
			}
		} catch (Exception exc) {

		}
		FormElements fe = new FormElements();
		EmailLinks emailLinks = new EmailLinks();
		fe = this.get_params(request, response);
		ApplicationContext factory = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		Configuration cv = (Configuration) factory.getBean("propertiesBean");
		HashMap hmProps = cv.getProperties();
		HashMap hm = fe.getElements();//

		if (!(request instanceof MultipartHttpServletRequest)) {
			System.out.println("Invalid request (multipart request expected)");

		}

		String fs = System.getProperty("file.separator");
		String datafolder = (String) hmProps.get("datafolder");
		int datafilesize = Integer.parseInt((String) hmProps.get("datafilesize"));
		String filetype = (String) hmProps.get("datafiletype");
		Map<String, MultipartFile> files = ((MultipartHttpServletRequest) request)
				.getFileMap();
		Set<String> keys = files.keySet();
		Iterator I = keys.iterator();
		String key = "";
		MultipartFile multipartfile = null;
		while (I.hasNext()) {
			key = (String) I.next();
			multipartfile = files.get(key);

			String orgName = multipartfile.getOriginalFilename();

			if (orgName != null && orgName.length() > 0) {

				String mimeType = multipartfile.getContentType();

				if (!(filetype.contains(mimeType))) {
					Metadata_Editor med = new Metadata_Editor();
					med.setLhm(hm);
					modelMap.addAttribute("editor", editor);
					modelMap.addAttribute("fgdcMap", med);
					modelMap.addAttribute("error",
							"Data file has to be " + filetype);
					return "form";
				}
				if (multipartfile.getSize() > datafilesize) {
					Metadata_Editor med = new Metadata_Editor();
					med.setLhm(hm);
					modelMap.addAttribute("editor", editor);
					modelMap.addAttribute("fgdcMap", med);
					modelMap.addAttribute("error",
							"Data file has to be less than "+datafilesize/(1024*1024)+" MB");
					// myModel = modelMap;
					return "form";
				}
				String filePath = System.getProperty("java.io.tmpdir") + "/"
						+ orgName;
				File dest = new File(filePath);
				try {
					orgName = addDateStamp(orgName);
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					multipartfile.transferTo(dest);
					try {
						SimpleFTPClient SFC = new SimpleFTPClient();
						SFC.setHost(datafolder);
						SFC.setRemoteFile(orgName);
						SFC.connectSimple();
						SFC.uploadFile(filePath);
					} catch (Exception e) {
						e.printStackTrace();
					}

					hm.put(key, "ftp://" + datafolder + orgName);

				} catch (IllegalStateException e) {
					e.printStackTrace();
					// System.out.println("File uploaded failed:" +
					// orgName);
				} catch (IOException e) {
					e.printStackTrace();
					// return "File uploaded failed:" + orgName;
				}

			}
		}

		hm.put("field_filestatus", "submit");
		fe.setElements(hm);
		EditorService editorService = new EditorService();
		editorService.buildXML(fe);

		emailLinks.setFilename(editorService.getFilename());
		emailLinks.setFilepath(editorService.getFilepath());
		modelMap.addAttribute("elinks", emailLinks);
		return "result";
	}
	
	@RequestMapping(value = "/saveForm", params = "draft", method = RequestMethod.POST)
	public ModelAndView saveDraft(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html");
		FormElements fe = new FormElements();
		if (request.getCharacterEncoding() == null)
			request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		fe = this.get_params(request, response);
		EmailLinks emailLinks = new EmailLinks();
		EditorService editorService = new EditorService();
		HashMap hm = fe.getElements();//
		hm.put("field_filestatus", "draft");
		fe.setElements(hm);
		editorService.buildXML(fe);
		emailLinks.setFilename(editorService.getFilename());
		emailLinks.setFilepath(editorService.getFilepath());
		return new ModelAndView("draft", "filename", emailLinks);
	}
	@RequestMapping(value = "/saveForm", params = "approve", method = RequestMethod.POST)
	public ModelAndView fileApprove(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html");
		FormElements fe = new FormElements();
		EmailLinks emailLinks = new EmailLinks();
		fe = this.get_params(request, response);
		HashMap hm = fe.getElements();//
		hm.put("field_filestatus", "approved");
		fe.setElements(hm);
		EditorService editorService = new EditorService();
		editorService.buildXML(fe);
		emailLinks.setFilename(editorService.getFilename());
		emailLinks.setFilepath(editorService.getFilepath());
		return new ModelAndView("approved", "elinks", emailLinks);
	}

	@RequestMapping(value = "/saveForm", params = "delete", method = RequestMethod.POST)
	public String fileDelete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html");
		FormElements fe = new FormElements();
		EmailLinks emailLinks = new EmailLinks();
		fe = this.get_params(request, response);
		HashMap hm = fe.getElements();//
		hm.put("field_filestatus", "approved");
		String record_id = (String) hm.get("field_record_id");
		EditorService editorService = new EditorService();
		Boolean flag = editorService.deleteRecord(record_id);
		if (!flag) {
			return "deleteResultError";
		} else
			return "deleteResult";
	}

	@RequestMapping(value = "/saveForm", params = "preview", method = RequestMethod.POST)
	public ModelAndView preview(@ModelAttribute("editor") Editor editor,
			BindingResult result, SessionStatus status, ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		TransactionDetail td = new TransactionDetail();
		StringBuilder sb = new StringBuilder();
		response.setContentType("text/html");
		FormElements fe = new FormElements();
		fe = this.get_params(request, response);
		HashMap hm = fe.getElements();//
		// hm.put("field_filestatus", "draft");
		// hm.put("field_draft", "false");
		fe.setElements(hm);
		EditorService editorService = new EditorService();
		sb.append(editorService.buildXMLString(fe));
		String fgdcText = sb.toString();
		editor.setFgdcText(fgdcText);
		model.addAttribute("preview", editor);
		td.setHtmltext(fgdcText);
		td.setFilename(editorService.getFilename());
		td.setFilepath(editorService.getFilepath());
		// return "preview";
		return new ModelAndView("preview", "td", td);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/saveForm", params = "savelocally", method = RequestMethod.POST)
	public void savelocally(@ModelAttribute("editor") Editor editor,
			BindingResult result, SessionStatus status, ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		StringBuilder sb = new StringBuilder();
		FormElements feo = this.get_params(request, response);
		HashMap hmo = feo.getElements();
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_MM_SS");
		java.util.Date date = new java.util.Date();
		String fileName = (String) hmo.get("field_filename");
		String field_ownername = (String) (hmo.get("field_ownername"));
		String field_dataset_id = (String) (hmo.get("field_title"));
		if (fileName == null || fileName == "") {

			if (field_dataset_id != null) {
				fileName = field_dataset_id;
			} else {
				if (field_ownername.length() > 100)
					fileName = field_ownername.subSequence(0, 100) + "_"
							+ dateFormat.format(date);
				else
					fileName = field_ownername + "_" + dateFormat.format(date);
			}
		}
		fileName = fileName.replace(".xml", "");
		fileName = fileName.replaceAll(" ", "_");
		fileName = fileName
				.replaceAll("^[.\\\\/:*?\"<>|]?[\\\\/:*?\"<>|]*", "");
		if (fileName.length() > 255) {
			fileName = (String) fileName.subSequence(0, 255);
		}
		response.setContentType("text/html");
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ fileName + ".xml\"");

		FormElements fe = new FormElements();
		fe = this.get_params(request, response);
		HashMap hm = fe.getElements();//

		// hm.put("field_draft", "false");
		fe.setElements(hm);
		EditorService editorService = new EditorService();
		sb.append(editorService.buildXMLString(fe));
		String fgdcText = sb.toString();
		response.getWriter().print(fgdcText);
		response.getWriter().flush();
		response.getWriter().close();
	}

	// previewFGDC
	@RequestMapping(value = "/saveForm", params = "previewFGDC", method = RequestMethod.POST)
	public ModelAndView previewFGDC(@ModelAttribute("editor") Editor editor,
			BindingResult result, SessionStatus status, ModelMap model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		TransactionDetail td = new TransactionDetail();

		StringBuilder sb = new StringBuilder();
		response.setContentType("text/html");
		FormElements fe = new FormElements();
		fe = this.get_params(request, response);
		HashMap hm = fe.getElements();//
		// hm.put("field_draft", "false");
		fe.setElements(hm);
		EditorService editorService = new EditorService();
		sb.append(editorService.buildXMLString(fe));
		String fgdcText = sb.toString();
		editor.setFgdcText(fgdcText);
		model.addAttribute("preview", editor);
		// return "preview";

		td.setHtmltext(fgdcText);

		return new ModelAndView("full", "td", td);

	}

	
	

	@RequestMapping(value = "/viewStyle", method = RequestMethod.GET)
	public ModelAndView viewStyle(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		TransactionDetail td = new TransactionDetail();
		StringBuffer htmlContent = new StringBuffer();
		BufferedReader in = null;
		String htmlurlString = request.getParameter("link");

		try {
			URL lURL = new URL(htmlurlString);

			URLConnection lURLconn = lURL.openConnection();
			in = new BufferedReader(new InputStreamReader(
					lURLconn.getInputStream(), "UTF-8"));

			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				htmlContent.append(inputLine);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("URL Error: '" + htmlurlString + "' e="
					+ ex.getMessage());

			return new ModelAndView("error", "td", td);
		}

		int startBodyIndex = htmlContent.indexOf("<!--BODY-->");
		if (startBodyIndex != -1) {
			htmlContent.delete(0, startBodyIndex);
		}
		int endBodyIndex = htmlContent.indexOf("<!--/BODY-->");
		if (endBodyIndex != -1) {
			htmlContent.delete(endBodyIndex + 12, htmlContent.length());

		}
		// System.out.println("htmlContent:" +htmlContent.toString());
		td.setHtmltext(htmlContent.toString());
		in.close();
		return new ModelAndView("full", "td", td);
	}

	public FormElements get_params(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		FormElements formElements = new FormElements();
		Enumeration paramNames = request.getParameterNames();
		HashMap hm = new HashMap();

		while (paramNames.hasMoreElements()) {

			String paramName = (String) paramNames.nextElement();

			// System.out.println(paramName);

			String[] paramValues = request.getParameterValues(paramName);

			if (paramValues.length == 1) {

				String paramValue = paramValues[0];

				if (paramValue.length() == 0) {

					// System.out.println("No Value");

				} else {

					if (paramValue != null) {

						hm.put(paramName, new String(paramValue));

						// hm.put(paramName, new
						// String(paramValue.getBytes("8859_1"),"UTF8"));

					}
				}

			} /*
			 * else {
			 * 
			 * for (int i = 0; i < paramValues.length; i++) {
			 * 
			 * // System.out.println(paramValues[i]); // hm.put(paramNames,
			 * paramValues[i]);
			 * 
			 * }
			 * 
			 * }
			 */
		}
		formElements.setElements(hm);
		return formElements;
	}

	/**
	 * @return true if the user has one of the specified roles.
	 */
	protected boolean hasRole(String[] roles) {
		boolean result = false;
		for (GrantedAuthority authority : SecurityContextHolder.getContext()
				.getAuthentication().getAuthorities()) {
			String userRole = authority.getAuthority();
			for (String role : roles) {
				if (role.equals(userRole)) {
					result = true;
					break;
				}
			}

			if (result) {
				break;
			}
		}

		return result;
	}


	private String addDateStamp(String filename) {
		String newfilename = "";
		System.out.println(filename);
		String[] parts = filename.split("\\.");
		System.out.println(parts.length);
		String ext = "." + parts[(parts.length - 1)];
		newfilename = filename.replace(ext, "");
		newfilename = newfilename.replaceAll(
				"^[.\\\\/:*?\"<>|]?[\\\\/:*?\"<>|]*", "");
		if (newfilename.length() > 200) {
			newfilename = (String) newfilename.subSequence(0, 200);
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_MM_SS");
		java.util.Date date = new java.util.Date();
		newfilename = newfilename + dateFormat.format(date) + ext;
		return newfilename;
	}

}
