/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.controller.deal;

import api.cache.ApiCA;
import api.entities.DealFavoriteListEnt;
import api.entities.DealListEnt;
import api.entities.DealObjectViewEnt;
import api.entities.DealViewEnt;
import api.entities.EventDealListEnt;
import api.entities.JsonResultEnt;
import api.serviceUtils.BrandService;
import api.serviceUtils.DealService;
import api.serviceUtils.EventService;
import api.serviceUtils.SearchService;
import api.utils.ErrorCode;
import com.nct.shop.thrift.deal.models.DealStatus;
import com.nct.shop.thrift.deal.models.TDeal;
import com.nct.shop.thrift.deal.models.TEventDealFilter;
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
 * @author liemtpt
 */
public class DealControllerv2 extends DealControllerv1 {

    private static final Logger logger = LogUtil.getLogger(DealControllerv2.class);

    @Override
    public JsonResultEnt processRequest(String _packageControler) {
        JsonResultEnt result;
        logger.info("PARAMS:" + getParams());
        if (!getMethod().equals("POST")) {
            return JsonResultEnt.getJsonUnsupportMethod();
        }
        switch (getAction()) {
            case "get-datas-for-deal-home":
                result = getDatasForDealHome();
                break;
            default:
                result = super.processRequest(_packageControler);
                break;
        }
        return result;
    }

    public JsonResultEnt getDatasForDealHome() {
        long startTime = DateTimeUtils.getMilisecondsNow();
        JsonResultEnt result = new JsonResultEnt();
        try {
            long userId = ConvertUtils.toLong(getParams().get("userId"), 0);
            int areaId = ConvertUtils.toInt(getParams().get("areaId"), 0);
            int pageIndex = ConvertUtils.toInt(getParams().get("pageIndex"), 1);
            int pageSize = ConvertUtils.toInt(getParams().get("pageSize"), 10);
            if (pageSize > 30) {
                pageSize = 30;
            }

            // get from cache            
            String cacheKey = String.format("%s-%s-%s-%s-%s", getAction(), userId, areaId, pageIndex, pageSize);
            JsonResultEnt cacheResult = ApiCA.get(StringUtils.doMD5(cacheKey));
            if (cacheResult != null) {
                logger.info("HIT CACHE KEY = " + cacheKey + " VALUE = " + cacheResult);
                return cacheResult;
            }

            // miss cache, build search value and get from ES
            TDealSearchValue searchVal = new TDealSearchValue();
            searchVal.setAreaId(areaId);
            searchVal.setOrderBy(OrderDealBy.dateCreated);
            searchVal.setOrderOrient(OrderOrient.DESC);
            searchVal.setStatus(DealStatus.APPROVED.getValue());
            searchVal.setDateExpired(DateTimeUtils.getMilisecondsNow() + "");
            searchVal.setPageIndex(pageIndex);
            searchVal.setPageSize(pageSize);

            // deal from brand that user isn't following
            DealListEnt otherDeals = new DealListEnt();
            List<DealViewEnt> otherDealEnts = new ArrayList<>();
            TSearchResult searchRes = SearchService.searchDeal(searchVal);
            if (searchRes != null && searchRes.getIds() != null) {
                for (TDeal dealT : DealService.getInstance().getMulti(searchRes.getIds())) {
                    otherDealEnts.add(new DealViewEnt(dealT, userId));
                }
                otherDeals.setListDeal(otherDealEnts, userId, true);
                otherDeals.setTotalRecords(searchRes.getTotalRecord());
                otherDeals.setLoadMore((pageSize * pageIndex) < searchRes.getTotalRecord());
            }

            // deal from brand that user is following
            DealFavoriteListEnt favouriteDeals = new DealFavoriteListEnt();
            if (pageIndex <= 1 && userId > 0) {
                searchVal.setBrandIds(BrandService.getInstance().getBrandsOfUser(userId));
                long twoDaysAgo = DateTimeUtils.getMilisecondsNow() - 2l * 24 * 60 * 60 * 1000;

                // get new deal that published within 2 days ago
                searchVal.setDatePublished(twoDaysAgo + "");
                TSearchResult res = SearchService.searchDeal(searchVal);

                if (res != null && res.getIds() != null) {
                    List<DealViewEnt> newDealEnts = new ArrayList<>();
                    for (TDeal dealT : DealService.getInstance().getMulti(res.getIds())) {
                        newDealEnts.add(new DealViewEnt(dealT, false, userId, true, false));
                    }

                    DealListEnt newDeals = new DealListEnt();
                    newDeals.setListDeal(newDealEnts, userId, false);
                    favouriteDeals.setLstNew(newDeals.getListDeal());
                }

                // get older deal that published more than 3 days ago
                searchVal.setDatePublished(0 + "," + twoDaysAgo);
                TSearchResult res2 = SearchService.searchDeal(searchVal);

                if (res2 != null && res2.getIds() != null) {
                    List<DealViewEnt> olderDealEnts = new ArrayList<>();
                    for (TDeal dealT : DealService.getInstance().getMulti(res2.getIds())) {
                        olderDealEnts.add(new DealViewEnt(dealT, userId));
                    }
                    
                    DealListEnt olderDeals = new DealListEnt();
                    olderDeals.setListDeal(olderDealEnts, userId, false);
                    favouriteDeals.setLstOther(olderDeals.getListDeal());
                }
            }
            
            // event deal
            TEventDealFilter efilter = new TEventDealFilter();
            efilter.setAreaId(ConvertUtils.toInt(areaId, 0));
            efilter.includeExpired = false;
            EventDealListEnt eventDeals = (pageIndex > 1) ? new EventDealListEnt()
                    : EventService.getInstance().getListEventDeal(efilter, false);

            // finalize the result
            DealObjectViewEnt objEnt = new DealObjectViewEnt(eventDeals, otherDeals, favouriteDeals);
            result.setData(objEnt);
            result.setCode(ErrorCode.SUCCESS.getValue());

            // put to cache if request from non-login user
            if (userId == 0) {
                ApiCA.set(StringUtils.doMD5(cacheKey), result);
            }

        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        } finally {
            long endTime = DateTimeUtils.getMilisecondsNow();
            logger.info("StartTime: " + startTime + ", endTime: " + endTime + ", ProcessTime: " + (endTime - startTime));
        }
        return result;
    }

    @Override
    public JsonResultEnt searchDeals() {
        long startTime = DateTimeUtils.getMilisecondsNow();
        JsonResultEnt result = new JsonResultEnt(ErrorCode.SUCCESS.getValue());
        try {
            long userId = ConvertUtils.toLong(getParams().get("userId"), 0);
            String listCategoryId = ConvertUtils.toString(getParams().get("categoryId"), "");
            int dealType = ConvertUtils.toInt(getParams().get("dealType"), 0);
            int couponType = ConvertUtils.toInt(getParams().get("couponType"), 0);
            int groupType = ConvertUtils.toInt(getParams().get("groupType"), 0);
            String keyword = ConvertUtils.toString(getParams().get("keyword"), "");
            String orderBy = ConvertUtils.toString(getParams().get("orderBy"), "");
            int areaId = ConvertUtils.toInt(getParams().get("areaId"), 0);
            int pageIndex = ConvertUtils.toInt(getParams().get("pageIndex"), 1);
            int pageSize = ConvertUtils.toInt(getParams().get("pageSize"), 10);

            // get from cache            
            String cacheKey = String.format("%s-%s-%s-%s-%s-%s-%s-%s-%s-%s", getAction(), userId, listCategoryId, dealType, couponType, keyword, orderBy, areaId, pageIndex, pageSize);
            JsonResultEnt cacheResult = ApiCA.get(StringUtils.doMD5(cacheKey));
            if (cacheResult != null) {
                logger.info("HIT CACHE KEY = " + cacheKey + " VALUE = " + cacheResult);
                return cacheResult;
            }

            // miss cache, build search value and get from ES
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
            searchVal.setOrderOrient(OrderOrient.DESC);
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
                List<DealViewEnt> deals = new ArrayList<>();
                for (TDeal tDeal : tDeals) {
                    deals.add(new DealViewEnt(tDeal, false, userId, true, false));
                }

                DealListEnt data = new DealListEnt();
                data.setListDeal(deals, userId, false);
                result.setData(data.getListDeal());
                result.setTotal(searchRes.getTotalRecord());
                result.setLoadMore((pageSize * pageIndex) < searchRes.getTotalRecord());
            }

            // put to cache if request from non-login user
            if (ConvertUtils.toLong(userId, 0) == 0) {
                ApiCA.set(StringUtils.doMD5(cacheKey), result);
            }

        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        long endTime = DateTimeUtils.getMilisecondsNow();
        logger.info("StartTime: " + startTime + ", endTime: " + endTime + ", ProcessTime: " + (endTime - startTime));
        return result;
    }
}
