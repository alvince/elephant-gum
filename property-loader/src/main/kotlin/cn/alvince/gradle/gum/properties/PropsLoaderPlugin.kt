package cn.alvince.gradle.gum.properties

import groovy.lang.Closure
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.util.*

class PropsLoaderPlugin : Plugin<Project> {

    companion object {
        const val EXT_LOCAL_PROPS = "localProps"

        const val FUN_GET_PROP_LOCAL_FIRST = "getPropLocalFirst"

        const val FUN_GET_PROP_LOCAL_ONLY = "getLocalProp"
    }

    override fun apply(target: Project) {
        loadLocalProps(target)
        installFunGetProp(target)
        installFunGetPropLocal(target)
        installTasks(target)
    }

    private fun loadLocalProps(target: Project) {
        searchFiles(target) { list ->
            Properties().apply {
                list.forEach { file ->
                    DataInputStream(FileInputStream(file)).let { ins ->
                        Properties().apply {
                            ins.use { load(it) }
                        }
                    }
                }
            }.also {
                target.extensions.extraProperties.set(EXT_LOCAL_PROPS, it)
            }
        }
    }

    private inline fun searchFiles(target: Project, block: (List<File>) -> Unit) {
        mutableListOf<File>().apply {
            var proj: Project? = target
            do {
                proj?.file("local.properties")
                        ?.takeIf { it.exists() }
                        ?.also { add(it) }
                proj = proj?.parent
            } while (proj != null)
            reverse()
        }.takeIf {
            it.isNotEmpty()
        }?.also(block)
    }

    private fun installFunGetProp(target: Project) {
        target.extensions.add(FUN_GET_PROP_LOCAL_FIRST, object : Closure<String>(target, target) {

            override fun call(vararg args: Any?): String = args.getOrNull(0)
                    ?.let { it as? String }
                    ?.let { name ->
                        (thisObject as? Project)?.let { proj ->
                            val localProps = proj.extensions.extraProperties[EXT_LOCAL_PROPS] as? Properties
                            localProps?.takeIf { it.containsKey(name) }?.getProperty(name)
                                    ?: proj.takeIf { it.hasProperty(name) }?.property(name)?.toString()
                                    ?: ""
                        } ?: ""
                    } ?: ""
        })
    }

    private fun installFunGetPropLocal(target: Project) {
        target.extensions.add(FUN_GET_PROP_LOCAL_ONLY, object : Closure<String>(target, target) {

            override fun call(vararg args: Any?): String = args.getOrNull(0)
                    ?.let { it as? String }
                    ?.let { name ->
                        (thisObject as? Project)?.let { proj ->
                            val localProps = proj.extensions.extraProperties[EXT_LOCAL_PROPS] as? Properties
                            localProps?.takeIf { it.containsKey(name) }?.getProperty(name) ?: ""
                        } ?: ""
                    } ?: ""
        })
    }

    private fun installTasks(target: Project) {
        target.tasks.create(PropsTask.TASK_VIEW_PROPS, PropsTask::class.java) { it.viewProperties() }
    }

}
