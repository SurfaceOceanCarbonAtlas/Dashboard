/**
* Author: Mercury Software Consortium, Oak Ridge National Laboratory, Oak Ridge, TN
* Contact: zzr@ornl.gov 
*/
package ornl.client;

import java.util.Iterator;
import java.util.List;

import ornl.database.UserRoles;
import ornl.database.UserRolesDAO;

public class UserInfo{
	public String[] getUsers() {
		UserRolesDAO userRolesDAO = new UserRolesDAO();
		List all = userRolesDAO.findAll();
		StringBuffer retKeys = new StringBuffer();
		Iterator ite = all.iterator();
		
		while (ite.hasNext()) {
			UserRoles ite2 = (UserRoles) ite.next();
			if (ite2.getAuthority().equalsIgnoreCase("ROLE_USER")) {
				//System.out.println(ite2.getUsers().getUsername());
				retKeys.append(ite2.getUsers().getUsername()+"#");
			}
		}
		
		return retKeys.toString().split("#");
	}
	
	
	public boolean isAdmin(String user) {
	boolean admin = false;
	//System.out.println("user: "+user);
		UserRolesDAO userRolesDAO = new UserRolesDAO();
		List all = userRolesDAO.findByAuthority("ROLE_ADMIN");
		Iterator ite = all.iterator();
		while (ite.hasNext()) {
				UserRoles users = (UserRoles)ite.next();			
				if(users.getUsers().getUsername().equalsIgnoreCase(user)){
					admin = true;
					//System.out.println("admin=true: "+admin);
					break;
				}
		}
		//System.out.println("admin: "+admin);
	return admin;
	}
}
