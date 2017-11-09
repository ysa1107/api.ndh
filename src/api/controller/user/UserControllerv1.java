/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.controller.user;

import api.baseController.BaseController;
import api.entities.JsonResultEnt;
import api.entities.UserViewEnt;
import api.serviceUtils.NotificationService;
import api.serviceUtils.UserService;
import api.utils.Constant;
import api.utils.ErrorCode;
import api.utils.FunctionUtils;
import com.nct.shop.thrift.deal.models.NotificationStatus;
import com.nct.shop.thrift.deal.models.NotificationType;
import com.nct.shop.thrift.deal.models.PushedUserType;
import com.nct.shop.thrift.deal.models.TNotificationFilter;
import com.nct.shop.thrift.deal.models.TNotificationResult;
import com.nct.shop.thrift.deal.models.TNotificationUser;
import com.nct.shop.thrift.deal.models.TNotificationUserResult;
import com.shopiness.framework.common.Config;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.util.ConvertUtils;
import com.shopiness.framework.util.JSONUtil;
import com.shopiness.framework.util.StringUtils;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author huanlh
 */
public class UserControllerv1 extends BaseController {

    private static final Logger logger = LogUtil.getLogger(UserControllerv1.class);

    private static final int MIN_RANGE = 1111;
    private static final int MAX_RANGE = 1111;
    private static final int OTP_EXPIRED_SEC = 60 * 5000;
    private static final int OTP_LIMIT_SEC = 60 * 10000; //10 phut
    private static final int OTP_LIMIT_TIME = 5000; // Toi da 5 lan genOTP trong 10 phut

    @Override
    public JsonResultEnt processRequest(String _packageControler) {
        JsonResultEnt result;
        logger.info("PARAMS:" + getParams());
        if (!getMethod().equals("POST")) {
            return JsonResultEnt.getJsonUnsupportMethod();
        }
        switch (getAction()) {
            case "gen-otp":
                result = generateOTP();
                break;
            case "check-otp":
                result = checkOTP();
                break;
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
            case "notifications":
                result = getSummaryNotification();
                break;
            case "deal-notifications":
                result = getNotifications(Constant.GROUP_DEAL);
                break;
            case "community-notifications":
                result = getNotifications(Constant.GROUP_LIKE_COMMENT);
                break;
            case "view-notification":
                result = viewNotification();
                break;
            case "delete-notification":
                result = deleteNotificationUser();
                break;
            case "settings":
                result = updateSettings();
                break;
            case "location":
                result = updateLocation();
                break;
            case "locationEx":
                result = updateLocationV2();
                break;
            default:
                result = JsonResultEnt.getJsonUnsupportMethod();
                break;
        }
        logger.info("RESULT: " + JSONUtil.Serialize(result));
        return result;
    }

    public JsonResultEnt updateLocation() {
        String username = getParams().get("username");
        String areaStr = getParams().get("area");
        String latStr = getParams().get("lat");
        String lngStr = getParams().get("lng");

        Double lat = null;
        Double lng = null;

        if (!FunctionUtils.IsNullOrEmpty(latStr)) {
            lat = ConvertUtils.toDouble(latStr);
        }
        if (!FunctionUtils.IsNullOrEmpty(lngStr)) {
            lng = ConvertUtils.toDouble(lngStr);
        }
        if (!FunctionUtils.IsNullOrEmpty(areaStr)) {
            areaStr = areaStr.trim();
        }

        return UserService.getInstance().updateLocation(username, areaStr, lat, lng);
    }

    public JsonResultEnt updateLocationV2() {
        String username = getParams().get("username");
        String areaStr = getParams().get("area");
        String latStr = getParams().get("lat");
        String lngStr = getParams().get("lng");

        Double lat = null;
        Double lng = null;

        if (!FunctionUtils.IsNullOrEmpty(latStr)) {
            lat = ConvertUtils.toDouble(latStr);
        }
        if (!FunctionUtils.IsNullOrEmpty(lngStr)) {
            lng = ConvertUtils.toDouble(lngStr);
        }
        if (!FunctionUtils.IsNullOrEmpty(areaStr)) {
            areaStr = areaStr.trim();
        }

        return UserService.getInstance().updateLocationV2(username, areaStr, lat, lng);
    }

    public JsonResultEnt updateSettings() {
        String username = getParams().get("username");
        String favoriteDealStr = getParams().get("favoriteDeal");
        String featureDealStr = getParams().get("featureDeal");
        String communityStr = getParams().get("community");

        if (FunctionUtils.IsNullOrEmpty(username)) {
            return JsonResultEnt.getJsonInvalidRequest();
        } else {
            Boolean favoriteDeal = null;
            Boolean featureDeal = null;
            Boolean community = null;

            if (!FunctionUtils.IsNullOrEmpty(favoriteDealStr)) {
                favoriteDeal = ConvertUtils.toBoolean(favoriteDealStr, false);
            }
            if (!FunctionUtils.IsNullOrEmpty(featureDealStr)) {
                featureDeal = ConvertUtils.toBoolean(featureDealStr, false);
            }
            if (!FunctionUtils.IsNullOrEmpty(communityStr)) {
                community = ConvertUtils.toBoolean(communityStr, false);
            }

            return UserService.getInstance().updateSettings(username, favoriteDeal, featureDeal, community);
        }
    }

    public JsonResultEnt viewNotification() {
        String username = getParams().get("username");
        String notiType = getParams().get("notiType");
        String viewType = getParams().get("viewType");
        String objectIds = getParams().get("objectIds");

        Integer type = ConvertUtils.toInt(notiType, 0);
        Integer vType = ConvertUtils.toInt(viewType, 0);

        if (FunctionUtils.IsNullOrEmpty(username)
                || (type != Constant.GROUP_DEAL && type != Constant.GROUP_LIKE_COMMENT)
                || (vType != Constant.ALL && vType != Constant.OBJECT_IDS)) {
            return JsonResultEnt.getJsonInvalidRequest();
        } else {
            return NotificationService.getInstance().viewNotification(username, type, vType, objectIds);
        }
    }

    public JsonResultEnt getSummaryNotification() {
        String username = getParams().get("username");

        if (FunctionUtils.IsNullOrEmpty(username)) {
            return JsonResultEnt.getJsonInvalidRequest();
        } else {
            return NotificationService.getInstance().getSummaryNotification(username);
        }
    }

    public JsonResultEnt getNotifications(int groupType) {
        String username = getParams().get("username");
        String index = getParams().get("pageIndex");
        String size = getParams().get("pageSize");

        TNotificationFilter filter = new TNotificationFilter();
        filter.setGroupNotificationType(groupType);
        filter.setStatus(NotificationStatus.SENDED.getValue());
        filter.setPageIndex(ConvertUtils.toInt(index, 1));
        filter.setPageSize(ConvertUtils.toInt(size, 10));

        //Neu nguoi dung chua dang nhap thi lay danh sach push all
        if (!FunctionUtils.IsNullOrEmpty(username)) {
            filter.setUsername(username);
        } else {
            filter.setPushedUserType(PushedUserType.ALL.getValue());
        }

        return NotificationService.getInstance().getJsonNotifications(filter);
    }

    public JsonResultEnt deleteNotificationUser() {
        String id = getParams().get("id");
        String username = getParams().get("username");
        long notificatioId = ConvertUtils.toLong(id, 0);

        if (notificatioId <= 0 || StringUtils.isEmpty(username)) {
            return JsonResultEnt.getJsonInvalidRequest();
        }
        // check and delete notification is like/comment 
        TNotificationResult notification = NotificationService.getInstance().getNotification(notificatioId);
        if (notification == null || notification.error != 0 || notification.value == null) {
            return JsonResultEnt.getJsonDataNotExist();
        }
        if (notification.value.type == NotificationType.LIKE || notification.value.type == NotificationType.COMMENT) {
            if (!NotificationService.getInstance().deleteNotificationUser(notificatioId)) {
                return JsonResultEnt.getJsonDataNotExist();
            }
        }
        // delete notification of each user
        TNotificationUserResult notiUser = NotificationService.getInstance().getNotificationUserByUsername(username);
        if (notiUser != null && notiUser.getValue() != null) {
            List<String> newIds = StringUtils.toList(notiUser.getValue().newIds, ",");
            List<String> notiIds = StringUtils.toList(notiUser.getValue().notificationIds, ",");
            newIds.remove(id);
            notiIds.remove(id);

            TNotificationUser newVal = notiUser.getValue();
            newVal.setNewIds(StringUtils.join(newIds, ","));
            newVal.setNotificationIds(StringUtils.join(notiIds, ","));
            if (!NotificationService.getInstance().updateNotificationUser(newVal)) {
                return JsonResultEnt.getJsonDataNotExist();
            }
        }

        return JsonResultEnt.getJsonSuccess();
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

    public JsonResultEnt generateOTP() {
        String phoneNumber = getParams().get("phoneNumber");
        String usernameHash = getParams().get("usernameHash");
        String type = getParams().get("isRegister");
        String hashCode = FunctionUtils.DoMD5(phoneNumber + Config.getParam("settings", "secrect_key"));

        if (FunctionUtils.IsNullOrEmpty(phoneNumber)) {
            return JsonResultEnt.getJsonInvalidRequest();
        }
        if (!hashCode.equals(usernameHash)) {
            return JsonResultEnt.getJsonUsernameHashInvalid();
        }

        boolean isRegister = ConvertUtils.toBoolean(type, true);
        return UserService.getInstance().generateOTP(phoneNumber, isRegister, MIN_RANGE, MAX_RANGE,
                OTP_EXPIRED_SEC, OTP_LIMIT_SEC, OTP_LIMIT_TIME);
    }

    public JsonResultEnt checkOTP() {
        String phoneNumber = getParams().get("phoneNumber");
        String otpCode = getParams().get("otpCode");

        if (FunctionUtils.IsNullOrEmpty(phoneNumber)
                || FunctionUtils.IsNullOrEmpty(otpCode)) {
            return JsonResultEnt.getJsonInvalidRequest();
        } else {
            return UserService.getInstance().checkOTP(phoneNumber, otpCode);
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
