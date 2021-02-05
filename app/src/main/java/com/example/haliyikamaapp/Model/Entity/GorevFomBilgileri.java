package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "GOREV_FORM_BILGILERI")
public class GorevFomBilgileri {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    Long mid;

    @ColumnInfo(name = "mustId")
    Long mustId;

    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "gorevId")
    public Long gorevId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "typeName")
    public String typeName;

    @ColumnInfo(name = "readable")
    public Boolean readable;

    @ColumnInfo(name = "writeable")
    public Boolean writeable;

    @ColumnInfo(name = "required")
    public Boolean required;

    @ColumnInfo(name = "value")
    public String value;

    @ColumnInfo(name = "formValues")
    public String formValues;

    @ColumnInfo(name = "senkronEdildi")
    public Boolean senkronEdildi;

    public Boolean getSenkronEdildi() {
        return senkronEdildi;
    }

    public void setSenkronEdildi(Boolean senkronEdildi) {
        this.senkronEdildi = senkronEdildi;
    }



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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Boolean getReadable() {
        return readable;
    }

    public void setReadable(Boolean readable) {
        this.readable = readable;
    }

    public Boolean getWriteable() {
        return writeable;
    }

    public void setWriteable(Boolean writeable) {
        this.writeable = writeable;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFormValues() {
        return formValues;
    }

    public void setFormValues(String formValues) {
        this.formValues = formValues;
    }

    public Long getGorevId() {
        return gorevId;
    }

    public void setGorevId(Long gorevId) {
        this.gorevId = gorevId;
    }
}
