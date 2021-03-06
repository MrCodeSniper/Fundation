package com.hrz.common.ibase;

/**
 * ================================================
 * 框架要求框架中的每个 Model 都需要实现此类,以满足规范
 * ================================================
 */
public interface IModel {

    /**
     * 在框架中 时会默认调用 {@link IModel#onDestroy()}
     */
    void onDestroy();
}
