package com.xirgonium.android.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;

import com.xirgonium.android.manager.CommonStationManager;
import com.xirgonium.android.veloid.R;

public class NetworkSkeletonParameters {

    private static Vector<NetworkSkeletonParameter> networks = new Vector<NetworkSkeletonParameter>();
    private static Activity                         act      = null;

    public static Vector<NetworkSkeletonParameter> getNetworks() {
        return networks;
    }

    public static void init(Activity act){
        NetworkSkeletonParameters.act = act;

        try {

            InputStream is = act.getResources().openRawResource(R.raw.network_manager);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            ByteArrayInputStream bais = new ByteArrayInputStream(FormatUtility.slurp(is, Constant.CHARSET_UTF8).getBytes());
            Document doc = db.parse(bais);
            int index = 0;
            Element managers = (Element) doc.getElementsByTagName("managers").item(0);
            NodeList managers_children = managers.getChildNodes();
            for (int i = 0; i < managers_children.getLength(); i++) {
                Node aNode = managers_children.item(i);
                if (aNode instanceof Element) {
                    String id = ((Element) aNode).getAttribute("id");
                    String className = ((Element) aNode).getAttribute("class");
                    String commonName = ((Element) aNode).getAttribute("name");
                    String location = ((Element) aNode).getAttribute("location");
                    //int order = Integer.parseInt(((Element) aNode).getAttribute("order"));

                    NetworkSkeletonParameter aNetwork = new NetworkSkeletonParameter();
                    aNetwork.setClassname(className);
                    aNetwork.setId(id);
                    aNetwork.setCommonName(commonName);
                    aNetwork.setLocation(location);
                    aNetwork.setOrder(index++);

                    networks.add(aNetwork);
                }
            }
            Collections.sort(networks);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CommonStationManager buildRequiredNetwork(int index) {

        NetworkSkeletonParameter parameter = networks.elementAt(index);

        try {
            Class myManagerClass = Class.forName(parameter.getClassname());
            CommonStationManager myManager = (CommonStationManager) myManagerClass.newInstance();
            myManager.assignDbHelper(act);
            myManager.setNetworkId(parameter.getId());
            myManager.setCommonName(parameter.getId());

            return myManager;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static CommonStationManager buildRequiredNetwork(String id) {

        NetworkSkeletonParameter parameter = null;

        for (Iterator<NetworkSkeletonParameter> iterator = networks.iterator(); iterator.hasNext();) {
            NetworkSkeletonParameter aParam = (NetworkSkeletonParameter) iterator.next();
            if (aParam.getId().equals(id)) {
                parameter = aParam;
                break;
            }
        }

        try {
            Class myManagerClass = Class.forName(parameter.getClassname());
            CommonStationManager myManager = (CommonStationManager) myManagerClass.newInstance();
            myManager.setNetworkId(parameter.getId());
            myManager.setCommonName(parameter.getCommonName());
            myManager.assignDbHelper(act);

            return myManager;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] getDescriptionArray() {
        String[] result = new String[networks.size()];
        int i = 0;
        for (Iterator<NetworkSkeletonParameter> iterator = networks.iterator(); iterator.hasNext();) {
            NetworkSkeletonParameter aNetwork = (NetworkSkeletonParameter) iterator.next();
           
            String description = aNetwork.getLocation() + " [" + aNetwork.getCommonName() + "]";
            result[i++] = description;
        }
        return result;
    }
    
    public static String[] getIdArray() {
        String[] result = new String[networks.size()];
        int i = 0;
        for (Iterator<NetworkSkeletonParameter> iterator = networks.iterator(); iterator.hasNext();) {
            NetworkSkeletonParameter aNetwork = (NetworkSkeletonParameter) iterator.next();
            result[i++] = aNetwork.getId();
        }
        return result;
    }
}
