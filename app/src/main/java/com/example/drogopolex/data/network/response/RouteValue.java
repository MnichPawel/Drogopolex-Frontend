package com.example.drogopolex.data.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RouteValue {

    @SerializedName("bbox_koniec")
    @Expose
    private String bboxKoniec;
    @SerializedName("bbox_start")
    @Expose
    private String bboxStart;
    @SerializedName("czas")
    @Expose
    private Double czas;
    @SerializedName("dystans")
    @Expose
    private Double dystans;
    @SerializedName("geometria")
    @Expose
    private Object geometria;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nazwa")
    @Expose
    private String nazwa;
    @SerializedName("start_raw")
    @Expose
    private String startRaw;
    @SerializedName("stop_raw")
    @Expose
    private String stopRaw;

    public String getBboxKoniec() {
        return bboxKoniec;
    }

    public void setBboxKoniec(String bboxKoniec) {
        this.bboxKoniec = bboxKoniec;
    }

    public String getBboxStart() {
        return bboxStart;
    }

    public void setBboxStart(String bboxStart) {
        this.bboxStart = bboxStart;
    }

    public Double getCzas() {
        return czas;
    }

    public void setCzas(Double czas) {
        this.czas = czas;
    }

    public Double getDystans() {
        return dystans;
    }

    public void setDystans(Double dystans) {
        this.dystans = dystans;
    }

    public Object getGeometria() {
        return geometria;
    }

    public void setGeometria(Object geometria) {
        this.geometria = geometria;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getStartRaw() {
        return startRaw;
    }

    public void setStartRaw(String startRaw) {
        this.startRaw = startRaw;
    }

    public String getStopRaw() {
        return stopRaw;
    }

    public void setStopRaw(String stopRaw) {
        this.stopRaw = stopRaw;
    }

}
