/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.controller.user;

import api.baseController.BaseController;
import api.entities.JsonResultEnt;
import api.serviceUtils.NotificationService;
import api.serviceUtils.UserService;
import api.utils.Constant;
import api.utils.ErrorCode;
import api.utils.FunctionUtils;
import com.kyt.framework.config.LogUtil;
import com.kyt.framework.util.JSONUtil;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Y Sa
 */
public class UserControllerv1 extends BaseController {

    private static final Logger logger = LogUtil.getLogger(UserControllerv1.class);

    @Override
    public JsonResultEnt processRequest(String _packageControler) {
        JsonResultEnt result;
        logger.info("PARAMS:" + getParams());
        if (!getMethod().equals("POST")) {
            return JsonResultEnt.getJsonUnsupportMethod();
        }
        switch (getAction()) {            
            case "login":
                result = login();
                break;
            case "logout":
                result = logout();
                break;
            case "reset-password":
                result = resetPassword();
                break;
            case "change-password":
                result = changePassword();
                break;
            case "register":
                result = register();
                break;
            case "get-user-info":
                result = getUserInfo();
                break;
            case "upload-avatar":
                result = uploadAvatar();
                break;                 
            default:
                result = JsonResultEnt.getJsonUnsupportMethod();
                break;
        }
        logger.info("RESULT: " + JSONUtil.Serialize(result));
        return result;
    }
  

    public JsonResultEnt logout() {
        String username = getParams().get("username");
        String accessToken = getParams().get("accessToken");

        if (FunctionUtils.IsNullOrEmpty(username)) {
            return JsonResultEnt.getJsonInvalidRequest();
        } else {
            return UserService.getInstance().logout(username, accessToken);
        }
    }

    public JsonResultEnt uploadAvatar() {
        String username = getParams().get("username");
        String fileName = getParams().get("fileName");

        logger.info("Param avatar ---- username: " + username + ", fileName: " + fileName);

        if (FunctionUtils.IsNullOrEmpty(username)
                || FunctionUtils.IsNullOrEmpty(fileName)) {
            return JsonResultEnt.getJsonInvalidRequest();
        } else {
            return UserService.getInstance().updateAvatar(username, fileName);
        }
    }

    public JsonResultEnt login() {
        String username = getParams().get("username");
        String password = getParams().get("password");

        if (FunctionUtils.IsNullOrEmpty(username)
                || FunctionUtils.IsNullOrEmpty(password)) {
            return JsonResultEnt.getJsonInvalidRequest();
        } else {
            return UserService.getInstance().login(username, password);
        }
    }

    public JsonResultEnt resetPassword() {
        String username = getParams().get("username");
        String password = getParams().get("newPassword");

        if (FunctionUtils.IsNullOrEmpty(username)
                || FunctionUtils.IsNullOrEmpty(password)) {
            return JsonResultEnt.getJsonInvalidRequest();
        } else {
            return UserService.getInstance().resetPassword(username, password);
        }
    }

    public JsonResultEnt changePassword() {
        String username = getParams().get("username");
        String oldPassword = getParams().get("oldPassword");
        String newPassword = getParams().get("newPassword");

        if (FunctionUtils.IsNullOrEmpty(username)
                || FunctionUtils.IsNullOrEmpty(oldPassword)
                || FunctionUtils.IsNullOrEmpty(newPassword)) {
            return JsonResultEnt.getJsonInvalidRequest();
        } else {
            return UserService.getInstance().changePassword(username, oldPassword, newPassword);
        }
    }

    public JsonResultEnt register() {
        String username = getParams().get("username");
        String info = getParams().get("info");
        String isUpdate = getParams().get("isUpdate");

        if (FunctionUtils.IsNullOrEmpty(username)
                || FunctionUtils.IsNullOrEmpty(info)) {
            return JsonResultEnt.getJsonInvalidRequest();
        } else {
            UserViewEnt userInfo = JSONUtil.DeSerialize(info, UserViewEnt.class);
            if (userInfo == null) {
                return new JsonResultEnt(ErrorCode.INVALID_USER_INFO.getValue());
            }
            return UserService.getInstance().register(username, userInfo, ConvertUtils.toBoolean(isUpdate, false));
        }
    }

    public JsonResultEnt getUserInfo() {
        String username = getParams().get("username");
        boolean area = ConvertUtils.toBoolean(getParams().get("areaname"), true);

        if (FunctionUtils.IsNullOrEmpty(username)) {
            return JsonResultEnt.getJsonInvalidRequest();
        } else if (area) {
            return UserService.getInstance().getUserInfoWithArea(username);
        } else {
            return UserService.getInstance().getUserInfo(username);
        }
    }
}
