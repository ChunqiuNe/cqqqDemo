package cn.tycoding.common.handle;

import cn.tycoding.common.dto.ResponseCode;
import cn.tycoding.common.enums.StatusEnums;
import cn.tycoding.common.exception.GlobalException;
import cn.tycoding.common.utils.HttpUtil;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 简单处理全局异常信息
 *
 * @author tycoding
 * @date 2019-02-13
 */
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandle {

    @ExceptionHandler(value = Exception.class)
    public ResponseCode exception(Exception e) {
        e.printStackTrace();
        return new ResponseCode(StatusEnums.SYSTEM_ERROR);
    }

    @ExceptionHandler(value = AuthorizationException.class)
    public Object handleAuthorizationException(Exception e, HttpServletRequest request) {
        e.printStackTrace();
        if (HttpUtil.isAjaxRequest(request)) {
            return new ResponseCode(StatusEnums.PERMISSION_ERROR);
        } else {
            ModelAndView view = new ModelAndView();
            view.setViewName("error/403");
            return view;
        }
    }

    @ExceptionHandler(value = GlobalException.class)
    public ResponseCode globalExceptionHandle(GlobalException e, HttpServletResponse response) {
        e.printStackTrace();
        return new ResponseCode(response.getStatus(), e.getMsg());
    }
}