/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.JsonResultEnt;
import api.entities.UserViewEnt;
import entity.TListUserResult;
import entity.TUserFilter;

/**
 *
 * @author Y Sa
 */
public interface UserIface {

    JsonResultEnt login(String username, String password);
    
    JsonResultEnt resetPassword(String username, String password);
    
    JsonResultEnt register(String username, UserViewEnt info, boolean isUpdate);
    
    JsonResultEnt getUserInfo(String username);

    JsonResultEnt changePassword(String username, String oldPassword, String newPassword);

    JsonResultEnt logout(String username, String accessToken);

    long getUserIdByUsername(String username);

    JsonResultEnt updateAvatar(String username, String fileName);

    JsonResultEnt getUsers(TUserFilter filter);
}
