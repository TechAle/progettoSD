package it.unimib.finalproject.server.utils.socket;

public class clientDB extends tcpClient{

    private String toSend;
    private String output;
    public clientDB(String address, int port, String toSend) {
        super(address, port);
        this.toSend = toSend;
    }


    @Override
    protected void body() {
        System.out.println("Connesso2");
        this.sendMessage(toSend);
        output = this.getMessage();
    }


    public String getResponse(){
        return output;
    }
}