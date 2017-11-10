package api.utils;

import com.kyt.framework.util.ConvertUtils;

/*
* @author Y Sa
 */
public enum ErrorCode {

    SUCCESS(0),
    VALID(100),
    INVALID_TOKEN(200),
    INVALID_REQUEST(201),
    SYSTEM_ERROR(202),
    ACCESS_DENIED(203),
    USER_NOT_LOGIN(204),
    EXPIRED_TOKEN(205),
    PASSWORD_INVALID(206),
    INVALID_TIMESTAMP(207),
    DATA_NOT_EXISTS(208),
    UNSUPPORT_METHOD_TYPE(209),
    INVALID_DEVICE(212),
    DEVICE_EMPTY(213),
    TOKEN_NOT_EXISTS(214),
    INVALID_SECRET_KEY(215),
    TOKEN_NOT_WRITE(216),
    UNKNOW_ERROR(217),
    USERNAME_HASH_INVALID(218),
    LOGIN_UNSUCCESS(219),
    USERNAME_NOT_EXISTS(220),
    USERNAME_EXISTS(221),
    INVALID_USER_INFO(222),
    WRONG_PASSWORD(223),
    GENERATE_DENIED(224),
    AVATAR_INAVALID(228),
    TRANSACTION_ERROR(234),;

    private final int value;

    private ErrorCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static String getMsgCode(ErrorCode code) {
        return ConvertUtils.toString(code.getValue());
    }
}
