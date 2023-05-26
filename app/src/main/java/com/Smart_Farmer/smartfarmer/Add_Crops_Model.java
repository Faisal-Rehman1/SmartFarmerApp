package com.Smart_Farmer.smartfarmer;

public class Add_Crops_Model {

    String cropNameSindhi, cropNameEng, cropUid, cropImg;

    public Add_Crops_Model(String cropNameSindhi, String cropNameEng, String cropUid, String cropImg) {
        this.cropNameSindhi = cropNameSindhi;
        this.cropNameEng = cropNameEng;
        this.cropUid = cropUid;
        this.cropImg = cropImg;
    }

    public Add_Crops_Model() {
    }

    public String getCropNameSindhi() {
        return cropNameSindhi;
    }

    public void setCropNameSindhi(String cropNameSindhi) {
        this.cropNameSindhi = cropNameSindhi;
    }

    public String getCropNameEng() {
        return cropNameEng;
    }

    public void setCropNameEng(String cropNameEng) {
        this.cropNameEng = cropNameEng;
    }

    public String getCropUid() {
        return cropUid;
    }

    public void setCropUid(String cropUid) {
        this.cropUid = cropUid;
    }

    public String getCropImg() {
        return cropImg;
    }

    public void setCropImg(String cropImg) {
        this.cropImg = cropImg;
    }
}
