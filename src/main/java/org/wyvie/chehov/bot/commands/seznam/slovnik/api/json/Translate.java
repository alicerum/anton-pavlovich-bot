package org.wyvie.chehov.bot.commands.seznam.slovnik.api.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Translate {

    @SerializedName("head")
    @Expose
    private Head head;
    @SerializedName("grps")
    @Expose
    private List<Grp> grps = null;

    /**
     * No args constructor for use in serialization
     */
    public Translate() {
    }

    /**
     * @param grps
     * @param head
     */
    public Translate(Head head, List<Grp> grps) {
        super();
        this.head = head;
        this.grps = grps;
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public List<Grp> getGrps() {
        return grps;
    }

    public void setGrps(List<Grp> grps) {
        this.grps = grps;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("head", head).append("grps", grps).toString();
    }

}