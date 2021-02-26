package com.example.haliyikamaapp.Model.Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "GOREVLER")
public class Gorevler {
    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "mid")
    Long mid;

    @ColumnInfo(name = "mustId")
    Long mustId;

    @ColumnInfo(name = "taskId")
    public Long taskId;

    @ColumnInfo(name = "taskName")
    public String taskName;

    @ColumnInfo(name = "taskDescription")
    public String taskDescription;

    @ColumnInfo(name = "taskDefKey")
    public String taskDefKey;

    @ColumnInfo(name = "priority")
    public String priority;


    @ColumnInfo(name = "owner")
    public String owner;

    @ColumnInfo(name = "assignee")
    public String assignee;

    @ColumnInfo(name = "procInstId")
    public Long procInstId;

    @ColumnInfo(name = "executionId")
    public Long executionId;

    @ColumnInfo(name = "procDefId")
    public String procDefId;

    @ColumnInfo(name = "taskCreateTime")
    public String taskCreateTime;


    @ColumnInfo(name = "taskDefinitionKey")
    public String taskDefinitionKey;

    @ColumnInfo(name = "dueDate")
    public String dueDate;

    @ColumnInfo(name = "taskCategory")
    public String taskCategory;

    @ColumnInfo(name = "parentTaskId")
    public Long parentTaskId;

    @ColumnInfo(name = "tenantId")
    public Long tenantId;

    @ColumnInfo(name = "formKey")
    public String formKey;

    @ColumnInfo(name = "siparisId")
    public Long siparisId;

    @ColumnInfo(name = "subeId")
    public Long subeId;

    @ColumnInfo(name = "kaynakId")
    public Long kaynakId;

    @ColumnInfo(name = "musteriId")
    public Long musteriId;

    @ColumnInfo(name = "teslimAlinacak")
    public String teslimAlinacak;

    @ColumnInfo(name = "siparisTarihi")
    public String siparisTarihi;

    @ColumnInfo(name = "siparisDurumu")
    public String siparisDurumu;

    @ColumnInfo(name = "kaynakAdi")
    public String kaynakAdi;

    @ColumnInfo(name = "musteriAdi")
    public String musteriAdi;

    @ColumnInfo(name = "musteriSoyadi")
    public String musteriSoyadi;

    @ColumnInfo(name = "musteriTuru")
    public String musteriTuru;

    @ColumnInfo(name = "telefonNumarasi")
    public String telefonNumarasi;

    @ColumnInfo(name = "subeAdi")
    public String subeAdi;

    @ColumnInfo(name = "siparisToplamTutar")
    public Double siparisToplamTutar;

    @ColumnInfo(name = "claimTime")
    public String claimTime;

    @ColumnInfo(name = "processDefinitionName")
    public String processDefinitionName;

    @ColumnInfo(name = "startTime")
    public String startTime;

    @ColumnInfo(name = "startUserId")
    public String startUserId;

    @ColumnInfo(name = "projectName")
    public String projectName;

    @ColumnInfo(name = "senkronEdildi")
    public Boolean senkronEdildi;

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

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskCreateTime() {
        return taskCreateTime;
    }

    public void setTaskCreateTime(String taskCreateTime) {
        this.taskCreateTime = taskCreateTime;
    }


    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public void setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Long getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(Long procInstId) {
        this.procInstId = procInstId;
    }

    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(String procDefId) {
        this.procDefId = procDefId;
    }

    public String getCreateTime() {
        return taskCreateTime;
    }

    public void setCreateTime(String createTime) {
        this.taskCreateTime = createTime;
    }

    public String getTaskDefinitionKey() {
        return taskDefinitionKey;
    }

    public void setTaskDefinitionKey(String taskDefinitionKey) {
        this.taskDefinitionKey = taskDefinitionKey;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getTaskCategory() {
        return taskCategory;
    }

    public void setTaskCategory(String taskCategory) {
        this.taskCategory = taskCategory;
    }

    public Long getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Long parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getFormKey() {
        return formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    public Long getSiparisId() {
        return siparisId;
    }

    public void setSiparisId(Long siparisId) {
        this.siparisId = siparisId;
    }

    public Long getSubeId() {
        return subeId;
    }

    public void setSubeId(Long subeId) {
        this.subeId = subeId;
    }

    public Long getKaynakId() {
        return kaynakId;
    }

    public void setKaynakId(Long kaynakId) {
        this.kaynakId = kaynakId;
    }

    public Long getMusteriId() {
        return musteriId;
    }

    public void setMusteriId(Long musteriId) {
        this.musteriId = musteriId;
    }

    public String getTeslimAlinacak() {
        return teslimAlinacak;
    }

    public void setTeslimAlinacak(String teslimAlinacak) {
        this.teslimAlinacak = teslimAlinacak;
    }

    public String getSiparisTarihi() {
        return siparisTarihi;
    }

    public void setSiparisTarihi(String siparisTarihi) {
        this.siparisTarihi = siparisTarihi;
    }

    public String getSiparisDurumu() {
        return siparisDurumu;
    }

    public void setSiparisDurumu(String siparisDurumu) {
        this.siparisDurumu = siparisDurumu;
    }

    public String getKaynakAdi() {
        return kaynakAdi;
    }

    public void setKaynakAdi(String kaynakAdi) {
        this.kaynakAdi = kaynakAdi;
    }

    public String getMusteriAdi() {
        return musteriAdi;
    }

    public void setMusteriAdi(String musteriAdi) {
        this.musteriAdi = musteriAdi;
    }

    public String getMusteriSoyadi() {
        return musteriSoyadi;
    }

    public void setMusteriSoyadi(String musteriSoyadi) {
        this.musteriSoyadi = musteriSoyadi;
    }

    public String getMusteriTuru() {
        return musteriTuru;
    }

    public void setMusteriTuru(String musteriTuru) {
        this.musteriTuru = musteriTuru;
    }

    public String getTelefonNumarasi() {
        return telefonNumarasi;
    }

    public void setTelefonNumarasi(String telefonNumarasi) {
        this.telefonNumarasi = telefonNumarasi;
    }

    public String getSubeAdi() {
        return subeAdi;
    }

    public void setSubeAdi(String subeAdi) {
        this.subeAdi = subeAdi;
    }

    public Double getSiparisToplamTutar() {
        return siparisToplamTutar;
    }

    public void setSiparisToplamTutar(Double siparisToplamTutar) {
        this.siparisToplamTutar = siparisToplamTutar;
    }

    public String getClaimTime() {
        return claimTime;
    }

    public void setClaimTime(String claimTime) {
        this.claimTime = claimTime;
    }

    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public void setProcessDefinitionName(String processDefinitionName) {
        this.processDefinitionName = processDefinitionName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStartUserId() {
        return startUserId;
    }

    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Boolean getSenkronEdildi() {
        return senkronEdildi;
    }

    public void setSenkronEdildi(Boolean senkronEdildi) {
        this.senkronEdildi = senkronEdildi;
    }
}

