package org.wyvie.chehov.bot.commands.seznam.slovnik.api.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Relations {

    @SerializedName("Synonyma")
    @Expose
    private List<String> synonyma = null;
    @SerializedName("Antonyma")
    @Expose
    private List<String> antonyma = null;
    @SerializedName("P\u0159edpony")
    @Expose
    private List<String> predpony = null;
    @SerializedName("Odvozen\u00e1 slova")
    @Expose
    private List<String> odvozenaSlova = null;
    @SerializedName("Slovn\u00ed spojen\u00ed")
    @Expose
    private List<String> slovniSpojen = null;
    @SerializedName("dict")
    @Expose
    private String dict;
    @SerializedName("direction")
    @Expose
    private String direction;

    /**
     * No args constructor for use in serialization
     */
    public Relations() {
    }

    /**
     * @param dict
     * @param direction
     * @param slovniSpojen
     * @param odvozenaSlova
     * @param synonyma
     */
    public Relations(List<String> synonyma,
                     List<String> antonyma,
                     List<String> predpony,
                     List<String> odvozenaSlova,
                     List<String> slovniSpojen,
                     String dict,
                     String direction) {
        super();
        this.synonyma = synonyma;
        this.antonyma = antonyma;
        this.predpony = predpony;
        this.odvozenaSlova = odvozenaSlova;
        this.slovniSpojen = slovniSpojen;
        this.dict = dict;
        this.direction = direction;
    }

    public List<String> getOdvozenaSlova() {
        return odvozenaSlova;
    }

    public void setOdvozenaSlova(List<String> odvozenaSlova) {
        this.odvozenaSlova = odvozenaSlova;
    }

    public List<String> getSlovniSpojen() {
        return slovniSpojen;
    }

    public void setSlovniSpojen(List<String> slovniSpojen) {
        this.slovniSpojen = slovniSpojen;
    }

    public String getDict() {
        return dict;
    }

    public void setDict(String dict) {
        this.dict = dict;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public List<String> getSynonyma() {
        return synonyma;
    }

    public List<String> getAntonyma() {
        return antonyma;
    }

    public void setAntonyma(List<String> antonyma) {
        this.antonyma = antonyma;
    }

    public List<String> getPredpony() {
        return predpony;
    }

    public void setPredpony(List<String> predpony) {
        this.predpony = predpony;
    }

    public void setSynonyma(List<String> synonyma) {
        this.synonyma = synonyma;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("synonyma", synonyma)
                .append("antonyma", antonyma)
                .append("predpony", predpony)
                .append("odvozenaSlova", odvozenaSlova)
                .append("slovniSpojen", slovniSpojen)
                .append("dict", dict)
                .append("direction", direction)
                .toString();
    }

}