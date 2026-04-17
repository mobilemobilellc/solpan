import javax.inject.Inject
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import groovy.json.JsonOutput
import org.gradle.tooling.provider.model.ToolingModelBuilderRegistry
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.provider.Property
import org.gradle.api.tasks.OutputFile
import org.gradle.api.file.RegularFileProperty

abstract class DumpModelTask @Inject constructor(
  @get: Internal
  val registry: ToolingModelBuilderRegistry
) : DefaultTask() {

  @get:Input
  abstract val modelName: Property<String>

  @get:OutputFile
  abstract val outputFile: RegularFileProperty

  @TaskAction
  fun dumpModel() {
    project.logger.lifecycle("Model Name: ${modelName.get()} for module: ${project.path} written to ${outputFile.get().asFile.absolutePath}")
    val basicBuilder = registry.getBuilder(modelName.get())
    val model = basicBuilder.buildAll(modelName.get(), project)
    val jsonOutput = JsonOutput.prettyPrint(JsonOutput.toJson(model))
    outputFile.get().asFile.writeText(jsonOutput)
  }
}

abstract class DumpModelsPlugin @Inject constructor(
  val registry: ToolingModelBuilderRegistry
) : Plugin<Project> {

  override fun apply(project: Project) {
    val lifecycleTask = project.tasks.register("dumpModels") {
      group = "Sync"
      description = "Prints model information about this project"
    }

    listOf("BasicAndroidProject", "AndroidProject").forEach { modelName ->
      val dumpTask = project.tasks.register("dump${modelName}Model", DumpModelTask::class.java) {
        this.modelName.set("com.android.builder.model.v2.models.$modelName")
        outputFile.set(project.layout.buildDirectory.file("${modelName}.json"))
      }
      lifecycleTask.configure {
        dependsOn(dumpTask)
      }
    }
  }
}
