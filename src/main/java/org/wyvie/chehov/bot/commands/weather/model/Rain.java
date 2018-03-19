package org.wyvie.chehov.bot.commands.weather.model;

import com.google.gson.annotations.SerializedName;

public class Rain {

    @SerializedName("3h")
    private int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
