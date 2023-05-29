package top.endant.reggie.common;

/**
 * 基于ThreadLocal的工具类
 */
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long geiCurrentId(){
        return threadLocal.get();
    }
}
