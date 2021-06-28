package com.example.ppn;

import java.util.Map;

public class PointsRecord {
    static int myPoints;
    static Map<String,Integer> myPointsSheet;
    static int groupTotalPoints;

    static int refreshGroupPoints(){
        // TODO: 23/06/2021 connect with friebase DB or smth
        return 1;
    }

    static boolean recalculateMyPoint(){
        // TODO: 23/06/2021 recalculate points based on completed tasks
        return true;
    }
}
