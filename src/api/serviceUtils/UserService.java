/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;


import api.entities.JsonResultEnt;
import api.entities.UserViewEnt;
import api.iface.UserIface;
import api.utils.ErrorCode;
import api.utils.FunctionUtils;
import bussiness.UserBC;
import com.kyt.framework.config.LogUtil;
import com.kyt.framework.util.StringUtils;
import entity.TListUserResult;
import entity.TUserFilter;
import entity.TUserResult;
import entity.TUserValue;
import org.apache.log4j.Logger;

/**
 *
 * @author Y Sa
 */
public class UserService implements UserIface {

    private static final Logger logger = LogUtil.getLogger(UserService.class);
    public static UserService instance = null;

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public UserService() {

    }


    @Override
    public JsonResultEnt login(String username, String password) {
        JsonResultEnt result = new JsonResultEnt(ErrorCode.LOGIN_UNSUCCESS.getValue());
        try {
            UserBC ubc = new UserBC();
            TUserResult rs = ubc.getUserByUserName(username);
            if (rs != null && rs.getValue() != null) {
                 UserViewEnt user = new UserViewEnt(rs.getValue());
                if (password.equals(rs.getValue().getPassword())) {
                    result = new JsonResultEnt();
                    result.setCode(ErrorCode.SUCCESS.getValue());
                    result.setData(user);
                }
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt resetPassword(String username, String password) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            UserBC ubc = new UserBC();

            TUserResult rs = ubc.getUserByUserName(username);
            if (rs != null && rs.getValue() != null) {
                TUserValue user = rs.getValue();
                user.setPassword(password);
                if (ubc.update(user)) {
                    UserViewEnt userEnt = new UserViewEnt(user);
                    result = new JsonResultEnt();

                    result.setCode(ErrorCode.SUCCESS.getValue());
                    result.setData(userEnt);
                }
            } else {
                result = new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public synchronized JsonResultEnt register(String username, UserViewEnt info, boolean isUpdate) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            UserBC ubc = new UserBC();

            TUserResult rs = ubc.getUserByUserName(username);
            if (isUpdate) {
                if (rs == null || rs.getValue() == null) {
                    return new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
                }

                TUserValue user = rs.getValue();
                user.setFullName(info.fullName);
                user.setEmail(info.email);
                user.setAddress(info.address);
                user.setAvatarURL(info.avatar);
                user.setIdentityCard(info.identityCard);
                user.setSchoolID(info.schoolID);

                if (StringUtils.isEmpty(info.avatar)) {
                    info.avatar = info.avatar.replace(";and;", "&");
                    String avatarUrl = FunctionUtils.downloadAvatar(info.avatar, "user");
                    user.setAvatarURL(avatarUrl);
                }

                if (ubc.update(user)) {
                    UserViewEnt userEnt;
                    userEnt = new UserViewEnt(user);
                    result = new JsonResultEnt();

                    result.setCode(ErrorCode.SUCCESS.getValue());
                    result.setData(userEnt);
                    logger.info("*****RESULT DATA USER ENTITY: " + userEnt + "*******");
                }

            } else {
                if (rs != null && rs.getValue() != null) {
                    return new JsonResultEnt(ErrorCode.USERNAME_EXISTS.getValue());
                }

                TUserValue user = new TUserValue();
                user.setUserName(info.username);
                user.setPassword(info.password);
                user.setFullName(info.fullName);
                user.setEmail(info.email);
                user.setIdentityCard(info.email);
                user.setSchoolID(info.schoolID);

                if (!FunctionUtils.IsNullOrEmpty(info.avatar)) {
                    info.avatar = info.avatar.replace(";and;", "&");
                    String avatarUrl = FunctionUtils.downloadAvatar(info.avatar, "user");
                    user.setAvatarURL(avatarUrl);
                }

                TUserResult userRS = ubc.insert(user);
                if (userRS != null && userRS.getValue() != null) {
                    //Tra ve thong tin user
                    UserViewEnt userEnt = new UserViewEnt(userRS.getValue());
                    result = new JsonResultEnt();
                    result.setCode(ErrorCode.SUCCESS.getValue());
                    result.setData(userEnt);
                    logger.info("*****RESULT DATA USER ENTITY: " + userEnt + "*******");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public JsonResultEnt getUserInfo(String username) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            UserBC ubc = new UserBC();

            TUserResult rs = ubc.getUserByUserName(username);
            if (rs != null && rs.getValue() != null) {
                result = new JsonResultEnt();
                result.setData(new UserViewEnt(rs.getValue()));
                result.setCode(ErrorCode.SUCCESS.getValue());

            } else {
                result = new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt changePassword(String username, String oldPass, String newPass) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            UserBC ubc = new UserBC();

            TUserResult rs = ubc.getUserByUserName(username);
            if (rs != null && rs.getValue() != null) {
                if (!rs.getValue().getPassword().equals(oldPass)) {
                    return new JsonResultEnt(ErrorCode.WRONG_PASSWORD.getValue());
                } else {
                    TUserValue user = rs.getValue();
                    user.setPassword(newPass);
                    if (ubc.update(user)) {
                        result = JsonResultEnt.getJsonSuccess();
                    }
                }
            } else {
                result = new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt updateAvatar(String username, String fileName) {
        logger.info("*****UPDATE AVATAR------username: " + username + "--- fileName: " + fileName);
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        try {
            UserBC ubc = new UserBC();

            TUserResult rs = ubc.getUserByUserName(username);
            if (rs != null && rs.getValue() != null) {
                TUserValue user = rs.getValue();
                user.setAvatarURL(fileName);
                if (ubc.update(user)) {
                    result = new JsonResultEnt();
                    result.setData(new UserViewEnt(rs.getValue()));
                    result.setCode(ErrorCode.SUCCESS.getValue());
                }
            } else {
                result = new JsonResultEnt(ErrorCode.USERNAME_NOT_EXISTS.getValue());
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt getUsers(TUserFilter filter) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();
        logger.info("get-list: ");
        try{
            UserBC ubc = new UserBC();
            TListUserResult userResult= ubc.getUsers(filter);
            result.setData(userResult.listData);
            result.setLoadMore(false);
            result.setTotal(userResult.totalRecords);
        }catch(Exception ex){
            logger.error(ex.getStackTrace());
        }
        return result;
    }

    @Override
    public JsonResultEnt logout(String username, String accessToken) {
        JsonResultEnt result = JsonResultEnt.getJsonSystemError();

        logger.info("logout: " + username + " - " + accessToken);
        try {
            //update with JWT
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return result;
    }

    @Override
    public long getUserIdByUsername(String username) {
        long ret = 0;
        try {
           UserBC ubc = new UserBC();
            TUserResult result = ubc.getUserByUserName(username);
            if (result != null && result.getValue() != null) {
                ret = result.getValue().getUserID();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return ret;
    }


    public TUserValue getUser(long userId) {
        try {
//            UserBC ubc = new UserBC();
//            TUserResult rs = client.getUser(userId);
//            if (rs != null) {
//                return rs.getValue();
//            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return null;
    }

}
