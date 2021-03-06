package com.haalthy.service.controller.post;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import com.haalthy.service.JPush.JPushService;
import com.haalthy.service.common.ProcessImageURL;
import com.haalthy.service.controller.Interface.ContentIntEapsulate;
import com.haalthy.service.controller.Interface.GetCountResponse;
import com.haalthy.service.controller.Interface.ImageInfo;
import com.haalthy.service.controller.Interface.InputUsernameRequest;
import com.haalthy.service.controller.Interface.OSSFile;
import com.haalthy.service.controller.Interface.comment.GetCommentsResponse;
import com.haalthy.service.controller.Interface.post.AddPostRequest;
import com.haalthy.service.controller.Interface.post.AddUpdatePostResponse;
import com.haalthy.service.controller.Interface.post.AppendImageRequest;
import com.haalthy.service.controller.Interface.post.GetFeedsRequest;
import com.haalthy.service.controller.Interface.post.GetPostsResponse;
import com.haalthy.service.controller.Interface.post.GetUpdatedPostCountResponse;
import com.haalthy.service.controller.Interface.post.MarkAllMessageAsReadResponse;
import com.haalthy.service.domain.Mention;
import com.haalthy.service.domain.PatientStatus;
import com.haalthy.service.domain.Post;
import com.haalthy.service.domain.PostTag;
import com.haalthy.service.domain.Tag;
import com.haalthy.service.openservice.OssService;
import com.haalthy.service.openservice.PatientService;
import com.haalthy.service.openservice.PostService;


@Controller
@RequestMapping("/security/post")
public class PostSecurityController {
	@Autowired
	private transient PostService postService;
	
	@Autowired
	private transient OssService ossService;
	
	@Autowired
	private transient PatientService patientService;
	
    @Autowired
    private transient JPushService jPushService;
    
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
			post.setHasImage(addPostRequest.getHasImage());

			String tagString = null;
			if (addPostRequest.getTags() != null) {
				Iterator<Tag> tagItr = addPostRequest.getTags().iterator();
				StringBuilder stringBuilder = new StringBuilder();
				while (tagItr.hasNext()) {
					String tag = tagItr.next().getName();
					stringBuilder.append(tag);
					stringBuilder.append(", ");
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
					Map<String,String> extras = new HashMap<String, String>();
					extras.put("type", "mentioned");
					extras.put("id", String.valueOf(post.getPostID()));
					int unreadMentionedPostCount = postService.getUnreadMentionedPostCountByUsername(mUsername);
					extras.put("count", String.valueOf(unreadMentionedPostCount));
					jPushService.SendMessageToUser(mUsername, addPostRequest.getInsertUsername(), "您一条新消息@", extras);

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
				ContentIntEapsulate contentIntEapsulate = new ContentIntEapsulate();
				contentIntEapsulate.setCount(post.getPostID());
				addPostResponse.setContent(contentIntEapsulate);
			}
		} catch (Exception e) {
			e.printStackTrace();
			addPostResponse.setResult(-1);
			addPostResponse.setResultDesp("数据库连接错误");
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
			}
			else{
				updatePostResponse.setResult(-2);
				updatePostResponse.setResultDesp("此postId不存在");
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
		try {
			if (getFeedsRequest.getCount() == 0) {
				getFeedsRequest.setCount(50);
			}
			getFeedsRequest.setBeginIndex(getFeedsRequest.getCount() * getFeedsRequest.getPage());
			List<Post> posts = postService.getPosts(getFeedsRequest);
			for (Post post : posts){
				if (post.getType() == 2) {
					PatientStatus patientStatus = patientService.getPatientStatusById(post.getPatientStatusID());
					if ((patientStatus != null) && (patientStatus.getImageURL() != null)){
						post.setImageURL(patientStatus.getImageURL());
					}
				}
				if (post.getImageURL() != null){
					ProcessImageURL processImageURL = new ProcessImageURL();
					post.setImageURL(processImageURL.processImageURL(post.getImageURL()));
				}
			}
			getPostsResponse.setContent(posts);
			getPostsResponse.setResult(1);
			getPostsResponse.setResultDesp("返回成功");
		} catch (Exception e) {
			e.printStackTrace();
			getPostsResponse.setResult(-1);
			getPostsResponse.setResultDesp("数据库连接错误");
		}
 	    return getPostsResponse;
    }
    
    @RequestMapping(value = "/postcount", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
	public GetUpdatedPostCountResponse getUpdatedPostCount(@RequestBody GetFeedsRequest getFeedsRequest){
    	GetUpdatedPostCountResponse getUpdatedPostCountResponse = new GetUpdatedPostCountResponse();
    	try{
    		getUpdatedPostCountResponse.setResult(1);
    		getUpdatedPostCountResponse.setResultDesp("返回成功");
    		ContentIntEapsulate contentIntEapsulate = new ContentIntEapsulate();
			getFeedsRequest.setBeginIndex(getFeedsRequest.getCount() * getFeedsRequest.getPage());
    		contentIntEapsulate.setCount(postService.getUpdatedPostCount(getFeedsRequest));
    		getUpdatedPostCountResponse.setContent(contentIntEapsulate);
    	}catch(Exception e){
    		getUpdatedPostCountResponse.setResult(-1);
    		getUpdatedPostCountResponse.setResultDesp("数据库连接错误");
    	}
    	return getUpdatedPostCountResponse;
    }
    
    @RequestMapping(value = "/posts/username", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public GetPostsResponse getPostsByUsername(@RequestBody GetFeedsRequest getFeedsRequest){
    	GetPostsResponse getPostsResponse = new GetPostsResponse();
    	try{
    		getFeedsRequest.setBeginIndex(getFeedsRequest.getCount() * getFeedsRequest.getPage());
    		List<Post> posts = postService.getPostsByUsername(getFeedsRequest);
			for (Post post : posts){
				if (post.getType() == 2) {
					PatientStatus patientStatus = patientService.getPatientStatusById(post.getPatientStatusID());
					if ((patientStatus != null) && (patientStatus.getImageURL() != null)){
						post.setImageURL(patientStatus.getImageURL());
					}
				}
				if (post.getImageURL() != null){
					ProcessImageURL processImageURL = new ProcessImageURL();
					post.setImageURL(processImageURL.processImageURL(post.getImageURL()));
				}
			}
    		getPostsResponse.setContent(posts);
    		getPostsResponse.setResult(1);
    		getPostsResponse.setResultDesp("返回成功");
    	}catch(Exception e){
    		e.printStackTrace();
    		getPostsResponse.setResult(-1);
    		getPostsResponse.setResultDesp("数据库连接错误");
    	}
    	return getPostsResponse;
    }
    
    @RequestMapping(value = "/comments", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
    public GetCommentsResponse getCommentsByUsername(@RequestBody GetFeedsRequest getFeedsRequest){
    	GetCommentsResponse getCommentsResponse = new GetCommentsResponse();
    	try{
    		getFeedsRequest.setBeginIndex(getFeedsRequest.getCount() * getFeedsRequest.getPage());
    		getCommentsResponse.setContent(postService.getCommentsByUsername(getFeedsRequest));
    		getCommentsResponse.setResult(1);
    		getCommentsResponse.setResultDesp("返回成功");
    	}catch(Exception e){
    		e.printStackTrace();
    		getCommentsResponse.setResult(-1);
    		getCommentsResponse.setResultDesp("数据库连接错误");
    	}
    	return getCommentsResponse;

    }
    
    @RequestMapping(value = "/mentionedpost/unreadcount", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
	public GetCountResponse getUnreadMentionedPostCountByUsername(@RequestBody InputUsernameRequest inputUsernameRequest){
    	GetCountResponse getCountResponse = new GetCountResponse();
    	try{
    		ContentIntEapsulate contentIntEapsulate = new ContentIntEapsulate();
    		contentIntEapsulate.setCount(postService.getUnreadMentionedPostCountByUsername(inputUsernameRequest.getUsername()));
    		getCountResponse.setContent(contentIntEapsulate);
    		getCountResponse.setResult(1);
    		getCountResponse.setResultDesp("返回成功");
    	}catch(Exception e){
    		e.printStackTrace();
    		getCountResponse.setResult(-1);
    		getCountResponse.setResultDesp("数据库连接错误");
    	}
		return getCountResponse;

	}
	
    @RequestMapping(value = "/mentionedpost/list", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
	public GetPostsResponse getMentionedPostsByUsername(@RequestBody GetFeedsRequest request) throws IOException{
		GetPostsResponse getPostsResponse = new GetPostsResponse();
		try {
			request.setBeginIndex(request.getCount() * request.getPage());
			List<Post> posts = postService.getMentionedPostsByUsername(request);
			for (Post post : posts){
				if (post.getType() == 2) {
					PatientStatus patientStatus = patientService.getPatientStatusById(post.getPatientStatusID());
					if ((patientStatus != null) && (patientStatus.getImageURL() != null)){
						post.setImageURL(patientStatus.getImageURL());
					}
				}
				if (post.getImageURL() != null){
					ProcessImageURL processImageURL = new ProcessImageURL();
					post.setImageURL(processImageURL.processImageURL(post.getImageURL()));
				}
			}
			getPostsResponse.setContent(posts);
			getPostsResponse.setResult(1);
			getPostsResponse.setResultDesp("返回成功");
		} catch (Exception e) {
			e.printStackTrace();
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
    
    @RequestMapping(value = "/appendimage", method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"})
    @ResponseBody
	public AddUpdatePostResponse appendImage(@RequestBody AppendImageRequest appendImageToPostRequest){
    	AddUpdatePostResponse addUpdatePostResponse = new AddUpdatePostResponse();
    	ContentIntEapsulate contentIntEapsulate = new ContentIntEapsulate();
    	try{
    		Post post = postService.getPostById(appendImageToPostRequest.getId());
    		if (postService == null) {
    			contentIntEapsulate.setCount(-2);
    			addUpdatePostResponse.setContent(contentIntEapsulate);
    			addUpdatePostResponse.setResult(-2);
    			addUpdatePostResponse.setResultDesp("该postId不存在");
    		}else{
				OSSFile ossFile = new OSSFile();
				ossFile.setFileType(appendImageToPostRequest.getImageInfo().getType());
				ossFile.setFunctionType("post");
				ossFile.setImg(appendImageToPostRequest.getImageInfo().getData());
				ossFile.setModifyType("append");
				ossFile.setId(String.valueOf(post.getPostID()));
				if (post.getImageURL() == null){
					ossService.ossUploadSingleFile(ossFile, appendImageToPostRequest.getImageIndex(), "update");
				}else{
					ossService.ossUploadSingleFile(ossFile, appendImageToPostRequest.getImageIndex(), "append");
				}
    			contentIntEapsulate.setCount(1);
    			addUpdatePostResponse.setContent(contentIntEapsulate);
    			addUpdatePostResponse.setResult(1);
    			addUpdatePostResponse.setResultDesp("插入成功");
    		}
    	}catch(Exception e){
    		e.printStackTrace();
			contentIntEapsulate.setCount(-1);
			addUpdatePostResponse.setContent(contentIntEapsulate);
			addUpdatePostResponse.setResult(-1);
			addUpdatePostResponse.setResultDesp("系统异常");
    	}
    	return addUpdatePostResponse;
	}
    
}
