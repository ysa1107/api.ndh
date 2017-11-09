/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.CommentLikeEntList;
import api.entities.JsonResultEnt;
import com.nct.shop.thrift.brand.models.CommentLikeType;
import com.nct.shop.thrift.brand.models.TCommentLikeFilter;
import com.nct.shop.thrift.brand.models.TQuestionFilter;

/**
 *
 * @author liemtpt
 */
public interface CommentIface {
    // Question
    JsonResultEnt getQuestions(TQuestionFilter filter, boolean isFull);
    JsonResultEnt getQuestions(TQuestionFilter filter, boolean isFull, int topAnswer);
    JsonResultEnt addQuestion(long userId, String content, String image,long dealId);
    JsonResultEnt addOrRemovedFavoriteQuestion(long questionId, long userId, boolean isFavorite);
    
    // Answer
    JsonResultEnt getCommentReply(TCommentLikeFilter filter);
    JsonResultEnt replyComment(long userId, String content, String image,long dealId);
    JsonResultEnt removeCommentLikeById(long questionId, long userId, CommentLikeType type);
    CommentLikeEntList getListTopAnswersByQuestionId(TCommentLikeFilter filter);
}
