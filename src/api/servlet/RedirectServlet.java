/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.servlet;

import api.configuration.ConfigInfo;
import api.utils.LocalCached;
import com.kyt.framework.config.LogUtil;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nct.rewrite.entity.RewriteEntity;
import nct.rewrite.handle.NctRewrite;

/**
 *
 * @author liempt
 */
public class RedirectServlet extends HttpServlet {

    private static final org.apache.log4j.Logger _log = LogUtil.getLogger(RedirectServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {

            if (request.getRequestURI().contains("favicon.ico")) {
                return;
            }

            NctRewrite rewrite = new NctRewrite();
            try {
                rewrite.forwardRequestAPIMobile(request, response, getServletContext(), (List<RewriteEntity>) LocalCached.get(ConfigInfo.REWRITE_ENTITY_MEM_KEY));
            } catch (Exception ex) {
                _log.error(LogUtil.stackTrace(ex));
            }

        } catch (Exception ex) {
            _log.error(LogUtil.stackTrace(ex));
        }
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
