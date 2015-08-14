package com.tran.camera365days;

import java.util.Comparator;

public class YMDComparator implements Comparator<GridItem> { 
 
    @Override 
    public int compare(GridItem o1, GridItem o2) { 
        return o1.getTime().compareTo(o2.getTime()); 
    } 
}
