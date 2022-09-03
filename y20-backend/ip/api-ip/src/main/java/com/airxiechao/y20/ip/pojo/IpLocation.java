package com.airxiechao.y20.ip.pojo;

public class IpLocation {
    private String country;
    private String province;
    private String city;
    private String district;

    public IpLocation() {
    }

    public IpLocation(String country, String province, String city, String district) {
        this.country = country;
        this.province = province;
        this.city = city;
        this.district = district;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }
}
