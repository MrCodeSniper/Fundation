package com.hrz.common.hybird;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;

public interface IJsBridge {


    /**
     *   JS用法
     *   WebViewJavascriptBridge.registerHandler("functionInJs", function(data, responseCallback) {
     *         document.getElementById("show").innerHTML = ("data from Java: = " + data);
     *         var responseData = "Javascript Says Right back aka!";
     *         responseCallback(responseData);
     *     });
     *
     */


    /**
     * android调JS提供的服务
     * @param handleName 方法名
     * @param paramJson  JSON形式参数
     * @param callBackFunction 回调
     */
    public  void  loadJs(String handleName, String paramJson, CallBackFunction callBackFunction);







    /**
     *  android端注册服务给JS用
     * @param handlerName 服务名
     *@param bridgeHandler 处理回调 返回给js什么
     */
    public void  provideAndroidService(String handlerName,BridgeHandler bridgeHandler);


    /**
     * JS用法
     *  WebViewJavascriptBridge.callHandler(
     *         'submitFromWeb'
     *         , {'param': str1}
     *         , function(responseData) {
     *             document.getElementById("show").innerHTML = "send get responseData from java, data = " + responseData
     *         }
     *     );
     *
     */


    /**
     * JS用法
     *
     *   window.WebViewJavascriptBridge.send(
     *         data
     *         , function(responseData) {
     *             document.getElementById("show").innerHTML = "repsonseData from java, data = " + responseData
     *         }
     *     );
     *
     */

    //提供默认Android服务
    public void provideDefaultService();


    /**
     * JS用法
     *
     *
     *  bridge.init(function(message, responseCallback) {
     *         console.log('JS got a message', message);
     *         var data = {
     *             'Javascript Responds': 'Wee!'
     *         };
     *         console.log('JS responding with', data);
     *         responseCallback(data);
     *     });
     *
     */


    //调用JS默认服务
    public void invokeDefaultJsService(String paramJson);


}
