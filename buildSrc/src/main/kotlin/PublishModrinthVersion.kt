import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.time.Duration
import java.util.UUID

abstract class PublishModrinthVersion : DefaultTask() {
    @get:Internal
    abstract val token: Property<String>

    @get:Input
    abstract val apiUrl: Property<String>

    @get:Input
    abstract val projectId: Property<String>

    @get:Input
    abstract val versionNumber: Property<String>

    @get:Input
    abstract val versionName: Property<String>

    @get:Input
    abstract val versionType: Property<String>

    @get:Input
    abstract val gameVersions: ListProperty<String>

    @get:Input
    abstract val loaders: ListProperty<String>

    @get:Input
    abstract val environment: Property<String>

    @get:InputFile
    abstract val primaryFile: RegularFileProperty

    @get:InputFile
    @get:Optional
    abstract val sourcesFile: RegularFileProperty

    @get:Input
    abstract val dryRun: Property<Boolean>

    init {
        group = "publishing"
        description = "Publishes this target to Modrinth"
        versionType.convention("release")
        environment.convention("client_only")
        apiUrl.convention(
            project.providers.gradleProperty("modrinthApiUrl").orElse("https://api.modrinth.com/v2")
        )
        dryRun.convention(
            project.providers.gradleProperty("modrinthDryRun").map(String::toBoolean).orElse(false)
        )
    }

    @TaskAction
    fun publish() {
        val primary = primaryFile.get().asFile.toPath()
        val sources = sourcesFile.orNull?.asFile?.toPath()

        if (dryRun.get()) {
            logger.lifecycle("Modrinth dry run: {}", JsonOutput.prettyPrint(versionData(projectId.get(), primary, sources)))
            return
        }

        val apiToken = token.orNull ?: throw GradleException("MODRINTH_TOKEN is not set")
        val client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build()
        val resolvedProjectId = resolveProjectId(client, apiToken)
        val existingVersionId = findExistingVersion(client, apiToken, resolvedProjectId)
        if (existingVersionId != null) {
            logger.lifecycle(
                "Skipping existing Modrinth version {} with ID {}",
                versionNumber.get(),
                existingVersionId
            )
            return
        }
        val body = multipartBody(resolvedProjectId, primary, sources)
        val request = HttpRequest.newBuilder(URI.create("${apiUrl.get().trimEnd('/')}/version"))
            .timeout(Duration.ofMinutes(5))
            .header("Authorization", apiToken)
            .header("User-Agent", "lunalna/modifier-keybinds (https://github.com/lunalna/modifier-keybinds)")
            .header("Content-Type", "multipart/form-data; boundary=${body.boundary}")
            .POST(body.publisher)
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() !in 200..299) {
            throw GradleException("Modrinth upload failed with HTTP ${response.statusCode()}: ${response.body().take(2_000)}")
        }

        val result = JsonSlurper().parseText(response.body()) as Map<*, *>
        logger.lifecycle(
            "Published Modrinth version {} with ID {}",
            result["version_number"],
            result["id"]
        )
    }

    private fun resolveProjectId(client: HttpClient, apiToken: String): String {
        val slug = encodePathSegment(projectId.get())
        val request = HttpRequest.newBuilder(URI.create("${apiUrl.get().trimEnd('/')}/project/$slug"))
            .timeout(Duration.ofSeconds(30))
            .header("Authorization", apiToken)
            .header("User-Agent", "lunalna/modifier-keybinds (https://github.com/lunalna/modifier-keybinds)")
            .GET()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() !in 200..299) {
            throw GradleException("Could not resolve Modrinth project '${projectId.get()}': HTTP ${response.statusCode()}")
        }

        val project = JsonSlurper().parseText(response.body()) as Map<*, *>
        return project["id"] as? String ?: throw GradleException("Modrinth project response did not contain an ID")
    }

    private fun findExistingVersion(client: HttpClient, apiToken: String, project: String): String? {
        val encodedProject = encodePathSegment(project)
        val encodedVersion = encodePathSegment(versionNumber.get())
        val request = HttpRequest.newBuilder(
            URI.create("${apiUrl.get().trimEnd('/')}/project/$encodedProject/version/$encodedVersion")
        )
            .timeout(Duration.ofSeconds(30))
            .header("Authorization", apiToken)
            .header("User-Agent", "lunalna/modifier-keybinds (https://github.com/lunalna/modifier-keybinds)")
            .GET()
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() == 404) return null
        if (response.statusCode() !in 200..299) {
            throw GradleException(
                "Could not check Modrinth version '${versionNumber.get()}': " +
                    "HTTP ${response.statusCode()}: ${response.body().take(2_000)}"
            )
        }

        val version = JsonSlurper().parseText(response.body()) as Map<*, *>
        return version["id"] as? String
            ?: throw GradleException("Modrinth version response did not contain an ID")
    }

    private fun encodePathSegment(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20")

    private fun multipartBody(project: String, primary: Path, sources: Path?): MultipartBody {
        val boundary = "ModifierKeybinds-${UUID.randomUUID()}"
        val publishers = mutableListOf<HttpRequest.BodyPublisher>()

        publishers += textPart(boundary, "data", versionData(project, primary, sources))
        publishers += filePart(boundary, "primary", primary)
        if (sources != null) publishers += filePart(boundary, "sources", sources)
        publishers += HttpRequest.BodyPublishers.ofByteArray("--$boundary--\r\n".toByteArray())

        return MultipartBody(boundary, HttpRequest.BodyPublishers.concat(*publishers.toTypedArray()))
    }

    private fun versionData(project: String, primary: Path, sources: Path?): String {
        check(primary.fileName.toString().endsWith(".jar")) { "Primary Modrinth file must be a jar" }
        val fileParts = buildList {
            add("primary")
            if (sources != null) add("sources")
        }
        val data = linkedMapOf<String, Any>(
            "name" to versionName.get(),
            "version_number" to versionNumber.get(),
            "changelog" to "No changelog was specified.",
            "dependencies" to emptyList<Any>(),
            "game_versions" to gameVersions.get(),
            "version_type" to versionType.get(),
            "loaders" to loaders.get(),
            "featured" to false,
            "project_id" to project,
            "file_parts" to fileParts,
            "primary_file" to "primary",
            "environment" to environment.get()
        )
        if (sources != null) data["file_types"] = mapOf("sources" to "sources-jar")
        return JsonOutput.toJson(data)
    }

    private fun textPart(boundary: String, name: String, value: String): HttpRequest.BodyPublisher {
        val part = "--$boundary\r\n" +
            "Content-Disposition: form-data; name=\"$name\"\r\n" +
            "Content-Type: application/json\r\n\r\n" +
            value + "\r\n"
        return HttpRequest.BodyPublishers.ofByteArray(part.toByteArray())
    }

    private fun filePart(boundary: String, name: String, file: Path): HttpRequest.BodyPublisher {
        val header = "--$boundary\r\n" +
            "Content-Disposition: form-data; name=\"$name\"; filename=\"${file.fileName}\"\r\n" +
            "Content-Type: application/java-archive\r\n\r\n"
        return HttpRequest.BodyPublishers.concat(
            HttpRequest.BodyPublishers.ofByteArray(header.toByteArray()),
            HttpRequest.BodyPublishers.ofFile(file),
            HttpRequest.BodyPublishers.ofByteArray("\r\n".toByteArray())
        )
    }

    private data class MultipartBody(
        val boundary: String,
        val publisher: HttpRequest.BodyPublisher
    )
}
