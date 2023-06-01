package database;

import database.utils.parserUtils;

public class test {
    public static void main(String[] args) {
        parserUtils.parseRedis("[VIEW:1:2:3\r\nDEL:0[:5]]");
        RegisterManager t = RegisterManager.getInstance();
        System.out.println(t.getStanze());

    }
}
