package com.immoc.oa.biz;

import com.immoc.oa.entity.Employee;

public interface GlobalBiz {
    //登录方法
    Employee login(String sn,String password);
    //修改密码
    void changePassword(Employee employee);

}
