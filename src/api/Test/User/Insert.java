package api.Test.User;

import api.configuration.ConfigInfo;
import api.entities.UserViewEnt;
import api.serviceUtils.UserService;
import com.kyt.framework.util.StringUtils;
import constant.User;
import entity.TUserValue;

/**
 * Created by ysa on 11/13/17.
 */
public class Insert {
    public static void main(String[] args) {
        try {
            TUserValue value = new TUserValue();
            value.setAvatarURL("");
            value.setGender(User.GENDER.UNKNOWN);
            value.setAddress("address receiver");
            value.setBirthday(0);
            value.setPassword(StringUtils.doMD5("123456" + ConfigInfo.SECRET_KEY));
            value.setDateCreated(System.currentTimeMillis());
            value.setEmail("requester@ndh.vn");
            value.setUserName("requester");
            value.setFullName("Requester");
            value.setPhone("01222403111");
            value.setSchoolID(1);
            System.out.println(UserService.getInstance().register("requester",new UserViewEnt(value),false));
        }catch (Exception ex){

        }
    }
}
