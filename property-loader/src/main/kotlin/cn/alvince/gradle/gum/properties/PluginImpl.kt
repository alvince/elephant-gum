package cn.alvince.gradle.gum.properties

import groovy.lang.Closure
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.DataInputStream
import java.io.FileInputStream
import java.util.*

class PluginImpl : Plugin<Project> {

    companion object {
        const val EXT_LOCAL_PROPS = "localProps"

        const val FUN_GET_PROP_LOCAL_FIRST = "getPropLocalFirst"

        const val FUN_GET_PROP_LOCAL_ONLY = "getLocalProp"
    }

    override fun apply(target: Project) {
        loadLocalProps(target)
        installFunGetProp(target)
        installFunGetPropLocal(target)
    }

    private fun loadLocalProps(target: Project) {
        target.file("local.properties").takeIf { it.exists() }?.let {
            DataInputStream(FileInputStream(it))
        }?.let { ins ->
            Properties().apply {
                ins.use { load(it) }
            }
        }?.also {
            target.extensions.extraProperties.set(EXT_LOCAL_PROPS, it)
        }
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

}