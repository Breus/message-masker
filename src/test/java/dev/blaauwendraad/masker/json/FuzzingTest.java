package dev.blaauwendraad.masker.json;

import com.fasterxml.jackson.databind.JsonNode;
import dev.blaauwendraad.masker.json.config.JsonMaskingConfig;
import dev.blaauwendraad.masker.json.path.JsonPath;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import randomgen.json.RandomJsonGenerator;
import randomgen.json.RandomJsonGeneratorConfig;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

final class FuzzingTest {
    private static final Set<String> DEFAULT_TARGET_KEYS = Set.of("targetKey1", "targetKey2", "targetKey3");
    private static final Set<String> DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY = Set.of("targetKey1", "$.targetKey2", "targetKey3");
    private static final int SECONDS_FOR_EACH_TEST_TO_RUN = 3;

    @ParameterizedTest
    @MethodSource("jsonMaskingConfigs")
    void fuzzingAgainstParseAndMaskUsingJackson(JsonMaskingConfig jsonMaskingConfig) {
        Instant startTime = Instant.now();
        int randomTestExecuted = 0;
        Set<String> allKeys = new HashSet<>(jsonMaskingConfig.getTargetKeys());
        allKeys.addAll(jsonMaskingConfig.getTargetJsonPaths().stream().map(JsonPath::getLastComponent).collect(Collectors.toSet()));
        RandomJsonGenerator randomJsonGenerator = new RandomJsonGenerator(RandomJsonGeneratorConfig.builder()
                .setTargetKeys(allKeys)
                .createConfig());
        JsonMasker masker = JsonMasker.getMasker(jsonMaskingConfig);
        while (Duration.between(startTime, Instant.now()).getSeconds() < SECONDS_FOR_EACH_TEST_TO_RUN) {
            JsonNode randomJsonNode = randomJsonGenerator.createRandomJsonNode();
            String randomJsonNodeString = randomJsonNode.toPrettyString();
            String keyContainsOutput = masker.mask(randomJsonNodeString);
            String jacksonMaskingOutput = ParseAndMaskUtil.mask(randomJsonNode, jsonMaskingConfig).toPrettyString();
            assertThat(keyContainsOutput).as("Failed for input: " + randomJsonNodeString).isEqualTo(jacksonMaskingOutput);
            randomTestExecuted++;
        }
        System.out.printf(
                "Executed %d randomly generated test scenarios in %d seconds%n",
                randomTestExecuted,
                SECONDS_FOR_EACH_TEST_TO_RUN
        );
    }

    @Nonnull
    private static Stream<JsonMaskingConfig> jsonMaskingConfigs() {
        return Stream.of(
                JsonMaskingConfig.getDefault(DEFAULT_TARGET_KEYS),
                JsonMaskingConfig.getDefault(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS, JsonMaskingConfig.TargetKeyMode.MASK)
                        .maskNumericValuesWith(1)
                        .disableJsonPathResolving()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS, JsonMaskingConfig.TargetKeyMode.MASK)
                        .caseSensitiveTargetKeys()
                        .disableJsonPathResolving()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS, JsonMaskingConfig.TargetKeyMode.MASK)
                        .obfuscationLength(3)
                        .disableJsonPathResolving()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS, JsonMaskingConfig.TargetKeyMode.MASK)
                        .obfuscationLength(0)
                        .disableJsonPathResolving()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS, JsonMaskingConfig.TargetKeyMode.MASK)
                        .obfuscationLength(1)
                        .maskNumericValuesWith(1)
                        .disableJsonPathResolving()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS, JsonMaskingConfig.TargetKeyMode.MASK)
                        .obfuscationLength(3)
                        .maskNumericValuesWith(3)
                        .disableJsonPathResolving()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS, JsonMaskingConfig.TargetKeyMode.ALLOW).disableJsonPathResolving().build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS, JsonMaskingConfig.TargetKeyMode.ALLOW)
                        .caseSensitiveTargetKeys()
                        .disableJsonPathResolving()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS, JsonMaskingConfig.TargetKeyMode.ALLOW)
                        .maskNumericValuesWith(2)
                        .disableJsonPathResolving()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS, JsonMaskingConfig.TargetKeyMode.ALLOW)
                        .obfuscationLength(0)
                        .disableJsonPathResolving()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS, JsonMaskingConfig.TargetKeyMode.ALLOW)
                        .obfuscationLength(4)
                        .maskNumericValuesWith(8)
                        .disableJsonPathResolving()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS, JsonMaskingConfig.TargetKeyMode.ALLOW)
                        .obfuscationLength(4)
                        .disableJsonPathResolving()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY, JsonMaskingConfig.TargetKeyMode.MASK)
                        .maskNumericValuesWith(1)
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY, JsonMaskingConfig.TargetKeyMode.MASK)
                        .caseSensitiveTargetKeys()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY, JsonMaskingConfig.TargetKeyMode.MASK)
                        .obfuscationLength(3)
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY, JsonMaskingConfig.TargetKeyMode.MASK)
                        .obfuscationLength(0)
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY, JsonMaskingConfig.TargetKeyMode.MASK)
                        .obfuscationLength(1)
                        .maskNumericValuesWith(1)
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY, JsonMaskingConfig.TargetKeyMode.MASK)
                        .obfuscationLength(3)
                        .maskNumericValuesWith(3)
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY, JsonMaskingConfig.TargetKeyMode.ALLOW).build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY, JsonMaskingConfig.TargetKeyMode.ALLOW)
                        .caseSensitiveTargetKeys()
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY, JsonMaskingConfig.TargetKeyMode.ALLOW)
                        .maskNumericValuesWith(2)
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY, JsonMaskingConfig.TargetKeyMode.ALLOW)
                        .obfuscationLength(0)
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY, JsonMaskingConfig.TargetKeyMode.ALLOW)
                        .obfuscationLength(4)
                        .maskNumericValuesWith(8)
                        .build(),
                JsonMaskingConfig.custom(DEFAULT_TARGET_KEYS_WITH_JSON_PATH_KEY, JsonMaskingConfig.TargetKeyMode.ALLOW)
                        .obfuscationLength(4)
                        .build());
    }

    private static RandomJsonGenerator createRandomJsonGeneratorForDefaultKeys() {
        return new RandomJsonGenerator(RandomJsonGeneratorConfig.builder()
                .setTargetKeys(DEFAULT_TARGET_KEYS)
                .createConfig());
    }
}
