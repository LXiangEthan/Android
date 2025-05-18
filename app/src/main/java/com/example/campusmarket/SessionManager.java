package com.example.campusmarket;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    
    // 共享首选项模式
    private static final int PRIVATE_MODE = 0;
    
    // 共享首选项文件名
    private static final String PREF_NAME = "CampusMarketPref";
    
    // 共享首选项键
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "userId";
    
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }
    
    /**
     * 创建登录会话
     */
    public void createLoginSession(String username, long userId) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putLong(KEY_USER_ID, userId);
        editor.commit();
    }
    
    /**
     * 检查用户是否已登录
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * 获取用户名
     */
    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, null);
    }
    
    /**
     * 获取用户ID
     */
    public long getUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, -1);
    }
    
    /**
     * 注销用户
     */
    public void logout() {
        editor.clear();
        editor.commit();
    }
} 