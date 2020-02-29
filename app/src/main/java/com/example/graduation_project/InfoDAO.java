package com.example.graduation_project;

import com.skt.Tmap.TMapPoint;

public class InfoDAO {
    String name;
    TMapPoint tMapPoint;
    Double longtitude;
    Double latitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TMapPoint gettMapPoint() {
        return tMapPoint;
    }

    public void settMapPoint(TMapPoint tMapPoint) {
        this.tMapPoint = tMapPoint;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

}
