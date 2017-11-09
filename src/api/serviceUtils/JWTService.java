/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import com.shopiness.framework.common.LogUtil;
import java.util.Map;
import org.apache.log4j.Logger;
import shopiness.api.jwt.token.model.JwtTokenBC;
import shopiness.api.jwt.token.model.JwtTokenResult;

/**
 *
 * @author Y Sa
 */
public class JWTService {

    private static final Logger logger = LogUtil.getLogger(JWTService.class);

    static public JwtTokenResult createToken(Map<String, String> data) {
        if (data != null) {
            JwtTokenResult result = JwtTokenBC.createToken(shopiness.api.config.ConfigInfo.SECRECT_KEY, data, shopiness.api.config.ConfigInfo.TIME_JWTTOKEN_EXPIRED);
            return result;
        }
        return null;
    }

    static public JwtTokenResult checkToken(String token) {
        try {
            JwtTokenResult result = JwtTokenBC.isValidToken(shopiness.api.config.ConfigInfo.SECRECT_KEY, token);
            return result;
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
        return null;
    }

    static public JwtTokenResult renewToken(String token) {
        try {
            JwtTokenResult result = JwtTokenBC.renewToken(shopiness.api.config.ConfigInfo.SECRECT_KEY, token, shopiness.api.config.ConfigInfo.TIME_JWTTOKEN_EXPIRED);
            return result;
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
        return null;
    }

    static public JwtTokenResult decryptionToken(String token) {
        try {
            JwtTokenResult result = JwtTokenBC.decryptionToken(shopiness.api.config.ConfigInfo.SECRECT_KEY, token);
            return result;
        } catch (Exception e) {
            logger.error(e.getStackTrace());
        }
        return null;
    }
}
