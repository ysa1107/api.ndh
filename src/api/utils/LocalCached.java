/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.utils;

import com.shopiness.framework.common.LogUtil;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author danhnc
 */
public class LocalCached {

    private static final org.apache.log4j.Logger _log = LogUtil.getLogger(LocalCached.class);
    static private Map<String, Object> cache = null;

    public static void getInstance() {
        if (LocalCached.cache == null) {
            LocalCached.cache = new HashMap<>();
        }
    }

    public static void put(String key, Object value) {
        try {
            if (key != null && value != null && !key.isEmpty()) {
                LocalCached.getInstance();
                cache.put(key,  value);
            }
        } catch (Exception ex) {
            _log.error(LogUtil.stackTrace(ex));
        }
    }

    public static Object get(String key) {
        try {
            if (key != null && !key.isEmpty()) {            
                LocalCached.getInstance();
                Object valCached = cache.get(key);
                if (valCached != null) {
                    return valCached;
                } else {
                    return null;
                }
            }
            return null;
        } catch (Exception ex) {
            _log.error(LogUtil.stackTrace(ex));
            return "";
        }
    }
}
