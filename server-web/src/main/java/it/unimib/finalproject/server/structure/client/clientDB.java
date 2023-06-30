package it.unimib.finalproject.server.structure.client;

public class clientDB extends tcpClient{

    public clientDB(String address, int port) {
        super(address, port);
    }


    @Override
    protected void body() {

    }


    public String getResponse(){
        return this.getMessage();
    }
}
