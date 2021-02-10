package com.example.haliyikamaapp.Model.Entity;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "USER")
//////giriş yapan kullanıcı bilgilerinin tutulduğu tablodur
public class User {

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

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "accessToken")
    public String accessToken;

    @ColumnInfo(name = "refreshToken")
    public String refreshToken;

    @ColumnInfo(name = "roles")
    public String roles;

    @ColumnInfo(name = "pic")
    public String pic;

    @ColumnInfo(name = "fullname")
    public String fullname;

    @ColumnInfo(name = "occupation")
    public String occupation;

    @ColumnInfo(name = "companyName")
    public String companyName;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "tenantId")
    public String tenantId;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "socialNetworks")
    public String socialNetworks;

    @ColumnInfo(name = "passMustBeChanged")
    public Boolean passMustBeChanged;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "surname")
    public String surname;

    @ColumnInfo(name = "phone2")
    public String phone2;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "profileImg")
    public String profileImg;

    @ColumnInfo(name = "senkronEdildi")
    public Boolean senkronEdildi;

    @ColumnInfo(name = "cihazAdi")
    public String cihazAdi;

    @ColumnInfo(name = "cihazNo")
    public String cihazNo;


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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSocialNetworks() {
        return socialNetworks;
    }

    public void setSocialNetworks(String socialNetworks) {
        this.socialNetworks = socialNetworks;
    }

    public Boolean getPassMustBeChanged() {
        return passMustBeChanged;
    }

    public void setPassMustBeChanged(Boolean passMustBeChanged) {
        this.passMustBeChanged = passMustBeChanged;
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

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public Boolean getSenkronEdildi() {
        return senkronEdildi;
    }

    public void setSenkronEdildi(Boolean senkronEdildi) {
        this.senkronEdildi = senkronEdildi;
    }

    public String getCihazAdi() {
        return cihazAdi;
    }

    public void setCihazAdi(String cihazAdi) {
        this.cihazAdi = cihazAdi;
    }

    public String getCihazNo() {
        return cihazNo;
    }

    public void setCihazNo(String cihazNo) {
        this.cihazNo = cihazNo;
    }
}
