package com.example.himalaya.base;

public interface IBasePresenter<T> {

    /**
     * 注册UI的回调基础
     * @param t
     */
    void registerViewCallback(T t);

    /**
     * 取消注册UI
     * @param t
     */
    void unRegisterViewCallback(T t);
}
