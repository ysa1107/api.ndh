//package api.controller.category;
//
//import api.baseController.BaseController;
//import api.entities.JsonResultEnt;
//import api.serviceUtils.CategoryService;
//import com.nct.shop.thrift.brand.models.CategoryStatus;
//import com.nct.shop.thrift.brand.models.CategoryType;
//import com.nct.shop.thrift.brand.models.TCategoryFilter;
//import com.shopiness.framework.common.LogUtil;
//import com.shopiness.framework.util.ConvertUtils;
//import org.apache.log4j.Logger;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
///**
// *
// * @author trailn
// */
//public class CategoryControllerv1 extends BaseController {
//
//    private static final Logger logger = LogUtil.getLogger(CategoryControllerv1.class);
//    
//    @Override
//    public JsonResultEnt processRequest(String _packageControler) {
//        JsonResultEnt result;
//        logger.info("PARAMS:" + getParams());
//        switch (getAction()) {
//            case "search-categories":
//                result = getCategories();
//                break;
//              
//            default:
//                result = JsonResultEnt.getJsonUnsupportMethod();
//                break;
//        }
//        return result;
//    }
//    
//    public JsonResultEnt getCategories() {
//        JsonResultEnt result = new JsonResultEnt();
//        try {
//            String page = getParams().get("pageIndex");
//            String view = getParams().get("pageSize");
//            int pageIndex = ConvertUtils.toInt(page, 1);
//            int pageSize = ConvertUtils.toInt(view, 10);
//            TCategoryFilter filter = new TCategoryFilter();
//            filter.setStatus(CategoryStatus.ENABLED.getValue());
//            filter.setType(CategoryType.BRAND.getValue());
//            filter.setPageIndex(pageIndex);
//            filter.setPageSize(pageSize);
//            result = CategoryService.getInstance().getCategories(filter, false);
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//        }
//        return result;
//    }
//    
//}
