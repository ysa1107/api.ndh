package api.controller.request;

import api.baseController.BaseController;
import api.configuration.ConfigInfo;
import api.entities.DeviceInfoEnt;
import api.entities.JsonResultEnt;
import api.entities.TokenEnt;
import api.serviceUtils.JWTService;
import api.utils.ErrorCodeUtils;
import com.kyt.framework.config.LogUtil;
import com.kyt.framework.util.ConvertUtils;
import com.kyt.framework.util.DateTimeUtils;
import com.kyt.framework.util.JSONUtil;
import com.kyt.framework.util.StringUtils;
import org.apache.log4j.Logger;
import shopiness.api.jwt.token.model.JwtTokenResult;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Y Sa
 */
public class RequestControllerv1 extends BaseController {

    private static final Logger logger = LogUtil.getLogger(RequestControllerv1.class);

    @Override
    public JsonResultEnt processRequest(String packageControler) {
        JsonResultEnt result;
        logger.info("PARAMS:" + getParams());
        switch (getAction()) {
            case "get-list":
                if (!getMethod().equals("POST")) {
                    return JsonResultEnt.getJsonUnsupportMethod();
                }
                result = getList();

                break;
            default:
                result = JsonResultEnt.getJsonUnsupportMethod();
                break;
        }
        logger.info("RESULT: " + JSONUtil.Serialize(result));
        return result;
    }


        public JsonResultEnt getList() {
            short type = ConvertUtils.toShort(getParams().get("type"));
            String str = "{\"code\":202,\"msgCode\":\"202\",\"data\":[{\"requestId\":1,\"requesterId\":2,\"receiverId\":1,\"type\":0,\"status\":0,\"description\":\"tôi đang ở ngoài đường\",\"dateCreated\":1510544152349,\"rating\":0,\"dateModified\":0},{\"requestId\":1,\"requesterId\":1,\"receiverId\":1,\"type\":0,\"status\":0,\"description\":\"tôi đang ở ngoài đường\",\"dateCreated\":1510544152349,\"rating\":0,\"dateModified\":0}],\"loadmore\":false,\"total\":3}";
            JsonResultEnt result = JSONUtil.DeSerialize(str,JsonResultEnt.class);
        return result;
    }
}
