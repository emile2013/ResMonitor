# ResMonitor
[![license](http://img.shields.io/badge/license-BSD3-brightgreen.svg?style=flat)](https://github.com/emile2013/ResMonitor/tree/master/LICENSE)
[![Release Version](https://jitpack.io/v/emile2013/nav2main.svg)](https://jitpack.io/#emile2013/ResMonitor)

A repository for android application or library module to check classes in layout or manifest xml file if exist.

> AS Migrate to Androidx 功能存在修改布局文件类错乱，人工不易发现问题。本项目初衷是监控此类错误，避免带来线上问题。

> Android Studio Migrate to Androidx make classes in layout xml file not right that developer cannot found easily.This program am to monitor above issues.
## Getting Started 

> Edit root project build.gradle file, append res-monitor plugin in  `buildscript`  classpath ，and do not forget add maven { url 'https://jitpack.io' } too.

```groovy
buildscript {
    ext.resmonitor_version='0.1.0'
    repositories {
        maven { url 'https://jitpack.io' } //add this line
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath "com.github.emile2013:res-monitor:$resmonitor_version" //add this line
    }
}

allprojects {
    repositories {
      maven { url 'https://jitpack.io' }  //add this line
      google()
      mavenCentral()
      jcenter()
    }
}
```

>  App module build.gradle file  add res-monitor dependency

```groovy
apply plugin: 'com.android.application'
apply plugin: 'com.github.emile2013.res-monitor' // add this line
```

> Run task

```
./gradlew checkReleaseRes

```

## Samples 
- [sample](https://github.com/emile2013/ResMonitor/tree/master/app)


## Tips
 
> 此项目原理是拿到layout和manifest xml导出的混淆文件,再通过javassist检测类是否存在。所以依赖的项目要开启混淆配置(minifyEnabled=true)

> This program uses aapt_rule.txt and javassist to handle functions, so must set minifyEnabled=true in build.gradle file.



## License

ResMonitor is licensed under the [BSD 3-Clause License](./LICENSE).
