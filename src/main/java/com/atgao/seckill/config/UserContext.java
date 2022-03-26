package com.atgao.seckill.config;

import com.atgao.seckill.pojo.SysUser;

/**
 * @since 1.0.0
 */
public class UserContext {

	private static ThreadLocal<SysUser> userHolder = new ThreadLocal<SysUser>();

	public static void setUser(SysUser user) {
		userHolder.set(user);
	}

	public static SysUser getUser() {
		return userHolder.get();
	}
}
