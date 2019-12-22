# ResMonitor
[![license](http://img.shields.io/badge/license-BSD3-brightgreen.svg?style=flat)](https://github.com/emile2013/ResMonitor/tree/master/LICENSE)
[![Release Version](https://jitpack.io/v/emile2013/ResMonitor.svg)](https://jitpack.io/#emile2013/ResMonitor)

用于检测Android项目layout文件中控件类是否存在，避免因控件类不存在，带来线上ClassNotFoundException问题！
[README English Version](README.md)

## 使用

#### 1. 增加依赖

>  根项目build.gradle增加classpath依赖

```groovy
buildscript {
    ext.resmonitor_version='0.1.1'
    repositories {
        maven { url 'https://jitpack.io' } //add this line
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath "com.github.emile2013:ResMonitor:$resmonitor_version" //add this line
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

>  App build.gradle apply `com.github.emile2013.res-monito` plugin

```groovy
apply plugin: 'com.android.application'
apply plugin: 'com.github.emile2013.res-monitor' // add this line
```

## 运行&日记输出

```
./gradlew checkReleaseRes

something output on  console like:

Execution failed for task ':app:checkReleaseRes'.
> java.lang.Exception: androidx.core.view.ViewPager not exist !! but declare at:
  # Referenced at /ResMonitor/sample/app/src/main/res/layout/content_main.xml:11

```

## 示例
- [sample](https://github.com/emile2013/ResMonitor/tree/master/sample)


## 原理
 
> 此项目原理是拿到layout和manifest xml导出的混淆文件,再通过javassist检测类是否存在。所以依赖的项目要开启混淆配置(minifyEnabled=true)
> 应用场景例如用于Migrate to AndroidX,无论AS自带功能或自写脚本方式均有可能对layout xml存在错误修改，本项目可以对此类问题进行检测。

## TODO
 
> 考虑到library相对功能不多，可人工或在集成到app时作较验，本项目暂时不支持library项目。

## License

ResMonitor is licensed under the [BSD 3-Clause License](./LICENSE).
