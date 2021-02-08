package com.example.haliyikamaapp.Model.Entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "S_USER")
public class S_User {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    Long mid;

    @ColumnInfo(name = "mustId")
    Long mustId;

    @ColumnInfo(name = "id")
    public Long id;

    @ColumnInfo(name = "userName")
    public String userName;

    @ColumnInfo(name = "userPassword")
    public String userPassword;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "surname")
    public String surname;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "mobileNumber")
    public String mobileNumber;

    @ColumnInfo(name = "officeNumber")
    public String officeNumber;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "tenantId")
    public String tenantId;

    @ColumnInfo(name = "active")
    public Boolean active;

    @ColumnInfo(name = "passMustBeChanged")
    public Boolean passMustBeChanged;

    @ColumnInfo(name = "profileImg")
    public String profileImg;

    @ColumnInfo(name = "profileImgStr")
    public String profileImgStr;

    @ColumnInfo(name = "createDate")
    public String createDate;

    @ColumnInfo(name = "senkronEdildi")
    public String senkronEdildi;


    @NonNull
    public Long getMid() {
        return mid;
    }

    public void setMid(@NonNull Long mid) {
        this.mid = mid;
    }

    public Long getMustId() {
        return mustId;
    }

    public void setMustId(Long mustId) {
        this.mustId = mustId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getPassMustBeChanged() {
        return passMustBeChanged;
    }

    public void setPassMustBeChanged(Boolean passMustBeChanged) {
        this.passMustBeChanged = passMustBeChanged;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getProfileImgStr() {
        return profileImgStr;
    }

    public void setProfileImgStr(String profileImgStr) {
        this.profileImgStr = profileImgStr;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getSenkronEdildi() {
        return senkronEdildi;
    }

    public void setSenkronEdildi(String senkronEdildi) {
        this.senkronEdildi = senkronEdildi;
    }


}

