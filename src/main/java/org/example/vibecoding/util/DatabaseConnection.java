package org.example.vibecoding.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/nom_de_la_bdd";
    private static final String USER = "postgres";
    private static final String PASSWORD = "votre_mot_de_passe";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
