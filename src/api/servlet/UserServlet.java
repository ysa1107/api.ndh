/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.servlet;

import api.baseController.BaseController;
import api.baseController.HttpBasePage;
import api.entities.JsonResultEnt;
import api.utils.FunctionUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.kyt.framework.config.LogUtil;
import com.kyt.framework.util.ConvertUtils;
import com.kyt.framework.util.JSONUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author Y Sa
 */
public class UserServlet extends HttpBasePage<UserServlet> {

    private static final Logger logger = LogUtil.getLogger(UserServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        JsonResultEnt outputJson = new JsonResultEnt();
        try {
            Map<String, String> mapParams = FunctionUtils.collectParams(request);
            if(FunctionUtils.checkAccessToken(request, response, mapParams)){
                mapParams.put("module", "users");
                int version = ConvertUtils.toInt(mapParams.get("version"), 1);
                version = getVersion("users", version);
                
                String _packageController = getPackageHandler(UserServlet.class, version);
                BaseController bs = new BaseController();
                bs.popularParams(mapParams);
                switch (version) {
                    default:
                        JsonResultEnt result = bs.processRequest(_packageController);
                        if (result != null) {
                            outputJson = result;
                        }
                        break;
                }
            } else {
                outputJson = JsonResultEnt.getJsonInvalidToken();
            }
        } catch (Exception ex) {
            logger.error(LogUtil.stackTrace(ex));
            outputJson = JsonResultEnt.getJsonSystemError();
        } finally {
            response.addHeader("Access-Control-Allow-Origin", "*");
            out.println(JSONUtil.Serialize(outputJson));
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
