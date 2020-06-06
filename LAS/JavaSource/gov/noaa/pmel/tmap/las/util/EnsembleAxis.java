package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gov.noaa.pmel.tmap.las.client.serializable.EnsembleMemberSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.EnsembleAxisSerializable;
import gov.noaa.pmel.tmap.las.client.serializable.EnsembleVariableSerializable;

import org.jdom.Element;

public class EnsembleAxis extends Container implements EnsembleInterface {

	public EnsembleAxis(Element element) {
		super(element);
	}

	public EnsembleAxisSerializable getEnsembleAxisSerializable() {
	    EnsembleAxisSerializable wire_ensemble = new EnsembleAxisSerializable();
	    List<EnsembleMemberSerializable> members = new ArrayList<EnsembleMemberSerializable>();
	    List membersE = getElement().getChildren("member");
	    if ( membersE != null && membersE.size() > 0 ) {
	        for (Iterator memIt = membersE.iterator(); memIt.hasNext();) {
	            Element memberE = (Element) memIt.next();
	            EnsembleMemberSerializable member = new EnsembleMemberSerializable();
	            member.setID(memberE.getAttributeValue("IDREF"));
	            member.setURL(memberE.getAttributeValue("url"));
	            member.setName(memberE.getAttributeValue("name"));
	            List memberVars = memberE.getChildren("variable");
	            EnsembleVariableSerializable[] memberVariables = new EnsembleVariableSerializable[memberVars.size()];
	            int i = 0;
	            for (Iterator varIt = memberVars.iterator(); varIt.hasNext();) {
	                Element memberVar = (Element) varIt.next();
	                EnsembleVariableSerializable memberVariable = new EnsembleVariableSerializable();
	                memberVariable.setID(memberVar.getAttributeValue("IDREF"));
	                memberVariable.setName(memberVar.getAttributeValue("name"));
	                memberVariable.setShortName(memberVar.getAttributeValue("short_name"));
	                memberVariables[i] = memberVariable;
	                i++;
	            }
	            member.setMemberVariables(memberVariables);
	            members.add(member);
	        }
	        EnsembleMemberSerializable[] wire_members = new EnsembleMemberSerializable[members.size()];
	        for (int i = 0; i < members.size(); i++) {
	            wire_members[i] = members.get(i);
	        }
	        wire_ensemble.setMembers(wire_members);
	        wire_ensemble.setHasMembers(true);
	    } else {
	        wire_ensemble.setHasMembers(false);
	        List v = getElement().getChildren("v");
	        String[] values = new String[v.size()];
	        String[] names = new String[v.size()];
	        int vint = 0;
	        for (Iterator vIt = v.iterator(); vIt.hasNext();) {
                Element valueE = (Element) vIt.next();
                String name = valueE.getAttributeValue("label");
                String value = valueE.getTextNormalize();
                if ( name != null ) {
                    names[vint] = name;
                } else {
                    names[vint] = value;
                }
                values[vint] = value;
                vint++;
            }
	        wire_ensemble.setValues(values);
	        wire_ensemble.setNames(names);
	    }
	    String l = element.getAttributeValue("label");
	    if ( l != null && !l.equals("") ) {
	           wire_ensemble.setLabel(l);
	    } else {
	        wire_ensemble.setLabel("Ensemble member");
	    }
	    return wire_ensemble;
	}
	public List<EnsembleMember> getMembers() {
		List membersE = getElement().getChildren("member");
		List<EnsembleMember> members = new ArrayList<EnsembleMember>();
		for (Iterator memIt = membersE.iterator(); memIt.hasNext();) {
			Element memberE = (Element) memIt.next();
			EnsembleMember member = new EnsembleMember(memberE);
			members.add(member);
		}
		return members;
	}
}
