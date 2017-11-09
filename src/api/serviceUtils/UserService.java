/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.cache.ApiCA;
import api.configuration.ConfigInfo;
import api.entities.AppConfigEnt;
import api.entities.AppVersionViewEnt;
import api.entities.AreaViewEnt;
import api.entities.DeviceInfoEnt;
import api.entities.JsonResultEnt;
import api.entities.UserViewEnt;
import api.iface.UserIface;
import api.utils.ErrorCode;
import api.utils.FunctionUtils;
//import com.nct.api.tokenv2.model.TokenBC;
import com.nct.shop.thrift.brand.client.TBrandClient;
import com.nct.shop.thrift.brand.models.CommentLikeType;
import com.nct.shop.thrift.brand.models.TAreaFilter;
import com.nct.shop.thrift.brand.models.TAreaListResult;
import com.nct.shop.thrift.brand.models.TAreaResult;
import com.nct.shop.thrift.deal.client.TAppVersionClient;
import com.nct.shop.thrift.deal.client.TUserClient;
import com.nct.shop.thrift.deal.client.TUserDeviceClient;
import com.nct.shop.thrift.deal.models.LoginStatus;
import com.nct.shop.thrift.deal.models.TAppVersionFilter;
import com.nct.shop.thrift.deal.models.TAppVersionListResult;
import com.nct.shop.thrift.deal.models.TNotificationUser;
import com.nct.shop.thrift.deal.models.TNotificationUserResult;
import com.nct.shop.thrift.deal.models.TUser;
import com.nct.shop.thrift.deal.models.TUserDevice;
import com.nct.shop.thrift.deal.models.TUserDeviceResult;
import com.nct.shop.thrift.deal.models.TUserFilter;
import com.nct.shop.thrift.deal.models.TUserListResult;
import com.nct.shop.thrift.deal.models.TUserResult;
import com.nct.shop.thrift.deal.models.UserGender;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.util.JSONUtil;
import com.shopiness.notification.service.GearmanClient;
import org.apache.log4j.Logger;
import shopiness.api.tokenv2.model.TokenBC;

/**
 *
 * @author huanlh
 */
public class UserService implements UserIface {

    private static final Logger logger = LogUtil.getLogger(UserService.class);
    public static UserService instance = null;

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public UserService() {

    }

    @Override
    public JsonResultEnt generateOTP(String phoneNumber, boolean isRegister, int minRange, int maxRange, int expiredSec, int limitSec, int limitTime) {
        JsonResultEnt result = new JsonResultEnt(ErrorCode.LIMIT_GEN_OTP_TIME.getValue());
        try {
            TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            TUserResult urs = client.getUserByUsername(phoneNumber);

            if (isRegister) {//Truong hop dang ky
                if (urs != null && urs.getValue() != null) {
                    return new JsonResultEnt(ErrorCode.USERNAME_EXISTS.getValue());
                } else {
//                boolean rs = client.generateOTP(phoneNumber, minRange, maxRange, expiredSec, limitSec, limitTime);
//                if (rs) {
                    result = JsonResultEnt.getJsonSuccess();
//                }
                }
            } else //Truong hop quen mat khau
            {
                if (urs != null && urs.getValue() != null) {
//                boolean rs = client.generateOTP(phoneNumber, minRange, maxRange, expiredSec, limitSec, limitTime);
//                if (rs) {
                    result = JsonResultEnt.getJsonSuccess();
//                }
                } else {
                    return new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt checkOTP(String phoneNumber, String otpCode) {
        JsonResultEnt result = new JsonResultEnt(ErrorCode.INVALID_OTP_CODE.getValue());
        try {
            TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            boolean rs = client.checkOTP(phoneNumber, otpCode);
            if (rs) {
                result = JsonResultEnt.getJsonSuccess();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt login(String username, String password) {
        JsonResultEnt result = new JsonResultEnt(ErrorCode.LOGIN_UNSUCCESS.getValue());
        try {
            TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            TUserResult rs = client.getUserByUsername(username);
            if (rs != null && rs.getValue() != null) {
                UserViewEnt user = new UserViewEnt(rs.getValue());
                if (password.equals(rs.getValue().getPassword())) {
                    result = new JsonResultEnt();

                    result.setCode(ErrorCode.SUCCESS.getValue());
                    result.setData(user);
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt resetPassword(String username, String password) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

            TUserResult rs = client.getUserByUsername(username);
            if (rs != null && rs.getValue() != null) {
                TUser user = rs.getValue();
                user.setPassword(password);
                if (client.updateUser(user)) {
                    UserViewEnt userEnt = new UserViewEnt(user);
                    result = new JsonResultEnt();

                    result.setCode(ErrorCode.SUCCESS.getValue());
                    result.setData(userEnt);
                }
            } else {
                result = new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public synchronized JsonResultEnt register(String username, UserViewEnt info, boolean isUpdate) {
        logger.info("*****REGISTER PARAMS------username: " + username + "--- UserViewEnt: " + info + "--- isUpdate: " + isUpdate + "*****");
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

            TUserResult rs = client.getUserByUsername(username);
            if (isUpdate) {
                if (rs == null || rs.getValue() == null) {
                    return new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
                }

                TUser user = rs.getValue();
                user.setFullName(info.fullName);
                user.setEmail(info.email);
                user.setGender(UserGender.findByValue(info.gender));
                user.setBirthday(info.birthday);
                user.setAreaId(info.areaId);

                if (!FunctionUtils.IsNullOrEmpty(info.avatar)) {
                    info.avatar = info.avatar.replace(";and;", "&");
                    String avatarUrl = FunctionUtils.downloadAvatar(info.avatar, "user");
                    logger.info("*****RESULT DOWNLOAD AVATAR------avatarUrl: " + avatarUrl + "*****");
                    user.setAvatar(avatarUrl);
                }

                if (client.updateUser(user)) {
                    UserViewEnt userEnt = new UserViewEnt(user);
                    result = new JsonResultEnt();

                    result.setCode(ErrorCode.SUCCESS.getValue());
                    result.setData(userEnt);
                    logger.info("*****RESULT DATA USER ENTITY: " + userEnt + "*******");
                }

            } else {
                if (rs != null && rs.getValue() != null) {
                    return new JsonResultEnt(ErrorCode.USERNAME_EXISTS.getValue());
                }

                TUser user = new TUser();
                user.setUsername(username);
                user.setPassword(info.password);
                user.setFullName(info.fullName);
                user.setEmail(info.email);
                user.setGender(UserGender.findByValue(info.gender));
                user.setBirthday(info.birthday);
                user.setAreaId(info.areaId);

                if (!FunctionUtils.IsNullOrEmpty(info.avatar)) {
                    info.avatar = info.avatar.replace(";and;", "&");
                    String avatarUrl = FunctionUtils.downloadAvatar(info.avatar, "user");
                    user.setAvatar(avatarUrl);
                }

                TUserResult userRS = client.insertUser(user);
                if (userRS != null && userRS.getValue() != null) {

                    TNotificationUserResult urs = NotificationService.getInstance().getNotificationUserByUsername(username);
                    if (urs.getValue() == null) {
                        //Tao rescord trong bang notification_user de sau nay luu notify cua user
                        TNotificationUser notiUser = new TNotificationUser();
                        notiUser.setUsername(username);
                        NotificationService.getInstance().insertNotificationUser(notiUser);
                    }

                    //Tra ve thong tin user
                    UserViewEnt userEnt = new UserViewEnt(userRS.getValue());

                    result = new JsonResultEnt();

                    result.setCode(ErrorCode.SUCCESS.getValue());
                    result.setData(userEnt);
                    logger.info("*****RESULT DATA USER ENTITY: " + userEnt + "*******");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public JsonResultEnt getUserInfo(String username) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

            TUserResult rs = client.getUserByUsername(username);
            if (rs != null && rs.getValue() != null) {
                result = new JsonResultEnt();
                result.setData(new UserViewEnt(rs.getValue()));
                result.setCode(ErrorCode.SUCCESS.getValue());

            } else {
                result = new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt getUserInfoWithArea(String username) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            TBrandClient brClient = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);

            TUserResult rs = client.getUserByUsername(username);
            if (rs != null && rs.getValue() != null) {
                TAreaResult area = brClient.getArea(rs.getValue().getAreaId());
                String areaName = (area != null && area.getValue() != null) ? area.getValue().getAreaName() : "";

                result = new JsonResultEnt();
                result.setData(new UserViewEnt(rs.getValue(), areaName));
                result.setCode(ErrorCode.SUCCESS.getValue());

            } else {
                result = new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt changePassword(String username, String oldPass, String newPass) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

            TUserResult rs = client.getUserByUsername(username);
            if (rs != null && rs.getValue() != null) {
                if (!rs.getValue().getPassword().equals(oldPass)) {
                    return new JsonResultEnt(ErrorCode.WRONG_PASSWORD.getValue());
                } else {
                    TUser user = rs.getValue();
                    user.setPassword(newPass);
                    if (client.updateUser(user)) {
                        result = JsonResultEnt.getJsonSuccess();
                    }
                }
            } else {
                result = new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt updateAvatar(String username, String fileName) {
        logger.info("*****UPDATE AVATAR------username: " + username + "--- fileName: " + fileName);
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

            TUserResult rs = client.getUserByUsername(username);
            if (rs != null && rs.getValue() != null) {
                TUser user = rs.getValue();
                user.setAvatar(fileName);
                if (client.updateUser(user)) {
                    result = new JsonResultEnt();
                    result.setData(new UserViewEnt(rs.getValue()));
                    result.setCode(ErrorCode.SUCCESS.getValue());
                }
            } else {
                result = new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt logout(String username, String accessToken) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
        logger.info("logout: " + username + " - " + accessToken);
        try {
            TUserResult rs = client.getUserByUsername(username);
            if (rs != null && rs.getValue() != null) {
                String dvInfo = TokenBC.getDeviceInfo(accessToken);
                DeviceInfoEnt device = JSONUtil.DeSerialize(dvInfo, DeviceInfoEnt.class);
                logger.info("logout: device:  " + dvInfo + " - " + accessToken);
                if (device != null) {
                    device.setUsername("");
                    boolean rsUpdate = TokenBC.updateDeviceInfo(accessToken, JSONUtil.Serialize(device));
                    if (rsUpdate) {
                        ////push update logout
                        GearmanClient pushv2Client = GearmanClient.getInstance(ConfigInfo.GEARMAN_PUSH_V2_SERVER);
                        logger.info("pushLogout " + pushv2Client.logoutByDeviceId(rs.value.getUserId(), device.getDeviceId()));
                        return JsonResultEnt.getJsonSuccess();
                    }
                }

                //Cap nhat lai trang thai login cua user device
                TUserDeviceClient dClient = TUserDeviceClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
                TUserDeviceResult drs = dClient.getUserDevice(device.deviceId);
                if (drs != null && drs.getValue() != null) {
                    TUserDevice uDevice = drs.getValue();
                    uDevice.loginStatus = LoginStatus.LOGGED_OUT;
                    dClient.updateUserDevice(uDevice);
                }

            } else {
                result = new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    @Override
    public JsonResultEnt updateSettings(String username, Boolean favoriteDeal, Boolean featureDeal,
            Boolean community) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

            TUserResult rs = client.getUserByUsername(username);
            if (rs != null && rs.getValue() != null) {
                TUser user = rs.getValue();

                if (favoriteDeal != null) {
                    user.setNotifyFavoriteDeal(favoriteDeal);
                }
                if (featureDeal != null) {
                    user.setNotifyFeatureDeal(featureDeal);
                }
                if (community != null) {
                    user.setNotifyCommunity(community);
                }

                if (client.updateUser(user)) {
                    UserViewEnt userEnt = new UserViewEnt(user);
                    result = new JsonResultEnt();

                    result.setCode(ErrorCode.SUCCESS.getValue());
                    result.setData(userEnt);

                    //push update settings
                    GearmanClient pushv2Client = GearmanClient.getInstance(ConfigInfo.GEARMAN_PUSH_V2_SERVER);
                    logger.info("updateSettings pushV2: " + pushv2Client.setPushSetting(username, favoriteDeal, featureDeal, community));
                }
            } else {
                result = new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt updateLocation(String username, String areaStr, Double lat, Double lng) {
        JsonResultEnt result = null;
        try {
            TUserClient uClient = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            TBrandClient bClient = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TAreaListResult ars = null;
            String areaName = "";
            if (!FunctionUtils.IsNullOrEmpty(areaStr)) {
                TAreaFilter filter = new TAreaFilter();
                filter.setAreaName(areaStr);

                ars = bClient.getAreas(filter);
                if (ars == null || ars.getListData() == null || ars.getListData().size() <= 0) {
                    return new JsonResultEnt(ErrorCode.AREA_NOT_EXISTS.getValue());
                }
                areaName = ars.getListData().get(0).getAreaName();

                result = new JsonResultEnt();
                result.setCode(ErrorCode.SUCCESS.getValue());

                TUserResult rs = uClient.getUserByUsername(username);
                if (rs != null && rs.getValue() != null) {
                    TUser user = rs.getValue();

                    user.areaId = ars.getListData().get(0).areaId;
                    if (lat != null) {
                        user.setLat(lat);
                    }
                    if (lng != null) {
                        user.setLng(lng);
                    }

                    if (uClient.updateUser(user)) {
                        UserViewEnt userEnt = new UserViewEnt(user);
                        userEnt.areaName = areaName;
                        result.setData(userEnt);
                    }
                } else {
                    AreaViewEnt areaEnt = new AreaViewEnt(ars.getListData().get(0), false);
                    result.setData(areaEnt);
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt updateLocationV2(String username, String areaStr, Double lat, Double lng) {
        JsonResultEnt result = null;
        try {
            TUserClient uClient = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            TBrandClient bClient = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TAreaListResult ars = null;
            String areaName = "";
            if (!FunctionUtils.IsNullOrEmpty(areaStr)) {
                TAreaFilter filter = new TAreaFilter();
                filter.setAreaName(areaStr);

                ars = bClient.getAreas(filter);
                if (ars == null || ars.getListData() == null || ars.getListData().size() <= 0) {
                    return new JsonResultEnt(ErrorCode.AREA_NOT_EXISTS.getValue());
                }
                areaName = ars.getListData().get(0).getAreaName();

                result = new JsonResultEnt();
                result.setCode(ErrorCode.SUCCESS.getValue());

                TUserResult rs = uClient.getUserByUsername(username);
                if (rs != null && rs.getValue() != null) {
                    TUser user = rs.getValue();

                    user.areaId = ars.getListData().get(0).areaId;
                    if (lat != null) {
                        user.setLat(lat);
                    }
                    if (lng != null) {
                        user.setLng(lng);
                    }

                    if (uClient.updateUser(user)) {
                        UserViewEnt userEnt = new UserViewEnt(user);
                        userEnt.areaName = areaName;
                        result.setData(userEnt);
                    }
                } else {
//            AreaViewEnt areaEnt = new AreaViewEnt(ars.getListData().get(0), false);
                    UserViewEnt userEnt = new UserViewEnt();
                    userEnt.areaId = ars.getListData().get(0).areaId;
                    userEnt.areaName = ars.getListData().get(0).areaName;
                    result.setData(userEnt);
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt getAppConfig(String osName) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            TAppVersionClient client = TAppVersionClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

            TAppVersionFilter filter = new TAppVersionFilter();
            filter.setPageIndex(1);
            filter.setPageSize(1);
            filter.setOsName(osName);
            filter.setOrderBy("date_released");
            filter.setOrderType("DESC");

            TAppVersionListResult rs = client.getAppVersions(filter);
            if (rs != null && rs.getListData() != null && rs.getListData().size() > 0) {
                AppConfigEnt appConfig = new AppConfigEnt();
                appConfig.enabledPushNearby = ConfigInfo.ENABLED_PUSH_NEARBY;
                appConfig.pushNearbyDistance = ConfigInfo.PUSH_NEARBY_DISTANCE;
                appConfig.limitLocationPushNearby = ConfigInfo.LIMIT_LOCATION_PUSH_NEARBY;
                appConfig.hotline = ConfigInfo.SHOPINESS_HOT_LINE;
                appConfig.facebook = ConfigInfo.SHOPINESS_FACEBOOK;
                appConfig.timeDelayPushNearby = ConfigInfo.LIMIT_TIME_PUSH_NEARBY;
                AppVersionViewEnt version = new AppVersionViewEnt(rs.getListData().get(0));
                appConfig.version = version;
                result = new JsonResultEnt();
                result.setData(appConfig);
                result.setCode(ErrorCode.SUCCESS.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public long getUserIdByUsername(String username) {
        long ret = 0;
        try {
            //get from cache
            ret = ApiCA.getCacheUserName(username);

            // miss cache, get from BE
            if (ret <= 0) {
                TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
                TUserResult result = client.getUserByUsername(username);
                if (result != null && result.getValue() != null) {
                    ret = result.getValue().getUserId();
                    // put back to cache
                    ApiCA.setCacheUserName(username, ret);
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return ret;
    }

    @Override
    public TUserListResult getUsers(TUserFilter filter) {
        TUserListResult rs = null;
        try {
            TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            rs = client.getUsers(filter);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return rs;
    }

    public TUser getUser(long userId) {
        try {
            TUserClient client = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            TUserResult rs = client.getUser(userId);
            if (rs != null) {
                return rs.getValue();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return null;
    }

}
