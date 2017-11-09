package api.controller.common;

import api.baseController.BaseController;
import api.configuration.ConfigInfo;
import api.entities.DeviceInfoEnt;
import api.entities.JsonResultEnt;
import api.entities.TokenEnt;
import api.serviceUtils.BrandUserService;
import api.serviceUtils.DealUserService;
import api.serviceUtils.UserDeviceService;
import api.serviceUtils.UserService;
import api.utils.ErrorCode;
import api.utils.FunctionUtils;
//import com.nct.api.tokenv2.model.TokenBC;
//import com.nct.api.tokenv2.model.TokenResult;
import com.shopiness.framework.common.Config;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.gearman.GClientManager;
import com.shopiness.framework.gearman.JobEnt;
import com.shopiness.framework.util.ConvertUtils;
import com.shopiness.framework.util.DateTimeUtils;
import com.shopiness.framework.util.JSONUtil;
import noti.entity.UserDevicePushEnt;
import org.apache.log4j.Logger;
import shopiness.api.tokenv2.model.TokenBC;
import shopiness.api.tokenv2.model.TokenResult;

/**
 *
 * Y Sa
 */
public class CommonControllerv1 extends BaseController {

    private static final Logger logger = LogUtil.getLogger(CommonControllerv1.class);

    @Override
    public JsonResultEnt processRequest(String packageControler) {
        JsonResultEnt result;
        logger.info("PARAMS:" + getParams());
        switch (getAction()) {
            case "token":
                if (!getMethod().equals("POST")) {
                    return JsonResultEnt.getJsonUnsupportMethod();
                }
                String deviceInfo = ConvertUtils.toString(getParams().get("deviceInfo"), "");
                long timestamp = ConvertUtils.toLong(getParams().get("timestamp"), 0);
                String md5 = ConvertUtils.toString(getParams().get("md5"));
                String refeshToken = ConvertUtils.toString(getParams().get("refeshToken"));
                result = getToken(deviceInfo, timestamp, md5, refeshToken);
                break;
            case "notify":
                if (!getMethod().equals("POST")) {
                    return JsonResultEnt.getJsonUnsupportMethod();
                }
                result = saveNotifyKey();
                break;
            case "app-config":
                if (!getMethod().equals("POST")) {
                    return JsonResultEnt.getJsonUnsupportMethod();
                }
                result = getAppConfig();
                break;
            default:
                result = JsonResultEnt.getJsonUnsupportMethod();
                break;
        }
        logger.info("RESULT: " + JSONUtil.Serialize(result));
        return result;
    }


    public JsonResultEnt getAppConfig() {
        String osName = getParams().get("osName");
        if (FunctionUtils.IsNullOrEmpty(osName)) {
            return JsonResultEnt.getJsonInvalidRequest();
        }
        return UserService.getInstance().getAppConfig(osName);
    }

    public JsonResultEnt saveNotifyKey() {
        try {
            String keyNotify = getParams().get("keyNotify");
            if (keyNotify == null) {
                return JsonResultEnt.getJsonInvalidRequest();
            }

            DeviceInfoEnt deviceInfo = getDeviceInfo();
            if (deviceInfo == null) {
                String strDeviceInfo = TokenBC.getDeviceInfo(getAccessToken());
                deviceInfo = JSONUtil.DeSerialize(strDeviceInfo, DeviceInfoEnt.class);
                if (deviceInfo == null) {
                    return JsonResultEnt.getJsonDeviceEmpty();
                }
            }
            logger.info("****SaveNotifyKey-keyPushNotify: " + keyNotify + "****");
            logger.info("****SaveNotifyKey-deviceInfo: " + deviceInfo + "****");
            if (keyNotify.length() > 0) {
                String info = getParams().get("deviceInfo");
                deviceInfo = JSONUtil.DeSerialize(info, DeviceInfoEnt.class);
                long uid = UserService.getInstance().getUserIdByUsername(deviceInfo.getUsername());
                logger.info("****SaveNotifyKey-params " + getParams() + " + uid: " + uid + "****");
                return JsonResultEnt.getJsonSuccess(sendKeyToWorker(info, keyNotify,uid));
            } else {
                logger.error("****SaveNotifyKey-keyNotify is null");
            }
            
            return JsonResultEnt.getJsonSuccess(false);
        } catch (Exception ex) {
            logger.error("****SaveNotifyKey---" + ex.getMessage() + "****");
            return JsonResultEnt.getJsonUnknownError();
        }
    }

//    public static boolean sendKeyToWorker(String deviceInfo, String keyNotify, long uid) {
//        boolean rs = false;
//        try {
//            if(uid == 0){
//                logger.error("user login >> don't push job");
//                return true;
//            }
//            UserDevicePushEnt pushed = new UserDevicePushEnt();
//            pushed.deviceInfo = deviceInfo;
//            pushed.keyNotify = keyNotify;            
//            //tao job
//            JobEnt job = new JobEnt();
//            job.ActionType = noti.utils.Constant.UPDATE_KEY_PUSH;
//            job.Timestamp = System.currentTimeMillis();
//            job.Data = JSONUtil.Serialize(pushed);
//            job.UserId = uid;
//            rs = GClientManager.getInstance(ConfigInfo.UPDATE_USER_DEVICE_WORKER).push(job);
//        } catch (Exception ex) {
//            logger.error(LogUtil.stackTrace(ex));
//        }
//        return rs;
//    }

    private String getAccessToken() {
        return getParams().get("accessToken");
    }

    private DeviceInfoEnt getDeviceInfo() {
        String deviceInfo = getParams().get("deviceInfo");
        DeviceInfoEnt ent = null;
        try {
            ent = JSONUtil.DeSerialize(deviceInfo, DeviceInfoEnt.class);
        } catch (Exception e) {
            logger.error("GET DEVICE INFO - DeSerialize fail- device: " + deviceInfo + "-" + e.getMessage());
            return ent;
        }
        return ent;
    }     
}
