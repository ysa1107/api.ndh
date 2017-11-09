/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.serviceUtils;

import api.configuration.ConfigInfo;
import api.entities.CommentLikeEntList;
import api.entities.CommentLikeViewEnt;
import api.entities.QuestionViewEnt;
import api.entities.JsonResultEnt;
import api.utils.ErrorCode;
import com.nct.shop.thrift.brand.client.TBrandClient;
import com.nct.shop.thrift.brand.models.TQuestion;
import com.nct.shop.thrift.brand.models.TQuestionFilter;
import com.nct.shop.thrift.brand.models.TQuestionListResult;
import com.nct.shop.thrift.brand.models.TQuestionResult;
import com.nct.shop.thrift.deal.client.TUserClient;
import com.nct.shop.thrift.deal.models.TUser;
import com.nct.shop.thrift.deal.models.TUserFilter;
import com.nct.shop.thrift.deal.models.TUserListResult;
import java.util.ArrayList;
import java.util.List;
import api.iface.CommentIface;
import com.nct.shop.thrift.brand.models.CommentLikeType;
import com.nct.shop.thrift.brand.models.TCommentLike;
import com.nct.shop.thrift.brand.models.TCommentLikeFilter;
import com.nct.shop.thrift.brand.models.TCommentLikeListResult;
import com.nct.shop.thrift.brand.models.TCommentLikeResult;
import com.shopiness.framework.common.LogUtil;
import com.shopiness.framework.gearman.GClientManager;
import com.shopiness.framework.gearman.JobEnt;
import com.shopiness.framework.util.JSONUtil;
import com.shopiness.notification.service.GearmanClient;
import noti.entity.PushedNotificationEnt;
import org.apache.log4j.Logger;

/**
 *
 * @author liemtpt
 */
public class CommentService implements CommentIface {

    public static CommentService instance = null;
    private static final Logger logger = LogUtil.getLogger(CommentService.class);

    public static CommentService getInstance() {
        if (instance == null) {
            instance = new CommentService();
        }
        return instance;
    }

    @Override
    public JsonResultEnt getQuestions(TQuestionFilter filter, boolean isFull) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TQuestionListResult rs = client.getQuestions(filter);
            List<QuestionViewEnt> lstQuestions = new ArrayList<>();
            long totalRecords = 0;
            boolean loadMore = false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                List<Long> lstUser = new ArrayList<>();
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TQuestion question = rs.getListData().get(i);
                    QuestionViewEnt vo = new QuestionViewEnt(question, isFull, filter.getUserId(), filter.getPageSize());
                    lstQuestions.add(vo);
                    lstUser.add(vo.userCreated);
                }

                // USER 
                TUserClient client2 = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
                TUserFilter ufilter = new TUserFilter();
                ufilter.setListUserId(lstUser);
                TUserListResult userRS = client2.getUsers(ufilter);
                if (userRS != null && userRS.listData != null && userRS.listData.size() > 0) {
                    for (QuestionViewEnt item : lstQuestions) {
                        for (TUser user : userRS.listData) {
                            if (item.userCreated == user.getUserId()) {
                                item.userComment = user.getFullName();
                                break;
                            }
                        }
                    }
                }

                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadMore = true;
                }
            }

            result.setData(lstQuestions);
            result.setTotal(totalRecords);
            result.setLoadMore(loadMore);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt getQuestions(TQuestionFilter filter, boolean isFull, int topAnswer) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TQuestionListResult rs = client.getQuestions(filter);
            List<QuestionViewEnt> lstQuestions = new ArrayList<>();
            long totalRecords = 0;
            boolean loadMore = false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                List<Long> lstUser = new ArrayList<>();
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TQuestion question = rs.getListData().get(i);
                    QuestionViewEnt vo = new QuestionViewEnt(question, isFull, filter.getUserId(), topAnswer);
                    lstQuestions.add(vo);
                    lstUser.add(vo.userCreated);
                }
                // USER 
                TUserClient client2 = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
                TUserFilter ufilter = new TUserFilter();
                ufilter.setListUserId(lstUser);
                TUserListResult userRS = client2.getUsers(ufilter);
                if (userRS != null && userRS.listData != null && userRS.listData.size() > 0) {
                    for (QuestionViewEnt item : lstQuestions) {
                        for (TUser user : userRS.listData) {
                            if (item.userCreated == user.getUserId()) {
                                item.userComment = user.getFullName();
                                break;
                            }
                        }
                    }
                }

                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadMore = true;
                }
            }

            result.setData(lstQuestions);
            result.setTotal(totalRecords);
            result.setLoadMore(loadMore);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

//    @Override
//    public JsonResultEnt addOrRemovedFavoriteQuestion(long questionId, long userId, boolean isFavorite) {
//        JsonResultEnt result = new JsonResultEnt();
//        try {
//            String userIDs;
//            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
//            TQuestionResult rs = client.getQuestion(questionId);
//            boolean kq1, kq2 = false;
//            if (rs.value == null) {
//                logger.error(LogUtil.stackTrace(e));
//            }
//            TQuestion question = rs.value;
//            if (isFavorite) {
//                // like = add item in field user_ids in question's table
//                userIDs = FunctionUtils.addStringIDs(question.getUserIDs(), userId);
//                question.setUserIDs(userIDs);
//                kq1 = client.updateQuestion(question);
//                // like = insert 1 record in comment_like's table
//                TCommentLike cmt = new TCommentLike();
//                cmt.setUserCreated(userId);
//                cmt.setQuestionId(question.questionId);
//                cmt.setType(CommentLikeType.LIKE);
//                TBrandClient bClient = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
//                TCommentLikeResult brs = bClient.insertCommentLike(cmt);
//                TQuestionResult qRs = client.getQuestion(questionId);
//                if (brs != null && brs.value != null) {
//                    kq2 = true;
//                }
//                if (kq1 && kq2) {
//                    result = JsonResultEnt.getJsonSuccess();
//                    //Push notify
//                    if (qRs != null && qRs.value != null && qRs.value.getUserCreated() > 0 && qRs.value.getUserCreated() != userId) {
//                        PushedNotificationEnt pushed = new PushedNotificationEnt();
//                        pushed.userCreated = userId;
//                        pushed.likeCommentType = CommentLikeType.LIKE.getValue();
//                        pushed.questionId = questionId;
//                        //tao job
//                        JobEnt job = new JobEnt();
//                        job.ActionType = noti.utils.Constant.PUSH_NOTIFY_LIKE_COMMENT;
//                        job.Timestamp = System.currentTimeMillis();
//                        job.Data = JSONUtil.Serialize(pushed);
//                        boolean kq = GClientManager.getInstance(noti.config.ConfigInfo.GEARMAN_SERVER).push(job);
//                        logger.info("Send push comment question result: " + kq);
//                    }
//
//                } else {
//                    result = JsonResultEnt.getJsonSystemError();
//                }
//            } else {
//                // unlike = remove item in field user_ids in question's table
//                userIDs = FunctionUtils.removedStringIDs(question.getUserIDs(), userId);
//                question.setUserIDs(userIDs);
//                kq1 = client.updateQuestion(question);
//                // unlike = delete 1 record in comment_like's table
//                TBrandClient bClient = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
//                TCommentLikeResult brs = client.getCommentLikeByUserId(questionId, userId, CommentLikeType.LIKE);
//                if (brs != null && brs.value != null) {
//                    kq2 = bClient.deleteCommentLike(brs.value.id);
//                }
//                if (kq1 && kq2) {
//                    result = JsonResultEnt.getJsonSuccess();
//                } else {
//                    result = JsonResultEnt.getJsonSystemError();
//                }
//            }
//        } catch (Exception ex) {
//            logger.error(LogUtil.stackTrace(e));
//        }
//        return result;
//    }
    @Override
    public JsonResultEnt addOrRemovedFavoriteQuestion(long questionId, long userId, boolean isFavorite) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TCommentLikeResult rs = client.getCommentLikeByUserId(questionId, userId, CommentLikeType.LIKE);
            boolean kq = false;
            if (isFavorite) {
                if (rs == null || rs.value == null) {
                    // like = insert new record in comment_like's table
                    TCommentLike cmt = new TCommentLike();
                    cmt.setUserCreated(userId);
                    cmt.setQuestionId(questionId);
                    cmt.setType(CommentLikeType.LIKE);
                    TCommentLikeResult brs = client.insertCommentLike(cmt);
                    if (brs != null && brs.value != null) {
                        result = JsonResultEnt.getJsonSuccess();
                        //Push notify
                        TQuestionResult qRs = client.getQuestion(questionId);
                        if (qRs != null && qRs.value != null && qRs.value.getUserCreated() > 0 && qRs.value.getUserCreated() != userId) {
                            PushedNotificationEnt pushed = new PushedNotificationEnt();
                            pushed.userCreated = userId;
                            pushed.likeCommentType = CommentLikeType.LIKE.getValue();
                            pushed.questionId = questionId;
                            //tao job
                            JobEnt job = new JobEnt();
                            job.ActionType = noti.utils.Constant.PUSH_NOTIFY_LIKE_COMMENT;
                            job.Timestamp = System.currentTimeMillis();
                            job.Data = JSONUtil.Serialize(pushed);
                            boolean kqPush = GClientManager.getInstance(noti.config.ConfigInfo.GEARMAN_SERVER).push(job);
                            logger.info("Send push comment question result: " + kqPush);
                            
                            //push v2 
                            GearmanClient pushv2Client = GearmanClient.getInstance(ConfigInfo.GEARMAN_PUSH_V2_SERVER);
                            com.shopiness.notification.ent.PushedNotificationEnt ent2 = new com.shopiness.notification.ent.PushedNotificationEnt();
                            ent2.userCreated = userId;
                            ent2.likeCommentType = CommentLikeType.LIKE.getValue();
                            ent2.questionId = questionId;
                            logger.info("Send push comment question result v2: " + pushv2Client.notifyLikeComment(ent2));
                            
                        }
                    } else {
                        result = JsonResultEnt.getJsonSystemError();
                    }
                } else {
//                    result.setCode(0);
//                    result.setMsgCode("0");
                    return result;
                }
            } else if (rs != null && rs.value != null && rs.value.getId() > 0) {
                // unlike = delete record in comment_like's table
                kq = client.deleteCommentLike(rs.value.getId());
                if (kq) {
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
    public JsonResultEnt addQuestion(long userId, String content, String imageUrl, long dealId) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TQuestion question = new TQuestion();
            question.setDealId(dealId);
            question.setContent(content);
            question.setUserCreated(userId);
            question.setImage(imageUrl);
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TQuestionResult rs = client.insertQuestion(question);
            QuestionViewEnt vo;
            if (rs != null && rs.value != null) {
                vo = new QuestionViewEnt(rs.value, false);
            } else {
                return JsonResultEnt.getJsonSystemError();
            }
            result.setData(vo);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt getCommentReply(TCommentLikeFilter filter) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TCommentLikeListResult rs = client.getCommentLikes(filter);
            List<CommentLikeViewEnt> lstAnswers = new ArrayList<>();
            long totalRecords = 0;
            boolean loadMore = false;
            if (rs.getListData() != null && rs.getListData().size() > 0) {
                List<Long> lstUser = new ArrayList<>();
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TCommentLike cmt = rs.getListData().get(i);
                    CommentLikeViewEnt vo = new CommentLikeViewEnt(cmt);
                    lstAnswers.add(vo);
                    lstUser.add(vo.userCreated);
                }
                // USER 
                TUserClient client2 = TUserClient.getInstance(ConfigInfo.SERVER_USER_DEAL);
                TUserFilter ufilter = new TUserFilter();
                ufilter.setListUserId(lstUser);
                TUserListResult userRS = client2.getUsers(ufilter);
                if (userRS != null && userRS.listData != null && userRS.listData.size() > 0) {
                    for (CommentLikeViewEnt item : lstAnswers) {
                        for (TUser user : userRS.listData) {
                            if (item.userCreated == user.getUserId()) {
                                item.userComment = user.getFullName();
                                break;
                            }
                        }
                    }
                }
                totalRecords = rs.getTotalRecord();
                if (filter.getPageSize() * filter.getPageIndex() < totalRecords) {
                    loadMore = true;
                }
            }
            result.setData(lstAnswers);
            result.setTotal(totalRecords);
            result.setLoadMore(loadMore);
            result.setCode(ErrorCode.SUCCESS.getValue());
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public CommentLikeEntList getListTopAnswersByQuestionId(TCommentLikeFilter filter) {
        CommentLikeEntList lstEnt = new CommentLikeEntList();
        List<CommentLikeViewEnt> lstAns = new ArrayList<>();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TCommentLikeListResult rs = client.getCommentLikes(filter);
            if (rs != null && rs.getListData() != null && rs.getListData().size() > 0) {
                for (int i = 0; i < rs.getListData().size(); i++) {
                    TCommentLike cmt = rs.getListData().get(i);
                    CommentLikeViewEnt vo = new CommentLikeViewEnt(cmt);
                    lstAns.add(vo);
                }
                lstEnt = new CommentLikeEntList(lstAns, rs.totalRecord);
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return lstEnt;
    }

    @Override
    public JsonResultEnt replyComment(long userId, String content, String imageUrl, long questionId) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            CommentLikeViewEnt vo;
            // comment = insert new record in comment_like's table
            TCommentLike cmt = new TCommentLike();
            cmt.setQuestionId(questionId);
            cmt.setType(CommentLikeType.COMMENT);
            cmt.setContent(content);
            cmt.setUserCreated(userId);
//            cmt.setImage(imageUrl);
            TCommentLikeResult rs = client.insertCommentLike(cmt);
            if (rs != null && rs.value != null) {
                vo = new CommentLikeViewEnt(rs.value);
                result.setData(vo);
                result.setCode(ErrorCode.SUCCESS.getValue());
                //Push notify
                TQuestionResult qRs = client.getQuestion(questionId);
                if (qRs != null && qRs.value != null && qRs.value.getUserCreated() > 0 && qRs.value.getUserCreated() != userId) {
                    PushedNotificationEnt pushed = new PushedNotificationEnt();
                    pushed.userCreated = userId;
                    pushed.likeCommentType = CommentLikeType.COMMENT.getValue();
                    pushed.questionId = questionId;
                    //tao job 
                    JobEnt job = new JobEnt();
                    job.ActionType = noti.utils.Constant.PUSH_NOTIFY_LIKE_COMMENT;
                    job.Timestamp = System.currentTimeMillis();
                    job.Data = JSONUtil.Serialize(pushed);
                    boolean kqPush = GClientManager.getInstance(noti.config.ConfigInfo.GEARMAN_SERVER).push(job);
                    logger.info("Send push comment question result: " + kqPush);
                    
                    //push v2 
                    GearmanClient pushv2Client = GearmanClient.getInstance(ConfigInfo.GEARMAN_PUSH_V2_SERVER);
                    com.shopiness.notification.ent.PushedNotificationEnt ent2 = new com.shopiness.notification.ent.PushedNotificationEnt();
                    ent2.userCreated = userId;
                    ent2.likeCommentType = CommentLikeType.COMMENT.getValue();
                    ent2.questionId = questionId;
                    logger.info("Send push comment question result v2: " + pushv2Client.notifyLikeComment(ent2));
                }
            } else {
                return JsonResultEnt.getJsonSystemError();
            }
        } catch (Exception e) {
            logger.error(LogUtil.stackTrace(e));
        }
        return result;
    }

    @Override
    public JsonResultEnt removeCommentLikeById(long questionId, long userId, CommentLikeType type) {
        JsonResultEnt result = new JsonResultEnt();
        try {
            TBrandClient client = TBrandClient.getInstance(ConfigInfo.SERVER_BRAND_THRIFT);
            TCommentLikeResult rs = client.getCommentLikeByUserId(questionId, userId, type);
            if (rs != null && rs.value != null) {
                boolean kq = client.deleteCommentLike(rs.value.id);
                if (kq) {
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
}
