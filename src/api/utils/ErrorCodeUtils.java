/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.utils;


import com.kyt.framework.util.ConvertUtils;

/**
 *
 * @author danhnc
 */
public class ErrorCodeUtils {

    public enum ErrorCode {

        success(0),
        valid(100),
        invalid_token(200),
        invalid_request(201),
        system_error(202),
        invalid_client(204),
        unauthorized_client(205),
        unsupported_response_type(206),
        unsupported_grant_type(207),
        access_denied(208),
        insufficient_scope(209),
        server_error(211),
        user_not_login(212),
        expired_token(213),
        invalid_client_ip(214),
        password_invalid(215),
        password_not_match(216),
        invalid_timestamp(217),
        data_not_exist(218),
        unsupported_method_type(219),
        over_limit_otp(220),
        device_invalid(221),
        invalid_jwt_secret_key(222),
        
        ;

        private final int value;

        ErrorCode(int val) {
            this.value = val;
        }

        public int getValue() {
            return this.value;
        }

        public static String getMsgCode(ErrorCode code) {
            return ConvertUtils.toString(code.getValue());

        }
    }
}
