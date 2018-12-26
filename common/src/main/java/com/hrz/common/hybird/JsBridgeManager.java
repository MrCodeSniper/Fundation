package com.hrz.common.hybird;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;

/**
 * //PS: https://blog.csdn.net/c10wtiybq1ye3/article/details/79160542
 *
 * 这个JSBRIGE会注册一个对象到window上
 * JS使用前需要判断这个对象是否存在 如果不存在 则监听他存在时的回调 后再做操作
 *
 *  if (window.WebViewJavascriptBridge) {
 *         //do your work here
 *     } else {
 *         document.addEventListener(
 *             'WebViewJavascriptBridgeReady'
 *             , function() {
 *                 //do your work here
 *             },
 *             false
 *         );
 *     }
 *
 */
public class JsBridgeManager implements IJsBridge {

    public BridgeWebView mBridgeWebView;

    public JsBridgeManager(BridgeWebView mBridgeWebView) {
        this.mBridgeWebView=mBridgeWebView;
    }

    @Override
    public void loadJs(String handleName, String paramJson, CallBackFunction callBackFunction) {
        mBridgeWebView.callHandler(handleName, paramJson,callBackFunction);
    }

    @Override
    public void provideAndroidService(String handlerName,BridgeHandler handler) {
        mBridgeWebView.registerHandler(handlerName, handler);
    }

    @Override
    public void provideDefaultService() {
        DefaultHandler defaultHandler=new DefaultHandler();
        mBridgeWebView.setDefaultHandler(defaultHandler);
    }

    @Override
    public void invokeDefaultJsService(String paramJson) {
        mBridgeWebView.send(paramJson);
    }
}


