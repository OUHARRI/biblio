package com.biblio.libs;

import com.biblio.core.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class db implements AutoCloseable {
    protected Connection connection = null;
    protected String _table = null;
    protected String[] _primaryKey = {"id"};
    protected String _foreignKey = null;

    protected Boolean _softDelete = true;

    private boolean inTransaction = false;

    public db(String tableName, String[] primaryKey) {
        this.connection = database.getConnection();
        this._table = tableName;
        if (primaryKey != null && primaryKey.length > 0) {
            this._primaryKey = primaryKey;
        }
    }

    public void beginTransaction() throws SQLException {
        if (!inTransaction) {
            this.connection.setAutoCommit(false);
            this.inTransaction = true;
        }
    }

    public void commitTransaction() throws SQLException {
        if (inTransaction) {
            this.connection.commit();
            this.connection.setAutoCommit(true);
            this.inTransaction = false;
        }
    }

    public void rollbackTransaction() throws SQLException {
        if (inTransaction) {
            this.connection.rollback();
            this.connection.setAutoCommit(true);
            this.inTransaction = false;
        }
    }

    public List<Map<String, String>> getAll() {
        List<Map<String, String>> resultList = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + this._table;

            // Ajoutez la condition pour le soft delete si _softDelete est true
            if (this._softDelete) {
                query += " WHERE delete_at IS NULL";
            }

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, String> rowData = new HashMap<>();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    rowData.put(columnName, resultSet.getString(columnName));
                }

                resultList.add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }


    public boolean create(Map<String, String> data) throws SQLException {
        try {
            StringBuilder columns = new StringBuilder();
            StringBuilder values = new StringBuilder();

            for (Map.Entry<String, String> entry : data.entrySet()) {
                columns.append(entry.getKey()).append(",");
                values.append("?").append(",");
            }

            columns.setLength(columns.length() - 1);
            values.setLength(values.length() - 1);

            String query = "INSERT INTO " + _table + " (" + columns.toString() + ") VALUES (" + values.toString() + ")";
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);

            int index = 1;
            for (String value : data.values()) {
                preparedStatement.setString(index++, value);
            }

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw e;
        }
    }

    public Map<String, String> read(String[] ids) {
        try {
            // Construisez la clause WHERE en fonction des clés primaires
            StringBuilder whereClause = new StringBuilder();
            for (int i = 0; i < _primaryKey.length; i++) {
                whereClause.append(_primaryKey[i]).append(" = ?");
                if (i < _primaryKey.length - 1) {
                    whereClause.append(" AND ");
                }
            }

            String query = "SELECT * FROM " + this._table + " WHERE " + whereClause.toString();

            // Ajoutez la condition pour le soft delete si _softDelete est true
            if (this._softDelete) {
                query += " AND delete_at IS NULL";
            }

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);

            for (int i = 0; i < ids.length; i++) {
                preparedStatement.setString(i + 1, ids[i]);
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Map<String, String> rowData = new HashMap<>();

                // Remplir la map avec les données de la ligne
                // Utilisez resultSet.getString("nom_de_la_colonne") pour récupérer les valeurs

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    rowData.put(columnName, resultSet.getString(columnName));
                }

                return rowData;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, String> read(String columnName, String value) {
        try {
            String query = "SELECT * FROM " + this._table + " WHERE " + columnName + " = ?";

            // Ajoutez la condition pour le soft delete si _softDelete est true
            if (this._softDelete) {
                query += " AND delete_at IS NULL";
            }

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, value);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Map<String, String> rowData = new HashMap<>();

                // Remplir la map avec les données de la ligne
                // Utilisez resultSet.getString("nom_de_la_colonne") pour récupérer les valeurs

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 1; i <= columnCount; i++) {
                    String column = metaData.getColumnName(i);
                    rowData.put(column, resultSet.getString(column));
                }

                return rowData;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public List<Map<String, String>> readAll(String columnName, String value) {
        List<Map<String, String>> resultList = new ArrayList<>();
        try {
            String query = "SELECT * FROM " + this._table + " WHERE " + columnName + " = ?";

            // Ajoutez la condition pour le soft delete si _softDelete est true
            if (this._softDelete) {
                query += " AND delete_at IS NULL";
            }

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);
            preparedStatement.setString(1, value);

            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, String> rowData = new HashMap<>();

                for (int i = 1; i <= columnCount; i++) {
                    String column = metaData.getColumnName(i);
                    rowData.put(column, resultSet.getString(column));
                }

                resultList.add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public List<Map<String, String>> readAll(String[] ids) {
        List<Map<String, String>> resultList = new ArrayList<>();
        try {
            // Construisez la clause WHERE en fonction des clés primaires
            StringBuilder whereClause = new StringBuilder();
            for (int i = 0; i < _primaryKey.length; i++) {
                whereClause.append(_primaryKey[i]).append(" = ?");
                if (i < _primaryKey.length - 1) {
                    whereClause.append(" AND ");
                }
            }

            String query = "SELECT * FROM " + this._table + " WHERE " + whereClause.toString();

            // Ajoutez la condition pour le soft delete si _softDelete est true
            if (this._softDelete) {
                query += " AND delete_at IS NULL";
            }

            PreparedStatement preparedStatement = this.connection.prepareStatement(query);

            for (int i = 0; i < ids.length; i++) {
                preparedStatement.setString(i + 1, ids[i]);
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, String> rowData = new HashMap<>();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    rowData.put(columnName, resultSet.getString(columnName));
                }

                resultList.add(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultList;
    }


    public boolean update(Map<String, String> data,String[] ids) {
        try {
            // Construisez la clause SET pour la mise à jour
            StringBuilder setClause = new StringBuilder();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                setClause.append(entry.getKey()).append(" = ?,");
            }
            setClause.setLength(setClause.length() - 1); // Supprimer la virgule finale

            // Construisez la clause WHERE en fonction des clés primaires
            StringBuilder whereClause = new StringBuilder();
            for (int i = 0; i < _primaryKey.length; i++) {
                whereClause.append(_primaryKey[i]).append(" = ?");
                if (i < _primaryKey.length - 1) {
                    whereClause.append(" AND ");
                }
            }

            String query = "UPDATE " + _table + " SET " + setClause.toString() + " WHERE " + whereClause.toString();
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);

            // Définissez les nouvelles valeurs
            int index = 1;
            for (String value : data.values()) {
                preparedStatement.setString(index++, value);
            }

            // Définissez les valeurs des clés primaires
            for (String id : ids) {
                preparedStatement.setString(index++, id);
            }

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String[] ids) {
        try {
            // Construisez la clause WHERE en fonction des clés primaires
            StringBuilder whereClause = new StringBuilder();
            for (int i = 0; i < _primaryKey.length; i++) {
                whereClause.append(_primaryKey[i]).append(" = ?");
                if (i < _primaryKey.length - 1) {
                    whereClause.append(" AND ");
                }
            }

            String query = "DELETE FROM " + _table + " WHERE " + whereClause.toString();
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);

            // Définissez les valeurs des clés primaires
            for (int i = 0; i < ids.length; i++) {
                preparedStatement.setString(i + 1, ids[i]);
            }

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean softDelete(String[] ids) {
        try {
            StringBuilder setClause = new StringBuilder();
            setClause.append("delete_at = ?");

            String query = "UPDATE " + _table + " SET " + setClause.toString() + " WHERE ";

            StringBuilder whereClause = new StringBuilder();
            for (int i = 0; i < _primaryKey.length; i++) {
                whereClause.append(_primaryKey[i]).append(" = ?");
                if (i < _primaryKey.length - 1) {
                    whereClause.append(" AND ");
                }
            }

            query += whereClause.toString();
            PreparedStatement preparedStatement = this.connection.prepareStatement(query);

            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
            preparedStatement.setTimestamp(1, currentTimestamp);

            for (int i = 0; i < ids.length; i++) {
                preparedStatement.setString(i + 2, ids[i]);
            }

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void close() throws Exception {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}