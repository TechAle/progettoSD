package it.unimib.finalproject.server;
import it.unimib.finalproject.server.structure.client.clientDB;
import it.unimib.finalproject.server.utils.queryAssembler;
import it.unimib.finalproject.server.utils.msgParser;

import java.util.ArrayList;

public class test {

    public static void main(String args[]){
        clientDB dbSocket = new clientDB("127.0.0.1", 3030, "[VIEW]");
        dbSocket.start();
        System.out.println(dbSocket.getResponse());
        /*
        testView();
        testAdd();
        testDelete();
         */
        //testParseQuery();
    }

    static void testView(){
        // TEST VIEW SENZA PARAMETRI
        System.out.println("Comando VIEW");
        String q1 = queryAssembler.generateView();
        System.out.println(q1);

        //TEST VIEW CON PARAMETRO
        System.out.println("Comando VIEW:id");
        String q2 = queryAssembler.generateView(1);
        System.out.println(q2);
    }

    static void testAdd() {
        // TEST ADD CON ID PROIEZIONE E LISTA POSTI
        System.out.println("Comando ADD:id:[:posto:posto...]");
        String q1 = queryAssembler.generateAdd(1, "[3, 4, 5, 6]");
        System.out.println(q1);

        //TEST DELETE CON DUE ID E LISTA
        System.out.println("Comando ADD:id:id:[:posto:posto:...]");
        String q2 = queryAssembler.generateAdd(1, 2, "[3, 4, 5, 6]");
        System.out.println(q2);
    }


    static void testDelete(){
        // TEST DELETE CON DUE ID
        System.out.println("Comando DELETE:id:id");
        String q1 = queryAssembler.generateDelete(1, 2);
        System.out.println(q1);

        //TEST DELETE CON DUE ID E LISTA
        System.out.println("Comando DELETE:id:id:[:posto:posto:...]");
        String q2 = queryAssembler.generateDelete(1, 2, "[3, 4, 5, 6]");
        System.out.println(q2);

        //TEST DELETE CON DUE ID E LISTA VUOTA
        System.out.println("Comando DELETE:id:id:[]");
        String q3 = queryAssembler.generateDelete(1, 2, "[]");
        System.out.println(q3);
    }


    static void testParseQuery(){
        String queryOrdinaria = "[+[[$nomeFilm1$descrizioneFilm1$sala1:100[]$2002-06-01][$nomeFilm2$descrizioneFilm2$sala2:50[:0:1:0:4:1:6]$2003-06-02][$nomeFilm3$descrizioneFilm3$sala3:40[:0:3]$2002-08-05]]]";
        ArrayList<Object> output = msgParser.parse(queryOrdinaria);

        //  Output.
        for(int i = 0; i < output.size(); i++) {
            System.out.print(output.get(i));
            if (i != output.size() - 1)
                System.out.print(", ");
        }

    }
}
