package com.haalthy.service.controller.post;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

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

import com.haalthy.service.controller.Interface.GetUnreadMentionedPostRequest;
import com.haalthy.service.controller.Interface.ImageInfo;
import com.haalthy.service.controller.Interface.InputUsernameRequest;
import com.haalthy.service.controller.Interface.OSSFile;
import com.haalthy.service.controller.Interface.comment.GetCommentsResponse;
import com.haalthy.service.controller.Interface.post.AddPostRequest;
import com.haalthy.service.controller.Interface.post.AddUpdatePostResponse;
import com.haalthy.service.controller.Interface.post.GetFeedsRequest;
import com.haalthy.service.controller.Interface.post.GetPostResponse;
import com.haalthy.service.controller.Interface.post.GetPostsResponse;
import com.haalthy.service.controller.Interface.post.GetUpdatedPostCountResponse;
import com.haalthy.service.controller.Interface.post.MarkAllMessageAsReadResponse;
import com.haalthy.service.domain.Comment;
import com.haalthy.service.domain.Mention;
import com.haalthy.service.domain.Post;
import com.haalthy.service.domain.PostAndUser;
import com.haalthy.service.domain.PostTag;
import com.haalthy.service.domain.Tag;
import com.haalthy.service.openservice.OssService;
import com.haalthy.service.openservice.PostService;

import com.haalthy.service.configuration.*;

@Controller
@RequestMapping("/security/post")
public class PostSecurityController {
	@Autowired
	private transient PostService postService;
	
	@Autowired
	private transient OssService ossService;
	
	private static final String imageLocation = "/Users/lily/haalthyServer/post/";
	
    @RequestMapping(value = "/add", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public AddUpdatePostResponse addPost(@RequestBody AddPostRequest addPostRequest) throws IOException{
    	AddUpdatePostResponse addPostResponse = new AddUpdatePostResponse();
		try {
			Post post = new Post();
			if (addPostRequest.getBody() != null) {
				post.setBody(addPostRequest.getBody());
			}else{
				post.setBody("");
			}
			post.setClosed(addPostRequest.getClosed());
			// PostType postType = PostType.valueOf(addPostRequest.getType());
			// post.setType(postType.getValue());
			post.setType(0);
			post.setCountBookmarks(0);
			post.setCountComments(0);
			post.setCountViews(0);
			post.setIsActive(1);

			java.util.Date today = new java.util.Date();
			Timestamp now = new java.sql.Timestamp(today.getTime());
			post.setDateInserted(now);
			post.setDateUpdated(now);

			post.setInsertUsername(addPostRequest.getInsertUsername());
			post.setIsBroadcast(addPostRequest.getIsBroadcast());

			if (addPostRequest.getImageInfos() != null) {
				post.setHasImage(addPostRequest.getImageInfos().size());
			} else {
				post.setHasImage(0);
			}

			String tagString = null;
			if (addPostRequest.getTags() != null) {
				Iterator<Tag> tagItr = addPostRequest.getTags().iterator();
				StringBuilder stringBuilder = new StringBuilder();
				while (tagItr.hasNext()) {
					String tag = tagItr.next().getName();
					stringBuilder.append(tag);
					stringBuilder.append("**");
				}
				tagString = stringBuilder.toString();
				post.setTags(tagString);
			}
			int insertPostRow = postService.addPost(post);
			if (addPostRequest.getTags() != null) {
				List<PostTag> postTagList = new ArrayList<PostTag>();
				Iterator<Tag> tagDBItr = addPostRequest.getTags().iterator();
				while (tagDBItr.hasNext()) {
					PostTag postTag = new PostTag();
					postTag.setPostID(post.getPostID());
					postTag.setTagId(tagDBItr.next().getTagId());
					postTag.setCreateTime(now);
					postTagList.add(postTag);
				}
				postService.addPostTag(postTagList);
			}
			if (addPostRequest.getMentionUsers() != null) {
				List<Mention> mentionList = new ArrayList<Mention>();
				Iterator<String> usernameItr = addPostRequest.getMentionUsers().iterator();
				while (usernameItr.hasNext()) {
					Mention mention = new Mention();
					mention.setIsUnRead(1);
					mention.setPostID(post.getPostID());
					String mUsername = usernameItr.next();
					mention.setUsername(mUsername);

					mentionList.add(mention);
				}
				postService.addMention(mentionList);
			}
			List<OSSFile> ossFileList = new ArrayList();
			if (addPostRequest.getImageInfos() != null) {
				Iterator<ImageInfo> imageItr = addPostRequest.getImageInfos().iterator();
				while (imageItr.hasNext()) {
					ImageInfo image = imageItr.next();
					OSSFile ossFile = new OSSFile();
					ossFile.setFileType(image.getType());
					ossFile.setFunctionType("post");
					ossFile.setId(String.valueOf(post.getPostID()));
					ossFile.setImg(image.getData());
					ossFile.setModifyType("append");
					ossFileList.add(ossFile);
				}
				ossService.ossUploadFile(ossFileList);
			}
			if (insertPostRow != 0){
				addPostResponse.setResult(1);
				addPostResponse.setResultDesp("返回成功");
				addPostResponse.setPostId(post.getPostID());
			}
		} catch (Exception e) {
			addPostResponse.setResult(-1);
			addPostResponse.setResultDesp("数据库连接错误");
			System.out.println(e.getMessage());
		}
        return addPostResponse;
    }
    //http://localhost:8080/haalthyservice/security/post/inactive/42?access_token=
    
    @RequestMapping(value = "/inactive/{postid}", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public AddUpdatePostResponse inactivePost(@RequestBody Post post){
    	AddUpdatePostResponse updatePostResponse = new AddUpdatePostResponse();
		try {
			if (postService.inactivePost(post) != 0){
				updatePostResponse.setResult(1);
				updatePostResponse.setResultDesp("返回成功");
//				updatePostResponse.setStatus("inactive successful!");
			}
			else{
				updatePostResponse.setResult(-2);
				updatePostResponse.setResultDesp("此postId不存在");
//				updatePostResponse.setStatus("inactive unsuccessful");
			}
		} catch (Exception e) {
			updatePostResponse.setResult(-1);
			updatePostResponse.setResultDesp("数据库连接错误");
		}
    	return updatePostResponse;
    }
    
    @RequestMapping(value = "/posts", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
	public GetPostsResponse getPosts(@RequestBody GetFeedsRequest getFeedsRequest){
		GetPostsResponse getPostsResponse = new GetPostsResponse();
//		try {
			if (getFeedsRequest.getCount() == 0) {
				getFeedsRequest.setCount(50);
			}
			List<Post> posts = postService.getPosts(getFeedsRequest);
			Iterator<Post> postItr = posts.iterator();
			while (postItr.hasNext()) {
				Post post = postItr.next();
				if (post.getHasImage() != 0) {
					List<byte[]> postImageList = new ArrayList();
					int index = 1;
					// while (index <= post.getHasImage()) {
					// BufferedImage img = null;
					// String path = imageLocation +
					// Integer.toString(post.getPostID()) + "." + index +
					// ".small" + ".jpg";
					// File smallImageFile = new File(path);
					// if (smallImageFile.exists()) {
					// img = ImageIO.read(new File(path));
					// ByteArrayOutputStream baos = new ByteArrayOutputStream();
					// ImageIO.write(img, "jpg", baos);
					// byte[] bytes = baos.toByteArray();
					// postImageList.add(bytes);
					// }
					// index++;
					// }
					// post.setPostImageList(postImageList);
				}
			}
			getPostsResponse.setContent(posts);
			getPostsResponse.setResult(1);
			getPostsResponse.setResultDesp("返回成功");
//		} catch (Exception e) {
//			getPostsResponse.setResult(-1);
//			getPostsResponse.setResultDesp("数据库连接错误");
//		}
 	    return getPostsResponse;
    }
    
    @RequestMapping(value = "/postcount", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
	public GetUpdatedPostCountResponse getUpdatedPostCount(@RequestBody GetFeedsRequest getFeedsRequest){
    	GetUpdatedPostCountResponse getUpdatedPostCountResponse = new GetUpdatedPostCountResponse();
    	try{
    		getUpdatedPostCountResponse.setResult(1);
    		getUpdatedPostCountResponse.setResultDesp("返回成功");
    		getUpdatedPostCountResponse.setCount(postService.getUpdatedPostCount(getFeedsRequest));
    	}catch(Exception e){
    		getUpdatedPostCountResponse.setResult(-1);
    		getUpdatedPostCountResponse.setResultDesp("数据库连接错误");
    	}
    	return getUpdatedPostCountResponse;
    }
    
    @RequestMapping(value = "/posts/{username}", method = RequestMethod.GET, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public GetPostsResponse getPostsByUsername(@PathVariable String username){
    	GetPostsResponse getPostsResponse = new GetPostsResponse();
    	try{
    		getPostsResponse.setContent(postService.getPostsByUsername(username));
    		getPostsResponse.setResult(1);
    		getPostsResponse.setResultDesp("返回成功");
    	}catch(Exception e){
    		getPostsResponse.setResult(-1);
    		getPostsResponse.setResultDesp("数据库连接错误");
    	}
    	return getPostsResponse;
    }
    
    @RequestMapping(value = "/comments", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public GetCommentsResponse getCommentsByUsername(@RequestBody InputUsernameRequest inputUsernameRequest){
    	GetCommentsResponse getCommentsResponse = new GetCommentsResponse();
    	try{
    		getCommentsResponse.setContent(postService.getCommentsByUsername(inputUsernameRequest.getUsername()));
    		getCommentsResponse.setResult(1);
    		getCommentsResponse.setResultDesp("返回成功");
    	}catch(Exception e){
    		getCommentsResponse.setResult(-1);
    		getCommentsResponse.setResultDesp("数据库连接错误");
    	}
    	return getCommentsResponse;
    }
    
    @RequestMapping(value = "/mentionedpost/unreadcount", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
	public int getUnreadMentionedPostCountByUsername(@RequestBody InputUsernameRequest inputUsernameRequest){
    	
		return postService.getUnreadMentionedPostCountByUsername(inputUsernameRequest.getUsername());
	}
	
    @RequestMapping(value = "/mentionedpost/list", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
	public GetPostsResponse getMentionedPostsByUsername(@RequestBody GetFeedsRequest request) throws IOException{
		GetPostsResponse getPostsResponse = new GetPostsResponse();
		try {
			List<Post> posts = postService.getMentionedPostsByUsername(request);
			Iterator<Post> postItr = posts.iterator();
			// while (postItr.hasNext()) {
			// Post post = postItr.next();
			// if (post.getHasImage() != 0) {
			// List<byte[]> postImageList = new ArrayList();
			// int index = 1;
			// while (index <= post.getHasImage()) {
			// BufferedImage img = null;
			// String path = imageLocation + Integer.toString(post.getPostID())
			// + "." + index + ".small" + ".jpg";
			// File smallImageFile = new File(path);
			// if (smallImageFile.exists()) {
			// img = ImageIO.read(new File(path));
			// ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// ImageIO.write(img, "jpg", baos);
			// byte[] bytes = baos.toByteArray();
			// postImageList.add(bytes);
			// }
			// index++;
			// }
			// post.setPostImageList(postImageList);
			// }
			// }
			getPostsResponse.setContent(posts);
			getPostsResponse.setResult(1);
			getPostsResponse.setResultDesp("返回成功");
		} catch (Exception e) {
			getPostsResponse.setResult(-1);
			getPostsResponse.setResultDesp("数据库连接错误");
		}
		return getPostsResponse;
	}
    
    @RequestMapping(value = "/mentionedpost/markasread", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
	public MarkAllMessageAsReadResponse refreshUnreadMentionedPostsByUsername(@RequestBody InputUsernameRequest inputUsernameRequest){
    	MarkAllMessageAsReadResponse markAllMessageAsReadResponse = new MarkAllMessageAsReadResponse();
    	try{
 	   		postService.markMentionedPostAsRead(inputUsernameRequest.getUsername());
 	   		markAllMessageAsReadResponse.setResult(1);
 	   		markAllMessageAsReadResponse.setResultDesp("返回成功");
    	}catch(Exception e){
     	   	markAllMessageAsReadResponse.setResult(-1);
      	    markAllMessageAsReadResponse.setResultDesp("数据库连接错误");
    	}
    	return markAllMessageAsReadResponse;
	}
}
