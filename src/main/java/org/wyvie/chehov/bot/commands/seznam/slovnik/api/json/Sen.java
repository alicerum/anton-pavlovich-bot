package org.wyvie.chehov.bot.commands.seznam.slovnik.api.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Sen {

    @SerializedName("morf")
    @Expose
    private String morf;
    @SerializedName("numb")
    @Expose
    private String numb;
    @SerializedName("phrs")
    @Expose
    private String phrs;
    @SerializedName("styl")
    @Expose
    private String styl;
    @SerializedName("form")
    @Expose
    private String form;
    @SerializedName("trans")
    @Expose
    private List<List<String>> trans = null;
    @SerializedName("coll2")
    @Expose
    private List<Coll2> coll2 = null;
    @SerializedName("samp2")
    @Expose
    private List<Samp2> samp2 = null;
    @SerializedName("note2")
    @Expose
    private String note2;
    @SerializedName("desc2")
    @Expose
    private String desc2;
    @SerializedName("link2")
    @Expose
    private List<String> link2 = null;

    /**
     * No args constructor for use in serialization
     */
    public Sen() {
    }

    /**
     * @param phrs
     * @param morf
     * @param trans
     * @param form
     * @param numb
     * @param link2
     * @param styl
     * @param note2
     * @param coll2
     * @param samp2
     * @param desc2
     */
    public Sen(String morf, String numb, String phrs, String styl, String form, List<List<String>> trans, List<Coll2> coll2, List<Samp2> samp2, String note2, String desc2, List<String> link2) {
        super();
        this.morf = morf;
        this.numb = numb;
        this.phrs = phrs;
        this.styl = styl;
        this.form = form;
        this.trans = trans;
        this.coll2 = coll2;
        this.samp2 = samp2;
        this.note2 = note2;
        this.desc2 = desc2;
        this.link2 = link2;
    }

    public String getMorf() {
        return morf;
    }

    public void setMorf(String morf) {
        this.morf = morf;
    }

    public String getNumb() {
        return numb;
    }

    public void setNumb(String numb) {
        this.numb = numb;
    }

    public String getPhrs() {
        return phrs;
    }

    public void setPhrs(String phrs) {
        this.phrs = phrs;
    }

    public String getStyl() {
        return styl;
    }

    public void setStyl(String styl) {
        this.styl = styl;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public List<List<String>> getTrans() {
        return trans;
    }

    public void setTrans(List<List<String>> trans) {
        this.trans = trans;
    }

    public List<Coll2> getColl2() {
        return coll2;
    }

    public void setColl2(List<Coll2> coll2) {
        this.coll2 = coll2;
    }

    public List<Samp2> getSamp2() {
        return samp2;
    }

    public void setSamp2(List<Samp2> samp2) {
        this.samp2 = samp2;
    }

    public String getNote2() {
        return note2;
    }

    public void setNote2(String note2) {
        this.note2 = note2;
    }

    public String getDesc2() {
        return desc2;
    }

    public void setDesc2(String desc2) {
        this.desc2 = desc2;
    }

    public List<String> getLink2() {
        return link2;
    }

    public void setLink2(List<String> link2) {
        this.link2 = link2;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("morf", morf).append("numb", numb).append("phrs", phrs).append("styl", styl).append("form", form).append("trans", trans).append("coll2", coll2).append("samp2", samp2).append("note2", note2).append("desc2", desc2).append("link2", link2).toString();
    }

}