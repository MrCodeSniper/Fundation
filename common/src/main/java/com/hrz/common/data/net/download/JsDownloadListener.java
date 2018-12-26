package com.hrz.common.data.net.download;

/**
 * Created by mac on 2018/8/26.
 */

public interface JsDownloadListener {
    void onStartDownload(long length);
    void onProgress(int progress);
    void onFail(String errorInfo);
}
