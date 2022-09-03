package com.airxiechao.y20.common.pojo.constant.auth;

/**
 * 单个权限满足以下条件：
 * - 每个权限级别按序号排序
 * - 高级权限拥有低级权限
 * - 高级权限序号大于所有低于它级别的权限之和
 *
 * 组合权限有如下性质：
 * - 是否拥有权限只需要比较序号大小
 */
public class EnumAccessMode {

    public static final int R = 1;
    public static final int W = 2;
    public static final int X = 4;
}
