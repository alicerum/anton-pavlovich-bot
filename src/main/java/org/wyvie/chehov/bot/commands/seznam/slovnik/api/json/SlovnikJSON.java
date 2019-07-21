package org.wyvie.chehov.bot.commands.seznam.slovnik.api.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class SlovnikJSON {

    @SerializedName("translate")
    @Expose
    private List<Translate> translate = null;
    @SerializedName("sound")
    @Expose
    private String sound;
    @SerializedName("relations")
    @Expose
    private Relations relations = null;
    @SerializedName("ftx_samp")
    @Expose
    private List<FtxSamp> ftxSamp = null;
    @SerializedName("other")
    @Expose
    private List<Other> other = null;
    @SerializedName("morf_table")
    @Expose
    private List<Object> morfTable = null;
    @SerializedName("short")
    @Expose
    private List<Object> _short = null;

    /**
     * No args constructor for use in serialization
     */
    public SlovnikJSON() {
    }

    /**
     * @param translate
     * @param sound
     * @param relations
     * @param ftxSamp
     * @param other
     * @param morfTable
     * @param _short
     */
    public SlovnikJSON(List<Translate> translate, String sound, Relations relations, List<FtxSamp> ftxSamp, List<Other> other, List<Object> morfTable, List<Object> _short) {
        super();
        this.translate = translate;
        this.sound = sound;
        this.relations = relations;
        this.ftxSamp = ftxSamp;
        this.other = other;
        this.morfTable = morfTable;
        this._short = _short;
    }

    public List<Translate> getTranslate() {
        return translate;
    }

    public void setTranslate(List<Translate> translate) {
        this.translate = translate;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public Relations getRelations() {
        return relations;
    }

    public void setRelations(Relations relations) {
        this.relations = relations;
    }

    public List<FtxSamp> getFtxSamp() {
        return ftxSamp;
    }

    public void setFtxSamp(List<FtxSamp> ftxSamp) {
        this.ftxSamp = ftxSamp;
    }

    public List<Other> getOther() {
        return other;
    }

    public void setOther(List<Other> other) {
        this.other = other;
    }

    public List<Object> getMorfTable() {
        return morfTable;
    }

    public void setMorfTable(List<Object> morfTable) {
        this.morfTable = morfTable;
    }

    public List<Object> getShort() {
        return _short;
    }

    public void setShort(List<Object> _short) {
        this._short = _short;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("translate", translate).append("sound", sound).append("relations", relations).append("ftxSamp", ftxSamp).append("other", other).append("morfTable", morfTable).append("_short", _short).toString();
    }

}