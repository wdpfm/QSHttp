package org.song.http;


import org.song.http.framework.HttpEnum;
import org.song.http.framework.Interceptor;
import org.song.http.framework.QSHttpConfig;
import org.song.http.framework.QSHttpManage;
import org.song.http.framework.RequestParams;

/*
 * Created by song on 2016/9/26.
 * 辅助构建类,简化调用代码
 */
public class QSHttp {

    /**
     * 使用前进行初始化
     * 才能支持缓存 cookie ssl证书 网络状态判断
     */
    public static void init(QSHttpConfig qsHttpConfig) {
        QSHttpManage.init(qsHttpConfig);
    }

    /**
     * 拦截器,可用来添加全局请求参数
     */
    public static void setInterceptor(Interceptor interceptor) {
        QSHttpManage.setInterceptor(interceptor);
    }

    public static RequestParams.Builder get(String url) {
        return build(url, HttpEnum.RequestMethod.GET);
    }

    public static RequestParams.Builder post(String url) {
        return build(url, HttpEnum.RequestMethod.POST);
    }

    public static RequestParams.Builder postJSON(String url) {
        return post(url).toJsonBody();
    }

    public static RequestParams.Builder postMulti(String url) {
        return post(url).toMultiBody();
    }

    public static RequestParams.Builder put(String url) {
        return build(url, HttpEnum.RequestMethod.PUT);
    }

    public static RequestParams.Builder putJSON(String url) {
        return put(url).toJsonBody();
    }

    public static RequestParams.Builder putMulti(String url) {
        return put(url).toMultiBody();
    }

    public static RequestParams.Builder head(String url) {
        return build(url, HttpEnum.RequestMethod.HEAD);
    }

    public static RequestParams.Builder delete(String url) {
        return build(url, HttpEnum.RequestMethod.DELETE);
    }


    public static RequestParams.Builder download(String url, String path) {
        return get(url).resultByFile(path);
    }

    public static RequestParams.Builder upload(String url) {
        return postMulti(url);
    }

    //这里可以添加公共参数鉴权
    private static RequestParams.Builder build(String url, HttpEnum.RequestMethod requestMethod) {
        return RequestParams.Build(url)
                .requestMethod(requestMethod);
        //.header("sessionKey", "sessionKey");
    }
}
