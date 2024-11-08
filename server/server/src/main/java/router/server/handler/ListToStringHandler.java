package router.server.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ListToStringHandler extends BaseTypeHandler<List> {
    private static ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List list, JdbcType jdbcType) throws SQLException {
        try {
            preparedStatement.setString(i, mapper.writeValueAsString(list));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List getNullableResult(ResultSet resultSet, String s) throws SQLException {
        String string = resultSet.getString(s);
        try {
            return mapper.readValue(string, List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String string = resultSet.getString(i);
        try {
            return mapper.readValue(string, List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }    }

    @Override
    public List getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String string = callableStatement.getString(i);
        try {
            return mapper.readValue(string, List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}