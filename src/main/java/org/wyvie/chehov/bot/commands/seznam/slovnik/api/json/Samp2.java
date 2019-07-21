package org.wyvie.chehov.bot.commands.seznam.slovnik.api.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Samp2 {

    @SerializedName("samp2s")
    @Expose
    private String samp2s;
    @SerializedName("samp2t")
    @Expose
    private String samp2t;

    /**
     * No args constructor for use in serialization
     */
    public Samp2() {
    }

    /**
     * @param samp2t
     * @param samp2s
     */
    public Samp2(String samp2s, String samp2t) {
        super();
        this.samp2s = samp2s;
        this.samp2t = samp2t;
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
        return new ToStringBuilder(this).append("samp2s", samp2s).append("samp2t", samp2t).toString();
    }

}