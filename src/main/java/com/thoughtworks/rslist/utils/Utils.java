package com.thoughtworks.rslist.utils;

import com.thoughtworks.rslist.domain.User;
import java.util.List;

public class Utils {

    public static void isNull(Object object, String msg) {
        if (object == null) {
            throw new RuntimeException(msg);
        }
    }

    public static boolean strIsBlank(String str) {
        if (str == null) return false;
        return !str.isEmpty();
    }


    public static boolean containSameUserInList(User addUser, List<User> userList) {
        for (User u : userList) {
            if (u.getUserName().equals(addUser.getUserName()))
                return true;
        }
        return false;
    }
}
