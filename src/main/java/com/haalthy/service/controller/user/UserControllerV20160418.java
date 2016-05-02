package com.haalthy.service.controller.user;

import com.haalthy.service.JPush.JPushService;
import com.haalthy.service.controller.Interface.ContentStringEapsulate;
import com.haalthy.service.controller.Interface.Response;
import com.haalthy.service.domain.User;
import com.haalthy.service.openservice.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;

import java.util.Date;
import java.util.Random;

/**
 * Created by ken on 18/4/16.
 */
@Controller
@RequestMapping(headers="api-version=2016-04-18",value="/open/user")
public class UserControllerV20160418 {

    private String decodePassword(String password){
        String[] codeUnits = password.split("a");
        String passwordDecode = "";
        for(int i = 0; i< codeUnits.length; i++){
            if(!codeUnits[i].equals("")){
                int intCode = Integer.valueOf(codeUnits[i]).intValue();
                char a = (char)intCode;
                passwordDecode += a;
            }
        }
        return passwordDecode;
    }

    @Autowired
    private transient UserServiceV20160418 userServiceV20160418;

    @Autowired
    private transient OssService ossService;

    @Autowired
    private transient JPushService jPushService;

    @Autowired
    private transient AuthCodeService authCodeService;


    private int getRandom() {
        int max = 999;
        int min = 100;
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;
    }

    private String generateUsername(String prefix){
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomInt = String.valueOf(getRandom());
        return prefix+timestamp+"."+randomInt;
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST, headers = "Accept=application/json", produces = {"application/json"}, consumes = {"application/json"})
    @ResponseBody
    public Response addUserV20160418(@RequestBody User user) throws Exception
    {
        Response response = new Response();
        try {
                user.setEmail(null);
            if (user.getPhone() == "")
                user.setPhone(null);

            if(user.getEmail() == null && user.getPhone() == null)
            {
                response.setResult(-5);
                response.setResultDesp("必须通过手机号码或者邮箱地址注册!");
                response.setContent(null);
            }
            else
            {
                if(userServiceV20160418.checkUserEmail(user.getEmail()) >0)
                {
                    response.setResult(-6);
                    response.setResultDesp("邮箱地址已经被注册!");
                    response.setContent(null);
                }
                else {
                    if (userServiceV20160418.checkUserPhone(user.getPhone()) > 0) {
                        response.setResult(-7);
                        response.setResultDesp("手机号码已经被注册!");
                        response.setContent(null);
                    }
                    else
                    {
                        user.setPassword(decodePassword(user.getPassword()));
                        if (user.getUserType().equals("AY")) {
                            String username = generateUsername("AY");
                            user.setUsername(username);
                        }
                        if (user.getUserType().equals("WC")) {
                            String username = generateUsername("WC");
                            user.setUsername(username);
                        }
                        // set encoded password
                        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                        String hashedPassword = passwordEncoder.encode(user.getPassword());
                        user.setPassword(hashedPassword);

                        // set create date and update date
                        Date now = new Date();
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDt = sdf.format(now);
                        user.setCreateDate(currentDt);
                        user.setUpdateDate(currentDt);
                        if (user.getEmail() == "")
                        if(userServiceV20160418.addUserV20160418(user) == 1)
                        {
                            response.setResult(1);
                            response.setResultDesp("注册成功");
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
                    }
                }
            }
        }
        catch (Exception e)
        {
            response.setResult(-3);
            response.setResultDesp("其他异常");
            response.setContent(e);
            //throw e;
        }
        return response;
    }


}
