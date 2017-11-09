package com.AppsByCollin.kdbInterface;

import java.io.IOException;
import kx.c;

import static kx.c.n;

class KDB {
    private String IP_Addr;
    private int portNum;
    private String username_password;

    //Constructor: Initializes IP address, port number and user credentials.
    KDB(String IP_Addr,int portNum, String username_password){
        this.IP_Addr = IP_Addr;
        this.portNum = portNum;
        this.username_password = username_password;
    }

    //Creates & returns connection to kdb instance.
    c kdbConnect(){
    c conn = null;
        try {
            conn = new c(IP_Addr, portNum, username_password);
        } catch (c.KException | IOException ex) {
            System.out.println("An exception has occurred: " + ex.getMessage() +
            ". Please ensure that the entered credentials are correct.");
        }
        return conn;
    }

    //Runs synchronous query. Returned string depends on query.
    void runQuery(c conn, String query) {
        try {
            Object result = conn.k(query);
            System.out.println("Query result: " + result.toString());
        } catch (IOException | c.KException ex) {
            System.out.println("An exception has occured in runQuery: " + ex.getMessage());
        }
    }

    //Runs synchronous query, returning a table.
    void requestTable(c conn, String query, boolean keyed) {
        try {
            //Handles keyed and un-keyed tables.
            c.Flip result = (keyed ? c.td(conn.k(query)) : (c.Flip) conn.k(query));

            //Print column titles.
            for(int col=0; col < result.x.length; col++)
                System.out.print((col > 0 ? " | " : "" ) + result.x[col]);
            System.out.println();

            //Print details.
            for(int row=0; row < n(result.y[0]); row++) {
                for (int col = 0; col < result.x.length; col++)
                    System.out.print((col > 0 ? " | " : "") + c.at(result.y[col], row));
                System.out.println();
            }
        } catch (IOException | c.KException ex) {
            System.out.println("An exception has occured in requestUnkeyedTable: " + ex.getMessage()
                    + ". Please ensure your query is correct and try again.");
        }
    }
}
