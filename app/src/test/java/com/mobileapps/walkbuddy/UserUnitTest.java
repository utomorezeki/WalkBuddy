package com.mobileapps.walkbuddy;

import com.mobileapps.walkbuddy.models.User;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by kurti on 11/19/2017.
 */

public class UserUnitTest {
    @Test
    public void testGetName() {
        User user = new User("name", "email");

        Assert.assertEquals("name", user.getName());
    }

    @Test
    public void testGetEmail() {
        User user = new User("name", "email");

        Assert.assertEquals("email", user.getEmail());
    }
}
