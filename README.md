QSHttp
====
  * 一句代码联网,参数控制方便,自动json解析,使用简单
  * 支持http/自签名https(get post put head...) 文件上传、下载、进度监听、自动解析,基于Okhttp的支持cookie自动管理,缓存控制
  * 支持自定义有效时间缓存,错误缓存(联网失败时使用)
  * 详细的请求信息回调、错误类型(网络链接失败,超时,断网,解析失败,404...)
  * 详细的访问日记打印,非常方便调试
  * 提供拦截器,可添加一些公共鉴权参数...
  * 模块化设计,联网模块可更换,目前提供OkHttp和java原生两种实现

### Gradle
```
dependencies {
    compile 'com.github.tohodog:QSHttp:1.2.0'
}
```

### 最简单的使用例子
```
QSHttp.get("http://xxx").buildAndExecute();
```


### 普通带参数get请求
```
        String url = "https://www.baidu.com/s";
        QSHttp.get(url)
                .param("wd", "安卓http")
                .param("ie", "UTF-8")//自动构建url--https://www.baidu.com/s?ie=UTF-8&wd=安卓http
                //.path(123,11) 这个参数会构建这样的url--https://www.baidu.com/s/123/11
                .buildAndExecute(new HttpCallback() {
                    @Override
                    public void onSuccess(ResponseParams response) {
                        response.string();//响应内容
                    }

                    @Override
                    public void onFailure(HttpException e) {
                        e.show();
                    }
                });
```


### 普通键值对post请求
```
        String url = "https://www.baidu.com";
        QSHttp.post(url)
                .param("userid", 10086)
                .param("password", "qwe123456")
                .buildAndExecute(new HttpCallback() {
                    @Override
                    public void onSuccess(ResponseParams response) {
                        response.string();//响应内容
                    }

                    @Override
                    public void onFailure(HttpException e) {
                        e.show();
                    }
                });
```

###  post一个json给服务器 并自动解析服务器返回信息
```
        String url = "https://www.baidu.com";
        QSHttp.postJSON(url)
                .param("userid", 10086)
                .param("password", "qwe123456")
                //.jsonBody(Object) 这个参数可以直接传一个实体类,fastjson会自动转化成json字符串
                .jsonModel(Bean.class)
                .buildAndExecute(new HttpCallback() {
                    @Override
                    public void onSuccess(ResponseParams response) {
                        response.string();//响应内容
                        Bean b = response.parserObject();//自动解析好的实体类
                        b.getUserid();
                    }

                    @Override
                    public void onFailure(HttpException e) {
                        e.show();
                    }
                });
```


###  文件下载
```
        String url = "https://www.baidu.com";
        QSHttp.download(url,"/xxx/xxx.txt")
                .buildAndExecute(new ProgressCallback() {
                    @Override
                    public void onProgress(long var1, long var2, String var3) {
                        Log.i("http",var1 * 100 / var2 + "%\n");
                    }

                    @Override
                    public void onSuccess(ResponseParams response) {
                        response.headers().toString();//获取响应求头
                        response.bytes();
                    }

                    @Override
                    public void onFailure(HttpException e) {
                        e.show();
                    }
                });
```


###  文件上传
```
        String url = "https://www.baidu.com";
        QSHttp.upload(url)
                .param("userid", 10086)
                .param("password", "qwe123456")

                .param("bytes", new byte[1024])//multipart方式上传一个字节数组
                .param("file", new File("xx.jpg"))//multipart方式上传一个文件
                .multipartBody("icon", "image/*", "x.jpg", new byte[1024])

                .buildAndExecute(new ProgressCallback() {
                    @Override
                    public void onProgress(long rwLen, long allLen, String mark) {
                        int i=rwLen * 100 / allLen ;//百分比
                        //mark 在传文件的时候为文件路径 其他无意义
                    }

                    @Override
                    public void onSuccess(ResponseParams response) {
                        response.string();//获取响应内容
                    }

                    @Override
                    public void onFailure(HttpException e) {
                        e.show();
                    }
                });
```



### 基本所有API一览

``` 
        //使用前进行初始化,才可支持缓存
        QSHttpManage.init(getApplication());
        //日记
        QSHttpManage.DEBUG = true;

        //配置自签名 读取assets/cers文件夹里的证书
        //第二个参数设置需要自签名的主机地址,不设置则只能访问证书里的https网站
        QSHttpManage.setSSL(Utils.getAssetsSocketFactory(this, "cers"), "kyfw.12306.cn","...");

        //拦截器 统一添加参数 鉴权
        QSHttpManage.setInterceptor(new Interceptor() {
                    @Override
                    public ResponseParams intercept(Chain chain) throws HttpException {
                        RequestParams r = chain.request().newBuild().header("keytoken", "23333").build();
                        return chain.proceed(r);
                    }
                });
        //还有线程池,缓存大小等等设置

        String url = "https://www.baidu.com/s";
                QSHttp.post(url)//选择请求的类型
                        .header("User-Agent", "QsHttp/Android")//添加请求头

                        .path(2333, "video")//构建成这样的url https://www.baidu.com/s/2233/video

                        .param("userid", 123456)//键值对参数
                        .param("password", "asdfgh")//键值对参数
                        .param(new Bean())//键值对参数

                        .toJsonBody()//把 params 转为json;application/json
                        .jsonBody(new Bean())//传入一个对象,会自动转化为json上传;application/json

                        .requestBody("image/jpeg", new File("xx.jpg"))//直接上传自定义的内容 自定义contentType (postjson内部是调用这个实现)

                        .param("bytes", new byte[1024])//传一个字节数组,multipart支持此参数
                        .param("file", new File("xx.jpg"))//传一个文件,multipart支持此参数
                        .toMultiBody()//把 params 转为multipartBody参数;multipart/form-data


                        .parser(parser)//自定义解析,由自己写解析逻辑
                        .jsonModel(Bean.class)//使用FastJson自动解析json,传一个实体类即可

                        .resultByBytes()//请求结果返回一个字节组 默认是返回字符
                        .resultByFile(".../1.txt")//本地路径 有此参数 请求的内容将被写入文件

                        .errCache()//开启这个 [联网失败]会使用缓存,如果有的话
                        .clientCache(24 * 3600)//开启缓存,有效时间一天
                        .timeOut(10 * 1000)
                        .openServerCache()//开启服务器缓存规则 基于okhttp支持
                        //构建好参数和配置后调用执行联网
                        .buildAndExecute(new ProgressCallback() {

                            //-----回调均已在主线程

                            @Override
                            public void onProgress(long var1, long var2, String var3) {
                                //进度回调 不需要监听进度 buildAndExecute()传 new HttpCallback(){...}即可
                                long i = var1 * 100 / var2;//百分比
                                //var3 在传文件的时候为文件路径 其他无意义
                            }

                            @Override
                            public void onSuccess(ResponseParams response) {
                                response.string();//获得响应字符串 *默认
                                response.file();//设置了下载 获得路径
                                response.bytes();//设置了返回字节组 获得字节组

                                response.headers();//获得响应头

                                //获得自动解析/自定义解析的结果
                                Bean b = response.parserObject();
                                b.getUserid();
                            }

                            @Override
                            public void onFailure(HttpException e) {
                                e.show();//弹出错误提示 网络连接失败 超时 404 解析失败 ...等
                            }
                        });
```
## Log
### v1.2.0(2019-03-27)
  * 船新版本,使用更愉悦
  * 支持自定义有效期缓存