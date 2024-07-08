package insyncwithfoo.uv.commands

import com.jetbrains.python.packaging.common.PythonPackage
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.nio.file.Path


@Serializable
private class PythonPackageSurrogate(val name: String, val version: String)


private object PythonPackageDeserializer : KSerializer<PythonPackage> {
    
    override val descriptor: SerialDescriptor
        get() = PythonPackageSurrogate.serializer().descriptor
    
    override fun serialize(encoder: Encoder, value: PythonPackage) {
        throw NotImplementedError("The serializer should not be used")
    }
    
    override fun deserialize(decoder: Decoder): PythonPackage {
        val surrogate = decoder.decodeSerializableValue(PythonPackageSurrogate.serializer())
        return with(surrogate) { PythonPackage(name, version) }
    }
    
}


internal class ListPackagesCommand(
    override val executable: Path,
    override val workingDirectory: Path
) : Command<InstalledPackages?>() {
    
    override val arguments: List<String>
        get() = listOf(
            "pip", "list",
            "--format", "json"
        )
    
    override fun run(): InstalledPackages? {
        val output = runProcess()
        
        if (!output.checkSuccess(LOGGER)) {
            return null
        }
        
        LOGGER.info("Raw result: $output")
        
        val json = Json { ignoreUnknownKeys = true }
        
        return try {
            json.decodeFromString(ListSerializer(PythonPackageDeserializer), output.stdout)
        } catch (exception: SerializationException) {
            LOGGER.error(exception)
            null
        }
    }
    
}
