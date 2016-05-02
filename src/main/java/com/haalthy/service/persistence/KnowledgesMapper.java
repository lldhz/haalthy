package com.haalthy.service.persistence;

import com.haalthy.service.domain.Knowledges;

import java.util.List;

/**
 * Created by Ken-Asus on 2016/5/2.
 */
public interface KnowledgesMapper {
    String addKnowledges(Knowledges knowledges);

    String updateKnowledges(Knowledges knowledges);

    List<Knowledges> selectKnowledgesTitle(Knowledges knowledges);
    List<Knowledges> selectKnowledgesComment(Knowledges knowledges);
    List<Knowledges> selectKnowledgesKeywords(Knowledges knowledges);
}
