package org.wyvie.chehov.bot.commands.seznam.slovnik.api.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Coll2 {

    @SerializedName("coll2s")
    @Expose
    private String coll2s;
    @SerializedName("coll2t")
    @Expose
    private String coll2t;

    /**
     * No args constructor for use in serialization
     */
    public Coll2() {
    }

    /**
     * @param coll2s
     * @param coll2t
     */
    public Coll2(String coll2s, String coll2t) {
        super();
        this.coll2s = coll2s;
        this.coll2t = coll2t;
    }

    public String getColl2s() {
        return coll2s;
    }

    public void setColl2s(String coll2s) {
        this.coll2s = coll2s;
    }

    public String getColl2t() {
        return coll2t;
    }

    public void setColl2t(String coll2t) {
        this.coll2t = coll2t;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("coll2s", coll2s).append("coll2t", coll2t).toString();
    }

}