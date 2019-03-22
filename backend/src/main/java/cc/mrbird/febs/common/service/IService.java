package cc.mrbird.febs.common.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IService<T> {

	List<T> selectAll();

	T selectByKey(Object key);

	int save(T entity);

	int delete(Object key);

	int batchDelete(List<String> list, String property, Class<T> clazz);

	int updateAll(T entity);

	int updateNotNull(T entity);

	List<T> selectByExample(Object example);
}