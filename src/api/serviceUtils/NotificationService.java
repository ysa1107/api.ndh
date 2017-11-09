/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.JsonResultEnt;
import api.entities.NotificationEnt;
import api.entities.NotificationListEnt;
import api.entities.SummaryNotification;
import api.iface.NotificationIface;
import api.utils.Constant;
import api.utils.ErrorCode;
import api.utils.FunctionUtils;
import com.nct.shop.thrift.deal.client.TNotificationClient;
import com.nct.shop.thrift.deal.models.TNotification;
import com.nct.shop.thrift.deal.models.TNotificationFilter;
import com.nct.shop.thrift.deal.models.TNotificationListResult;
import com.nct.shop.thrift.deal.models.TNotificationResult;
import com.nct.shop.thrift.deal.models.TNotificationUser;
import com.nct.shop.thrift.deal.models.TNotificationUserFilter;
import com.nct.shop.thrift.deal.models.TNotificationUserListResult;
import com.nct.shop.thrift.deal.models.TNotificationUserResult;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.util.ConvertUtils;
import com.shopiness.framework.util.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author huanlh
 */
public class NotificationService implements NotificationIface {

    private static final Logger logger = LogUtil.getLogger(NotificationService.class);
    public static NotificationService instance = null;

    public static NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
        }
        return instance;
    }

    public NotificationService() {

    }

    @Override
    public boolean updateNotificationUser(TNotificationUser notificationUser) {
        try {
            TNotificationClient client = TNotificationClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            return client.updateNotificationUser(notificationUser);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public List<NotificationEnt> getNotifications(TNotificationFilter filter) {
        List<NotificationEnt> lstNoti = new ArrayList<>();
        TNotificationClient client = TNotificationClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

        try {

            TNotificationListResult nrs = client.getNotifications(filter);

            if (nrs.getListData() != null && nrs.getListData().size() > 0) {
                for (int i = 0; i < nrs.getListData().size(); i++) {
                    TNotification tNoti = nrs.getListData().get(i);
                    NotificationEnt noti = new NotificationEnt(tNoti, true);

                    lstNoti.add(noti);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return lstNoti;
    }

    @Override
    public JsonResultEnt getJsonNotifications(TNotificationFilter filter) {
        JsonResultEnt result = new JsonResultEnt();
        NotificationListEnt notiListEnt = new NotificationListEnt();

        try {
            //Lay thong bao Deal
            TNotificationClient client = TNotificationClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            TNotificationListResult rs = client.getNotifications(filter);
            List<NotificationEnt> lstNotiDeal = new ArrayList<>();
            long totalRecords = 0;
            boolean loadmore = false;

            if (rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TNotification tNoti = rs.getListData().get(i);
                    NotificationEnt notiDealEnt = new NotificationEnt(tNoti, true);

                    lstNotiDeal.add(notiDealEnt);
                }

                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadmore = true;
                }
            }

            //Set vao NotificationListEnt de truy van multi deal
            notiListEnt.setLstDealNotification(lstNotiDeal, filter.getUsername());

            //Set trang thai da xem
            if (lstNotiDeal.size() > 0 && !FunctionUtils.IsNullOrEmpty(filter.username)) {
                TNotificationUserResult nrs = getNotificationUserByUsername(filter.username);
                if (nrs != null && nrs.getValue() != null) {
                    TNotificationUser notiUser = nrs.getValue();

                    if (!FunctionUtils.IsNullOrEmpty(notiUser.newIds)) {
                        TNotificationFilter nfilter = new TNotificationFilter();
                        nfilter.setIsNew(Constant.UNVIEWED_NOTIFY);
                        nfilter.setUsername(filter.username);
                        nfilter.setGroupNotificationType(filter.groupNotificationType);
                        nfilter.setOrderBy("date_created");
                        nfilter.setOrderType("ASC");

                        TNotificationListResult lstNoti = client.getNotifications(nfilter);
                        if (lstNoti != null && lstNoti.getListData() != null && lstNoti.getListData().size() > 0) {
                            for (TNotification noti : lstNoti.getListData()) {
                                String unViewedIds
                                        = FunctionUtils.removedStringIDs(notiUser.newIds, noti.getNotificationId());
                                String viewedIds
                                        = FunctionUtils.addStringIDs(notiUser.notificationIds, noti.getNotificationId());

                                notiUser.setNotificationIds(viewedIds);
                                notiUser.setNewIds(unViewedIds);
                            }

                            //Check xem danh sach thong bao cua user da vuot 100 item chua
                            notiUser.notificationIds = FunctionUtils.getNewestNotificationId(notiUser.notificationIds);
                            updateNotificationUser(notiUser);
                        }
                    }
                }
            }

            result.setData(notiListEnt);
            result.setTotal(totalRecords);
            result.setLoadMore(loadmore);
            result.setCode(ErrorCode.SUCCESS.getValue());

        } catch (Exception e) {
            logger.error(e.getMessage());
            return JsonResultEnt.getJsonDataNotExist();
        }
        return result;
    }

    @Override
    public TNotificationUserResult insertNotificationUser(TNotificationUser notiUser) {
        TNotificationUserResult nrs = null;
        try {
            TNotificationClient client = TNotificationClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            nrs = client.insertNotificationUser(notiUser);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return nrs;
    }

    @Override
    public TNotificationUserResult getNotificationUserByUsername(String username) {
        TNotificationUserResult nrs = null;
        try {
            TNotificationClient client = TNotificationClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            nrs = client.getNotificationUserByUsername(username);

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return nrs;
    }

    @Override
    public JsonResultEnt getSummaryNotification(String username) {
        JsonResultEnt result = new JsonResultEnt();
        SummaryNotification totalNoti = new SummaryNotification();
        TNotificationClient client = TNotificationClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
        try {
            TNotificationFilter filter = new TNotificationFilter();
            filter.setGroupNotificationType(Constant.GROUP_DEAL);
            filter.setIsNew(Constant.UNVIEWED_NOTIFY);
            filter.setUsername(username);

            int dealNum = client.countNotifications(filter);

            filter.setGroupNotificationType(Constant.GROUP_LIKE_COMMENT);
            int communityNum = client.countNotifications(filter);

            totalNoti.newDeal = dealNum;
            totalNoti.newCommunity = communityNum;

            //Lay tong notification cua Comment Like
            result.setData(totalNoti);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public JsonResultEnt viewNotification(String username, Integer notiType, Integer viewType, String objectIds) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        TNotificationClient client = TNotificationClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
        try {

            TNotificationUserResult nrs = getNotificationUserByUsername(username);
            if (nrs != null && nrs.getValue() != null) {
                TNotificationUser notiUser = nrs.getValue();

                if (viewType.equals(Constant.ALL)) {
                    objectIds = "";
                    TNotificationFilter filter = new TNotificationFilter();
                    filter.setIsNew(Constant.UNVIEWED_NOTIFY);
                    filter.setUsername(username);
                    filter.setGroupNotificationType(notiType);

                    TNotificationListResult lstNoti = client.getNotifications(filter);
                    if (lstNoti != null && lstNoti.getListData() != null && lstNoti.getListData().size() > 0) {
                        for (TNotification noti : lstNoti.getListData()) {
                            objectIds += noti.getNotificationId() + ";";
                        }
                    }
                }

                String[] objIds = null;
                String[] newIds = null;
                if (!FunctionUtils.IsNullOrEmpty(objectIds)
                        && !FunctionUtils.IsNullOrEmpty(notiUser.newIds)) {
                    objIds = objectIds.split(";");
                    newIds = notiUser.newIds.split(",");

                    if (objIds.length > 0 && newIds.length > 0) {
                        for (int i = 0; i < objIds.length; i++) {
                            String unViewedIds
                                    = FunctionUtils.removedStringIDs(notiUser.newIds, ConvertUtils.toInt(objIds[i]));
                            String viewedIds
                                    = FunctionUtils.addStringIDs(notiUser.notificationIds, ConvertUtils.toInt(objIds[i]));

                            notiUser.setNotificationIds(viewedIds);
                            notiUser.setNewIds(unViewedIds);
                        }

                        boolean rs = updateNotificationUser(notiUser);
                        if (rs) {
                            return JsonResultEnt.getJsonSuccess();
                        }
                    }
                }
            } else {
                return JsonResultEnt.getJsonDataNotExist();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    public static void main(String[] args) {

        String abc = StringUtils.urlEncode("Giảm Tới 15%");
        System.out.println(abc);
    }

    @Override
    public JsonResultEnt updateComment() {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        TNotificationClient client = TNotificationClient.getInstance(ConfigInfo.SERVER_USER_DEAL);

        try {
            TNotificationFilter filter = new TNotificationFilter();
            filter.setGroupNotificationType(Constant.GROUP_LIKE_COMMENT);

            TNotificationListResult nrs = client.getNotifications(filter);
            if (nrs != null && nrs.listData != null && nrs.listData.size() > 0) {
                for (TNotification noti : nrs.listData) {
                    TNotificationUserFilter nuFilter = new TNotificationUserFilter();
                    nuFilter.setNotificationId(noti.notificationId);

                    TNotificationUserListResult urs = client.getNotificationUsers(nuFilter);

                    if (urs != null && urs.listData != null && urs.listData.size() > 0) {
                        for (TNotificationUser user : urs.listData) {
                            if (user.notificationIds != null && user.newIds != null
                                    && (user.notificationIds.contains(String.valueOf(noti.notificationId))
                                    || user.newIds.contains(String.valueOf(noti.notificationId)))) {
                                user.notificationIds = FunctionUtils.removedStringIDs(user.notificationIds, noti.notificationId);
                                user.newIds = FunctionUtils.removedStringIDs(user.newIds, noti.notificationId);

                                client.updateNotificationUser(user);
                            }
                        }
                    }
                }
            }
            result = JsonResultEnt.getJsonSuccess();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    @Override
    public boolean deleteNotificationUser(long notificationId) {
        try {
            TNotificationClient client = TNotificationClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            return client.deteleNotificationUser(notificationId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    @Override
    public TNotificationResult getNotification(long notificationId) {
        try {
            TNotificationClient client = TNotificationClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
            return client.getNotification(notificationId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
}
