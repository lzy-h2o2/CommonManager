# CommonManager

Android University Functions `'lib'`（安卓通用功能库）

*建议您使用Android Studio作为开发工具，本lib的IDE是AS，某些加载资源文件功能可能不会支持Eclipse*

使用方式：TODO

[⭐]_使用频度_

@[TOC](目录)

### Log
日志输出相关功能

提供了默认日志显示和动态配置两种方式，默认方式您在初始化之后可以完全无顾虑地像使用Android自带的Log一样调动处理（默认TAG：`ZLogger`），
也可以动态控制日志的展示或者保存日志文件，默认在`SD卡根目录/zlogger/logs/sync/`目录下（Android 6.0+ 需要获取存储权限），日志显示开关默认会关闭，您可以通过设置开关参数`ZLogger.setDebug(boolean)`去开启日志。
另外还可以在您项目，我姑且叫`app`module下的`assets`目录下新建一个配置文件`log.properties`，用于控制日志的开关以及开启何种类型的日志，比如:

```xml
saveFile=true
i=true
d=true
w=true
v=true
e=true
```

**API**

1. `ZLogger.init(Context)` [⭐⭐⭐⭐⭐]

    初始化，建议在`Application`的`onCreate(...)`方法中调用。

2. `ZLogger.setDebug(boolean)` [⭐⭐⭐⭐]

    设置是否开启日志，建议参数根据您应用`BuildConfig.Debug`字段传入，这样可以根据打包的类型动态控制是否显示日志（所以我建议您还是用Android Studio开发吧)。

4. `ZLogger.setSaveMode(boolean)`
	
	设置日志保存模式，`false` -- 不保存为文件， `true` -- 保存为文件，路径为`Environment.getExternalStorageDirectory() + "/zlogger/logs/sync/"`。

5. `ZLogger.debugAll(boolean)`
    
    通过API的形式控制`log.properties`中所有的字段，是否开启相应功能。

6. `ZLogger.debug(String, boolean)`
    
    通过API的形式控制`log.properties`中指定的字段，是否开启相应功能。

7. `ZLogger.v/d/i/w/e(String...)` [⭐⭐⭐⭐⭐]

    在debug可用状态下显示对应的debug信息。
    
8. `ZLogger.e(String, Exception)` [⭐⭐]

    显示error日志的构造函数，可以传入`Exception`。
    
9. `ZLogger.e(Throwable)` [⭐]

    显示error日志的构造函数，可以传入`Throwable`。
