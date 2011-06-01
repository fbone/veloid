package com.xirgonium.android.util;

public class NetworkSkeletonParameter implements Comparable<NetworkSkeletonParameter> {

    private String id;
    private String classname;
    private String location;
    private String commonName;
    private int    order;

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
    
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int compareTo(NetworkSkeletonParameter otherNetwork) {
        if (otherNetwork.getOrder() < this.getOrder()) {
            return 1;
        } else {
            return -1;
        }
    }

    

}
