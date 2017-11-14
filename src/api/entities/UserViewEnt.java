/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.entities;

import api.configuration.ConfigInfo;
import api.utils.FunctionUtils;
import com.google.gson.annotations.SerializedName;
import com.kyt.framework.config.LogUtil;
import entity.TUserValue;
import org.apache.log4j.Logger;

import javax.xml.ws.WebServiceRef;

/**
 *
 * @author Y Sa
 */
public class UserViewEnt {

    private static final Logger _logger = LogUtil.getLogger(UserViewEnt.class);

    @SerializedName("1")
    public Long userId;
    @SerializedName("2")
    public String identityCard;
    @SerializedName("3")
    public String username;
    @SerializedName("4")
    public String password;
    @SerializedName("5")
    public String email;
    @SerializedName("6")
    public String address;
    @SerializedName("7")
    public String phone;
    @SerializedName("8")
    public Long schoolID;
    @SerializedName("9")
    public  String schoolName;
    @SerializedName("10")
    public Long dateCreated;
    @SerializedName("11")
    public String avatar;
    @SerializedName("12")
    public Long dateModified;
    @SerializedName("13")
    public String fullName;
    @SerializedName("14")
    public Long birthday;
    @SerializedName("15")
    public short gender;

    public UserViewEnt() {
        this.userId = 0L;
        this.identityCard= "";
        this.username = "";
        this.password = "";
        this.fullName = "";
        this.email = "";
        this.avatar = "";
        this.address= "";
        this.phone = "";
        this.schoolID = 0L;
        this.dateCreated = 0L;
        this.dateModified = 0L;
        this.gender = 0;
        this.birthday = 0L;
    }
    
    public UserViewEnt(TUserValue user){
        this.userId = user.userID;
        this.username = user.userName;
        this.fullName = user.fullName;
        this.email = user.email;
        this.avatar = !FunctionUtils.IsNullOrEmpty(user.avatarURL) ? (ConfigInfo.ROOT_IMAGE_URL + "/" + ConfigInfo.PREFIX_USER + "/" + user.avatarURL) : "";
        this.password = user.password;
        this.identityCard = user.identityCard;
        this.address = user.address;
        this.phone = user.phone;
        this.schoolID = user.schoolID;
        this.dateCreated= user.dateCreated;
        this.dateModified= user.dateModified;
        this.birthday = user.birthday;
        this.gender = user.gender;
    }
}
