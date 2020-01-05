/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lte_asn_converter;

/**
 *
 * @author vazhenin
 */
public class Main {

    /**
     * @param args the command line arguments
     * 0 - parameters.xml file / help command
     * 1 - debug msisdn
     */
    public static void main(String[] args) {
        String info = "0 - parameters.xml file / help command/n"
                + "1 - debug msisdn";
//        System.out.println("Test");
        try {
            /* using help command */
            /* if number of incoming parameters = 1 , bot value isn't 'help' */ {
                if (args.length == 1 && args[0].toString().indexOf("help") != -1) {
                    System.err.println(info);
                } else if (args.length == 1 && args[0].toString().indexOf("help") == -1) {
                    new Worker(args[0], null).run();
                }

                if (args.length == 2) {
                    new Worker(args[0], args[1]).run();
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
