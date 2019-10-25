package model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.sql.Timestamp;

public class Journal {
    private String title;
    private String thought;
    private String imageUrl;
    private String userId;
    private Timestamp timeAdded;
    private String userName;
    private Location location;
   // private LatLng latLng;
    private double latitude;
    private  double longitude;


    public Journal()//Must for fireStore to work
    {

    }

    public Journal(String title, String thought, String imageUrl, String userId, Timestamp timeAdded, String userName) {
        this.title = title;
        this.thought = thought;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timeAdded = timeAdded;
        this.userName = userName;
    }

//    public Location getLocation() {
//        return location;
//    }

//    public void setLocation(Location location) {
//         this.location = location;
//         this.latitude= location.getLatitude();
//         this.longitude = location.getLongitude();
//    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public double getLatitude(){return latitude; }
    public  double getLongitude(){return longitude;}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp setTimeAdded() {
        return timeAdded;
    }
}
