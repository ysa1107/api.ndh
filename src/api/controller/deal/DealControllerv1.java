/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.controller.deal;

import api.baseController.BaseController;
import api.entities.DealViewEnt;
import api.entities.JsonResultEnt;
import api.serviceUtils.DealService;
import api.serviceUtils.DealUserService;
import api.serviceUtils.PushV2Service;
import api.serviceUtils.ScribeService;
import api.serviceUtils.SearchService;
import api.utils.Constant;
import api.utils.FunctionUtils;
import com.nct.shop.thrift.deal.models.DealStatus;
import com.nct.shop.thrift.deal.models.TDeal;
import com.nct.shop.thrift.deal.models.TDealFilter;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.util.ConvertUtils;
import com.shopiness.framework.util.DateTimeUtils;
import com.shopiness.framework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import shopiness.search.thrift.OrderDealBy;
import shopiness.search.thrift.OrderOrient;
import shopiness.search.thrift.TDealSearchValue;
import shopiness.search.thrift.TSearchResult;

/**
 *
 * @author huanlh
 */
public class DealControllerv1 extends BaseController {

    private static final Logger logger = LogUtil.getLogger(DealControllerv1.class);

    @Override
    public JsonResultEnt processRequest(String _packageControler) {
        JsonResultEnt result;
        logger.info("PARAMS:" + getParams());
        if (!getMethod().equals("POST")) {
            return JsonResultEnt.getJsonUnsupportMethod();
        }
        switch (getAction()) {
            case "deals-from-favorite-brands":
                result = getDealsFromFavorites(true);
                break;
            case "other-deals":
                result = getDealsFromFavorites(false);
                break;
            case "search-deals":
                result = searchDeals();
                break;
//            case "deals-of-brand":
//                resulget-datas-for-deal-homet = getDealsOfBrand();
//                break;
            case "favorite-deals":
                result = getFavoriteDeals();
                break;
            case "like-deal":
                result = likeDeal(true);
                break;
            case "unlike-deal":
                result = likeDeal(false);
                break;
            case "get-code":
                result = getVoucherCode();
                break;
            case "view-deal":
                result = viewDeal();
                break;
            case "get-deal-by-key-code":
                result = getDealByKeyCode();
                break;
//            case "get-datas-for-deal-home":
//                result = getDatasForDealHome();
//                break;
            default:
                result = JsonResultEnt.getJsonUnsupportMethod();
                break;
        }
//        logger.info("RESULT: " + JSONUtil.Serialize(result));
        return result;
    }

    public JsonResultEnt viewDeal() {
        JsonResultEnt result = new JsonResultEnt();

        try {
            String dealId = getParams().get("dealId");

            if (FunctionUtils.IsNullOrEmpty(dealId)) {
                return JsonResultEnt.getJsonInvalidRequest();
            }

            result = DealService.getInstance().viewDeal(ConvertUtils.toLong(dealId));

        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return result;
    }

    public JsonResultEnt getVoucherCode() {
        JsonResultEnt result = new JsonResultEnt();

        try {
            String dealId = getParams().get("dealId");
            String username = getParams().get("username");
            String type = getParams().get("type");

            if (FunctionUtils.IsNullOrEmpty(dealId)
                    || FunctionUtils.IsNullOrEmpty(type)) {
                return JsonResultEnt.getJsonInvalidRequest();
            }
            if (FunctionUtils.IsNullOrEmpty(username) && type.equals("get")) {
                return JsonResultEnt.getJsonInvalidRequest();
            }

            result = DealService.getInstance().getVoucherCode(ConvertUtils.toLong(dealId), username, type);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return result;
    }

    public JsonResultEnt likeDeal(boolean isSave) {
        JsonResultEnt result = new JsonResultEnt();

        try {
            String dealId = getParams().get("dealId");
            String userId = getParams().get("userId");

            if (FunctionUtils.IsNullOrEmpty(dealId)) {
                return JsonResultEnt.getJsonInvalidRequest();
            }
            if (FunctionUtils.IsNullOrEmpty(userId)) {
                return JsonResultEnt.getJsonInvalidRequest();
            }

            if (isSave) {
                result = DealUserService.getInstance().saveDeal(ConvertUtils.toLong(dealId), ConvertUtils.toLong(userId));
            } else {
                result = DealUserService.getInstance().unSaveDeal(ConvertUtils.toLong(dealId), ConvertUtils.toLong(userId));
            }
            //sub-unsub to Topic Push V2
            PushV2Service.likeUnlikeDeal(ConvertUtils.toLong(dealId), ConvertUtils.toLong(userId), isSave);
//            result = DealService.getInstance().saveDeal(ConvertUtils.toLong(dealId), ConvertUtils.toLong(userId), isSave);

        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return result;
    }

    public JsonResultEnt getDeal() {
        JsonResultEnt result = new JsonResultEnt();

        try {
            String dealId = getParams().get("dealId");
            String userId = getParams().get("userId");

            if (FunctionUtils.IsNullOrEmpty(dealId)) {
                return JsonResultEnt.getJsonInvalidRequest();
            }

            result = DealService.getInstance().getDeal(ConvertUtils.toLong(dealId), ConvertUtils.toLong(userId));
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return result;
    }

    public JsonResultEnt getDealsFromFavorites(Boolean isFavorite) {
        long startTime = DateTimeUtils.getMilisecondsNow();
        JsonResultEnt result = new JsonResultEnt();
        try {
            String userId = getParams().get("userId");
            String areaId = getParams().get("areaId");

            TDealFilter filter = new TDealFilter();
            filter.setUserId(ConvertUtils.toInt(userId, 0));
            filter.setAreaId(ConvertUtils.toInt(areaId, 0));
            filter.includeExpired = false;
            filter.isCheckEventDeal = true;
            if (isFavorite != null && isFavorite == true) {
                filter.setIsfavorite(Constant.FAVORITE_TRUE);
            } else if (isFavorite != null && isFavorite == false) {
                filter.setIsfavorite(Constant.FAVORITE_FALSE);
                String page = getParams().get("pageIndex");
                String view = getParams().get("pageSize");
                int pageIndex = ConvertUtils.toInt(page, 1);
                int pageSize = ConvertUtils.toInt(view, 10);

                filter.setPageIndex(pageIndex);
                filter.setPageSize(pageSize);
            }

//            if (isFavorite != null && isFavorite == false) {
//                String page = getParams().get("pageIndex");
//                String view = getParams().get("pageSize");
//                int pageIndex = ConvertUtils.toInt(page, 1);
//                int pageSize = ConvertUtils.toInt(view, 10);
//
//                filter.setPageIndex(pageIndex);
//                filter.setPageSize(pageSize);
//            }
            filter.setOrderBy("priority ASC, date_published");
            filter.setOrderType("DESC");

            result = DealService.getInstance().getDeals(filter, false, ConvertUtils.toLong(userId));
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        long endTime = DateTimeUtils.getMilisecondsNow();
        logger.info("StartTime: " + startTime + ", endTime: " + endTime + ", ProcessTime: " + (endTime - startTime));
        return result;
    }

    public JsonResultEnt searchDeals() {
        long startTime = DateTimeUtils.getMilisecondsNow();
        JsonResultEnt result = new JsonResultEnt();
        try {
            String userId = getParams().get("userId");
            String listCategoryId = getParams().get("categoryId");
            int dealType = ConvertUtils.toInt(getParams().get("dealType"), 0);
            int couponType = ConvertUtils.toInt(getParams().get("couponType"), 0);
            int groupType = ConvertUtils.toInt(getParams().get("groupType"), 0);
            String keyword = ConvertUtils.toString(getParams().get("keyword"), "");
            String orderBy = ConvertUtils.toString(getParams().get("orderBy"), "");
            int areaId = ConvertUtils.toInt(getParams().get("areaId"), 0);
            int pageIndex = ConvertUtils.toInt(getParams().get("pageIndex"), 1);
            int pageSize = ConvertUtils.toInt(getParams().get("pageSize"), 10);

            TDealSearchValue searchVal = new TDealSearchValue();
            searchVal.setPageIndex(pageIndex);
            searchVal.setPageSize(pageSize);
            searchVal.setAreaId(areaId);
//            searchVal.setIsCheckDealEvent(true);
            searchVal.setCateIds(StringUtils.toList(listCategoryId, ";", Long.class));
            searchVal.setDealType(dealType);
            searchVal.setCouponType(couponType);
            searchVal.setSaleType(groupType);
            searchVal.setKeyword(keyword);
            searchVal.setStatus(DealStatus.APPROVED.getValue());
            searchVal.setDateExpired(DateTimeUtils.getMilisecondsNow() + "," + Long.MAX_VALUE);
            searchVal.setOrderOrient(OrderOrient.ASC);
            searchVal.setOrderBy(OrderDealBy.dateCreated);

            if (!StringUtils.isEmpty(orderBy)) {
                if (orderBy.equalsIgnoreCase("numLike")) {
                    searchVal.setOrderOrient(OrderOrient.DESC);
                    searchVal.setOrderBy(OrderDealBy.priority);
                }
                if (orderBy.equalsIgnoreCase("dateExpired")) {
                    searchVal.setOrderOrient(OrderOrient.ASC);
                    searchVal.setOrderBy(OrderDealBy.dateExpired);
                }
            }

            TSearchResult searchRes = SearchService.searchDeal(searchVal);
            if (searchRes != null && searchRes.getIds() != null) {
                List<TDeal> tDeals = DealService.getInstance().getMulti(searchRes.getIds());
                List<DealViewEnt> data = new ArrayList<>();
                for (TDeal tDeal : tDeals) {
                    data.add(new DealViewEnt(tDeal, false, ConvertUtils.toLong(userId), true, false));
                }

                result.setData(data);
                result.setTotal(searchRes.getTotalRecord());
                result.setLoadMore((pageSize * pageIndex) < searchRes.getTotalRecord());
            }

            //LOG SCRIBE LOG SEARCH DEAL
            String clientIP = getParams().get("clientip");
            String action = getParams().get("action");
            String module = getParams().get("module");
            ScribeService.logSearchDeal(clientIP, module, action, keyword, listCategoryId, dealType, couponType, groupType, areaId, ConvertUtils.toLong(userId, 0));
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        long endTime = DateTimeUtils.getMilisecondsNow();
        logger.info("StartTime: " + startTime + ", endTime: " + endTime + ", ProcessTime: " + (endTime - startTime));
        return result;
    }

    public JsonResultEnt getDealsOfBrand() {
        JsonResultEnt result = new JsonResultEnt();
        try {
            String bId = getParams().get("brandId");
            String userId = getParams().get("userId");
            String areaId = getParams().get("areaId");

            if (FunctionUtils.IsNullOrEmpty(bId)) {
                return JsonResultEnt.getJsonInvalidRequest();
            }

            TDealFilter filter = new TDealFilter();
            filter.setBrandId(ConvertUtils.toInt(bId, 0));
            filter.setAreaId(ConvertUtils.toInt(areaId, 0));
            filter.isCheckEventDeal = true;
//            filter.includeExpired = false;

            String page = getParams().get("pageIndex");
            String view = getParams().get("pageSize");
            int pageIndex = ConvertUtils.toInt(page, 1);
            int pageSize = ConvertUtils.toInt(view, 10);

            filter.setPageIndex(pageIndex);
            filter.setPageSize(pageSize);
            filter.setOrderBy("priority ASC, date_published");
            filter.setOrderType("DESC");

            result = DealService.getInstance().getDeals(filter, true, ConvertUtils.toLong(userId));

        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    public JsonResultEnt getFavoriteDeals() {
        JsonResultEnt result = new JsonResultEnt();
        try {
            String userId = getParams().get("userId");
            String page = getParams().get("pageIndex");
            String view = getParams().get("pageSize");

            if (FunctionUtils.IsNullOrEmpty(userId)) {
                return JsonResultEnt.getJsonInvalidRequest();
            }

            int pageIndex = ConvertUtils.toInt(page, 1);
            int pageSize = ConvertUtils.toInt(view, 10);
            long uid = ConvertUtils.toLong(userId);

            TDealFilter filter = new TDealFilter();
            filter.setUserId(uid);
            filter.isCheckEventDeal = true;
            filter.setPageIndex(pageIndex);
            filter.setPageSize(pageSize);
            filter.setOrderBy("priority ASC, date_published");
            filter.setOrderType("DESC");

            result = DealService.getInstance().getDeals(filter, false, uid);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    public JsonResultEnt getDealByKeyCode() {
        JsonResultEnt result = new JsonResultEnt();

        try {
            String keyCode = getParams().get("keyCode");

            if (FunctionUtils.IsNullOrEmpty(keyCode)) {
                return JsonResultEnt.getJsonInvalidRequest();
            }
            result = DealService.getInstance().getDealByKeyCode(keyCode);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }

        return result;
    }

    public static void main(String[] args) {
    }
}
