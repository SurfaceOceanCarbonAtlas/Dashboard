package gov.noaa.pmel.tmap.addxml;

import java.util.Comparator;

public class NameComparator implements Comparator<VariableBean> {

    @Override
    public int compare(VariableBean o1, VariableBean o2) {
       return o1.getName().compareTo(o2.getName());
    }

}
