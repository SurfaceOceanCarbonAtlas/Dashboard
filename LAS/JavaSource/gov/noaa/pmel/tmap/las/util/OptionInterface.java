package gov.noaa.pmel.tmap.las.util;

import java.util.ArrayList;

public interface OptionInterface {

    public abstract String getHelp();

    public abstract void setHelp(String help);

    public abstract ArrayList<NameValuePair> getMenu();

    public abstract void setMenu(ArrayList<NameValuePair> menu);

    public abstract String getTitle();

    public abstract void setTitle(String title);

    public abstract String getType();

    public abstract void setType(String type);

}