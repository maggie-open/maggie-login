package com.maggie.dating.daos.mysql.basic;

import java.util.List;

public class PageData {
	
	/**
	 * 结果集
	 */
	private List<?> result = null;
	/**
	 * 总记录数
	 */
	private long total; 
	/**
	 * 当前页,即第几页
	 */
	private int page; 
	/**
	 * 每页大小,即每页的行数 默认10
	 */
	private int pageSize = 10; 
	/**
	 * 总页数
	 */
	private long pageCount = 0;
	
	public List<?> getResult() {
		return result;
	}
	public void setResult(List<?> result) {
		this.result = result;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		pageCount = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;
		this.total = total;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public long getPageCount() {
		return pageCount;
	}
	public void setPageCount(long pageCount) {
		this.pageCount = pageCount;
	}
}

