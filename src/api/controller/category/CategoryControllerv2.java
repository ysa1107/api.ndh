//package api.controller.category;
//
//import api.entities.JsonResultEnt;
//import com.shopiness.framework.common.LogUtil;
//import org.apache.log4j.Logger;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
///**
// *
// * @author liemtpt
// */
//public class CategoryControllerv2 extends CategoryControllerv1 {
//
//    private static final Logger logger = LogUtil.getLogger(CategoryControllerv2.class);
//    
//    @Override
//    public JsonResultEnt processRequest(String _packageControler) {
//        JsonResultEnt result;
//        logger.info("PARAMS:" + getParams());
//        switch (getAction()) {
//            default:
//                result = super.processRequest(_packageControler);
//                break;
//        }
//        return result;
//    }
//    
//}
