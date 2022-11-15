package masker.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Set;

class PrettyPrintTest {
    @Test
    void prettyPrintMasking() {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode().put("Test", "Value");
        JsonNode jsonNode = JsonNodeFactory.instance.objectNode().set("Test1", objectNode);
        String prettyString = jsonNode.toPrettyString();
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        "*****",
                        new SingleTargetMasker(JsonMaskingConfig.getDefault(Set.of("someKey"))).mask(prettyString)
                ),
                () -> Assertions.assertEquals(
                        "*****",
                        new KeyContainsMasker(JsonMaskingConfig.getDefault(Set.of("someKey"))).mask(prettyString)
                ),
                () -> Assertions.assertEquals(
                        "*****",
                        new PathAwareKeyContainsMasker(JsonMaskingConfig.getDefault(Set.of("someKey"))).mask(
                                prettyString)
                )
        );
    }

    @Test
    @Disabled
    void prettyPrintMaskingPathAwareKeyContains() throws JsonProcessingException {
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode().put("Test", "Value");
        JsonNode jsonNode = JsonNodeFactory.instance.objectNode().set("Test1", objectNode);
        String prettyString = jsonNode.toPrettyString();
        JsonMasker jsonMasker = new PathAwareKeyContainsMasker(JsonMaskingConfig.getDefault(Set.of("Test")));
        String mask = jsonMasker.mask(prettyString);
        Assertions.assertEquals(
                "*****",
                JsonMapper.builder()
                        .build()
                        .readValue(mask, JsonNode.class)
                        .findValue("Test")
                        .textValue()
        );
    }
}
