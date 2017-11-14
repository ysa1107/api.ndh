///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package api.controller.user;
//
//import api.entities.JsonResultEnt;
//import com.shopiness.framework.common.LogUtil;
//import com.shopiness.framework.util.JSONUtil;
//import org.apache.log4j.Logger;
//
///**
// *
// * @author huanlh
// */
//public class UserControllerv2 extends UserControllerv1 {
//
//    private static final Logger logger = LogUtil.getLogger(UserControllerv2.class);
//    @Override
//    public JsonResultEnt processRequest(String _packageControler) {
//        JsonResultEnt result;
//        logger.info("PARAMS:" + getParams());
//        if (!getMethod().equals("POST")) {
//            return JsonResultEnt.getJsonUnsupportMethod();
//        }
//        switch (getAction()) {
//            default:
//                result = super.processRequest(_packageControler);
//                break;
//        }
//        logger.info("RESULT: " + JSONUtil.Serialize(result));
//        return result;
//    }
//}
