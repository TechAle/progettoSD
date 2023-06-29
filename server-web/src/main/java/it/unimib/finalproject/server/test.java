package it.unimib.finalproject.server;
import it.unimib.finalproject.server.utils.queryAssembler;
import it.unimib.finalproject.server.utils.msgParser;

import java.util.ArrayList;

public class test {

    public static void main(String args[]){
        testView();
        testAdd();
        testDelete();
        testParseQuery();
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
        String queryOrdinaria = "-$testo:1[$testo:2[:3]]$testo";
        ArrayList<Object> output = msgParser.parser(queryOrdinaria);

        //  Output.
        for(int i = 0; i < output.size(); i++) {
            System.out.print(output.get(i));
            if (i != output.size() - 1)
                System.out.print(", ");
        }

    }
}
