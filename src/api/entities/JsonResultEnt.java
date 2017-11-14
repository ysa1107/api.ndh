package api.entities;

import api.utils.ErrorCode;
import com.kyt.framework.util.ConvertUtils;
import com.kyt.framework.util.JSONUtil;

/**
 * Created by ysa on 11/11/17.
 */
public class JsonResultEnt {

    public int code;
    public String msgCode;
    public Object data;
    public Object extend;
    public Boolean loadmore;
    public Long total;

    public JsonResultEnt() {

    }

    public JsonResultEnt(int code) {
        this.code = code;
        this.msgCode = ConvertUtils.toString(code);
    }

    public JsonResultEnt setCode(int code) {
        this.code = code;
        return this;
    }

    public JsonResultEnt setMsgCode(String msgCode) {
        this.msgCode = msgCode;
        return this;
    }

    public JsonResultEnt setData(Object data) {
        this.data = data;
        return this;
    }

    public JsonResultEnt setLoadMore(boolean b) {
        this.loadmore = b;
        return this;
    }

    public JsonResultEnt setTotal(long total) {
        this.total = total;
        return this;
    }

    public JsonResultEnt setExtend(Object extend) {
        if (extend != null) {
            this.extend = extend;
        }
        return this;
    }

    public JsonResultEnt(int code, String msgCode) {
        this.code = code;
        this.msgCode = msgCode;
    }

    public JsonResultEnt(int code, String msgCode, Object data) {
        this.code = code;
        this.msgCode = msgCode;
        this.data = data;
    }

    static public JsonResultEnt getJsonSuccess() {
        return new JsonResultEnt(ErrorCode.SUCCESS.getValue(), ErrorCode.getMsgCode(ErrorCode.SUCCESS));
    }

    static public JsonResultEnt getJsonSuccess(Object data) {
        return new JsonResultEnt(ErrorCode.SUCCESS.getValue(), ErrorCode.getMsgCode(ErrorCode.SUCCESS), data);
    }

    static public JsonResultEnt getJsonSuccess(int mgsCode, Object data) {
        return new JsonResultEnt(ErrorCode.SUCCESS.getValue(), String.valueOf(mgsCode), data);
    }

    static public JsonResultEnt getJsonSuccess(Object data, String msgCode) {
        return new JsonResultEnt(ErrorCode.SUCCESS.getValue(), msgCode, data);
    }

    static public JsonResultEnt getJsonSuccess(String msgCode) {
        return new JsonResultEnt(ErrorCode.SUCCESS.getValue(), msgCode);
    }

    static public JsonResultEnt getJsonInvalidToken() {
        return new JsonResultEnt(ErrorCode.INVALID_TOKEN.getValue(), ErrorCode.getMsgCode(ErrorCode.INVALID_TOKEN));
    }

    static public JsonResultEnt getJsonInvalidRequest() {
        return new JsonResultEnt(ErrorCode.INVALID_REQUEST.getValue(), ErrorCode.getMsgCode(ErrorCode.INVALID_REQUEST));
    }

    static public JsonResultEnt getJsonSystemError() {
        return new JsonResultEnt(ErrorCode.SYSTEM_ERROR.getValue(), ErrorCode.getMsgCode(ErrorCode.SYSTEM_ERROR));
    }

    static public JsonResultEnt getJsonAccessDenied() {
        return new JsonResultEnt(ErrorCode.ACCESS_DENIED.getValue(), ErrorCode.getMsgCode(ErrorCode.ACCESS_DENIED));
    }

    static public JsonResultEnt getJsonUserNotLogin() {
        return new JsonResultEnt(ErrorCode.USER_NOT_LOGIN.getValue(), ErrorCode.getMsgCode(ErrorCode.USER_NOT_LOGIN));
    }

    static public JsonResultEnt getJsonExpiredToken() {
        return new JsonResultEnt(ErrorCode.EXPIRED_TOKEN.getValue(), ErrorCode.getMsgCode(ErrorCode.EXPIRED_TOKEN));
    }

    static public JsonResultEnt getJsonPasswordInvalid() {
        return new JsonResultEnt(ErrorCode.PASSWORD_INVALID.getValue(), ErrorCode.getMsgCode(ErrorCode.PASSWORD_INVALID));
    }

    static public JsonResultEnt getJsonInvalidTimeStamp() {
        return new JsonResultEnt(ErrorCode.INVALID_TIMESTAMP.getValue(), ErrorCode.getMsgCode(ErrorCode.INVALID_TIMESTAMP));
    }

    static public JsonResultEnt getJsonDataNotExist() {
        return new JsonResultEnt(ErrorCode.DATA_NOT_EXISTS.getValue(), ErrorCode.getMsgCode(ErrorCode.DATA_NOT_EXISTS));
    }

    static public JsonResultEnt getJsonUnsupportMethod() {
        return new JsonResultEnt(ErrorCode.UNSUPPORT_METHOD_TYPE.getValue(), ErrorCode.getMsgCode(ErrorCode.UNSUPPORT_METHOD_TYPE));
    }

    static public JsonResultEnt getJsonOverLimitTime() {
        return new JsonResultEnt(ErrorCode.LIMIT_GEN_OTP_TIME.getValue(), ErrorCode.getMsgCode(ErrorCode.LIMIT_GEN_OTP_TIME));
    }

    static public JsonResultEnt getJsonInvalidDevice() {
        return new JsonResultEnt(ErrorCode.INVALID_DEVICE.getValue(), ErrorCode.getMsgCode(ErrorCode.INVALID_DEVICE));
    }

    static public JsonResultEnt getJsonDeviceEmpty() {
        return new JsonResultEnt(ErrorCode.DEVICE_EMPTY.getValue(), ErrorCode.getMsgCode(ErrorCode.INVALID_DEVICE));
    }

    static public JsonResultEnt getJsonTokenNotExists() {
        return new JsonResultEnt(ErrorCode.TOKEN_NOT_EXISTS.getValue(), ErrorCode.getMsgCode(ErrorCode.TOKEN_NOT_EXISTS));
    }

    static public JsonResultEnt getJsonTokenInvalidSecretKey() {
        return new JsonResultEnt(ErrorCode.INVALID_SECRET_KEY.getValue(), ErrorCode.getMsgCode(ErrorCode.INVALID_SECRET_KEY));
    }

    static public JsonResultEnt getJsonTokenNotWrite() {
        return new JsonResultEnt(ErrorCode.TOKEN_NOT_WRITE.getValue(), ErrorCode.getMsgCode(ErrorCode.TOKEN_NOT_WRITE));
    }

    static public JsonResultEnt getJsonUnknownError() {
        return new JsonResultEnt(ErrorCode.UNKNOW_ERROR.getValue(), ErrorCode.getMsgCode(ErrorCode.UNKNOW_ERROR));
    }

    static public JsonResultEnt getJsonUsernameHashInvalid() {
        return new JsonResultEnt(ErrorCode.USERNAME_HASH_INVALID.getValue(), ErrorCode.getMsgCode(ErrorCode.USERNAME_HASH_INVALID));
    }

    static public JsonResultEnt getJsonDealNotExists() {
        return new JsonResultEnt(ErrorCode.DEAL_NOT_EXISTS.getValue(), ErrorCode.getMsgCode(ErrorCode.DEAL_NOT_EXISTS));
    }

    static public JsonResultEnt getJsonAvatarInvalid() {
        return new JsonResultEnt(ErrorCode.AVATAR_INAVALID.getValue(), ErrorCode.getMsgCode(ErrorCode.AVATAR_INAVALID));
    }

    static public JsonResultEnt getJsonProductNotExists() {
        return new JsonResultEnt(ErrorCode.PRODUCT_NOT_EXISTS.getValue(), ErrorCode.getMsgCode(ErrorCode.PRODUCT_NOT_EXISTS));
    }

    static public JsonResultEnt getJsonEventUserExits() {
        return new JsonResultEnt(ErrorCode.EVENT_USER_EXISTS.getValue(), ErrorCode.getMsgCode(ErrorCode.EVENT_USER_EXISTS));
    }
    public static JsonResultEnt parserJsonResultEnt(String strJsonResult) {
        try {
            //strJsonResult = FunctionUtils.URLDecodeUTF8(strJsonResult);
            if (strJsonResult.startsWith("{") == false) {
                return null;
            }

            return JSONUtil.DeSerialize(strJsonResult, JsonResultEnt.class);
        } catch (Exception ex) {

        }
        return null;
    }
}
