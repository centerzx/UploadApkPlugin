# UploadApkPlugin

我们有这样的场景，在项目开发测试阶段，经常会将apk打包后，上传到蒲公英等三方平台，然后再发给公司其他人员进行测试、体验。每次发包的时候要去进行上传、上传完后通知相关人员，导致有点繁琐，浪费时间。此插件为了解决这个问题。

Gradle插件，依赖进项目工程，编译APK，使用Task命令一键上传apk到三方平台，如：蒲公英。（目前暂时只支持蒲公英，可以进行扩展）。
上传成功后，如果你需要提醒其他人员进行版本更新，如：钉钉群、飞书群、企业微信群等（还可扩展其他），配置相关参数，自动发送更新消息到群里。

## 更新记录：
2022-08-22：  
蒲公英上传接口做了调整，原有接口将废弃：https://www.pgyer.com/doc/view/api#fastUploadApp插件已根据文档做升级。  
修复版本v1.0.3   

2022-09-3：  
（1）修复发送消息到飞书出现msg_type参数错误问题；  
（2）新增Git提交日志参数，用于发送消息时携带Git日志；  
（3）完善飞书、钉钉、微信发送消息的类型并添加Git日志  
修复版本v1.0.6

## 使用步骤

1、在项目工程跟目录，工程的build.gradle dependencies中添加：

`classpath 'com.github.centerzx:UploadApkPlugin:v***'`  
目前版本为：

`classpath 'com.github.centerzx:UploadApkPlugin:v1.0.6'`

repositories中添加：

maven { url "[https://jitpack.io](https://links.jianshu.com/go?to=https%3A%2F%2Fjitpack.io)"} 

2、在app目录（需要打包上传的APK的Module）的build.gradle中添加引用插件：

`apply plugin: 'center.uploadpgy.plugin'`  
 

**引入后，配置自己的相关平台参数：**

### (1)上传到蒲公英的相关配置参数

```
uploadPgyParams {
     apiKey = "替换为自己蒲公英账户的apiKey"
     // apiKey = readProperties("PgyApiKey")
     //暂时无用
     appName = "TestGradlePlugin" 
     buildTypeName = "Release"
     buildInstallType = 2 
     buildPassword = "zx"
}
```

### (2)发送消息到钉钉的相关配置参数

```
buildDingParams {
    accessToken =  "替换为自己钉钉的token"
    // accessToken = readProperties("DingAccessToken")
    contentText = "最新开发测试包已经上传至蒲公英, 可以下载使用了"
    contentTitle = "开发测试包"
    //link类型（link）、markdown类型（markdown）、整体跳转ActionCard类型（actionCard），默认link
    msgtype = "actionCard"
    //如果使用markdown类型可添加参数是否@全体群人员，默认false：isAtAll = true。其他类型不支持
    isAtAll = true
    //存在点击时按钮的文案（link类型无）
    clickTxt = "点击进行下载"
    //是否单独支持发送Git记录数据，在配置了buildGitLogParams前提下有效，默认为true。link字数问题，无法支持
    isSupportGitLog = true
}
```

### (3)发送消息到飞书的相关配置参数

```
buildFeiShuParams {
    webHookHostUrl = "https://open.feishu.cn/open-apis/bot/v2/hook/************"
    // webHookHostUrl = readProperties("FeiShuWebHookHostUrl")
    contentTitle = "开发测试包"
    contentText = "最新开发测试包已经上传至蒲公英, 可以下载使用了"
    //富文本消息（post）、消息卡片（interactive），默认post
    msgtype = "post"
    //是否@全体群人员，默认false：isAtAll = true
    isAtAll = true
    clickTxt = "点击进行下载"
    //是否单独支持发送Git记录数据，在配置了buildGitLogParams前提下有效，默认为true
    isSupportGitLog = true
}
```
### (4)发送消息到企业微信群的相关配置参数

```
buildWeixinGroupParams {
    // webHookHostUrl = readProperties("WeixinWebHookUrl")
    webHookUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=************"
    //文本（text）、markdown（markdown）、图文（news），默认 markdown。由于字数限制，只有markdown支持携带Git记录
    msgtype = "markdown"
    //如果使用文本类型(text)可添加参数是否@全体群人员，默认true：isAtAll = true。其他类型不支持
    //isAtAll = true
    contentTitle = "开发测试包"
    contentText = "最新开发测试包已经上传至蒲公英, 可以下载使用了"
    //是否单独支持发送Git记录数据，在配置了buildGitLogParams前提下有效，默认为true。只有markdown类型支持
    isSupportGitLog = true
}
```

### (5)发送消息时携带Git提交记录相关配置参数
```
buildGitLogParams {
    //是否发送消息是携带Git记录日志，如果配置了这块参数才会携带Git记录，消息里面可以单独设置是否携带Git日志数据

    //获取以当前时间为基准至N天之前的Git记录（限定时间范围），不填或小于等于0为全部记录，会结合数量进行获取
    gitLogHistoryDayTime = 3
    //显示Git记录的最大数量，值范围1~50，不填默认是10条，最大数量50条
    gitLogMaxCount = 20
}
```

其中各个参数体的名称不能变，否则编译会报错，参数key不能修改，对于的值可以根据自己情况修改。 
参数中的“***************”号是根据你的蒲公英、钉钉、飞书、企业微信群等情况进行配置。
由于key、token等信息都需要保密，万一泄漏，可能被别有用心的人乱使用，故可以将相关关键信息保存在工程的：local.properties文件里面，此文件一般是根据自己本地进行配置的，不会上传到git。然后进行数据读取，如：readProperties("
PgyApiKey")

将密钥存在本地 防止泄露 local.properties 在Git的时候不会被上传
```
def readProperties(key) {
        File file = rootProject.file('local.properties')
        if (file.exists()) {
                InputStream inputStream = file.newDataInputStream()
                Properties properties = new Properties()
                properties.load(inputStream)
                if (properties.containsKey(key)) {
                        return properties.getProperty(key)
                    }
            }
     }
```

注意：由于这样会导致app的gradle看着很臃肿，因此可以单独新建一个gradle文件进行配置，然后再在app的gradle文件中apply from: "../******.gradle"
这个新建gradle就行。

3、配置完备，进行编译： 此时在studio的右边，tasks里面会新增一个publishToThirdPlatform
命令组，里面会有两个task。gradlew或者点击运行组里面的task命令，则可直接进行编译、打包、传送、发消息等一些列操作。
在执行task命令时，studio的run窗口会展示执行情况，包括蒲公英上传情况、钉钉飞书等消息发送情况，一目了然。

## 后期展望：
1、扩展新增firim上传APK包形式（来自：https://github.com/D-zy 建议）  
2、扩展新增发送的消息中自动携带Git的提交记录描述(已完成。来自 https://github.com/alizhijun 建议)  
3、扩展新增多渠道打包(来自：https://github.com/alizhijun 建议)  
4、扩展新增Apk加固(来自：https://github.com/j-gin 建议)  

大概情况是这样，如有不足和错误的地方，欢迎大家讨论指正和提建议！感谢来个star。
