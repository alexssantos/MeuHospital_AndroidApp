package seven.team.com.meuhospital.model;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {

    private String name;
    private String Address;
    private String phoneNumber;
    private String id;
    private LatLng latlng;

    public PlaceInfo(String name, String address, String phoneNumber, String id, LatLng larlng) {
        this.name = name;
        Address = address;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.latlng = larlng;
    }

    public PlaceInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng larlng) {
        this.latlng = larlng;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", Address='" + Address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", id='" + id + '\'' +
                ", larlng=" + latlng +
                '}';
    }
}
