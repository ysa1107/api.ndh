/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package api.iface;

import api.entities.JsonResultEnt;

/**
 *
 * @author liemtpt
 */
public interface QuestionUserIface {
    
    public JsonResultEnt updateQuestionUserTable();
    
    public JsonResultEnt insertQuestionUser(long questionId, long userId);
    
    public JsonResultEnt deleteQuestionUser(long questionId, long userId);
    
}
