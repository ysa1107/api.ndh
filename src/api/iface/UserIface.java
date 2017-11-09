/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.JsonResultEnt;
import api.entities.UserViewEnt;
import com.nct.shop.thrift.deal.models.TUserFilter;
import com.nct.shop.thrift.deal.models.TUserListResult;
import com.nct.shop.thrift.deal.models.TUserResult;

/**
 *
 * @author huanlh
 */
public interface UserIface {
    
    JsonResultEnt generateOTP(String username, boolean isRegister, int minRange, int maxRange, int expiredSec, int limitSec, int limitTime);
    
    JsonResultEnt checkOTP(String username, String otpCode);
    
    JsonResultEnt login(String username, String password);
    
    JsonResultEnt resetPassword(String username, String password);
    
    JsonResultEnt register(String username, UserViewEnt info, boolean isUpdate);
    
    JsonResultEnt getUserInfo(String username);
    
    JsonResultEnt getUserInfoWithArea(String username);
    
    JsonResultEnt changePassword(String username, String oldPassword, String newPassword);
    
    JsonResultEnt updateAvatar(String username, String avatar);
    
    JsonResultEnt logout(String username, String accessToken);
    
    JsonResultEnt updateSettings(String username, Boolean favoriteDeal, Boolean featureDeal, Boolean community);
    
    JsonResultEnt updateLocation(String username, String areaStr, Double lat, Double lng);
    
    JsonResultEnt updateLocationV2(String username, String areaStr, Double lat, Double lng);
    
    JsonResultEnt getAppConfig(String osName);
    
    long getUserIdByUsername(String username);
    
    TUserListResult getUsers(TUserFilter filter);
}
