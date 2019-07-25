package com.kontakt.sample.samples;

import com.kontakt.sdk.android.common.model.Device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Support {
    public static List<String> entrada=new ArrayList<>();
    public static List<String> lista=new ArrayList<>();
    public static HashMap<String, String> mapa=new HashMap<>();
    public static List<Device> devices=new ArrayList<>();
    public static List<Integer> bulkList=new ArrayList<>();
    public static void ordenar(){
        Collections.sort(lista);
        Comparator<Device> cmp = new Comparator<Device>() {
            public int compare(Device o1, Device o2) {
                return o1.getSecureProximity().toString().substring(33).compareTo(o2.getSecureProximity().toString().substring(33));
            }
        };
        Collections.sort(devices, cmp);
    }

}
