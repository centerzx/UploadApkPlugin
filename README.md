# UploadApkPlugin

我们有这样的场景，在项目开发测试阶段，经常会将apk打包后，上传到蒲公英等三方平台，然后再发给公司其他人员进行测试、体验。每次发包的时候要去进行上传、上传完后通知相关人员，导致有点繁琐，浪费时间。此插件为了解决这个问题。

Gradle插件，依赖进项目工程，编译APK，使用Task命令一键上传apk到三方平台，如：蒲公英。（目前暂时只支持蒲公英，可以进行扩展）。
上传成功后，如果你需要提醒其他人员进行版本更新，如：钉钉群、飞书群、企业微信群等（还可扩展其他），配置相关参数，自动发送更新消息到群里。

## 使用步骤

1、在项目工程跟目录，工程的build.gradle dependencies中添加：
`classpath 'com.github.centerzx:UploadApkPlugin:***'`  
目前版本为：
`classpath 'com.github.centerzx:UploadApkPlugin:1.0.1'`

2、在app目录的build.gradle中添加引用插件：
`apply plugin: 'center.uploadpgy.plugin'` 
引入后，配置自己的相关平台参数：

### (1)上传到蒲公英的相关配置参数

```
uploadPgyParams {
        apiKey = "替换为自己蒲公英账户的apiKey"
        // apiKey = readProperties("PgyApiKey")
        appName = "TestGradlePlugin"//暂时无用 buildTypeName = "Release"
        buildInstallType = 2 buildPassword = "zx"
    }
```

### (2)发送消息到钉钉的相关配置参数

```
buildDingParams {
        accessToken =  "替换为自己钉钉的token"
        // accessToken = readProperties("DingAccessToken")
        contentText = "最新开发测试包已经上传至蒲公英, 可以下载使用了"
        contentTitle = "开发测试包"
    }
```

### (3)发送消息到飞书的相关配置参数

```
buildFeiShuParams {
        webHookHostUrl = "https://open.feishu.cn/open-apis/bot/v2/hook/************"
        // webHookHostUrl = readProperties("FeiShuWebHookHostUrl")
        contentTitle = "开发测试包"
        contentText = "最新开发测试包已经上传至蒲公英, 可以下载使用了"
        clickTxt = "点击进行下载"
    }
```
### (4)发送消息到企业微信群的相关配置参数

```
buildWeixinGroupParams {
        // webHookHostUrl = readProperties("WeixinWebHookUrl")
        webHookUrl = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=************"
        //文本（text）、markdown（markdown）、图文（news）
        msgtype = "text"
        //如果使用文本可添加参数是否@全体群人员，默认true：isAtAll = true。其他类型不支持
        //isAtAll = true
        contentTitle = "开发测试包"
        contentText = "最新开发测试包已经上传至蒲公英, 可以下载使用了"
    }
```

其中各个参数体的名称不能变，否则编译会报错，参数key不能修改，对于的值可以根据自己情况修改。 
参数中的“***************”号是根据你的蒲公英、钉钉、飞书、企业微信群等情况进行配置。
由于key、token等信息都需要保密，万一泄漏，可能被别有用心的人乱使用，故可以将相关关键信息保存在工程的：local.properties文件里面，此文件一般是根据自己本地进行配置的，不会上传到git。然后进行数据读取，如：readProperties("
PgyApiKey")

//将密钥存在本地 防止泄露 local.properties 在Git的时候不会被上传
```
def readProperties(key) {
        File file = rootProject.file('local.properties')
        if (file.exists()) {
                InputStream inputStream = rootProject.file('local.properties').newDataInputStream()
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

大概情况是这样，如有不足和错误的地方，欢迎大家讨论指正！
