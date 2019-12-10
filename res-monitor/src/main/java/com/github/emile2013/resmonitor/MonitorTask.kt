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

        var hasRefer = false
        ruleFile?.readLines()?.filter { value ->

            //first filter white line and class from AndroidManifest
            (value.startsWith("-keep class") || value.startsWith("# Referenced"))
                    && (!value.contains("AndroidManifest.xml"))

        }?.forEach { value ->
            if (value.startsWith("# Referenced")) {
                referList.add(value)
                hasRefer = true
            } else if (value.startsWith("-keep") && hasRefer) {
                var classStr = value.removePrefix("-keep class").removeSuffix("{ <init>(...); }")
                    .removeSuffix("{ <init>(); }").trim()

                var orginValue = referList.toString()
                if (orginValue.isNullOrEmpty()) {
                    map[classStr] = "no referenced maybe error"
                } else {
                    map[classStr] =
                        orginValue.substring(1, orginValue.length - 1).replace("#", "\n")
                }

                referList.clear()
            }
        }


        map?.forEach {

            var classStr = it.key
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

        // for test
//        return FileUtils.join(
//            project.buildDir,
//            "aapt_rules.txt"
//        )
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