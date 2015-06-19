/**
 * Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
 * Contact: zzr@ornl.gov 
 */
package ornl.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
import org.springframework.web.servlet.ModelAndView;

import ornl.beans.Editor;
import ornl.beans.EmailLinks;
import ornl.beans.FormElements;
import ornl.beans.Metadata_Editor;
import ornl.beans.UploadForm;
import ornl.service.EditorService;
import ornl.validator.EditorValidator;

@Controller
public class EditorController extends BaseController {

	EditorValidator customerValidator;

	@Autowired
	public EditorController(EditorValidator customerValidator) {
		this.customerValidator = customerValidator;
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true));

	}

	@RequestMapping(value = "/editor.htm", method = RequestMethod.GET)
	public String showForm(ModelMap model, HttpServletRequest request) {

		UploadForm form = new UploadForm();
		Editor editor = new Editor();
		// model.addAttribute("editor", editor);
		model.addAttribute("FORM", form);
		return "EditorForm";
	}

	@RequestMapping(value = "/newForm")
	public String newForm(@ModelAttribute("editor") Editor editor,
			BindingResult result, SessionStatus status, ModelMap model) {

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

	@RequestMapping(value = "/uploadForm", method = RequestMethod.POST)
	public ModelAndView processForm(
			@ModelAttribute(value = "FORM") UploadForm form,
			BindingResult result, ModelMap model) {

		FileOutputStream outputStream = null;
		Editor editor = new Editor();
		try {
			FileNameMap fileNameMap = URLConnection.getFileNameMap();
			String mimeType = fileNameMap.getContentTypeFor(form.getFile()
					.getOriginalFilename());
			if (mimeType != null && mimeType.length() > 0) {
				if (!mimeType.equalsIgnoreCase("application/xml")) {
					model.addAttribute("error", "true");
					return new ModelAndView("EditorForm");
				}
			} else {
				return new ModelAndView("EditorForm");
			}
		} catch (Exception e) {
			model.addAttribute("error", "true");
			return new ModelAndView("EditorForm");
		}

		String filePath = System.getProperty("java.io.tmpdir") + "/"
				+ form.getFile().getOriginalFilename();

		try {
			outputStream = new FileOutputStream(new File(filePath));
			outputStream.write(form.getFile().getFileItem().get());

			EditorService esd = new EditorService();

			Metadata_Editor testMe = new Metadata_Editor();

			testMe = esd.readFGDC(filePath);

			editor.setMed(testMe);
			model.addAttribute("fgdcMap", testMe);
			return new ModelAndView("form", "editor", editor);

		} catch (UnsupportedEncodingException exc) {
			exc.printStackTrace();
			model.addAttribute("error", "true");
			return new ModelAndView("EditorForm");
		} catch (FileNotFoundException exc) {
			model.addAttribute("error", "true");
			exc.printStackTrace();
			return new ModelAndView("EditorForm");
		} catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("error", "true");
			return new ModelAndView("EditorForm");
		} catch (Exception e) {
			model.addAttribute("error", "true");
			return new ModelAndView("EditorForm");
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
			}

		}
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public String initForm(
			@RequestParam(value = "fileURI", required = false) String fileURI,
			ModelMap model, HttpServletRequest request) {
		Editor editor = new Editor();

		if ((null != fileURI) && (fileURI.trim().length() > 0)) {

			model.addAttribute("fileURI", fileURI);
			EditorService esd = new EditorService();

			Metadata_Editor testMe = new Metadata_Editor();
			try {
				testMe = esd.readURI(fileURI);
				editor.setMed(testMe);
				model.addAttribute("fgdcMap", testMe);

			} catch (Exception exc) {
				exc.printStackTrace();
				return "error";
			}
			return ("form");
		}

		return "EditorForm";

	}

	@RequestMapping(value = "/saveForm", params = "submit", method = RequestMethod.POST)
	public String saveAndSubmit(HttpServletRequest request,
			@ModelAttribute("editor") Editor editor, BindingResult result,
			HttpServletResponse response, ModelMap modelMap) throws Exception {

		FormElements fe = new FormElements();
		EmailLinks emailLinks = new EmailLinks();
		fe = this.get_params(request, response);
		HashMap hm = fe.getElements();
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
		System.out.println("######"+(String) (hmo.get("field_resolution_other")));
		String field_dataset_id = (String) (hmo.get("field_title"));
		if (fileName == null || fileName == "") {

			if (field_dataset_id != null) {
				fileName = field_dataset_id;
			} else if( field_ownername!=null){
				 if (field_ownername.length() > 100)
					fileName = field_ownername.subSequence(0, 100) + "_"
							+ dateFormat.format(date);
				else
					fileName = field_ownername + "_" + dateFormat.format(date);
			}
			else
				fileName = dateFormat.format(date);
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

		fe.setElements(hm);
		EditorService editorService = new EditorService();
		sb.append(editorService.buildXMLString(fe));
		String fgdcText = sb.toString();
		response.getWriter().print(fgdcText);
		response.getWriter().flush();
		response.getWriter().close();
	}

	public FormElements get_params(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		FormElements formElements = new FormElements();
		Enumeration paramNames = request.getParameterNames();
		HashMap hm = new HashMap();
		while (paramNames.hasMoreElements()) {
			String paramName = (String) paramNames.nextElement();
			String[] paramValues = request.getParameterValues(paramName);
			if (paramValues.length == 1) {
				String paramValue = paramValues[0];
				if (paramValue.length() == 0) {
				} else {
					if (paramValue != null) {
						hm.put(paramName, new String(paramValue));
					}
				}
			}
		}
		formElements.setElements(hm);
		return formElements;
	}

}
