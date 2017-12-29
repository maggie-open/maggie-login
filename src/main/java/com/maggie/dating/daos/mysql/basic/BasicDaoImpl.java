package com.maggie.dating.daos.mysql.basic;

import org.springframework.stereotype.Component;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Component
public class BasicDaoImpl implements BasicDao {
	
	@PersistenceContext
    private EntityManager entityManager;
	
	
    public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void batchSave(List list) {
        for (int i = 0; i < list.size(); i++) {
        	entityManager.persist(list.get(i));
            if (i % 20 == 0) {
            	entityManager.flush();
            	entityManager.clear();
            }
        }
    }
  
    public void batchUpdate(List list) {
        for (int i = 0; i < list.size(); i++) {
        	entityManager.merge(list.get(i));
            if (i % 20 == 0) {
            	entityManager.flush();
            	entityManager.clear();
            }
        }
    }
    
    public void batchDelete(List list) {
        for (int i = 0; i < list.size(); i++) 
        {
        	entityManager.remove(list.get(i));
            if (i % 20 == 0) {
            	entityManager.flush();
            	entityManager.clear();
            }
        }
    }
	
	public PageData findPageByHql(String hql, int pageSize, int page)
	{
		PageData pageData = new PageData();
		Query query = entityManager.createQuery(hql);
		int totalCount = query.getResultList().size();
		query.setFirstResult((page - 1) * pageSize);
		if(pageSize>0)
			query.setMaxResults(pageSize);
		pageData.setPage(page);
		pageData.setPageSize(pageSize);
		pageData.setTotal(totalCount);
		List<?> temp = query.getResultList();
		pageData.setResult(temp);
		return pageData;
	}
	
	public <T> List<T> findByHql(String hql)
	{
		return entityManager.createQuery(hql).getResultList();
	}
	

	public PageData  findPageBySql(String sql, int pageSize, int page) {
		PageData pageData = new PageData();
		Query query = entityManager.createNativeQuery(sql);
		int totalCount = query.getResultList().size();
		query.setFirstResult((page - 1) * pageSize);
		if(pageSize > 0)
			query.setMaxResults(pageSize);
		pageData.setPage(page);
		pageData.setPageSize(pageSize);
		pageData.setTotal(totalCount);
		List<?> temp = query.getResultList();
		pageData.setResult(temp);
		return pageData;
	}

	public List findBySql(String sql) {
		return entityManager.createNativeQuery(sql).getResultList();
	}

	public <T> List<T> findBySql(String sql, Class resultClass) {
		return entityManager.createNativeQuery(sql,resultClass).getResultList();
	}

	public <T> List<T>  prepareCall(String prepareSql) {
		return entityManager.createNativeQuery(prepareSql).getResultList();
	}

	public <T> List<T>  prepareCall(String prepareSql, Class resultClass) {
		return entityManager.createNativeQuery(prepareSql,resultClass).getResultList();
	}

	public int prepareCallNoResult(String prepareSql) {
		return entityManager.createNativeQuery(prepareSql).executeUpdate();
	}

	public int executeSql(String sql) {
		return entityManager.createNativeQuery(sql).executeUpdate();
	}
	
	public <T> List<T> executeQuerySql(String sql){
		return entityManager.createNativeQuery(sql).getResultList();
	}
	
	public <T> List<T> executeQuerySql(String sql,Class resultClass){
		return entityManager.createNativeQuery(sql, resultClass).getResultList();
	}
}
