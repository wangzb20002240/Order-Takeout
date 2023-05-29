package top.endant.reggie.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import top.endant.reggie.common.BaseContext;
import top.endant.reggie.common.R;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取当前请求路径
        String requestURI = request.getRequestURI();
        //放行路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/code",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs",
        };
        //与放行路径匹配上
        if (check(urls, requestURI)) {
            filterChain.doFilter(request, response);
            log.info("*********请求：{}放行*********", request.getRequestURI());
            return;
        }
        //已登录状态
        if (request.getSession().getAttribute("employee") != null) {
            log.info("*********请求：{}放行*********", request.getRequestURI());

            //设置线程
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));

            filterChain.doFilter(request, response);
            return;
        }
        //TODO：教的方式，直接在放行处加上了用户登录的放行，这样显然是不对的，用户与员工的权限校验肯定有区别，改进方式可能是springCloud？
        if (request.getSession().getAttribute("user") != null) {
            log.info("*********请求：{}放行*********", request.getRequestURI());

            //设置线程
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));

            filterChain.doFilter(request, response);
            return;
        }
        //未登录状态
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.warn("*********请求：{}不放行*********", request.getRequestURI());


    }

    /**
     * 路径匹配
     */
    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)) return true;
        }
        return false;
    }
}
