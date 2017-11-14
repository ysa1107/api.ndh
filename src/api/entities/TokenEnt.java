/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.entities;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Y Sa
 */
public class TokenEnt {
    @SerializedName("1")
    public String accessToken;
    @SerializedName("2")
    public long tokenExpiredAt;
    @SerializedName("3")
    public long currentTime;

    public TokenEnt() {
        this.accessToken = "";
        this.tokenExpiredAt = 0;
        this.currentTime = System.currentTimeMillis();
    }
    
    
}
