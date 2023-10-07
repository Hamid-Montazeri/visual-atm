package com.example.main.database;

import java.util.List;

public interface Dao<T> {

    long save(T t);

    T getById(long id);

    void update(T t, long id);

    void deleteById(long id);

    void deleteAll();

    List<T> getAll();

}
