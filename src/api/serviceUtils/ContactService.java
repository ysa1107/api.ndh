/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.ContactViewEnt;
import api.entities.JsonResultEnt;
import api.iface.ContactIface;
import api.utils.ErrorCode;
import com.nct.shop.thrift.brand.client.TBrandClient;
import com.nct.shop.thrift.brand.models.ContactObjectType;
import com.nct.shop.thrift.brand.models.ContactType;
import com.nct.shop.thrift.brand.models.TContact;
import com.nct.shop.thrift.brand.models.TContactResult;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.gearman.GClientManager;
import com.shopiness.framework.gearman.JobEnt;
import com.shopiness.framework.util.JSONUtil;
import org.apache.log4j.Logger;

/**
 *
 * @author liemtpt
 */
public class ContactService implements ContactIface {

    private static final Logger logger = LogUtil.getLogger(ContactService.class);
    public static ContactService instance = null;

    public static ContactService getInstance() {
        if (instance == null) {
            instance = new ContactService();
        }
        return instance;
    }

    @Override
    public JsonResultEnt suggestBrandNew(long userId, String name) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            ContactViewEnt vo = null;
            TContact contact = new TContact();
            contact.setUserCreated(userId);
            contact.setContactName(name);
            contact.setType(ContactType.SUGGEST_BRAND);
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TContactResult rs = client.insertContact(contact);
            if (rs != null && rs.value != null) {
                vo = new ContactViewEnt(rs.value, false);
                result.setData(vo);
                result.setCode(ErrorCode.SUCCESS.getValue());
            } else {
                return JsonResultEnt.getJsonSystemError();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt suggestDealForBrand(long userId, long brandId) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            ContactViewEnt vo;
            TContact contact = new TContact();
            contact.setUserCreated(userId);
            contact.setType(ContactType.SUGGEST_DEAL);
            contact.setBrandId(brandId);
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TContactResult rs = client.insertContact(contact);
            if (rs != null && rs.value != null) {
                vo = new ContactViewEnt(rs.value, false);
                result.setData(vo);
                result.setCode(ErrorCode.SUCCESS.getValue());
            } else {
                return JsonResultEnt.getJsonSystemError();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt feedbackCashback(long userId, String code, String email, String content, long historyId) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            ContactViewEnt vo;
            TContact contact = new TContact();
            contact.setUserCreated(userId);
            contact.setType(ContactType.FEEDBACK);
            contact.setBrandId(historyId); //temp for BrandID
            contact.setObjectType(ContactObjectType.CASHBACK);
            contact.setContactName(code);
            contact.setEmail(email);
            contact.setContent(content);
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TContactResult rs = client.insertContact(contact);

            if (rs != null && rs.value != null) {
                vo = new ContactViewEnt(rs.value, false);
                result.setData(vo);
                result.setCode(ErrorCode.SUCCESS.getValue());

                //PUSH SEND MAIL 
                logger.info("feedbackCashback sendMail: " + JSONUtil.Serialize(rs.value));
                JobEnt job = new JobEnt();
                job.Data = JSONUtil.Serialize(rs.value);
                job.ActionType = 1;
                GClientManager.getInstance(ConfigInfo.SENDMAIL_SERVER).push(job);
            } else {
                return JsonResultEnt.getJsonSystemError();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }
}
