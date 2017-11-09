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
 * @author huanlh
 */
public class CashbackServlet extends HttpBasePage<CashbackServlet> {

    private static final Logger logger = LogUtil.getLogger(CashbackServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long startTime = DateTimeUtils.getNanoSecondNow();
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonResultEnt outputJson = new JsonResultEnt();        
        boolean clickCashback = false;
        try {
            Map<String, String> mapParams = FunctionUtils.collectParams(request);            
            String action = mapParams.get("action");            
            clickCashback = (action != null && action.equalsIgnoreCase("click"));

            if (clickCashback || FunctionUtils.checkAccessToken(request, response, mapParams)) {
                mapParams.put("module", "cashbacks");
                int version = ConvertUtils.toInt(mapParams.get("version"), 1);
                version = getVersion("cashbacks", version);

                String _packageControler = getPackageHandler(CashbackServlet.class, version);
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
            } else {
                LatencyMetrics.getInstance(ConfigInfo.METRIC_SERVER).incError();
                outputJson = JsonResultEnt.getJsonInvalidToken();
            }
        } catch (Exception ex) {
            LatencyMetrics.getInstance(ConfigInfo.METRIC_SERVER).incError();
            logger.error(LogUtil.stackTrace(ex));
            outputJson = JsonResultEnt.getJsonSystemError();
        } finally {
            LatencyMetrics.getInstance(ConfigInfo.METRIC_SERVER).addNano(System.nanoTime() - startTime);
            if (clickCashback) {
                response.sendRedirect(outputJson.data.toString());
            } else {
                response.addHeader("Access-Control-Allow-Origin", "*");
                out.println(JSONUtil.Serialize(outputJson));
                out.close();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
//        PrintWriter out = resp.getWriter();
//        out.println(JSONUtil.Serialize(JsonResultEnt.getJsonUnsupportMethod()));
//        out.close();        
    }
}
