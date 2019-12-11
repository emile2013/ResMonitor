package com.github.emile2013.resmonitor

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Monitor Plugin
 *
 * @author y.huang
 * @since 2019-12-09
 */
class MonitorPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        with(project) {

            //only check app or library
            if (!plugins.hasPlugin("com.android.application") && !plugins.hasPlugin("com.android.library")) {
                return
            }

            extensions.create("res_monitor", ResMonitorExtension::class.java)

            afterEvaluate {

                if (plugins.hasPlugin("com.android.application")) {

                    var android = extensions.getByType(AppExtension::class.java)
                    android.applicationVariants.all { variant ->
                        var proguardEnable = variant.buildType.isMinifyEnabled
                        if (proguardEnable) {
                            registTasks(this, variant)
                        }
                    }
                } else if (project.plugins.hasPlugin("com.android.library")) {

                    var android = extensions.getByType(LibraryExtension::class.java)
                    android.libraryVariants.all { variant ->
                        var proguardEnable = variant.buildType.isMinifyEnabled
                        if (proguardEnable) {
                            registTasks(this, variant)
                        }

                    }
                }
            }
        }
    }

    private fun registTasks(project: Project, variant: BaseVariant) {

        var monitorTask: Task = project.tasks.create(
            "check${variant.name.capitalize()}Res",
            MonitorTask::class.java,
            MonitorTask.ConfigAction(variant.scope, project)
        )
        var assembleTask = project.tasks.getByName("assemble${variant.name.capitalize()}")
        assembleTask?.let {
            monitorTask.dependsOn(it)
            monitorTask.mustRunAfter(it)
        }

    }
}