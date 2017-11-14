/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.baseController;

import api.entities.JsonResultEnt;
import api.utils.FunctionUtils;
import com.kyt.framework.config.LogUtil;
import com.kyt.framework.util.ConvertUtils;
import com.kyt.framework.util.JSONUtil;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Y Sa
 */
public class BaseController {

    private static final Logger _logger = LogUtil.getLogger(BaseController.class);
    private String action;
    private String mainKey;
    private String subKey;
    private Map<String, String> params;
    private boolean isFullData;
    private String method;
    private String clientIP;
    
    //huanlh

    protected String getAction() {
        return this.action;
    }

    protected String getMainKey() {
        return this.mainKey;
    }

    protected String getSubKey() {

        return this.subKey;
    }

    protected Map<String, String> getParams() {
        return this.params;
    }
    
//    protected String getParams() {
//        return this.params;
//    }

    protected boolean isTiniData() {
        return this.isFullData;
    }

    protected String getMethod() {
        return this.method;
    }

    protected String getClientIP() {
        return this.clientIP;
    }

    public JsonResultEnt processRequest(String _packageControler) {

        try {
            //Redirect Controller 
            BaseController redirectController = (BaseController) (Class.forName(_packageControler).newInstance());
            redirectController.popularParams(params);
            
            JsonResultEnt jsonResultEnt = redirectController.processRequest(_packageControler);
            return jsonResultEnt;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            _logger.error(LogUtil.stackTrace(ex));
            _logger.error("Params: " + params);
            _logger.error("JSONUtil - Params: " + JSONUtil.Serialize(params));
        }

        return JsonResultEnt.getJsonSystemError();
    }

    public void popularParams(Map<String, String> params) {
        try {
            if (params == null || params.isEmpty()) {
                return;
            }
//            logger.error("popularParams: " + JSONUtil.Serialize(params));
            this.params = params;
            this.action = FunctionUtils.getValueOfMap(params, "action");
            this.mainKey = ConvertUtils.toString(FunctionUtils.getValueOfMap(params, "mainId"));
            this.subKey = ConvertUtils.toString(FunctionUtils.getValueOfMap(params, "subId"));
            if (StringUtils.isEmpty(this.mainKey)) {
                this.mainKey = this.action;
            }

            this.method = ConvertUtils.toString(FunctionUtils.getValueOfMap(params, "method"));
            this.clientIP = ConvertUtils.toString(FunctionUtils.getValueOfMap(params, "clientip"));

            this.isFullData = ConvertUtils.toString(FunctionUtils.getValueOfMap(params, "etype"), "").equalsIgnoreCase("tini");

        } catch (Exception e) {
            _logger.error(LogUtil.stackTrace(e));
        }

    }
    
    
}
