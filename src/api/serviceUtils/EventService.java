/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.EventDealListEnt;
import api.entities.EventDealViewEnt;
import api.entities.EventUserListEnt;
import api.entities.EventUserViewEnt;
import api.entities.JsonResultEnt;
import api.iface.EventIface;
import api.utils.ErrorCode;
import com.nct.shop.thrift.deal.client.TEventDealClient;
import com.nct.shop.thrift.deal.client.TEventUserClient;
import com.nct.shop.thrift.deal.models.EventUserStatus;
import com.nct.shop.thrift.deal.models.TEventDeal;
import com.nct.shop.thrift.deal.models.TEventDealFilter;
import com.nct.shop.thrift.deal.models.TEventDealListResult;
import com.nct.shop.thrift.deal.models.TEventDealResult;
import com.nct.shop.thrift.deal.models.TEventUser;
import com.nct.shop.thrift.deal.models.TEventUserFilter;
import com.nct.shop.thrift.deal.models.TEventUserListResult;
import com.nct.shop.thrift.deal.models.TEventUserResult;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.util.DateTimeUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author liemtpt
 */
public class EventService implements EventIface {

    private static final Logger logger = LogUtil.getLogger(EventService.class);
    public static EventService instance = null;
    private static TEventDealClient eClient;
    private static TEventUserClient uClient;

    public static EventService getInstance() {
        if (instance == null) {
            instance = new EventService();
        }
        return instance;
    }

    public EventService() {
        eClient = TEventDealClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
        uClient = TEventUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
    }

    @Override
    public JsonResultEnt getEventDeals(TEventDealFilter filter, boolean isFull) {
        JsonResultEnt result = new JsonResultEnt();
        TEventDeal item = null;
        EventDealViewEnt event = null;
        try {
            TEventDealListResult rs = eClient.getEventDeals(filter);
            List<EventDealViewEnt> lstEventDeal = new ArrayList<>();
            long totalRecords = 0;
            boolean loadmore = false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    item = rs.getListData().get(i);
                    event = new EventDealViewEnt(item, isFull);
                    lstEventDeal.add(event);
                }
                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadmore = true;
                }
            }
//            EventDealListEnt lstEventEnt = new EventDealListEnt();
//            lstEventEnt.setListEventDeal(lstEventDeal, userId, true);
//            result.setData(lstEventEnt.getListEventDeal());
            result.setData(lstEventDeal);
            result.setTotal(totalRecords);
            result.setLoadMore(loadmore);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt getEventDeal(long eventId, long userId, String deviceId) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TEventDealResult rs = eClient.getEventDeal(eventId);
            EventDealViewEnt event = null;
            if (rs.getValue() != null) {
                event = new EventDealViewEnt(rs.getValue(), true, userId, deviceId);
            }
            result.setData(event);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt registrationEventUser(long eventId, long userId, String deviceId) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            // check user co tham gia event
            TEventUserFilter filter = new TEventUserFilter();
            filter.setEventDealId(eventId);
            filter.setUserId(userId);
            filter.setStatus(EventUserStatus.APPROVED.getValue());
            TEventUserListResult rs = uClient.getEventUsers(filter);
            if ((rs != null && rs.getListData() != null && rs.getListData().size() > 0)) {
                result = JsonResultEnt.getJsonEventUserExits();
            } else {
                // Tham gia event
                TEventUser item = new TEventUser();
                item.setEventDealId(eventId);
                item.setUserId(userId);
                item.setDeviceId(deviceId);
                item.setStatus(EventUserStatus.APPROVED);
                item.setDateCreated(DateTimeUtils.getMilisecondsNow());
                TEventUserResult kq = uClient.insertEventUser(item);
                if (kq != null && kq.value != null) {
                    result = JsonResultEnt.getJsonSuccess();
                } else {
                    result = JsonResultEnt.getJsonSystemError();
                }
            }

        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt getEventUsers(TEventUserFilter filter, boolean isFull) {
        JsonResultEnt result = new JsonResultEnt();
        TEventUser item = null;
        EventUserViewEnt event = null;
        try {
            TEventUserListResult rs = uClient.getEventUsers(filter);
            List<EventUserViewEnt> lstEventUser = new ArrayList<>();
            long totalRecords = 0;
            boolean loadmore = false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    item = rs.getListData().get(i);
                    event = new EventUserViewEnt(item, false);
                    lstEventUser.add(event);
                }
                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadmore = true;
                }
            }
            EventUserListEnt lstEventUserEnt = new EventUserListEnt();
            lstEventUserEnt.setListEventUser(lstEventUser);
            result.setData(lstEventUserEnt.getListEventUser());
//            result.setData(lstEventUser);
            result.setTotal(totalRecords);
            result.setLoadMore(loadmore);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public TEventDealListResult getEventDeals(TEventDealFilter filter) {
        TEventDealListResult rs = null;
        try {
            TEventDealClient client = TEventDealClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            rs = client.getEventDeals(filter);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return rs;
    }
    
    @Override
    public EventDealListEnt getListEventDeal(TEventDealFilter filter, boolean isFull) {
        EventDealListEnt listEnt = new EventDealListEnt();
        List<EventDealViewEnt> eventDeals = new ArrayList<>();
        TEventDeal item;
        EventDealViewEnt event;
        try {
            TEventDealListResult rs = eClient.getEventDeals(filter);
            long totalRecords = 0;
            boolean loadmore = false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    item = rs.getListData().get(i);
                    event = new EventDealViewEnt(item, isFull);
                    eventDeals.add(event);
                }
                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadmore = true;
                }
            }
            listEnt.setListEventDeal(eventDeals, loadmore, totalRecords);
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return listEnt;
    }
}
