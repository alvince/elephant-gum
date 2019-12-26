package cn.alvince.gradle.gum.properties

import groovy.lang.Closure
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class PluginTest {

    @Rule
    lateinit var projectTemporary: TemporaryFolder

    lateinit var testProject: Project

    init {
        projectTemporary = TemporaryFolder()
    }

    @Before
    fun prepare() {
        val testProjectDir = projectTemporary.newFolder().also { parent ->
            File("src/test/resources/gradle.properties").also { src ->
                src.copyTo(File(parent, "gradle.properties"), true)
            }
            File("src/test/resources/local.properties").also { src ->
                src.copyTo(File(parent, "local.properties"), true)
            }
        }
        val testModuleDir = projectTemporary.newFolder().also { parent ->
            File("src/test/resources/module_gradle.properties").also { src ->
                src.copyTo(File(parent, "gradle.properties"), true)
            }
            File("src/test/resources/module_local.properties").also { src ->
                src.copyTo(File(parent, "local.properties"), true)
            }
        }
        testProject = ProjectBuilder.builder()
                .withName("module-test")
                .withProjectDir(testModuleDir)
                .withParent(ProjectBuilder.builder()
                        .withName("project-test")
                        .withProjectDir(testProjectDir)
                        .build()
                )
                .build()
    }

    @Test
    fun testLoadProps() {
        testProject.plugins.apply(PropsLoaderPlugin::class.java)
        Assert.assertTrue(testProject.extensions.extraProperties.has(PropsLoaderPlugin.EXT_LOCAL_PROPS))
        Assert.assertTrue(testProject.extensions.findByName(PropsLoaderPlugin.FUN_GET_PROP_LOCAL_FIRST) != null)
        Assert.assertTrue(testProject.extensions.findByName(PropsLoaderPlugin.FUN_GET_PROP_LOCAL_ONLY) != null)
        Assert.assertTrue(testProject.getTasksByName(PropsTask.TASK_VIEW_PROPS, false).isNotEmpty())
        testProject.getTasksByName(PropsTask.TASK_VIEW_PROPS, false).toList().first().also {
            it.actions[0].execute(it)
        }
    }

}
