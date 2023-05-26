package com.Smart_Farmer.smartfarmer;

public class Add_Crops_Infromation_Model {
    String headerTitleSin, headerTitleEng, ImageUri, DetailTxt_ENGLISH,DetailTxt_SINDHI,uid;

    public Add_Crops_Infromation_Model(String headerTitleSin, String headerTitleEng, String imageUri, String detailTxt_ENGLISH, String detailTxt_SINDHI, String uid) {
        this.headerTitleSin = headerTitleSin;
        this.headerTitleEng = headerTitleEng;
        this.ImageUri = imageUri;
        this.DetailTxt_ENGLISH = detailTxt_ENGLISH;
        this.DetailTxt_SINDHI = detailTxt_SINDHI;
        this.uid = uid;
    }

    public String getHeaderTitleSin() {
        return headerTitleSin;
    }

    public void setHeaderTitleSin(String headerTitleSin) {
        this.headerTitleSin = headerTitleSin;
    }

    public String getHeaderTitleEng() {
        return headerTitleEng;
    }

    public void setHeaderTitleEng(String headerTitleEng) {
        this.headerTitleEng = headerTitleEng;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public String getDetailTxt_ENGLISH() {
        return DetailTxt_ENGLISH;
    }

    public void setDetailTxt_ENGLISH(String detailTxt_ENGLISH) {
        DetailTxt_ENGLISH = detailTxt_ENGLISH;
    }

    public String getDetailTxt_SINDHI() {
        return DetailTxt_SINDHI;
    }

    public void setDetailTxt_SINDHI(String detailTxt_SINDHI) {
        DetailTxt_SINDHI = detailTxt_SINDHI;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Add_Crops_Infromation_Model() {


    }
}
