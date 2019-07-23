package org.wyvie.chehov.bot.commands.seznam.slovnik.api.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class FtxSamp {

    @SerializedName("reve")
    @Expose
    private Integer reve;
    @SerializedName("entr")
    @Expose
    private String entr;
    @SerializedName("samp2s")
    @Expose
    private String samp2s;
    @SerializedName("samp2t")
    @Expose
    private String samp2t;

    /**
     * No args constructor for use in serialization
     */
    public FtxSamp() {
    }

    /**
     * @param samp2t
     * @param samp2s
     * @param entr
     * @param reve
     */
    public FtxSamp(Integer reve, String entr, String samp2s, String samp2t) {
        super();
        this.reve = reve;
        this.entr = entr;
        this.samp2s = samp2s;
        this.samp2t = samp2t;
    }

    public Integer getReve() {
        return reve;
    }

    public void setReve(Integer reve) {
        this.reve = reve;
    }

    public String getEntr() {
        return entr;
    }

    public void setEntr(String entr) {
        this.entr = entr;
    }

    public String getSamp2s() {
        return samp2s;
    }

    public void setSamp2s(String samp2s) {
        this.samp2s = samp2s;
    }

    public String getSamp2t() {
        return samp2t;
    }

    public void setSamp2t(String samp2t) {
        this.samp2t = samp2t;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("reve", reve).append("entr", entr).append("samp2s", samp2s).append("samp2t", samp2t).toString();
    }

}