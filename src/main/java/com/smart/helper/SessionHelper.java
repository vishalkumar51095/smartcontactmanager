package com.smart.helper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SessionHelper {

    public void removeMessageFromSession(){

        try{

            System.out.println("removing message from session");

//            HttpSession session=((HttpServletRequest)RequestContextHolder.getRequestAttributes()).getSession();
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            HttpSession session = request.getSession();
            session.removeAttribute("message");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
