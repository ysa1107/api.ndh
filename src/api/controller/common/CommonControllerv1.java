package api.controller.common;

import api.baseController.BaseController;
import api.configuration.ConfigInfo;
import api.entities.DeviceInfoEnt;
import api.entities.JsonResultEnt;
import api.entities.TokenEnt;
import api.serviceUtils.JWTService;
import api.serviceUtils.UserService;
import api.utils.ErrorCodeUtils;
import api.utils.FunctionUtils;
import com.kyt.framework.config.LogUtil;
import com.kyt.framework.util.ConvertUtils;
import com.kyt.framework.util.DateTimeUtils;
import com.kyt.framework.util.JSONUtil;
import com.kyt.framework.util.StringUtils;
import org.apache.log4j.Logger;
import shopiness.api.jwt.token.model.JwtTokenResult;

import java.util.HashMap;
import java.util.Map;

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
                result = getJWTToken(deviceInfo, timestamp, md5);
                break;
            default:
                result = JsonResultEnt.getJsonUnsupportMethod();
                break;
        }
        logger.info("RESULT: " + JSONUtil.Serialize(result));
        return result;
    }


        public JsonResultEnt getJWTToken(String deviceInfo, long timestamp, String md5) {
        //logger.info("getJWTToken: deviceInfo: " + deviceInfo + " - t: " + timestamp + " - md5: " + md5 + " - token: " + jwtToken);

        DeviceInfoEnt deviceEnt = JSONUtil.DeSerialize(deviceInfo, DeviceInfoEnt.class);
        if (deviceEnt == null) {
            return JsonResultEnt.getJsonDataNotExist();
        }

        String _md5 = StringUtils.doMD5(ConfigInfo.SECRET_KEY + timestamp + deviceEnt.getDeviceId());

        if (!_md5.equals(md5)) {
            return new JsonResultEnt(ErrorCodeUtils.ErrorCode.invalid_jwt_secret_key.getValue(), ErrorCodeUtils.ErrorCode.getMsgCode(ErrorCodeUtils.ErrorCode.invalid_jwt_secret_key), "vn");
        }

        TokenEnt tokenViewEnt = new TokenEnt();
        JwtTokenResult token;

            Map mapData = new HashMap<>();
            mapData.put("deviceinfo", JSONUtil.Serialize(deviceEnt));
            token = JWTService.createToken(mapData);

            tokenViewEnt.accessToken = token.token;
            logger.info("JWT token = " + token.token);
            tokenViewEnt.refreshToken = token.token;
            tokenViewEnt.tokenExpiredAt = DateTimeUtils.getMilisecondsNow() + ConfigInfo.TIME_JWTTOKEN_EXPIRED;
        return JsonResultEnt.getJsonSystemError();
    }

    private String getAccessToken() {
        return getParams().get("accessToken");
    }
}
