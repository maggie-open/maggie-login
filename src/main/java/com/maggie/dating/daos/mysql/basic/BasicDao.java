package com.maggie.dating.daos.mysql.basic;

import java.util.List;

public interface BasicDao {
	
	/**
	 * 批量保存
	 * @param <T>
	 * @param list 要保存的对象集合
	 */
	public <T> void batchSave(List<T> list);
	
	/**
	 * 批量更新
	 * @param <T>
	 * @param list 要更新的对象集合
	 */
    public <T> void batchUpdate(List<T> list);
    
    /**
     * 批量删除
     * @param <T>
     * @param list 要删除的对象集合
     */
    public <T> void batchDelete(List<T> list);
    
    /**
     * 根据hql按分页返回数据
     * @param hql 查询语句
     * @param pageSize 每页行数
     * @param page 设置第几页
     * @return PageData
     */
	public PageData findPageByHql(String hql, int pageSize, int page);
	
	/**
     * 根据sql按分页返回数据
     * @param sql 查询语句
     * @param pageSize 每页行数
     * @param page 设置第几页
     * @return PageData
     */
	public PageData findPageBySql(String sql, int pageSize, int page);
	
	/**
	 * 根据hql返回数据
	 * @param <T>
	 * @param hql 查询语句
	 * @return 结果集
	 */
	public <T> List<T> findByHql(String hql);
	
	/**
	 * 根据sql返回数据
	 * @param <T>
	 * @param sql 查询语句
	 * @return 结果集
	 */
	public <T> List<T> findBySql(String sql);
	
	/**
	 * 根据sql返回数据
	 * @param <T>
	 * @param sql 查询语句
	 * @param resultClass 结果集对应的类
	 * @return 结果集
	 */
	public <T> List<T> findBySql(String sql, Class resultClass);
	
	/**
	 * 调用存储过程并返回结果集
	 * @param <T>
	 * @param prepareSql 执行存储过程的语句
	 * @return 结果集
	 */
	public <T> List<T> prepareCall(String prepareSql);
	
	/**
	 * 调用存储过程并返回结果集
	 * @param <T>
	 * @param prepareSql 执行存储过程的语句 {call prepare()}
	 * @param resultClass 结果集对应的类
	 * @return 结果集
	 */
	public <T> List<T> prepareCall(String prepareSql, Class resultClass);
	
	/**
	 * 调用存储过程
	 * @param prepareSql 执行存储过程的语句
	 * @return <0失败
	 */
	public int prepareCallNoResult(String prepareSql);
	
	/**
	 * 执行非查询的SQL
	 * @param sql 要执行的SQL
	 * @return 影响结果的记录数
	 */
	public int executeSql(String sql);
	
	/**
	 * 执行查询的SQL
	 * @param <T>
	 * @param sql 要执行的SQL
	 * @return 结果集
	 */
	public <T> List<T> executeQuerySql(String sql);
	
	/**
	 * 执行查询的SQL
	 * @param <T>
	 * @param sql 要执行的SQL
	 * @param resultClass 结果集对应的类
	 * @return 结果集
	 */
	public <T> List<T> executeQuerySql(String sql, Class resultClass);
	
}
