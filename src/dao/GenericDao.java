package dao;

import java.util.List;


public interface GenericDao<KEY, T> {

    List<T> findAll();

    T findOne(KEY id);
        
    void load(String path);
    
    void saveChanges();
}
