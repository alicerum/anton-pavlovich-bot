package org.wyvie.chehov.bot.commands.seznam.slovnik.api.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Other {

    @SerializedName("entr")
    @Expose
    private String entr;
    @SerializedName("dict")
    @Expose
    private String dict;
    @SerializedName("trans")
    @Expose
    private String trans;

    /**
     * No args constructor for use in serialization
     */
    public Other() {
    }

    /**
     * @param entr
     * @param trans
     * @param dict
     */
    public Other(String entr, String dict, String trans) {
        super();
        this.entr = entr;
        this.dict = dict;
        this.trans = trans;
    }

    public String getEntr() {
        return entr;
    }

    public void setEntr(String entr) {
        this.entr = entr;
    }

    public String getDict() {
        return dict;
    }

    public void setDict(String dict) {
        this.dict = dict;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("entr", entr).append("dict", dict).append("trans", trans).toString();
    }

}