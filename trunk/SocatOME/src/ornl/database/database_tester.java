/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.database;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Transaction;

public class database_tester {

	/**
	 * @param args
	 */
	static long s = 1111112124;
	//static long s1 = 1212;
	
	private static void addAuthor(){
	Author author = new Author();
	
	//author.setId(s1);
	author.setFullName("Ranjeet Devarakonda");
	author.setOrganization("Oak Ridge Nat'l Lab");
	author.setAddress("Bethel Valley Road, Oak Ridge, TN 37831");
	AuthorDAO authorDAO =new AuthorDAO();
	Transaction tx = authorDAO.getSession().beginTransaction();
	authorDAO.save(author);
	tx.commit();
	authorDAO.getSession().close();
	}
	
	
	public static void listAuthor(){
		AuthorDAO authorDAO = new AuthorDAO();
		//Author author =authorDAO.findById((long) 1);
		
	//	for(int i=0;i<authorDAO.findAll().size();i++){
			//System.out.println("Author: "+authorDAO.findById(s).getFullName()+ " "+authorDAO.findById(s).getAddress1()+ " "+authorDAO.findById(s).getAddress2());
		//System.out.println("Author: "+authorDAO.findAll());
	//	}
		List all = authorDAO.findAll();
		Iterator ite = all.iterator();
		while(ite.hasNext()){
			Author ite2 = (Author) ite.next();				
			
			
		}
	
		
		authorDAO.getSession().close();
		
	}
	
	private static void changeAuthor(){
		AuthorDAO authorDAO = new AuthorDAO();
		Author author =authorDAO.findById((long) 1111221);
		
	//	for(int i=0;i<authorDAO.findAll().size();i++){
			//System.out.println("Author: "+authorDAO.findById(s).getFullName()+ " "+authorDAO.findById(s).getAddress1()+ " "+authorDAO.findById(s).getAddress2());
		//System.out.println("Author: "+authorDAO.findAll());
	//	}
		author.setOrganization("Organization here");
		Transaction tx = authorDAO.getSession().beginTransaction();
		authorDAO.save(author);
		tx.commit();
		authorDAO.getSession().close();
	}
	
	
	public static void main(String[] args) {
	//	addAuthor();
	//	listAuthor();
		changeAuthor();
		
		
	}

}
