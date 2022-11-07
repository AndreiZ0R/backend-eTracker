package com.example.etracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class Repo {
    private static final String ITEMS_TABLE = "items";
    private static final String WORKERS_TABLE = "workers";
    private static final String REPORTS_TABLE = "reports";
    private static final String ACCOUNTS_TABLE = "Accounts";
    public static final String ITEM_COLUMN_NAMES = " (name, description, created_at, last_revised, revision_interval, location) ";
    public static final String WORKER_COLUMN_NAMES = " (full_name, company_name, phone, country_prefix, address, active) ";
    public static final String REPORT_COLUMN_NAMES = " (item_id, worker_id, type, date, link) ";
    public static final String ACCOUNT_COLUMN_NAMES = " (firstname, lastname, username, email, password, salt, role, worker_id) ";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public int delete(int index, String TABLE) {
        String sql = "DELETE FROM " + TABLE + String.format(" WHERE id=%s", index);
        int rowsAffected = jdbcTemplate.update(sql);
        jdbcTemplate.update("DELETE FROM reports WHERE item_id="+index);
        return (rowsAffected > 0) ? 1 : -1;
    }


    @Transactional
    public List<Map<String, Object>> getAll(String TABLE) {
        String sql = "SELECT * FROM " + TABLE;
        return jdbcTemplate.queryForList(sql);
    }

    @Transactional
    public Map<String, Object> getOne(int index, String TABLE) {
        String sql = "SELECT * FROM " + TABLE + String.format(" WHERE id=%s", index);
        return jdbcTemplate.queryForList(sql).get(0);
    }

    @Transactional
    public int update(int index, String col, String val, String TABLE) {
        String sql = "UPDATE " + TABLE + "SET %s=";
        int rowsAffected;
        if (col.equals("revision_interval") || col.equals("phone") || col.equals("country_prefix") || col.equals("item_id") || col.equals("worker_id")) {
            sql += "%d WHERE id=%s";
            rowsAffected = jdbcTemplate.update(String.format(sql, col, Integer.parseInt(val), index));
        } else {
            sql += "%s WHERE id=%s";
            rowsAffected = jdbcTemplate.update(String.format(sql, col, val, index));
        }
        return (rowsAffected > 0) ? 1 : -1;
    }

    @Transactional
    public int createItem(String name, String dsc, String cat, String lrev, int interval, String loc) {
        String sql = "INSERT INTO " + ITEMS_TABLE + ITEM_COLUMN_NAMES + " VALUES " + "('" + name + "','" + dsc + "','" + cat + "','" + lrev + "'," + interval + ",'" + loc + "')";
        int rowsAffected = this.jdbcTemplate.update(sql);
        return (rowsAffected > 0) ? 1 : -1;
    }

    @Transactional
    public int createWorker(String fname, String cname, int phone, int prefix, String addr, int active) {
        String sql = "INSERT INTO " + WORKERS_TABLE + WORKER_COLUMN_NAMES + " VALUES " + "('" + fname + "','" + cname + "'," + phone + "," + prefix + ",'" + addr + "'," + active + ")";
        int rowsAffected = jdbcTemplate.update(sql);
        return (rowsAffected > 0) ? 1 : -1;
    }

    @Transactional
    public int createReport(int item_id, int worker_id, String type, String date, String link) {
        String sql = "INSERT INTO " + REPORTS_TABLE + REPORT_COLUMN_NAMES + " VALUES " + "(" + item_id + "," + worker_id + ",'" + type + "','" + date + "','" + link + "')";
        int rowsAffected = jdbcTemplate.update(sql);
        return (rowsAffected > 0) ? 1 : -1;
    }

    @Transactional
    public int createAccount(String firstname, String lastname, String username, String email, String password, String role, int worker_id) throws NoSuchAlgorithmException {
        String badPass = password;
        password = password.trim();
        password = PasswordHash.generate(password, null);
        String sql = "INSERT INTO " + ACCOUNTS_TABLE + ACCOUNT_COLUMN_NAMES + " VALUES " + "('" + firstname + "','" + lastname + "','" + username + "','" + email + "','" + badPass + "',?,'" + role + "'," + worker_id + ")";
        jdbcTemplate.execute(sql, (PreparedStatementCallback<Boolean>) ps -> {
            ps.setBytes(1, PasswordHash.Salt);
            return ps.execute();
        });
        return 1;
    }

    public int login(String name, String password) throws NoSuchAlgorithmException {
        return checkCred(name, password);
    }

    private int checkCred(String name, String password) throws NoSuchAlgorithmException {
        String type;
        if (name.contains("@")) {
            type = "email";
        } else {
            type = "username";
        }
        String sql = "SELECT * FROM " + ACCOUNTS_TABLE + " WHERE " + type + "='" + name + "'";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        byte[] arr = jdbcTemplate.query(sql, new ResultSetExtractor<byte[]>() {

            @Override
            public byte[] extractData(ResultSet rs) throws SQLException, DataAccessException {
                rs.next();
                return rs.getBytes("salt");
            }
        });
        //password = PasswordHash.generate(password, arr);
        if (password.equals(result.get(0).get("password"))) {
            String email = (String) result.get(0).get("email");
            String username = (String) result.get(0).get("username");
            return 1;
        }

        return -1;
    }

}
