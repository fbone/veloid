package com.xirgonium.android.place;


public class FavoritePlace {

  Long id;
  String name;
  double latitude;
  double longitude;
  
  //Constructor
  public FavoritePlace(){
    
  }

  
  
//Getters an setters
  public Long getId() {
    return id;
  }

  
  public void setId(Long id) {
    this.id = id;
  }

  
  public String getName() {
    return name;
  }

  
  public void setName(String name) {
    this.name = name;
  }

  
  public double getLatitude() {
    return latitude;
  }

  
  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  
  public double getLongitude() {
    return longitude;
  }

  
  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }
  
  
  
}
