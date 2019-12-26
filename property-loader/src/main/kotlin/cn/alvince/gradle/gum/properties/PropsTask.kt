package cn.alvince.gradle.gum.properties

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.util.*

open class PropsTask : DefaultTask() {

    companion object {
        const val TASK_VIEW_PROPS = "viewProps"
    }

    @TaskAction
    fun viewProperties() {
        project.also { proj ->
            println("gradle props >>>")
            proj.properties.forEach { (name, value) -> println("$name: $value") }

            proj.extensions.extraProperties[PropsLoaderPlugin.EXT_LOCAL_PROPS]?.let { extra ->
                extra as? Properties
            }?.takeIf { props ->
                props.isNotEmpty()
            }?.also { props ->
                println("local props >>>")
                props.forEach { (name, value) -> println("$name: $value") }
            }
        }
    }

}
