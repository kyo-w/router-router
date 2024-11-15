package router.server.publish;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Json编码器
 */
public class ServerEncoder implements Encoder.Text<ProgressTask> {
    private static final JsonMapper jsonMapper = new JsonMapper();

    @Override
    public void destroy() {
    }

    @Override
    public void init(EndpointConfig arg0) {
    }
    @Override
    public String encode(ProgressTask result) throws EncodeException {
        try {
            return jsonMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}