/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lte_asn_converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author vazhenin
 */
public class ExportCDR {

    static Logger log = Logger.getLogger(ExportCDR.class);
    char delimiter = ';';

    void export(String outDir, String outExt, ArrayList<CDRoutCalls> outcdrs, String cdrFileName) {
        try {
            File outFile = new File(outDir + "\\" + cdrFileName.replace(cdrFileName.substring(cdrFileName.indexOf(".") + 1, cdrFileName.length()), outExt));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));

            bw.append("recordType" + delimiter);
            bw.append("callingNumber" + delimiter);
            bw.append("calledNumber" + delimiter);
            bw.append("callDuration" + delimiter);
            bw.append("seizureTime" + delimiter);
            bw.append("servedIMSI" + delimiter);
            bw.append("servedIMEI" + delimiter);
            bw.append("bsc_a" + delimiter);
            bw.append("cellA" + delimiter);
            bw.append("lacA" + delimiter);
            bw.append("vol_in" + delimiter);
            bw.append("vol_out" + delimiter);
            bw.append("ratingGroup" + delimiter);
            bw.append("QoS" + delimiter);
            bw.append("sgsnAddress" + delimiter);
            bw.append("mscIncomingROUTE" + delimiter);
            bw.append("mscOutgoingROUTE" + delimiter);
            bw.append("serviceCentre" + delimiter);
            bw.append("apn" + delimiter);
            bw.newLine();

            for (int i = 0; i < outcdrs.size(); i++) {
                CDRoutCalls get = outcdrs.get(i);
                bw.append(get.recordType + delimiter);
                bw.append(get.callingNumber + delimiter);
                bw.append(get.calledNumber + delimiter);
                bw.append(get.callDuration + delimiter);
                bw.append(get.seizureTime + delimiter);
                bw.append(get.servedIMSI + delimiter);
                bw.append(get.servedIMEI + delimiter);
                bw.append(get.bsc_a + delimiter);
                bw.append(get.cellA + delimiter);
                bw.append(get.lacA + delimiter);
                bw.append(get.vol_in + delimiter);
                bw.append(get.vol_out + delimiter);
                bw.append(get.ratingGroup + delimiter);
                bw.append(get.qos + delimiter);
                bw.append(get.sgsnAddress + delimiter);
                bw.append(get.mscIncomingROUTE + delimiter);
                bw.append(get.mscOutgoingROUTE + delimiter);
                bw.append(get.serviceCentre + delimiter);
                bw.append(get.apn + delimiter);
                bw.newLine();
            }

            bw.flush();
            bw.close();
        } catch (Exception e) {
            log.info(e);
        }
    }
}
