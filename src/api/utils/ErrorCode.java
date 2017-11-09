package api.utils;

import com.shopiness.framework.util.ConvertUtils;


/*
* @author huanlh
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
    LIMIT_GEN_OTP_TIME(210),
    INVALID_OTP_CODE(211),
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
    DEAL_NOT_EXISTS(225),
    AREA_NOT_EXISTS(226),
    DEAL_NOT_HAVE_UNIQUE_CODE(227),
    AVATAR_INAVALID(228),
    OUT_OF_VOUCHER_CODE(229),
    DEAL_EXPIRED(230),
    PRODUCT_NOT_EXISTS(231),
    EVENT_USER_EXISTS(232),
    USER_NOT_WINNER_EVENT(233),
    TRANSACTION_ERROR(234),
    ;

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
