package com.example.apkit.tugasbesar;

/**
 * Created by APKIT on 5/1/2016.
 */
public class DetailTransaksi {
    public String idTransasksi;
    public String idUser;
    public String tglTransaksi;
    public String via;
    public String jumlahTransfer;
    public String buktiURL;
    public String statusTransaksi;
    public String ketTransaksi;

    public String getKetTransaksi() {
        return ketTransaksi;
    }

    public void setKetTransaksi(String ketTransaksi) {
        this.ketTransaksi = ketTransaksi;
    }

    public String getTglTransaksi() {
        return tglTransaksi;
    }

    public void setTglTransaksi(String tglTransaksi) {
        this.tglTransaksi = tglTransaksi;
    }

    public String getIdTransasksi() {
        return idTransasksi;
    }

    public void setIdTransasksi(String idTransasksi) {
        this.idTransasksi = idTransasksi;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getJumlahTransfer() {
        return jumlahTransfer;
    }

    public void setJumlahTransfer(String jumlahTransfer) {
        this.jumlahTransfer = jumlahTransfer;
    }

    public String getBuktiURL() {
        return buktiURL;
    }

    public void setBuktiURL(String buktiURL) {
        this.buktiURL = buktiURL;
    }

    public String getStatusTransaksi() {
        return statusTransaksi;
    }

    public void setStatusTransaksi(String statusTransaksi) {
        this.statusTransaksi = statusTransaksi;
    }
}
