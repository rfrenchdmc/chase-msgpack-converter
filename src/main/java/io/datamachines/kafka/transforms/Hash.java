package io.datamachines.kafka.transforms;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SimpleConfig;

import java.util.Map;

public class Hash<R extends ConnectRecord<R>> implements Transformation<R> {

    public static final String OVERVIEW_DOC =
            "Runs a hash on the value and stores the result as the Key";

    private static final String FIELD_CONFIG = "hash";

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(FIELD_CONFIG, ConfigDef.Type.STRING, "SHA-256", ConfigDef.Importance.MEDIUM, "Hash algorithm to apply");

    private static final String PURPOSE = "Key Hash";

    private String hashFunction;

    @Override
    public void configure(Map<String, ?> props) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, props);
        hashFunction = config.getString(FIELD_CONFIG);
    }

    @Override
    public R apply(R record) {
        Map doc = (Map)record.value();
        try {
            String value = new ObjectMapper().writeValueAsString(doc);
            String digest = null;

            switch (hashFunction.toLowerCase()) {
                case "sha256":
                case "sha-256":
                    digest = DigestUtils.sha256Hex(value);
                    break;
                case "md5":
                    digest = DigestUtils.md5Hex(value);
                    break;
                default:
                    throw new KafkaException("Unknown Digest: " + hashFunction);
            }

            return record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(), digest, record.valueSchema(), record.value(), record.timestamp());
        } catch (JsonProcessingException pe) {
            pe.printStackTrace();
            throw new KafkaException(pe);
        }
    }

    @Override
    public void close() {}

    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }
}
