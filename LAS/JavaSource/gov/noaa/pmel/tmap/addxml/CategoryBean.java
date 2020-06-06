package gov.noaa.pmel.tmap.addxml;

import java.util.*;

import org.jdom.*;

/**
 * <p>Title: addXML</p>
 *
 * <p>Description: Reads local or OPeNDAP netCDF files and generates LAS XML
 * configuration information.</p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: NOAA/PMEL/TMAP</p>
 *
 * @author RHS
 * @version 1.0
 */


public class CategoryBean {
	private String name;
	private String doc;
	private String category_include;
	private String variable_include;
	private String constrain_include;
	private String category_include_header;
	private String variable_include_header;
	private String constrain_include_header;
	private List<FilterBean> filters;
	private List<CategoryBean> categories;
	private List<ContributorBean> contributors;
	private String id;
	// In esg we eliminate some sub-categories which we have to track.
	private Set<String> catids = new HashSet<String>();

	public CategoryBean() {
		filters = new ArrayList<FilterBean>();
		categories = new ArrayList<CategoryBean>();
		contributors = new ArrayList<ContributorBean>();
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}

	public void setCategory_include(String category_include) {
		this.category_include = category_include;
	}

	public void setVariable_include(String variable_include) {
		this.variable_include = variable_include;
	}

	public void setConstrain_include(String constrain_include) {
		this.constrain_include = constrain_include;
	}

	public void setCategory_include_header(String category_include_header) {
		this.category_include_header = category_include_header;
	}

	public void setVariable_include_header(String variable_include_header) {
		this.variable_include_header = variable_include_header;
	}

	public void setConstrain_include_header(String constrain_include_header) {
		this.constrain_include_header = constrain_include_header;
	}

	public void setFilters(List<FilterBean> filters) {
		this.filters = filters;
	}
	
	public void setID(String id) {
		this.id = id;
	}

	public void addFilter(FilterBean filter) {
		filters.add(filter);
	}

	public void setCategories(List<CategoryBean> categories) {
		this.categories = categories;
	}

	public void setContributors(List<ContributorBean> contributors) {
		this.contributors = contributors;
	}

	public String getName() {
		return name;
	}
	
	public String getID() {
		return id;
	}

	public String getDoc() {
		return doc;
	}

	public String getCategory_include() {
		return category_include;
	}

	public String getVariable_include() {
		return variable_include;
	}

	public String getConstrain_include() {
		return constrain_include;
	}

	public String getCategory_include_header() {
		return category_include_header;
	}

	public String getVariable_include_header() {
		return variable_include_header;
	}

	public String getConstrain_include_header() {
		return constrain_include_header;
	}

	public List<FilterBean> getFilters() {
		return filters;
	}

	public List<CategoryBean> getCategories() {
		return categories;
	}

	public List<ContributorBean> getContributors() {
		return contributors;
	}
	public void addCatID(String catid) {
		catids.add(catid);
	}
	/**
	 * toXml
	 *
	 * @return Element
	 */
	public Element toXml() {

		Element category = new Element("category");

		for (Iterator catidIt = catids.iterator(); catidIt.hasNext();) {
			String id = (String) catidIt.next();
			Element catid = new Element("catid");
			catid.setAttribute("ID", id);
			category.addContent(catid);
		}
		for (Iterator contribIt = contributors.iterator(); contribIt.hasNext(); ) {
			ContributorBean contribBean = (ContributorBean) contribIt.next();
			Element contributor = contribBean.toXml();

			category.addContent(contributor);
		}
		if ( name != null && !name.equals("") ) {
			category.setAttribute("name", name);
		}
		if ( id != null && !id.equals("") ) {
			category.setAttribute("ID", id);
		}
		if (doc != null && !doc.equals("")) {
			category.setAttribute("doc", doc);
		}
		Iterator subCatsIt = categories.iterator();
		while (subCatsIt.hasNext()) {
			CategoryBean scb = (CategoryBean) subCatsIt.next();
			Element subCatE = scb.toXml();
			if ( subCatE != null ) {
				category.addContent(subCatE);
			}
		}
		if (filters.size() > 0) {
			Iterator filterIt = filters.iterator();
			while (filterIt.hasNext()) {
				FilterBean fb = (FilterBean) filterIt.next();
				Element filterE = fb.toXml(); 

				category.addContent(filterE);
			}
		}    
		return category;
	}

	public void addCategory(CategoryBean c) {
		categories.add(c);		
	}

	
}
