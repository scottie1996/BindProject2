package dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

/**
 *
 * <code>ISQLDBDao<code> <strong></strong>
 * <p>
 * 说明：对使用SQL的数据库执行查询、更新、新增操作
 * <li></li>
 * </p>
 *
 * @since NC6.5
 * @version 2018年8月16日 上午11:33:46
 * @author sifh
 * @createDate 2018年8月16日 上午11:33:46
 */

public interface ISQLDBDao {

    /**
     *
     * <p>
     * 说明：使用主键查询数据
     * <li></li>
     * </p>
     *
     * @param idKey
     * @param idValue
     * @param fields 查询结果中需要的字段名称
     * @param future
     * @date 2018年8月20日 下午3:07:59
     * @since NC6.5
     */
    void queryByID(String table, String idKey, String idValue, List<String> fields,
                   Future<List<JsonObject>> future);

    /**
     *
     * <p>
     * 说明：使用指定的条件查询数据，目前条件只支持等值判断
     * <li></li>
     * </p>
     *
     * @param fields 查询结果中需要的字段名称
     * @param conditions 查询条件，map类型，key是字段名称，value是字段值
     * @param future
     * @date 2018年8月20日 下午3:10:09
     * @since NC6.5
     */
    void query(String table, List<String> fields, Map<String, Object> conditions,
               Future<List<JsonObject>> future);

    /**
     *
     * <p>
     * 说明：使用用户指定的SQL语句查询
     * <li></li>
     * </p>
     *
     * @param query 查询SQL语句
     * @param future
     * @date 2018年8月20日 下午3:10:13
     * @since NC6.5
     */
    void query(String table, String query, Future<List<JsonObject>> future);

    /**
     *
     * <p>
     * 说明：插入一条数据
     * <li></li>
     * </p>
     *
     * @param data 要新增的数据，map类型，key是字段名称，value是字段值
     * @param future
     * @date 2018年8月20日 下午3:10:16
     * @since NC6.5
     */
    void insert(String table, Map<String, Object> data, Future<Boolean> future);



    void insert(String table , String data , Future<JsonObject> future);
    /**
     *
     * <p>
     * 说明：批量插入数据
     * <li></li>
     * </p>
     *
     * @param table
     * @param fieldSet 数据表字段
     * @param data List类型，List的数据项是map类型，key是字段名称，value是字段值
     * @param future
     * @date 2019年11月26日 下午1:53:15
     * @since NC6.5
     */
    void insert(String table, Set<String> fieldSet, List<Map<String, Object>> data,
                Future<JsonObject> future);

    /**
     *
     * <p>
     * 说明：使用主键更新数据
     * <li>返回结果是根据数据库返回的受影响条数是否等于1判断的</li>
     * <li>对于MySQL如果更新的值和原始值相同，那么受影响条数也是0，也会返回false</li>
     * </p>
     *
     * @param idKey
     * @param idValue
     * @param data 要更新的字段和值，map类型，key是字段名称，value是字段值
     * @param future
     * @date 2018年8月20日 下午3:10:20
     * @since NC6.5
     */
    void updateByID(String table, String idKey, String idValue, Map<String, Object> data,
                    Future<Boolean> future);

    /**
     *
     * <p>
     * 说明：使用指定的条件更新数据，要求只更新一条数据，如果更新数量不是一条，返回false，但是不会回滚数据库
     * <li>返回结果是根据数据库返回的受影响条数是否等于1判断的</li>
     * <li>对于MySQL如果更新的值和原始值相同，那么受影响条数也是0，也会返回false</li>
     * </p>
     *
     * @param data 要更新的字段和值，map类型，key是字段名称，value是字段值
     * @param conditions 更新条件，map类型，key是字段名称，value是字段值
     * @param future
     * @date 2018年8月20日 下午3:10:23
     * @since NC6.5
     */
    void updateSingle(String table, Map<String, Object> data, Map<String, Object> conditions,
                      Future<Boolean> future);

    /**
     *
     * <p>
     * 说明：使用指定的条件更新数据
     * <li></li>
     * </p>
     *
     * @param data 要更新的字段和值，map类型，key是字段名称，value是字段值
     * @param conditions 更新条件，map类型，key是字段名称，value是字段值
     * @param future
     * @date 2018年8月20日 下午3:10:23
     * @since NC6.5
     */
    void update(String table, Map<String, Object> data, Map<String, Object> conditions,
                Future<JsonObject> future);

    /**
     *
     * <p>
     * 说明：使用用户指定的SQL语句更新
     * <li></li>
     * </p>
     *
     * @param update 更新SQL语句
     * @param future
     * @date 2018年8月20日 下午3:10:26
     * @since NC6.5
     */
    void update(String table, String update, Future<JsonObject> future);

    /**
     *
     * <p>
     * 说明：使用用户指定的SQL语句更新，要求只更新一条数据，如果更新数量不是一条，返回false，但是不会回滚数据库
     * <li>返回结果是根据数据库返回的受影响条数是否等于1判断的</li>
     * <li>对于MySQL如果更新的值和原始值相同，那么受影响条数也是0，也会返回false</li>
     * </p>
     *
     * @param update 更新SQL语句
     * @param future
     * @date 2018年8月20日 下午3:10:26
     * @since NC6.5
     */
    void updateSingle(String table, String update, Future<Boolean> future);

    /**
     *
     * <p>
     * 说明：
     * <li></li>
     * </p>
     *
     * @date 2018年8月20日 下午3:10:30
     * @since NC6.5
     */
    void close();

    /**
     *
     * <p>
     * 说明：
     * <li></li>
     * </p>
     *
     * @param handler
     * @date 2018年8月20日 下午3:10:33
     * @since NC6.5
     */
    void close(Handler<AsyncResult<Void>> handler);
}
