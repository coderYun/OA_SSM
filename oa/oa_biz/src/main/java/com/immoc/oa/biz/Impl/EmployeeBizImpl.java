package com.immoc.oa.biz.Impl;
import com.immoc.oa.biz.EmployeeBiz;
import com.immoc.oa.dao.EmployeeDao;
import com.immoc.oa.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service("employeeBiz")
public class EmployeeBizImpl implements EmployeeBiz {
    @Autowired
    private EmployeeDao employeeDao;

    public void add(Employee employee) {
        employee.setPassword("000000");
        employeeDao.insert(employee);
    }

    public void edit(Employee employee) {
        employeeDao.update(employee);

    }

    public void remove(String sn) {
        employeeDao.delete(sn);
    }

    public Employee get(String sn) {
        Employee employee = employeeDao.select(sn);
        return employee;
    }

    public List<Employee> getAll() {
        return employeeDao.selectAll();
    }
}
