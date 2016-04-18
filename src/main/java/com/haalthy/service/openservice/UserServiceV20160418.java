package com.haalthy.service.openservice;

import com.haalthy.service.domain.User;
import com.haalthy.service.persistence.UserMapperV20160418;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ken on 18/4/16.
 */

@Service
public class UserServiceV20160418 {
    @Autowired
    private UserMapperV20160418 userMapperV20160418;

    public int addUserV20160418(User user)
    {
        return userMapperV20160418.addUserV20160418(user);
    }

    public int updateUserV20160418(User user)
    {
        return userMapperV20160418.updateUserV20160418(user);
    }

    public int checkUserEmail(String keyword)
    {
        return userMapperV20160418.checkUserEmailV20160418(keyword);
    }

    public int checkUserPhone(String keyword)
    {
        return userMapperV20160418.checkUserPhoneV20160418(keyword);
    }
}
