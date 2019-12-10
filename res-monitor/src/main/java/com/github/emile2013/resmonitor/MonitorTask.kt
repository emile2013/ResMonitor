package com.github.emile2013.resmonitor

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.scope.VariantScope
import com.android.builder.model.AndroidProject
import com.android.utils.FileUtils
import javassist.ClassPool
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 *  Monitor Task
 * @author y.huang
 * @since 2019-12-09
 */
open class MonitorTask : DefaultTask() {


    lateinit var variantScope: VariantScope


    @TaskAction
    fun doTask() {


        var pool = initClassPool()


        var ruleFile = getResourceProguardFileCompat()


        var map = mutableMapOf<String, String>()

        var referList = mutableListOf<String>()

        var lastline: String
        ruleFile?.readLines()?.filter {
            lastline = it
            //filter white line and class from AndroidManifest
            (it.startsWith("-keep") || it.startsWith("# Referenced"))
                    && (!lastline.contains("AndroidManifest"))
        }?.forEach {

            if (it.startsWith("# Referenced")) {
                referList.add(it)
            } else if (it.startsWith("-keep")) {
                var classStr = it.removePrefix("-keep class").removeSuffix("{ <init>(...); }")
                    .removeSuffix("{ <init>(); }").trim()
                map.put(classStr, referList.toString())
                referList.clear()
            }
        }


        map?.forEach {

            var classStr = it.key

//            var ctclass = pool.getOrNull(classStr)
//                ?: throw Exception("$classStr not exist,please checkout layout files")
            pool.find(classStr)
                ?: throw Exception(messageDetail(classStr, it.value))
        }
    }

    private fun messageDetail(classStr: String, refer: String): String {

        var message = StringBuilder("$classStr not exist,but declare in:\n")
        message.append(refer)
        return message.toString()
    }


    fun getResourceProguardFileCompat(): File? {
        return variantScope.processAndroidResourcesProguardOutputFile
    }

    private fun initClassPool(): ClassPool {


        var pool: ClassPool = ClassPool.getDefault()


        var compileClasspath =
            variantScope.getArtifactCollection(
                AndroidArtifacts.ConsumedConfigType.COMPILE_CLASSPATH,
                AndroidArtifacts.ArtifactScope.EXTERNAL, AndroidArtifacts.ArtifactType.CLASSES
            )
        compileClasspath?.artifactFiles?.forEach {
            try {
                pool.appendClassPath(it.absolutePath)
                project.logger.info("compileClasspath:${it.absolutePath}")
            } catch (e: Exception) {
                project.logger.error("compileClasspath:${e}")
            }
        }


        var bootClasspath = project.getAndroid<BaseExtension>().bootClasspath
        bootClasspath?.forEach {
            try {
                pool.appendClassPath(it.absolutePath)
                project.logger.info("bootClasspath:${it.absolutePath}")
            } catch (e: Exception) {
                project.logger.error("bootClasspath:${e}")
            }
        }

        //javac
        var javacClasspath = FileUtils.join(
            project.buildDir,
            AndroidProject.FD_INTERMEDIATES,
            "javac",
            variantScope.variantData.name,
            "classes"
        ).absolutePath
        project.logger.info("javacClasspath:${javacClasspath}")
        pool.appendClassPath(javacClasspath)

        //kotlin temp
        var kotlinClasspath = FileUtils.join(
            project.buildDir,
            "tmp",
            "kotlin-classes",
            variantScope.variantData.name
        ).absolutePath
        project.logger.info("kotlinClasspath:${kotlinClasspath}")
        pool.appendClassPath(kotlinClasspath)

        return pool
    }


    class ConfigAction(private var variantScope: VariantScope, private var project: Project) :
        Action<MonitorTask> {

        override fun execute(task: MonitorTask) {
            task.variantScope = variantScope

        }

    }
}