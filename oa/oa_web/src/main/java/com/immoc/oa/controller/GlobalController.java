package com.immoc.oa.controller;

import com.immoc.oa.biz.GlobalBiz;
import com.immoc.oa.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller("globalController")
public class GlobalController {
    @Autowired
    private GlobalBiz globalBiz;
    @RequestMapping("/to_login")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/login")
    public String login(HttpSession session, @RequestParam String sn, @RequestParam String password){
        Employee employee = globalBiz.login(sn, password);
        if(employee==null) {
            return "redirect:to_login";
        }
        session.setAttribute("employee",employee);
        return "/self";
    }
    //登录成功之后跳转到个人信息页面
    @RequestMapping("/self")
    public String self(){
        return "self";
    }
    //退出登录
    @RequestMapping("/quit")
    public String quit(HttpSession session){
        session.setAttribute("employee",null);
        return "redirect:to_login";
    }

    //修改用户密码
    @RequestMapping("/to_change_password")
    public String changePassword(){
        return "change_password";
    }

    @RequestMapping("/change_password")
    public String changePassword(HttpSession session, @RequestParam String old, @RequestParam String new1,@RequestParam String new2){
        Employee employee = (Employee)session.getAttribute("employee");
        //如果输入的旧密码正确
        if(employee.getPassword().equals(old)){
            //如果两次输入的新密码一致
            if(new1.equals(new2)){
                employee.setPassword(new1);
                globalBiz.changePassword(employee);
                return "redirect:self";
            }

        }
        return "redirect:to_change_password";

    }




}
