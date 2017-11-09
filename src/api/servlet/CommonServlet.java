/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.servlet;

import api.baseController.BaseController;
import api.baseController.HttpBasePage;
import api.configuration.ConfigInfo;
import api.entities.JsonResultEnt;
import api.utils.FunctionUtils;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.metrics.LatencyMetrics;
import com.shopiness.framework.util.ConvertUtils;
import com.shopiness.framework.util.DateTimeUtils;
import com.shopiness.framework.util.JSONUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author PhongDQ
 */
public class CommonServlet extends HttpBasePage<CommonServlet> {

    private static final Logger logger = LogUtil.getLogger(CommonServlet.class);    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long startTime = DateTimeUtils.getNanoSecondNow();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonResultEnt outputJson = new JsonResultEnt();
        try {
            Map<String, String> mapParams = FunctionUtils.collectParams(request);
            mapParams.put("module", "commons");
            int version = ConvertUtils.toInt(mapParams.get("version"), 1);    
            
            version = getVersion("commons", version);
            
            String _packageControler = getPackageHandler(CommonServlet.class, version) ;
            BaseController bs = new BaseController();
            bs.popularParams(mapParams);
            switch (version) {
                default:
                    JsonResultEnt result = bs.processRequest(_packageControler);
                    if (result != null) {
                        outputJson = result;                        
                    }
                    break;
            }
        } catch (Exception ex) {
            LatencyMetrics.getInstance(ConfigInfo.METRIC_SERVER).incError();
            logger.error(LogUtil.stackTrace(ex));            
            outputJson = JsonResultEnt.getJsonSystemError();
        } 
        finally {
            LatencyMetrics.getInstance(ConfigInfo.METRIC_SERVER).addNano(System.nanoTime() - startTime);
            response.addHeader("Access-Control-Allow-Origin", "*");
//            response.addHeader("FailToken", "404");
            out.println(JSONUtil.Serialize(outputJson));
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }   
    
}
