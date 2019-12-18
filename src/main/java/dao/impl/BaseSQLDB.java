package dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


/**
 *
 * <code>BaseSQLDB<code>
 * <strong></strong>
 * <p>说明：提供insert、update、select、where四种SQL组成的拼接操作
 * <li></li>
 * </p>
 * @since NC6.5
 * @version 2018年8月16日 下午2:10:24
 * @author sifh
 * @createDate  2018年8月16日 上午11:33:46
 */

public abstract class BaseSQLDB {

    protected Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     *
     * <p>说明：拼接SQL语句的select部分 select ** from **
     * <li></li>
     * </p>
     * @param table 表名
     * @param fields 字段名称列表
     * @return SQL语句或者空字符串（如果表名或者字段名是null或者空字符串，或者fields是null）
     * @date 2018年8月20日 下午3:19:33
     * @since NC6.5
     */
    protected static String buildeSelectSQL(String table, List<String> fields) {
        if (table == null || "".equals(table)) {
            return "";
        }

        if (fields == null || fields.isEmpty()) {
            return "";
        }

        for(String field: fields) {
            if (field == null || "".equals(field)) {
                return "";
            }
        }

        String sql = "SELECT " + String.join(",", fields) + " from " + table + " ";
        return sql;
    }

    /**
     *
     * <p>说明：拼接参数化的insert语句
     * <li></li>
     * </p>
     * @param table 表名
     * @param data 要新增的数据，map类型，key是字段名称，value是字段值
     * @param placeholder 参数化SQL语句中的占位符
     * @param values 参数化SQL的参数列表，新的参数会追加在列表后面
     * @return SQL语句或者空字符串（如果表名、字段名、占位符是null或者空字符串，或者data、values是null）
     * @date 2018年8月20日 下午3:27:08
     * @since NC6.5
     */
    protected static String buildeInsertSQLWithParam(String table, Map<String, Object> data, String placeholder, List<Object> values) {
        if (table == null || "".equals(table)) {
            return "";
        }

        if (data == null || data.isEmpty()) {
            return "";
        }

        if (placeholder == null || "".equals(placeholder)) {
            return "";
        }

        if (values == null) {
            return "";
        }

        ArrayList<String> fields = new ArrayList<String>();
        ArrayList<String> valuePlaceholder = new ArrayList<String>();
        String field;
        Object value;

        for(Entry<String, Object> entry: data.entrySet()) {
            field = entry.getKey();
            value = entry.getValue();
            if (field == null || "".equals(field)) {
                return "";
            }
            fields.add(field);
            valuePlaceholder.add(placeholder);
            values.add(value);
        }

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into " + table + "(")
                .append(String.join(",", fields))
                .append(") values(").append(String.join(",", valuePlaceholder)).append(")");

        String sql = sqlBuilder.toString();
        return sql;
    }

    /**
     *
     * <p>
     * 说明：拼接参数化的批量insert语句
     * <li></li>
     * </p>
     *
     * @param table
     * @param fieldSet 数据表字段
     * @param data List类型，List的数据项是map类型，key是字段名称，value是字段值
     * @param placeholder 参数化SQL语句中的占位符
     * @param values 参数化SQL的参数列表，新的参数会追加在列表后面
     * @return SQL语句或者空字符串（如果表名、字段名、占位符是null或者空字符串，或者data、values是null）
     * @date 2019年11月26日 下午1:35:31
     * @since NC6.5
     */
    protected static String buildeBatchInsertSQLWithParam(String table, Set<String> fieldSet,
                                                          List<Map<String, Object>> data, String placeholder, List<Object> values) {
        if (table == null || "".equals(table)) {
            return "";
        }

        if (fieldSet == null || fieldSet.isEmpty()) {
            return "";
        }

        for (String field : fieldSet) {
            if (field == null || "".equals(field)) {
                return "";
            }
        }

        if (data == null || data.isEmpty()) {
            return "";
        }

        if (placeholder == null || "".equals(placeholder)) {
            return "";
        }

        if (values == null) {
            return "";
        }

        ArrayList<String> fields = new ArrayList<String>(fieldSet);
        ArrayList<String> valuePlaceholder = new ArrayList<String>();
        ArrayList<String> valueSqls = new ArrayList<String>();

        fields.forEach(field -> {
            valuePlaceholder.add(placeholder);
        });
        String valueSql = "(" + String.join(",", valuePlaceholder) + ")";

        data.forEach(item -> {
            fields.forEach(field -> {
                values.add(item.get(field));
            });
            valueSqls.add(valueSql);
        });

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("insert into " + table + "(").append(String.join(",", fields))
                .append(") values").append(String.join(",", valueSqls));

        String sql = sqlBuilder.toString();
        return sql;
    }

    /**
     *
     * <p>
     * 说明：拼接参数化的SQL语句的where部分，只能是等值判断 **=** and/or **=**
     * <li></li>
     * </p>
     *
     * @param data 查询条件，map类型，key是字段名称，value是字段值
     * @param placeholder 参数化SQL语句中的占位符
     * @param logic 逻辑运算符，目前只能是and或者or
     * @param values 参数化SQL的参数列表，新的参数会追加在列表后面
     * @return SQL语句或者空字符串（如果字段名、占位符是null或者空字符串，或者data、values是null，或者logic不是and或者or）
     * @date 2018年8月20日 下午3:35:13
     * @since NC6.5
     */
    protected static String buildeWhereSQLWithParam(Map<String, Object> data, String placeholder, String logic, List<Object> values) {
        if (data == null || data.isEmpty()) {
            return "";
        }

        if (placeholder == null || "".equals(placeholder)) {
            return "";
        }

        if (values == null) {
            return "";
        }

        if (!("and".equals(logic) || "or".equals(logic))) {
            return "";
        }

        ArrayList<String> fields = new ArrayList<String>();
        String field;
        Object value;

        for(Entry<String, Object> entry: data.entrySet()) {
            field = entry.getKey();
            value = entry.getValue();
            if (field == null || "".equals(field)) {
                return "";
            }
            fields.add(field + "=" + placeholder);
            values.add(value);
        }

        String sql = String.join(" " + logic + " ", fields);
        return sql;
    }

    /**
     *
     * <p>说明：拼接没有限制条件的参数化的update语句  update set **=**,**=**
     * <li></li>
     * </p>
     * @param table 表名
     * @param data 要更新的字段和值，map类型，key是字段名称，value是字段值
     * @param placeholder 参数化SQL语句中的占位符
     * @param values 参数化SQL的参数列表，新的参数会追加在列表后面
     * @return SQL语句或者空字符串（如果表名、字段名、占位符是null或者空字符串，或者data、values是null）
     * @date 2018年8月20日 下午3:44:47
     * @since NC6.5
     */
    protected static String buildeUpdateSQLWithParam(String table, Map<String, Object> data, String placeholder, List<Object> values) {
        if (table == null || "".equals(table)) {
            return "";
        }

        if (data == null || data.isEmpty()) {
            return "";
        }

        if (placeholder == null || "".equals(placeholder)) {
            return "";
        }

        if (values == null) {
            return "";
        }

        ArrayList<String> fields = new ArrayList<String>();
        String field;
        Object value;

        for(Entry<String, Object> entry: data.entrySet()) {
            field = entry.getKey();
            value = entry.getValue();
            if (field == null || "".equals(field)) {
                return "";
            }
            fields.add(field + "=" + placeholder);
            values.add(value);
        }

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("update " + table + " set ")
                .append(String.join(",", fields));

        String sql = sqlBuilder.toString();
        return sql;
    }

    public static void main(String[] args) {
        String emptyString = "";

        String table = "test";
        String placeholder = "?";

        Map<String, Object> data;
        List<Object> values;
        String logic;
        List<String> fields;

        System.out.println("测试select---------------------------");
        // 测试select 正常情况
        fields = new ArrayList<>();
        fields.add("123");
        System.out.println(buildeSelectSQL(table, fields));

        // 测试select 正常情况
        fields = new ArrayList<>();
        fields.add("123");
        fields.add("1234");
        System.out.println(buildeSelectSQL(table, fields));

        // 测试select null table
        fields = new ArrayList<>();
        fields.add("123");
        fields.add("1234");
        System.out.println(buildeSelectSQL(null, fields));

        // 测试select "" table
        fields = new ArrayList<>();
        fields.add("123");
        fields.add("1234");
        System.out.println(buildeSelectSQL(emptyString, fields));

        // 测试select empty field
        fields = new ArrayList<>();
        System.out.println(buildeSelectSQL(table, fields));

        // 测试select null fields
        fields = null;
        System.out.println(buildeSelectSQL(table, fields));

        // 测试select null field
        fields = new ArrayList<>();
        fields.add("123");
        fields.add(null);
        fields.add("1234");
        System.out.println(buildeSelectSQL(table, fields));

        // 测试select "" field
        fields = new ArrayList<>();
        fields.add("123");
        fields.add(emptyString);
        fields.add("1234");
        System.out.println(buildeSelectSQL(table, fields));
        System.out.println("测试select---------------------------end");

        System.out.println("\n");

        System.out.println("测试insert---------------------------");
        // 测试insert 正常情况
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeInsertSQLWithParam(table, data, placeholder, values));
        System.out.println(values);

        // 测试insert 正常情况
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        data.put("1234", 1234);
        values.add("dfsfa");
        System.out.println(buildeInsertSQLWithParam(table, data, placeholder, values));
        System.out.println(values);

        // 测试insert null table
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeInsertSQLWithParam(null, data, placeholder, values));
        System.out.println(values);

        // 测试insert "" table
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeInsertSQLWithParam(emptyString, data, placeholder, values));
        System.out.println(values);

        // 测试insert null data
        data = null;
        values = new ArrayList<>();
        System.out.println(buildeInsertSQLWithParam(table, data, placeholder, values));
        System.out.println(values);

        // 测试insert "" field
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put(emptyString, 123);
        System.out.println(buildeInsertSQLWithParam(table, data, placeholder, values));
        System.out.println(values);

        // 测试insert null field
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put(null, 123);
        System.out.println(buildeInsertSQLWithParam(table, data, placeholder, values));
        System.out.println(values);

        // 测试insert null placeholder
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeInsertSQLWithParam(table, data, null, values));
        System.out.println(values);

        // 测试insert "" placeholder
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeInsertSQLWithParam(table, data, emptyString, values));
        System.out.println(values);

        // 测试insert null values
        data = new HashMap<>();
        values = null;
        data.put(emptyString, 123);
        System.out.println(buildeInsertSQLWithParam(table, data, placeholder, values));
        System.out.println(values);
        System.out.println("测试insert---------------------------end");


        System.out.println("\n");

        System.out.println("测试where---------------------------");
        // 测试where 正常情况
        logic = "and";
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeWhereSQLWithParam(data, placeholder, logic, values));
        System.out.println(values);

        // 测试where 正常情况
        logic = "or";
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        data.put("1234", 1234);
        values.add("dfsfa");
        System.out.println(buildeWhereSQLWithParam(data, placeholder, logic, values));
        System.out.println(values);

        // 测试where null logic
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeWhereSQLWithParam(data, placeholder, null, values));
        System.out.println(values);

        // 测试where "" logic
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeWhereSQLWithParam(data, placeholder, emptyString, values));
        System.out.println(values);

        // 测试where other logic
        logic = "other";
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeWhereSQLWithParam(data, placeholder, logic, values));
        System.out.println(values);

        // 测试where null data
        data = null;
        values = new ArrayList<>();
        System.out.println(buildeWhereSQLWithParam(data, placeholder, logic, values));
        System.out.println(values);

        // 测试where "" field
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put(emptyString, 123);
        System.out.println(buildeWhereSQLWithParam(data, placeholder, logic, values));
        System.out.println(values);

        // 测试where null field
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put(null, 123);
        System.out.println(buildeWhereSQLWithParam(data, placeholder, logic, values));
        System.out.println(values);

        // 测试where null placeholder
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeWhereSQLWithParam(data, null, logic, values));
        System.out.println(values);

        // 测试where "" placeholder
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeWhereSQLWithParam(data, emptyString, logic, values));
        System.out.println(values);

        // 测试where null values
        data = new HashMap<>();
        values = null;
        data.put("123", 123);
        System.out.println(buildeWhereSQLWithParam(data, placeholder, logic, values));
        System.out.println(values);
        System.out.println("测试where---------------------------end");

        System.out.println("\n");

        System.out.println("测试update---------------------------");
        // 测试update 正常情况
        logic = "and";
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeUpdateSQLWithParam(table, data, placeholder, values));
        System.out.println(values);

        // 测试update 正常情况
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        data.put("1234", 1234);
        values.add("dfsfa");
        System.out.println(buildeUpdateSQLWithParam(table, data, placeholder, values));
        System.out.println(values);

        // 测试update null table
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeUpdateSQLWithParam(null, data, placeholder, values));
        System.out.println(values);

        // 测试update "" table
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeUpdateSQLWithParam(emptyString, data, placeholder, values));
        System.out.println(values);

        // 测试update null data
        data = null;
        values = new ArrayList<>();
        System.out.println(buildeUpdateSQLWithParam(table, data, placeholder, values));
        System.out.println(values);

        // 测试update "" field
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put(emptyString, 123);
        System.out.println(buildeUpdateSQLWithParam(table, data, placeholder, values));
        System.out.println(values);

        // 测试update null field
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put(null, 123);
        System.out.println(buildeUpdateSQLWithParam(table, data, placeholder, values));
        System.out.println(values);

        // 测试update null placeholder
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeUpdateSQLWithParam(table, data, null, values));
        System.out.println(values);

        // 测试update "" placeholder
        data = new HashMap<>();
        values = new ArrayList<>();
        data.put("123", 123);
        System.out.println(buildeUpdateSQLWithParam(table, data, emptyString, values));
        System.out.println(values);

        // 测试update null values
        data = new HashMap<>();
        values = null;
        data.put(emptyString, 123);
        System.out.println(buildeUpdateSQLWithParam(table, data, placeholder, values));
        System.out.println(values);
        System.out.println("测试update---------------------------end");


    }

}
