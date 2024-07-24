package insyncwithfoo.uv.commands

import com.jetbrains.python.packaging.common.PythonPackage
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.nio.file.Path


private fun PythonPackage(surrogate: PythonPackageSurrogate) =
    with(surrogate) { PythonPackage(name, version) }


@Serializable
private class PythonPackageSurrogate(
    val name: String,
    val version: String,
    @SerialName("editable_project_location")
    val editableProjectLocation: String? = null
) {
    val isNotEditable: Boolean
        get() = editableProjectLocation == null
}


private object PythonPackageSurrogateDeserializer : KSerializer<PythonPackageSurrogate> {
    
    override val descriptor: SerialDescriptor
        get() = PythonPackageSurrogate.serializer().descriptor
    
    override fun serialize(encoder: Encoder, value: PythonPackageSurrogate) {
        throw NotImplementedError("The serializer should not be used")
    }
    
    override fun deserialize(decoder: Decoder): PythonPackageSurrogate {
        return decoder.decodeSerializableValue(PythonPackageSurrogate.serializer())
    }
    
}


internal class PipListCommand(
    override val executable: Path,
    override val workingDirectory: Path
) : Command<InstalledPackages?>() {
    
    override val arguments: List<String>
        get() = listOf(
            "pip", "list",
            "--format", "json"
        )
    
    override fun run(): InstalledPackages? {
        val output = runAndLogProcess()
        
        if (!output.checkSuccess(LOGGER)) {
            return null
        }
        
        return try {
            parse(output.stdout).mapNotNull { surrogate ->
                PythonPackage(surrogate).takeIf { surrogate.isNotEditable }
            }
        } catch (exception: SerializationException) {
            LOGGER.error(exception)
            null
        }
    }
    
    private fun parse(stdout: String): List<PythonPackageSurrogate> {
        val json = Json { ignoreUnknownKeys = true }
        return json.decodeFromString(ListSerializer(PythonPackageSurrogateDeserializer), stdout)
    }
    
}
