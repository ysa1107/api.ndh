//package api.controller.common;
//
//import api.entities.DeviceInfoEnt;
//import api.entities.JsonResultEnt;
//import api.entities.TokenEnt;
//import api.serviceUtils.JWTService;
//
//import api.utils.ErrorCodeUtils;
//import com.kyt.framework.config.LogUtil;
//import java.util.HashMap;
//import java.util.Map;
//import org.apache.log4j.Logger;
//
///**
// *
// * @author Y Sa
// */
//public class CommonControllerv2 extends CommonControllerv1 {
//
//    private static final Logger logger = LogUtil.getLogger(CommonControllerv2.class);
//
//    @Override
//    public JsonResultEnt processRequest(String _packageControler) {
//        logger.info("PARAMS:" + getParams());
//
//        if (!getParams().get("clientip").equals("14.169.191.142")) { //27.77.121.164
//            return super.processRequest(_packageControler);
//        }
//
//        if (!getAction().equals("token")) {
//            return super.processRequest(_packageControler);
//        }
//
//        if (!getMethod().equals("POST")) {
//            return JsonResultEnt.getJsonUnsupportMethod();
//        }
//
//        String deviceInfo = ConvertUtils.toString(getParams().get("deviceInfo"), "");
//        long timestamp = ConvertUtils.toLong(getParams().get("timestamp"), 0);
//        String md5 = ConvertUtils.toString(getParams().get("md5"));
//        String accessToken = ConvertUtils.toString(getParams().get("accessToken"));
//        return getJWTToken(deviceInfo, timestamp, md5, accessToken);
//    }
//
//    public JsonResultEnt getJWTToken(String deviceInfo, long timestamp, String md5, String accessToken) {
//        //logger.info("getJWTToken: deviceInfo: " + deviceInfo + " - t: " + timestamp + " - md5: " + md5 + " - token: " + jwtToken);
//
//        DeviceInfoEnt deviceEnt = JSONUtil.DeSerialize(deviceInfo, DeviceInfoEnt.class);
//        if (deviceEnt == null) {
//            return JsonResultEnt.getJsonDataNotExist();
//        }
//
//        String _md5 = StringUtils.doMD5(shopiness.api.config.ConfigInfo.SECRECT_KEY + timestamp + deviceEnt.getDeviceId());
//
//        if (!_md5.equals(md5)) {
//            return new JsonResultEnt(ErrorCodeUtils.ErrorCode.invalid_jwt_secret_key.getValue(), ErrorCodeUtils.ErrorCode.getMsgCode(ErrorCodeUtils.ErrorCode.invalid_jwt_secret_key), "vn");
//        }
//
//        if (StringUtils.isEmpty(accessToken)) {
//            deviceEnt.username = "";
//        }
//
//        TokenEnt tokenViewEnt = new TokenEnt();
//
//        JwtTokenResult token = JWTService.checkToken(accessToken);
//
//        if (token != null) {
//            Map<String, String> mapData;
//            switch (token.status) {
//                case SUCCESS:
//                    token = JWTService.renewToken(accessToken);
//                    break;
//                case EXPIRED_TOKEN:
//                    token = JWTService.decryptionToken(accessToken);
//                    token = JWTService.createToken(token.data);
//                    break;
//                case ERROR:
//                case INVALID_TOKEN:
//                    mapData = new HashMap<>();
//                    mapData.put("deviceinfo", JSONUtil.Serialize(deviceEnt));
//                    token = JWTService.createToken(mapData);
//
//                    tokenViewEnt.accessToken = token.token;
//                    logger.info("JWT token = " + token.token);
//                    tokenViewEnt.refeshToken = token.token;
//                    tokenViewEnt.tokenExpiredAt = DateTimeUtils.getMilisecondsNow() + shopiness.api.config.ConfigInfo.TIME_JWTTOKEN_EXPIRED;
////                    tokenViewEnt. = deviceEnt.deviceId;
//                    break;
//                default:
//                    break;
//            }
//
//            tokenViewEnt.accessToken = token.token;
//            tokenViewEnt.refeshToken = token.token;
//            tokenViewEnt.tokenExpiredAt = DateTimeUtils.getMilisecondsNow() + shopiness.api.config.ConfigInfo.TIME_JWTTOKEN_EXPIRED;
////            tokenViewEnt. = deviceEnt.deviceId;
//            return JsonResultEnt.getJsonSuccess(tokenViewEnt);
//        }
//        return JsonResultEnt.getJsonSystemError();
//    }
//}
