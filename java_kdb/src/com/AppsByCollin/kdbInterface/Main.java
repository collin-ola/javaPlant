package com.AppsByCollin.kdbInterface;

import kx.c;

import java.io.*;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        c conn = null;

        while(conn == null) {
            log(logLevels.INFO, "Attempting connection to kdb instance...");
            conn = getCredentials().kdbConnect();
        }
        log(logLevels.INFO, "Connection successful.");


        long startTime = System.currentTimeMillis();
        while(System.currentTimeMillis() < startTime + 600000) { //Send for 10 min (600,000 nanoseconds).
            Thread.sleep(getRandomInt(0, 250)); //Simulation of delay between packets.
            Object dataToSend = generateData(getRandomInt(1, 10));
            log(logLevels.VERBOSE, "Sending data packet " + i++);

            try {
                //format:(function; table; data)
                conn.ks(".u.upd", "eqTrade", dataToSend); //async
            } catch (IOException | NullPointerException ex) {
                log(logLevels.FATAL, "Error: Failed to send async message: " + ex.getMessage());
                log(logLevels.FATAL, "Aborting...");
                break;
            }
        }
//        while(true) {
//            Request query from user
//            System.out.print("Enter query: ");
//            String query = in.nextLine();
//            if (query.contains("select"))
//                kdb.requestTable(conn, query, query.contains("by"));
//             else
//                kdb.runQuery(conn, query);

//           in.nextLine();
//       }
    }

    public enum logLevels{DEBUG, VERBOSE, INFO, WARNING, FATAL}
	
	private static KDB getCredentials(){
        final String IP_ADDR = "localhost";
        final int PORT = getPort();
		String username, password;
        Scanner in = new Scanner(System.in);

        System.out.print("Enter your username: ");
        username = in.nextLine();

//        System.out.print("Enter your password: ");
//        password = in.nextLine();

//        For console (echo off)
        java.io.Console console = System.console();
        password = new String(console.readPassword("Enter your password: "));

        return new KDB(IP_ADDR, PORT, username + ":" + password);
	}

    private static Object[] generateData(int numRows) {
        /*Function for the generation of random(ish) equity trade data.
        * Data created for the following kdb schema:
        * eqTrade:([]date:`date$(); time:`timespan$(); exchTime:`timespan$();
			sym:`$(); price:`float$(); size:`int$(); cond:`$())*/

        final int DAY_IN_MILLIS = 86400000;
        final long EIGHT_HRS_IN_NANOS = 28500000000000L;

        Date[] dates = new Date[numRows];
        c.Timespan[] times = new c.Timespan[numRows];
        c.Timespan[] exchTimes = new c.Timespan[numRows];
        String[] syms = new String[numRows];
        double[] prices = new double[numRows];
        long[] sizes = new long[numRows];
        String[] conds = new String[numRows];

        Date[] datePool = new Date[30];
        for(int i = 0; i < 30; i++)
            datePool[i] = new Date(System.currentTimeMillis() - i*DAY_IN_MILLIS);

        //times between 07:50:00 and 16:25:00.
        c.Timespan[] timePool = new c.Timespan[1020];
        for(int i = 0; i < 1020; i++)
            timePool[i] = new c.Timespan(EIGHT_HRS_IN_NANOS + 30000000000L*i + getRandomInt(0, Integer.MAX_VALUE));

        String[] symPool = new String[]{"BARC","BNC", "HSBA", "RBS", "STAN"};

        DecimalFormat dF = new DecimalFormat("####.##");

        for(int i = 0; i < numRows; i++){
            dates[i] = datePool[getRandomInt(0, 30)];
            times[i] = timePool[getRandomInt(0,1020)];
            exchTimes[i] = new c.Timespan(times[i].j + getRandomInt(500000000,1000000000));
            syms[i] = symPool[getRandomInt(0,5)];
            prices[i] = Double.parseDouble(dF.format(getRandomDouble(100, 1000)));
            sizes[i] = getRandomInt(10, 100)*10 ;
            conds[i] = times[i].j < 28740000000000L ? "U" : "A";
        }

        return new Object[]{dates, times, exchTimes, syms, prices, sizes, conds }; //row data
    }

    private static int  getPort() {
        //String path = "G:/MThree/Work/kdb/Presentations/kdbJavaInterfacing/tpPort.port";
        //String path = "/home/churchill03/kdb/java_plant/scripts_logs/tpPort.port";
		String path = "G:/MThree/Work/kdb/Presentations/java_plant/scripts_logs/tpPort.port";
		
        FileReader reader = null;
        BufferedReader bufReader;
        String line;
        int portNum = 0;

        try {
            reader = new FileReader(path);
            bufReader = new BufferedReader(reader);

            while((line = bufReader.readLine()) != null) {
                System.out.println("Found port: " + line);
                portNum  = Integer.parseInt(line);
            }
        } catch (FileNotFoundException FNFE) {
            System.out.println("Could not find file: " + FNFE.getMessage());
        }   catch (IOException IOEx) {
            System.out.println("IO Problem: " + IOEx.getMessage());
        }   finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (Exception Ex) {
                System.out.println("Could not close file: " + Ex.getMessage());
            }
        }
        return portNum;
    }

    private static int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min ,max);
    }

    private static double getRandomDouble(int min, int max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    private static void log(logLevels level, String msg) {
        //TODO: Create .log file; write all the messages below to it.
        System.out.println(Instant.now() + " [" + level + "] " + msg);
    }
}
