package org.wyvie.chehov.bot.commands.seznam.slovnik.api.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Grp {

    @SerializedName("sens")
    @Expose
    private List<Sen> sens = null;
    @SerializedName("morf")
    @Expose
    private String morf;


    /**
     * No args constructor for use in serialization
     */
    public Grp() {
    }

    /**
     * @param sens
     */
    public Grp(String morf, List<Sen> sens) {
        super();
        this.sens = sens;
        this.morf = morf;
    }

    public List<Sen> getSens() {
        return sens;
    }

    public void setSens(List<Sen> sens) {
        this.sens = sens;
    }

    public String getMorf() {
        return morf;
    }

    public void setMorf(String morf) {
        this.morf = morf;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("morf", morf).append("sens", sens).toString();
    }

}