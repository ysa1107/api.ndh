/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.JsonResultEnt;
import api.entities.NotificationEnt;
import com.nct.shop.thrift.deal.models.TNotificationFilter;
import com.nct.shop.thrift.deal.models.TNotificationResult;
import com.nct.shop.thrift.deal.models.TNotificationUser;
import com.nct.shop.thrift.deal.models.TNotificationUserResult;
import java.util.List;

/**
 *
 * @author huanlh
 */
public interface NotificationIface {

    boolean updateNotificationUser(TNotificationUser notificationUser);

    List<NotificationEnt> getNotifications(TNotificationFilter filter);

    JsonResultEnt getJsonNotifications(TNotificationFilter filter);

    TNotificationUserResult insertNotificationUser(TNotificationUser notiUser);

    TNotificationUserResult getNotificationUserByUsername(String username);

    JsonResultEnt getSummaryNotification(String username);

    JsonResultEnt viewNotification(String usernam, Integer notiType, Integer viewType, String objectIds);

    JsonResultEnt updateComment();

    boolean deleteNotificationUser(long notificationId);

    TNotificationResult getNotification(long notificationId);
}
