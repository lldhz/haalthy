package com.haalthy.service.controller.version;

import com.haalthy.service.common.ConfigLoader;
import com.haalthy.service.controller.Interface.Response;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ken on 18/4/16.
 */
@Controller
@RequestMapping(headers = "api-version=2016-04-18")
public class VersionController {
    final String mapping = "/open/api/version";

    @RequestMapping(value = mapping, method = RequestMethod.GET, headers = "Accept=application/json",
            produces = {"application/json"})
    @ResponseBody
    public Response GetAPIVersion()
    {
        ConfigLoader configLoader = ConfigLoader.getInstance();

        Response response = new Response();
        response.setResult(1);
        response.setResultDesp("获取版本信息成功");
        response.setContent(configLoader.getConfigProperty("api.verson"));
        return response;
    }

}
