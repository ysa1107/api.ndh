/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.notification.configuration.Const;
import com.shopiness.notification.service.GearmanClient;
import org.apache.log4j.Logger;

/**
 *
 * @author ysa
 */
public class PushV2Service {

    private static final Logger logger = LogUtil.getLogger(PushV2Service.class);
     
    
    public static void likeUnlikeBrand(long brandID, long userID, boolean islike) {
        try {
            if(brandID == 0 || userID == 0){
                return;
            }
            String topic = String.format(Const.TOPIC.LIKE_BRAND, brandID);
            boolean check = false;
            
            GearmanClient client = GearmanClient.getInstance(ConfigInfo.GEARMAN_PUSH_V2_SERVER);
            if(islike){ //subcribe
                check = client.subscribeTopic(topic, userID);
            }else{ //unsubcribe
                check = client.unSubscribeTopic(topic, userID);
            }
            logger.info("likeUnlikeBrand: brandID: " + brandID + " - uid: " + userID + " - isLike: " + islike + " - topic: " + topic + " - result: " + check);
                        
        } catch (Exception ex) {
            logger.error(LogUtil.stackTrace(ex));
        }
    }
    
    public static void likeUnlikeDeal(long dealID, long userID, boolean islike) {
        try {
            if(dealID == 0 || userID == 0){
                return;
            }
            String topic = String.format(Const.TOPIC.LIKE_DEAL, dealID);
            boolean check = false;
            
            GearmanClient client = GearmanClient.getInstance(ConfigInfo.GEARMAN_PUSH_V2_SERVER);
            if(islike){ //subcribe
                check = client.subscribeTopic(topic, userID);
            }else{ //unsubcribe
                check = client.unSubscribeTopic(topic, userID);
            }
            logger.info("likeUnlikeDeal: dealID: " + dealID + " - uid: " + userID + " - isLike: " + islike + " - topic: " + topic + " - result: " + check);
                        
        } catch (Exception ex) {
            logger.error(LogUtil.stackTrace(ex));
        }
    }
}
