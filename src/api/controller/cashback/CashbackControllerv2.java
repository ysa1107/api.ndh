//package api.controller.cashback;
//
//import api.baseController.BaseController;
//import api.configuration.ConfigInfo;
//import api.entities.BalanceViewEnt;
//import api.entities.BankViewEnt;
//import api.entities.BrandObjectViewEnt;
//import api.entities.BrandViewEnt;
//import api.entities.DealViewEnt;
//import api.entities.HistoryViewEnt;
//import api.entities.JsonResultEnt;
//import api.entities.TransactionViewEnt;
//import api.serviceUtils.BrandService;
//import api.serviceUtils.CashbackService;
//import api.serviceUtils.ContactService;
//import api.serviceUtils.DealService;
//import api.serviceUtils.ScribeService;
//import api.serviceUtils.UserService;
//import api.utils.CurrencyUtils;
//import api.utils.ErrorCode;
//import api.utils.FunctionUtils;
//import api.utils.SecurityUtil;
//import com.nct.shop.thrift.brand.models.BrandStatus;
//import com.nct.shop.thrift.brand.models.Const;
//import com.nct.shop.thrift.brand.models.TBrandFilter;
//import com.nct.shop.thrift.deal.models.DealType;
//import com.nct.shop.thrift.deal.models.TDealFilter;
//import com.nct.shop.thrift.deal.models.TDealResult;
//import com.nct.shop.thrift.deal.models.TUser;
//import com.shopiness.framework.common.LogUtil;
//import com.shopiness.framework.util.ConvertUtils;
//import com.shopiness.framework.util.DateTimeUtils;
//import com.shopiness.framework.util.StringUtils;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import org.apache.log4j.Logger;
//import shopiness.cashback.common.Const.Bank;
//import shopiness.cashback.thrift.TBank;
//import shopiness.cashback.thrift.TBankFilter;
//import shopiness.cashback.thrift.TBankListResult;
//import shopiness.cashback.thrift.THistory;
//import shopiness.cashback.thrift.THistoryFilter;
//import shopiness.cashback.thrift.THistoryListResult;
//import shopiness.cashback.thrift.THistoryResult;
//import shopiness.cashback.thrift.TLink;
//import shopiness.cashback.thrift.TTransaction;
//import shopiness.cashback.thrift.TTransactionListResult;
//import shopiness.cashback.thrift.TTransactionResult;
//
///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
///**
// *
// * @author nghiapht
// */
//public class CashbackControllerv2 extends BaseController {
//
//    private static final Logger logger = LogUtil.getLogger(CashbackControllerv2.class);
//
//    @Override
//    public JsonResultEnt processRequest(String _packageControler) {
//        JsonResultEnt result;
//        logger.info("PARAMS:" + getParams());
//        switch (getAction()) {
//            case "favourite-brands":
//                result = getCashbackBrands(true);
//                break;
//            case "other-brands":
//                result = getCashbackBrands(false);
//                break;
//            case "deals-of-brand":
//                result = getCashbackDeals();
//                break;
//            case "deals-in-popup":
//                result = getPopupDeals();
//                break;
//            case "get-history":
//                result = getHisory();
//                break;
//            case "click":
//                result = clickCashback();
//                break;
//            case "get-transaction":
//                result = getTransaction();
//                break;
//            case "add-transaction":
//                result = addTransaction();
//                break;
//            case "get-banks":
//                result = getBanks();
//                break;
//            case "get-balance":
//                result = getBalance();
//                break;
//            case "fill-transaction":
//                result = fillTransaction();
//                break;
//            case "feedback":
//                result = feedback();
//                break;
//
//            default:
//                result = JsonResultEnt.getJsonUnsupportMethod();
//                break;
//        }
//        return result;
//    }
//
//    private JsonResultEnt getCashbackBrands(boolean isFavorite) {
//        JsonResultEnt result = new JsonResultEnt();
//        try {
//            long userID = ConvertUtils.toLong(getParams().get("userId"), 0);
//            long areaId = ConvertUtils.toLong(getParams().get("areaId"), 0);
//            int pageIndex = ConvertUtils.toInt(getParams().get("pageIndex"), 1);
//            int pageSize = ConvertUtils.toInt(getParams().get("pageSize"), 10);
//
//            TBrandFilter filter = new TBrandFilter();
//            filter.setPageIndex(pageIndex);
//            filter.setPageSize(pageSize);
//            filter.setStatus(BrandStatus.APPROVED.getValue());
//            filter.setIsCheckEventDeal(true);
//            filter.setOrderBy("priority");
//            filter.setOrderType("desc");
//            filter.setIsCashback(true);
//            filter.setActiveDeal(Const.ActiveDeal.TRUE);
//
//            if (userID > 0) {
//                filter.setUserId(userID);
//                filter.setIsFavorite(isFavorite ? Const.Favorite.TRUE : Const.Favorite.FALSE);
//            }
//            if (areaId > 0) {
//                filter.setAreaId(areaId);
//            }
//            result = BrandService.getInstance().getBrands(filter, false, true);
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//        }
//        return result;
//    }
//
//    private JsonResultEnt getCashbackDeals() {
//        JsonResultEnt result = new JsonResultEnt();
//        try {
//            String userId = getParams().get("userId");
//            String brandId = getParams().get("brandId");
//            String areaId = getParams().get("areaId");
//            String dealId = getParams().get("dealId");
//
//            if (FunctionUtils.IsNullOrEmpty(userId) && FunctionUtils.IsNullOrEmpty(brandId) && FunctionUtils.IsNullOrEmpty(areaId)) {
//                return JsonResultEnt.getJsonInvalidRequest();
//            }
//            // get brand (by brand id) 
//            BrandViewEnt brandEnt = BrandService.getInstance().getBrandUser(ConvertUtils.toLong(brandId), ConvertUtils.toLong(userId), false);
//
//            // get list deals from brand (by brand id)
//            TDealFilter filter = new TDealFilter();
//            filter.setDealType(DealType.CASH_BACK.getValue());
//            filter.setBrandId(ConvertUtils.toInt(brandId, 0));
//            filter.setAreaId(ConvertUtils.toInt(areaId, 0));
//            filter.setId(ConvertUtils.toInt(dealId, 0));
//            filter.includeExpired = false;
//            filter.setOrderBy("priority ASC, date_published");
//            filter.setOrderType("DESC");
//            // Check co loc du lieu deal event 
//            filter.isCheckEventDeal = true;
//            if (!FunctionUtils.IsNullOrEmpty(dealId)) {
//                TDealResult dRs = DealService.getInstance().getDeal(ConvertUtils.toLong(dealId));
//                if (dRs != null && dRs.value != null && DealType.DEAL_EVENT.equals(dRs.value.getType())) {
//                    filter.isCheckEventDeal = false;
//                }
//            }
//            List<DealViewEnt> lstDealsEnt = BrandService.getInstance().getDealsOfBrand(filter, false, ConvertUtils.toLong(userId));
//            // set view obj entity
//            if (brandEnt != null) {
//                BrandObjectViewEnt obj = new BrandObjectViewEnt(brandEnt, lstDealsEnt);
//                result.setData(obj);
//            } else {
//                result.setData(new BrandObjectViewEnt());
//            }
//            result.setCode(ErrorCode.SUCCESS.getValue());
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//            return JsonResultEnt.getJsonDataNotExist();
//        }
//        return result;
//    }
//
//    private JsonResultEnt getPopupDeals() {
//        JsonResultEnt result = new JsonResultEnt();
//        try {
//            String userId = getParams().get("userId");
//            String brandId = getParams().get("brandId");
//            String areaId = getParams().get("areaId");
//
//            if (FunctionUtils.IsNullOrEmpty(userId) && FunctionUtils.IsNullOrEmpty(brandId) && FunctionUtils.IsNullOrEmpty(areaId)) {
//                return JsonResultEnt.getJsonInvalidRequest();
//            }
//
//            // get list deals from brand (by brand id)            
//            TDealFilter filter = new TDealFilter();
//            filter.setBrandId(ConvertUtils.toInt(brandId, 0));
//            filter.setAreaId(ConvertUtils.toInt(areaId, 0));
//            filter.includeExpired = false;
//            filter.setOrderBy("priority ASC, date_published");
//            filter.setOrderType("DESC");
//            filter.isCheckEventDeal = true;
//
//            filter.setDealType(DealType.CASH_BACK.getValue());
//            List<DealViewEnt> deals1 = BrandService.getInstance().getDealsOfBrand(filter, false, ConvertUtils.toLong(userId));
//
//            filter.setDealType(DealType.DEAL.getValue());
////            filter.setCouponType(CouponType.NO_REQUIRE.getValue());
//            List<DealViewEnt> deals2 = BrandService.getInstance().getDealsOfBrand(filter, false, ConvertUtils.toLong(userId));
//
//            filter.setDealType(DealType.CODE.getValue());
////            filter.setCouponType(CouponType.UNIQUE_CODE.getValue());
//            List<DealViewEnt> deals3 = BrandService.getInstance().getDealsOfBrand(filter, false, ConvertUtils.toLong(userId));
//
//            List<DealViewEnt> ret = new ArrayList();
//            if (deals1 != null) {
//                ret.addAll(deals1);
//            }
//            if (deals2 != null) {
//                ret.addAll(deals2);
//            }
//            if (deals3 != null) {
//                ret.addAll(deals3);
//            }
//
//            result.setData(ret);
//            result.setCode(ErrorCode.SUCCESS.getValue());
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//            return JsonResultEnt.getJsonDataNotExist();
//        }
//        return result;
//    }
//
//    private JsonResultEnt getHisory() {
//        JsonResultEnt result = new JsonResultEnt();
//        try {
//            long userID = ConvertUtils.toLong(getParams().get("userId"), 0);
//            short status = ConvertUtils.toShort(getParams().get("status"), shopiness.cashback.common.Const.History.Status.PROCESSING);
//            int pageIndex = ConvertUtils.toInt(getParams().get("pageIndex"), 1);
//            int pageSize = ConvertUtils.toInt(getParams().get("pageSize"), 10);
//
//            if (userID <= 0) {
//                return JsonResultEnt.getJsonInvalidRequest();
//            }
//
//            THistoryFilter filter = new THistoryFilter();
//            filter.setUserId(userID);
//            filter.setStatus(status);
//            filter.setPageIndex(pageIndex);
//            filter.setPageSize(pageSize);
//
//            THistoryListResult res = CashbackService.getHistory(filter);
//            if (res == null || res.getListData() == null) {
//                return JsonResultEnt.getJsonDataNotExist();
//            }
//
//            List<HistoryViewEnt> data = new ArrayList();
//            for (THistory hisT : res.getListData()) {
//                data.add(new HistoryViewEnt(hisT));
//            }
//
//            result.setTotal(res.getTotalRecord());
//            result.setData(data);
//            result.setCode(ErrorCode.SUCCESS.getValue());
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//            return JsonResultEnt.getJsonDataNotExist();
//        }
//        return result;
//    }
//
//    private JsonResultEnt clickCashback() {
//        JsonResultEnt ret = JsonResultEnt.getJsonSuccess();
//        ret.setData("http://shopiness.vn");
//        try {
//            String hash = ConvertUtils.toString(getParams().get("h"), "");
//            String source = ConvertUtils.toString(getParams().get("source"), "");
//            if (!StringUtils.isEmpty(hash)) {
//                String decoded = SecurityUtil.decrypt(hash);
//                String[] arr = decoded.split("_");
//
//                long linkId = ConvertUtils.toLong(arr[0], 0);
//                long userId = ConvertUtils.toLong(arr[1], 0);
//                long dealId = ConvertUtils.toLong(arr[2], 0);
//                long time = ConvertUtils.toLong(arr[3], 0);
//
//                // check time and prevent cheating
//                if (System.currentTimeMillis() - time > (3600 * 1000)) {
//                    return ret;
//                }
//
//                // check value decoded values
//                if (linkId <= 0 || userId <= 0 || dealId <= 0) {
//                    return ret;
//                }
//
//                // check link data
//                TLink linkT = CashbackService.getLink(linkId);
//                if (linkT == null || StringUtils.isEmpty(linkT.getUrl())) {
//                    return ret;
//                }
//
//                // insert new history record
//                THistory newHistory = new THistory();
//                newHistory.setLinkId(linkId);
//                newHistory.setDateCreated(DateTimeUtils.getMilisecondsNow());
//                newHistory.setDealId(dealId);
//                newHistory.setUserCreated(userId);
//                newHistory.setStatus(shopiness.cashback.common.Const.History.Status.PROCESSING);
//                if(!StringUtils.isEmpty(source)){
//                    newHistory.setSource((short)1); //source web
//                }
//                THistoryResult res = CashbackService.insertHistory(newHistory);
//                if (res == null || res.getValue() == null) {
//                    return ret;
//                }
//
//                //LOG SCRIBE LOG CLICK HISTORY
//                String clientIP = getParams().get("clientip");
//                String action = getParams().get("action");
//                String module = getParams().get("module");
//                ScribeService.logCashBackClickHistory(clientIP, module, action, res.value.id, res.value.linkId, res.value.dealId, userId, res.value.status);
//
//                // use this for mapping accesstrade transaction with shopiness history record
//                String utmContent = StringUtils.doMD5(ConfigInfo.CASHBACK_KEY + res.getValue().getId() + res.getValue().getLinkId());
//                ret.setData(linkT.getUrl() + (linkT.getUrl().contains("?") ? "" : "?") + "&utm_content=" + utmContent);
//
//            }
//
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//        }
//        return ret;
//    }
//
//    private JsonResultEnt getTransaction() {
//        JsonResultEnt result = new JsonResultEnt();
//        try {
//            long userID = ConvertUtils.toLong(getParams().get("userId"), 0);
//            int pageIndex = ConvertUtils.toInt(getParams().get("pageIndex"), 1);
//            int pageSize = ConvertUtils.toInt(getParams().get("pageSize"), 10);
//
//            if (userID <= 0) {
//                return JsonResultEnt.getJsonInvalidRequest();
//            }
//
//            TTransactionListResult res = CashbackService.getTransactionsOfUser(userID);
//            if (res == null || res.getListData() == null) {
//                return JsonResultEnt.getJsonDataNotExist();
//            }
//
//            List<TransactionViewEnt> data = new ArrayList();
//            for (TTransaction transT : res.getListData()) {
//                data.add(0, new TransactionViewEnt(transT));
//            }
//
//            result.setTotal(res.getTotalRecord());
//            result.setData(data);
//            result.setCode(ErrorCode.SUCCESS.getValue());
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//            return JsonResultEnt.getJsonDataNotExist();
//        }
//        return result;
//    }
//
//    private JsonResultEnt fillTransaction() {
//        JsonResultEnt result = new JsonResultEnt();
//        try {
//            long userID = ConvertUtils.toLong(getParams().get("userId"), 0);
//
//            if (userID <= 0) {
//                return JsonResultEnt.getJsonInvalidRequest();
//            }
//
//            TTransactionListResult res = CashbackService.getSavedTransactionsOfUser(userID);
//            if (res == null || res.getListData() == null) {
//                return JsonResultEnt.getJsonDataNotExist();
//            }
//
//            List<TransactionViewEnt> data = new ArrayList();
//            for (TTransaction transT : res.getListData()) {
//                data.add(0, new TransactionViewEnt(transT));
//            }
//
//            result.setTotal(res.getTotalRecord());
//            result.setData(data);
//            result.setCode(ErrorCode.SUCCESS.getValue());
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//            return JsonResultEnt.getJsonDataNotExist();
//        }
//        return result;
//    }
//
//    private JsonResultEnt addTransaction() {
//        try {
//            long userID = ConvertUtils.toLong(getParams().get("userId"), 0);
//            int value = ConvertUtils.toInt(getParams().get("value"), 0);
//            String accName = ConvertUtils.toString(getParams().get("accName"), "");
//            String accNum = ConvertUtils.toString(getParams().get("accNum"), "");
//            long bankId = ConvertUtils.toLong(getParams().get("bankId"), 0);
//            boolean checkOnly = ConvertUtils.toBoolean(getParams().get("checkOnly"), false);
//
//            // check if this user is exist
//            TUser userT = UserService.getInstance().getUser(userID);
//            if (userT == null || userT.getUserId() != userID) {
//                return JsonResultEnt.getJsonTransactionError("Người dùng không tồn tại. Vui lòng thử lại!");
//            }
//
//            // validate userId and transaction value
//            if (value <= 0) {
//                return JsonResultEnt.getJsonTransactionError("Số tiền không được nhỏ hơn 0");
//            }
//
//            // validate min value
//            if (value < ConfigInfo.MINIMUM_AMOUNT) {
//                return JsonResultEnt.getJsonTransactionError(String.format("Số tiền rút tối thiểu một lần là %s", CurrencyUtils.formatCurrency(ConfigInfo.MINIMUM_AMOUNT, "đ")));
//            }
//            // validate max value
//            if (value > ConfigInfo.MAXIMUM_AMOUNT) {
//                return JsonResultEnt.getJsonTransactionError(String.format("Số tiền rút tối đa một lần là %s", CurrencyUtils.formatCurrency(ConfigInfo.MAXIMUM_AMOUNT, "đ")));
//            }
//            // validate step value
//            if ((value % ConfigInfo.STEP_AMOUNT) != 0) {
//                return JsonResultEnt.getJsonTransactionError(String.format("Số tiền rút phải là bội số của %s", CurrencyUtils.formatCurrency(ConfigInfo.STEP_AMOUNT, "đ")));
//            }
//
//            // check if exceed the current account balance
//            int curBalance = userT.getAccReceive() - userT.getAccWithdraw();
//            if (value > curBalance) {
//                return JsonResultEnt.getJsonTransactionError("Số tiền vượt quá số dư hiện tại");
//            }
//
//            // check if exceed the limitation of daily withdraw
//            int totalTrans = CashbackService.getTotalTransactionToday(userID);
//            if ((totalTrans + value) > ConfigInfo.MAXIMUM_AMOUNT_PER_DAY) {
//                return JsonResultEnt.getJsonTransactionError(String.format("Số tiền rút tối đa trong ngày là %s triệu", CurrencyUtils.formatCurrency(ConfigInfo.MAXIMUM_AMOUNT_PER_DAY, "đ")));
//            }
//
//            TBank bankT = CashbackService.getBank(bankId);
//            if (bankT == null) {
//                return JsonResultEnt.getJsonTransactionError("Thông tin ngân hàng không hợp lệ");
//            }
//
//            TBank bankParent = CashbackService.getBank(bankT.getParentId());
//            if (bankParent == null) {
//                return JsonResultEnt.getJsonTransactionError("Thông tin ngân hàng không hợp lệ");
//            }
//
//            if (checkOnly) {
//                return JsonResultEnt.getJsonSuccess();
//            }
//
//            TTransaction tranT = new TTransaction();
//            tranT.setName(accName + " - " + bankParent.getName());
//            tranT.setValue(value);
//            tranT.setStatus(shopiness.cashback.common.Const.Transaction.Status.WAITING);
//            tranT.setUserCreated(userID);
//            tranT.setAccName(accName);
//            tranT.setAccNum(accNum);
//            tranT.setBankId(bankId);
//            tranT.setType(shopiness.cashback.common.Const.Transaction.Type.DEBIT);
//
//            TTransactionResult res = CashbackService.insertTransaction(tranT);
//            if (res == null || res.getValue() == null) {
//                return JsonResultEnt.getJsonUnknownError();
//            }
//
//            // save transaction for later use
//            CashbackService.saveTransaction(userID, res.getValue().getId());
//
//            //LOG SCRIBE CASHBACK TRANSACTION
//            String clientIP = getParams().get("clientip");
//            String action = getParams().get("action");
//            String module = getParams().get("module");
//            ScribeService.logCashbackTransaction(clientIP, module, action,value,accName,accNum,bankId,shopiness.cashback.common.Const.Transaction.Type.DEBIT,
//                    shopiness.cashback.common.Const.Transaction.Status.WAITING,userT.username,curBalance,userID);
//            return JsonResultEnt.getJsonSuccess();
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//        }
//        return JsonResultEnt.getJsonSystemError();
//    }
//
//    private JsonResultEnt getBanks() {
//        try {
//            Map<Long, BankViewEnt> datas = new HashMap();
//
//            TBankFilter filter = new TBankFilter();
//            filter.setPageIndex(1);
//            filter.setPageSize(100);
//
//            // get all head bank
//            filter.setType(Bank.Type.HEAD);
//            TBankListResult headBank = CashbackService.getBankFilter(filter);
//            if (headBank != null && headBank.getListData() != null) {
//                for (TBank bankT : headBank.getListData()) {
//                    datas.put(bankT.getId(), new BankViewEnt(bankT));
//                }
//            }
//
//            // get all branch bank
//            filter.setType(Bank.Type.BRANCH);
//            TBankListResult branchBank = CashbackService.getBankFilter(filter);
//            if (branchBank != null && branchBank.getListData() != null) {
//                for (TBank bankT : branchBank.getListData()) {
//                    long parentId = bankT.getParentId();
//                    if (datas.containsKey(parentId)) {
//                        datas.get(parentId).addBranch(bankT);
//                    }
//                }
//            }
//
//            JsonResultEnt ret = JsonResultEnt.getJsonSuccess();
//            ret.setData(datas.values());
//            return ret;
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//        }
//        return JsonResultEnt.getJsonSystemError();
//    }
//
//    private JsonResultEnt getBalance() {
//        JsonResultEnt result = new JsonResultEnt();
//        try {
//            long userID = ConvertUtils.toLong(getParams().get("userId"), 0);
//
//            // validate userId and transaction value
//            if (userID <= 0) {
//                return JsonResultEnt.getJsonInvalidRequest();
//            }
//
//            // check if this user is exist
//            TUser userT = UserService.getInstance().getUser(userID);
//            if (userT == null || userT.getUserId() != userID) {
//                return JsonResultEnt.getJsonDataNotExist();
//            }
//
//            result.setData(new BalanceViewEnt(userT.getAccWithdraw(), userT.getAccReceive()));
//            result.setCode(ErrorCode.SUCCESS.getValue());
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//            return JsonResultEnt.getJsonDataNotExist();
//        }
//        return result;
//    }
//
//    private JsonResultEnt feedback() {
//        try {
//            long userID = ConvertUtils.toLong(getParams().get("userId"), 0);
//            String code = ConvertUtils.toString(getParams().get("code"), "");
//            String email = ConvertUtils.toString(getParams().get("email"), "");
//            String content = ConvertUtils.toString(getParams().get("content"), "");
//            long historyid = ConvertUtils.toLong(getParams().get("historyid"), 0);
//            if (userID < 0 || historyid < 0 || StringUtils.isEmpty(code) || StringUtils.isEmpty(email) || StringUtils.isEmpty(content)) {
//                return JsonResultEnt.getJsonInvalidRequest();
//            }
//
//            return ContactService.getInstance().feedbackCashback(userID, code, email, content, historyid);
//
//        } catch (Exception e) {
//            logger.error(LogUtil.stackTrace(e));
//        }
//        return JsonResultEnt.getJsonUnknownError();
//    }
//}
