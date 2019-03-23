package com.immoc.oa.biz.Impl;

import com.immoc.oa.biz.GlobalBiz;
import com.immoc.oa.dao.EmployeeDao;
import com.immoc.oa.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("globalBiz")
public class GlobalBizImpl implements GlobalBiz
{
    @Autowired
    private EmployeeDao employeeDao;
    public Employee login(String sn, String password) {
        Employee employee = employeeDao.select(sn);
        if(employee!=null && password.equals(employee.getPassword())){
            return employee;
        }
        return null;
    }

    public void changePassword(Employee employee) {
        //要获取到表现层的提交过来的新密码然后直接更新
        employeeDao.update(employee);

    }
}
