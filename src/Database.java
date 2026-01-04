import com.mysql.cj.jdbc.exceptions.ConnectionFeatureNotAvailableException;
import com.mysql.cj.protocol.Resultset;

import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/Tetris?serverTimezone=UTC&createDatabaseIfNotExist=true";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    //DB 연결
    private Connection getConnection() throws SQLException{
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    public void createDB(){
        try{
            Connection con = getConnection();
            String sql = "create database if not exists Tetris;";
            Statement stmt = con.createStatement();
            stmt.execute(sql);
        }
        catch(SQLException e){
            System.out.println("SQLException"+e);
        }
    }

    public void createTable(){
        try{
            Connection con = getConnection();
            String userSql = "create table if not exists user (" +
                    "userID int auto_increment primary key," +
                    "username varchar(255) not null unique," +
                    "password varchar(255) not null)";
            String gameScoreSql = "create table if not exists gameScore (" +
                    "userID int primary key," +
                    "score int default 0," +
                    "foreign key (userID) references user(userID) on delete cascade)";
            Statement stmt = con.createStatement();
            stmt.execute(userSql);
            stmt.execute(gameScoreSql);
        }
        catch(SQLException e){
            System.out.println("SQLException"+e);
        }
    }
    public boolean register(String username, String password){
        try {
            Connection con=getConnection();

            String checkSql = "select count(*)from user where username = ?";
            String insertSql = "insert into user(username, password) values (?,?)";
            PreparedStatement checkPstmt = con.prepareStatement(checkSql);
            PreparedStatement insertPstmt = con.prepareStatement(insertSql);

            //중복 아이디 확인
            checkPstmt.setString(1,username);
            ResultSet rs = checkPstmt.executeQuery();
            if(rs.next()&&rs.getInt(1)>0){
                System.out.println("이미 존재하는 아이디입니다.");
                return false;
            }

            //회원가입
            insertPstmt.setString(1,username);
            insertPstmt.setString(2,password);
            insertPstmt.executeUpdate();
            return true;
        }
        catch(SQLException e){
            System.out.println("SQLException"+e);
            return false;
        }
    }

    public boolean login(String username, String password){
        try{
            Connection con = getConnection();

            String loginSql = "select userID from user where username = ? and password = ?";
            PreparedStatement loginSqlPtsmt = con.prepareStatement(loginSql);
            loginSqlPtsmt.setString(1,username);
            loginSqlPtsmt.setString(2,password);
            ResultSet rs = loginSqlPtsmt.executeQuery();
            if(rs.next()){
                return true;
            }
        }
        catch(SQLException e){
            System.out.println("SQLException"+e);
        }
        return false;
    }

    public void saveScore(int userID, int score){
        try{
            Connection con = getConnection();

            String saveSql = "insert into gameScore (userID, score) values (?,?) " +
                    "on duplicate key update score = greatest(gameScore.score, ?)";
            PreparedStatement saveSqlPstmt = con.prepareStatement(saveSql);
            saveSqlPstmt.setInt(1, userID);
            saveSqlPstmt.setInt(2, score);
            saveSqlPstmt.setInt(3, score);
            saveSqlPstmt.executeUpdate();
        }
        catch(SQLException e){
            System.out.println("SQLException"+e);
        }
    }
}
