package gov.noaa.pmel.tmap.las.client.serializable;

import java.util.List;
import java.util.Locale;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FacetSerializable implements IsSerializable {
    
    String name;
    String prettyName;
    // Key is the name, value is the count.
    List<FacetMember> members;
    
    public FacetSerializable() {
    }
    
    public FacetSerializable(String name) {
        super();
        this.name = name;
        this.prettyName = capitalize(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.prettyName = capitalize(name);
    }

    public String getPrettyName() {
        return prettyName;
    }

    public List<FacetMember> getMembers() {
        return members;
    }

    public void setMembers(List<FacetMember> members) {
        this.members = members;
    }

    public static String capitalize(String str) {

        if (str == null || str.length() == 0 ) {
            return str;
        }
        str = str.replaceAll("_", " ");
        str = str.replaceAll("cf", "CF");
        int strLen = str.length();
        StringBuffer buffer = new StringBuffer(strLen);
        boolean capitalizeNext = true;
        for (int i = 0; i < strLen; i++) {
            String ch = str.substring(i, i+1);

            if (ch.equals(" ")) {
                buffer.append(ch);
                capitalizeNext = true;
            } else if (capitalizeNext) {
                buffer.append(ch.toUpperCase(Locale.ENGLISH));
                capitalizeNext = false;
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }
}
