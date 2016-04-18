package com.haalthy.service.persistence;

import com.haalthy.service.domain.User;

/**
 * Created by ken on 18/4/16.
 */
public interface UserMapperV20160418 {

    int addUserV20160418(User user);


    int updateUserV20160418(User user);

    int checkUserEmailV20160418(String keyword);

    int checkUserPhoneV20160418(String keyword);

    int updateUserEmailorPhoneV20160418(User user);
}
