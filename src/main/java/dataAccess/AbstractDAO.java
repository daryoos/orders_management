package dataAccess;

import connection.DataBaseConnection;
import model.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static java.lang.Integer.parseInt;

public class AbstractDAO<T> {
    private static Connection connection;
    private static PreparedStatement preparedStatement;
    private static CallableStatement callableStatement;
    private static ResultSet resultSet;
    private static boolean hadResults;
    private static String query;

    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     *
     * @return extracts all instances from the matching table in MySQL to the Class T
     */
    public List<T> extractAll() {
        try {
            connection = DataBaseConnection.init();
            query = "select * from `" + type.getSimpleName().toLowerCase() + "`";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            return createObjects(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     *
     * @param instance the instance we want to extract from the table in MySQL. Instance is used only for the id
     * @return a list where we need only the first element
     */
    public List<T> extract(T instance) {
        try {
            connection = DataBaseConnection.init();
            Field[] fields = instance.getClass().getDeclaredFields();
            query = "select * from " + type.getSimpleName() + " where id = ?";
            preparedStatement = connection.prepareStatement(query);
            fields[0].setAccessible(true);
            preparedStatement.setObject(1, fields[0].get(instance));
            resultSet = preparedStatement.executeQuery();
            return createObjects(resultSet);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     *
     * @param instance is the instance we want to add to the MySQL table
     */
    public void add(T instance) {
        try {
            connection = DataBaseConnection.init();
            Field[] fields = instance.getClass().getDeclaredFields();
            StringBuilder fieldsName = new StringBuilder();
            StringBuilder whys = new StringBuilder();
            for (int i = 1; i < fields.length; i++) {
                fieldsName.append(fields[i].getName());
                whys.append("?");
                if (i < fields.length - 1) {
                    fieldsName.append(", ");
                    whys.append(", ");
                }
            }
            //System.out.println(fieldsName);
            //System.out.println(whys);

            String query = "insert into `" + type.getSimpleName().toLowerCase() + "` (" + fieldsName + ") values (" + whys +")";

            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int i = 1; i < fields.length; i++) {
                fields[i].setAccessible(true);
                StringBuilder fieldValue = new StringBuilder();
                fieldValue.append(fields[i].get(instance));
                //System.out.println(fieldValue);
                preparedStatement.setObject(i, fieldValue.toString());
            }
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                fields[0].setAccessible(true);
                fields[0].set(instance, resultSet.getInt(1));
            }

        } catch (SQLException |
                 IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *
     * @param instance is the instance we want to edit
     * @param newInstance is the instance that will replace instance values
     */
    public void edit(T instance, T newInstance) {
        try {
            connection = DataBaseConnection.init();
            Field[] fields = instance.getClass().getDeclaredFields();
            StringBuilder query = new StringBuilder();
            query.append("update `").append(type.getSimpleName().toLowerCase()).append("` set ");
            for (int i = 1; i < fields.length; i++) {
                fields[i].setAccessible(true);
                query.append(fields[i].getName());
                query.append(" = '");
                query.append(fields[i].get(newInstance));
                query.append("' ");
                if (i < type.getDeclaredFields().length - 1) {
                    query.append(", ");
                }
            }
            fields[0].setAccessible(true);
            query.append(" where id = '");
            query.append(fields[0].get(instance));
            query.append("'");
            System.out.println(query);

            Statement statement = connection.createStatement();
            statement.executeUpdate(query.toString());

            /*preparedStatement = connection.prepareStatement(query.toString());
            for (int i = 1; i < fields.length; i++) {
                fields[i].setAccessible(true);
                preparedStatement.setObject(i, fields[i].get(newInstance));
            }
            fields[0].setAccessible(true);
            preparedStatement.setObject(fields.length, fields[0].get(instance));
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();*/
        } catch (SQLException |
                 IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *
     * @param instance is the instance we want to delete
     */
    public void delete(T instance) {
        try {
            connection = DataBaseConnection.init();
            Field[] fields = instance.getClass().getDeclaredFields();
            StringBuilder query = new StringBuilder();
            query.append("delete from `").append(type.getSimpleName().toLowerCase()).append("` where id = ?");
            //System.out.println(query);

            preparedStatement = connection.prepareStatement(query.toString());
            fields[0].setAccessible(true);
            preparedStatement.setObject(1, fields[0].get(instance));
            System.out.println(preparedStatement);
            preparedStatement.execute();
        } catch (SQLException |
                 IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<T>();
        Constructor[] ctors = type.getDeclaredConstructors();
        Constructor ctor = null;
        for (int i = 0; i < ctors.length; i++) {
            ctor = ctors[i];
            if (ctor.getGenericParameterTypes().length == 0)
                break;
        }
        try {
            while (resultSet.next()) {
                ctor.setAccessible(true);
                T instance = (T)ctor.newInstance();
                for (Field field : type.getDeclaredFields()) {
                    String fieldName = field.getName();
                    Object value = resultSet.getObject(fieldName);
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(fieldName, type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (InstantiationException | IntrospectionException | SQLException | InvocationTargetException |
                 IllegalArgumentException | SecurityException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return list;
    }
}
