package com.jfzt.meeting.context;

public class BaseContext {

    public static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    public static ThreadLocal<Integer> threadLocal1 = new ThreadLocal<>();

    public static void setCurrentUserId(String id) {
        threadLocal.set(id);
    }
    public static String getCurrentId() {
        return threadLocal.get();
    }
    public static void setCurrentLevel(Integer level) {
        threadLocal1.set(level);
    }
    public static Integer getCurrentLevel() {
        return threadLocal1.get();
    }

    public static void removeCurrentId() {
        threadLocal.remove();
    }
    public static void removeCurrentLevel() {
        threadLocal1.remove();
    }


}
