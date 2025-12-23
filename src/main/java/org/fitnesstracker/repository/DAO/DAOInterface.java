package org.fitnesstracker.repository.DAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DAOInterface<T> {
    // CRUD

    // CREATE
    public Integer create(T entity) throws SQLException;
    // READ BY ID

    public Optional<T> findById(Integer id) throws SQLException;

    // READ ALL
    public List<T> findAll() throws SQLException;
    // UPDATE
    public T updateById(T entity) throws SQLException;
    // DELETE
    public boolean deleteById(Integer id) throws SQLException;
}
