package database;

import database.utils.structure.prenotazione;
import database.utils.structure.proiezione;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;

public class RegisterManager {

    private static RegisterManager register;
    Map<Integer, proiezione> database;

    private RegisterManager() {
        database = new HashMap<>();
        loadData();
    }

    private void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("proiezioni.csv"))) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                database.put(Integer.parseInt(parts[0].replaceAll(" ", "")),
                            new proiezione(parts[1], Integer.parseInt(parts[2].replaceAll(" ", "")), parts[3]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        int b = 0;

    }




    public static synchronized RegisterManager getInstance(){
        if (register == null)
            register = new RegisterManager();
        return register;
    }




}
