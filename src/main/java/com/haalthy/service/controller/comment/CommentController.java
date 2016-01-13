package com.haalthy.service.controller.comment;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haalthy.service.controller.Interface.IntRequest;
import com.haalthy.service.controller.Interface.comment.GetCommentsByPostIdResponse;
import com.haalthy.service.domain.Comment;
import com.haalthy.service.openservice.CommentService;

@Controller
@RequestMapping("/open/comment")
public class CommentController {
	@Autowired
	private transient CommentService commentService;
	
    @RequestMapping(value = "/post", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public GetCommentsByPostIdResponse getCommentsByPostId(@RequestBody IntRequest postid){
    	GetCommentsByPostIdResponse getCommentsByPostIdResponse = new GetCommentsByPostIdResponse();
    	try{
    		List<Comment> comments = commentService.getCommentsByPostId(postid.getId());
    		if (comments.size() > 0){
        		getCommentsByPostIdResponse.setResult(1);
        		getCommentsByPostIdResponse.setResultDesp("返回成功");
        		getCommentsByPostIdResponse.setContent(comments);
    		}else{
        		getCommentsByPostIdResponse.setResult(-2);
        		getCommentsByPostIdResponse.setResultDesp("无评论");
    		}
    	}catch(Exception e){
    		getCommentsByPostIdResponse.setResult(-1);
    		getCommentsByPostIdResponse.setResultDesp("数据库连接错误");
    	}
    	return getCommentsByPostIdResponse;
    }
}
