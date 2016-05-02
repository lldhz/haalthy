package com.haalthy.service.controller.user;

import com.haalthy.service.JPush.JPushService;
import com.haalthy.service.controller.Interface.ContentStringEapsulate;
import com.haalthy.service.controller.Interface.Response;
import com.haalthy.service.domain.User;
import com.haalthy.service.domain.UserV20160418;
import com.haalthy.service.openservice.UserService;
import com.haalthy.service.openservice.UserServiceV20160418;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ken on 18/4/16.
 */

@Controller
@RequestMapping(headers="api-version=2016-04-18",value="/security/user")
public class UserSecurityControllerV20160418 {

    @Autowired
    private transient UserServiceV20160418 userServiceV20160418;

    @Autowired
    private transient JPushService jPushService;


    @RequestMapping(value = "/update",method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"}, consumes = {"application/json"})
    @ResponseBody
    public Response updateUserV20160418(@RequestBody User user) throws Exception
    {
        Response response = new Response();
        if(userServiceV20160418.updateUserV20160418(user) == 1)
        {
            response.setResult(1);
            response.setResultDesp("更新成功");
            ContentStringEapsulate contentStringEapsulate = new ContentStringEapsulate();
            contentStringEapsulate.setResult(user.getUsername());
            response.setContent(contentStringEapsulate);

            if (user.getDeviceToken() != null){
                jPushService.Login(user.getUsername(),user.getDeviceToken());
            }
        }
        else
        {
            response.setResult(-2);
            response.setResultDesp("数据库连接失败");
            response.setContent(null);
        }
        return response;
    }
}
