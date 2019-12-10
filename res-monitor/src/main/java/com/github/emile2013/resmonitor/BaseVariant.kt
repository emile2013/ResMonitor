package com.github.emile2013.resmonitor

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.BaseVariant
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.internal.variant.BaseVariantData
import org.gradle.api.Project



/**
 * The variant scope
 *
 */
val BaseVariant.scope: VariantScope
    get() = variantData.scope

val BaseVariant.project: Project
    get() = scope.globalScope.project

/**
 * The variant data
 *
 */
val BaseVariant.variantData: BaseVariantData
    get() = javaClass.getDeclaredMethod("getVariantData").invoke(this) as BaseVariantData

/**
 * Returns android extension
 *
 */
inline fun <reified T : BaseExtension> Project.getAndroid(): T = extensions.getByName("android") as T