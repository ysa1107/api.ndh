/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.queue.QueueCommand;
import com.shopiness.framework.queue.QueueManager;
import com.shopiness.framework.scribe.LogEntry;
import com.shopiness.framework.scribe.ScribeClient;
import com.shopiness.framework.util.DateTimeUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Y Sa
 */
public class ScribeService {

    private static final Logger logger = LogUtil.getLogger(ScribeService.class);

    //LOG_MOBILE_HISTORY
    private static final String LogMobileClickHistory = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s";
    private static final String LogMobileSearchDeal = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s";
    private static final String LogMobileSearchBrand = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s";
    private static final String LogMobileTransaction = "%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s";
    

    class Category {

        private static final String LOG_MOBILE_CASHBACK_HISTORY = "shopiness_api_cashback_history";
        private static final String LOG_MOBILE_SEARCH_DEAL = "shopiness_api_search_deal";
        private static final String LOG_MOBILE_SEARCH_BRAND = "shopiness_api_search_brand";
        private static final String LOG_MOBILE_CASHBACK_TRANSACTION = "shopiness_api_cashback_transaction";
    }

    static final int worker_number = 32;
    static final int worker_size = 1000000;
    public static final QueueManager qm = QueueManager.getInstance("mobi_scribe_log");

    static {
        qm.init(worker_number, worker_size);
        qm.process();
    }

    static class ScribeLogCommand implements QueueCommand {

        private final LogEntry message;

        public ScribeLogCommand(LogEntry message) {
            this.message = message;
        }

        @Override
        public void execute() {
            try {
                List<LogEntry> messages = new ArrayList<>();
                messages.add(message);
                ScribeClient scribe = ScribeClient.getInstance(ConfigInfo.SCRIBE_SERVER);
                if (scribe != null) {
                    boolean check = scribe.log(messages);
                }
            } catch (Exception e) {
                logger.error(LogUtil.stackTrace(e));
            }
        }

    }

    /**
     * log an event to scribe server, used for reporting later then. Up to now,
     * there's no way to log zoneKey and bannerId during a tracking event since
     * jscore doesn't pass these params to api server. It may be updated later!!
     *
     * @param clientIP
     * @param module
     * @param actionName
     * @param historyID
     * @param linkID
     * @param dealID
     * @param userCreated
     * @param status
     */
    public static void logCashBackClickHistory(String clientIP, String module, String actionName, long historyID, long linkID, long dealID, long userCreated, long status) {
        if (ConfigInfo.SCRIBE_ENABLE) {
            String message = String.format(LogMobileClickHistory, clientIP, module, actionName, historyID, linkID, dealID, userCreated, status, DateTimeUtils.getNow());
            logger.info("Push ScribeLog logCashBackClickHistory: " + message);
            LogEntry entry = new LogEntry(Category.LOG_MOBILE_CASHBACK_HISTORY, message);
            qm.put(new ScribeLogCommand(entry));
        }
    }

    public static void logSearchDeal(String clientIP, String module, String actionName, String keyword,
            String listCatID, int dealType, int couponType, int groupType, int areaId, long userCreated) {
        if (ConfigInfo.SCRIBE_ENABLE) {
            String message = String.format(LogMobileSearchDeal, clientIP, module, actionName, keyword,
                    listCatID, dealType, couponType, groupType, areaId, userCreated, DateTimeUtils.getNow());
            logger.info("Push ScribeLog logSearchDeal: " + message);
            LogEntry entry = new LogEntry(Category.LOG_MOBILE_SEARCH_DEAL, message);
            qm.put(new ScribeLogCommand(entry));
        }
    }

    public static void logSearchBrand(String clientIP, String module, String actionName, String keyword,
            String activeDeal, int areaId, String listCatID, long userCreated) {
        if (ConfigInfo.SCRIBE_ENABLE) {
            String message = String.format(LogMobileSearchBrand, clientIP, module, actionName, keyword,
                    actionName, areaId, listCatID, userCreated, DateTimeUtils.getNow());
            logger.info("Push ScribeLog logSearchBrand: " + message);
            LogEntry entry = new LogEntry(Category.LOG_MOBILE_SEARCH_BRAND, message);
            qm.put(new ScribeLogCommand(entry));
        }
    }
    
    public static void logCashbackTransaction(String clientIP, String module, String actionName,
            int value,String accName, String accNum, long bankId,short type, short status, 
            String userName, int currBalance, long userCreated) {
        if (ConfigInfo.SCRIBE_ENABLE) {
            String message = String.format(LogMobileTransaction, clientIP, module, actionName,
                    value,accName,accNum,bankId,type,status,
                    userName,currBalance,userCreated, DateTimeUtils.getNow());
            logger.info("Push ScribeLog logCashbackTransaction: " + message);
            LogEntry entry = new LogEntry(Category.LOG_MOBILE_CASHBACK_TRANSACTION, message);
            qm.put(new ScribeLogCommand(entry));
        }
    }    
}
