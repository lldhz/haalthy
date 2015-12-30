package com.haalthy.service.controller.comment;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.haalthy.service.controller.Interface.UnreadCommentRequest;
import com.haalthy.service.controller.Interface.comment.AddCommentRequest;
import com.haalthy.service.controller.Interface.comment.AddUpdateCommentResponse;
import com.haalthy.service.controller.Interface.comment.GetCommentsResponse;
import com.haalthy.service.controller.Interface.comment.GetUnreadCommentCountResponse;
import com.haalthy.service.controller.Interface.comment.MarkCommentsAsReadByUsernameResponse;
import com.haalthy.service.domain.Comment;
import com.haalthy.service.openservice.CommentService;
import com.haalthy.service.openservice.PostService;

@Controller
@RequestMapping("/security/comment")
public class CommentSecurityController {
	@Autowired
	private transient CommentService commentService;
	
	@Autowired
	private transient PostService postService;
		
    @RequestMapping(value = "/add", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public AddUpdateCommentResponse addComment(@RequestBody AddCommentRequest addCommentRequest){
    	AddUpdateCommentResponse addCommentResponse = new AddUpdateCommentResponse();
    	Comment comment = new Comment();
    	comment.setBody(addCommentRequest.getBody());
    	comment.setInsertUsername(addCommentRequest.getInsertUsername());
    	Date now = new Date();
    	Timestamp ts_now = new Timestamp(now.getTime());
    	comment.setDateInserted(ts_now);
    	comment.setIsActive(1);
    	comment.setPostID(addCommentRequest.getPostID());
		try {
			postService.increasePostCountComment(addCommentRequest.getPostID());
			if (commentService.addComment(comment) > 0){
				addCommentResponse.setResult(1);
				addCommentResponse.setResultDesp("返回成功");
			}
		} catch (Exception e) {
			addCommentResponse.setResult(-1);
			addCommentResponse.setResultDesp("数据库连接错误");
		}
    	return addCommentResponse;
    }
    
    @RequestMapping(value = "/inactive/{commentid}", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
	public AddUpdateCommentResponse inactiveComment(@RequestBody Comment comment) {
		AddUpdateCommentResponse updateCommentResponse = new AddUpdateCommentResponse();
		try {
			if (commentService.inactiveComment(comment) != 0) {
				updateCommentResponse.setResult(1);
				updateCommentResponse.setResultDesp("返回成功");
			} else {
				updateCommentResponse.setResult(-2);
				updateCommentResponse.setResultDesp("删除失败");
			}
		} catch (Exception e) {
			updateCommentResponse.setResult(-2);
			updateCommentResponse.setResultDesp("数据库连接错误");
		}
		return updateCommentResponse;
	}
    
    @RequestMapping(value = "/unreadcommentscount", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public GetUnreadCommentCountResponse getUnreadCommentsCount(@RequestBody UnreadCommentRequest unreadCommentReqest){
    	GetUnreadCommentCountResponse getUnreadCommentCountResponse = new GetUnreadCommentCountResponse();
    	try{
    		getUnreadCommentCountResponse.setResult(1);
    		getUnreadCommentCountResponse.setResultDesp("返回成功");
    		getUnreadCommentCountResponse.setUnreadCommentCount(commentService.getUnreadCommentsCount(unreadCommentReqest.getUsername()));
    	}catch(Exception e){
    		getUnreadCommentCountResponse.setResult(-1);
    		getUnreadCommentCountResponse.setResultDesp("数据库连接错误");
    	}
    	return getUnreadCommentCountResponse;
    }
    
    @RequestMapping(value = "/list", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public GetCommentsResponse getCommentsByUsername(@RequestBody UnreadCommentRequest unreadCommentReqest){
    	GetCommentsResponse getCommentsByUsernameResponse = new GetCommentsResponse();
    	try{
    		getCommentsByUsernameResponse.setResult(1);
    		getCommentsByUsernameResponse.setResultDesp("返回成功");
    		getCommentsByUsernameResponse.setComments(commentService.getCommentsByUsername(unreadCommentReqest.getUsername()));
    	}catch(Exception e){
    		getCommentsByUsernameResponse.setResult(-1);
    		getCommentsByUsernameResponse.setResultDesp("数据库连接错误");
    	}
    	return getCommentsByUsernameResponse;
    }
    
    @RequestMapping(value = "/readallcomments", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public MarkCommentsAsReadByUsernameResponse markCommentsAsReadByUsername(@RequestBody UnreadCommentRequest unreadCommentReqest){
    	MarkCommentsAsReadByUsernameResponse markCommentsAsReadByUsernameResponse = new MarkCommentsAsReadByUsernameResponse();
    	try{
    		commentService.markCommentsAsReadByUsername(unreadCommentReqest.getUsername());
    		markCommentsAsReadByUsernameResponse.setResult(1);
    		markCommentsAsReadByUsernameResponse.setResultDesp("返回成功");
    	}catch(Exception e){
    		markCommentsAsReadByUsernameResponse.setResult(-1);
    		markCommentsAsReadByUsernameResponse.setResultDesp("数据库连接错误");
    	}
    	return markCommentsAsReadByUsernameResponse;
    }
}
