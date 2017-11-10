/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.servlet;

import api.entities.JsonResultEnt;
import com.kyt.framework.util.JSONUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Y Sa
 */
public class ErrorServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {       
        resp.setStatus(400);
        //resp.setHeader("link", "<http://stc.id.nixcdn.com/10/images/favicon.ico>; rel=\"shortcut icon\"");
        JsonResultEnt outputJson = JsonResultEnt.getJsonSystemError();
        try (PrintWriter out = resp.getWriter()) {
            out.print(JSONUtil.Serialize(outputJson));
        }
    }
}
