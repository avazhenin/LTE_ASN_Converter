/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lte_asn_converter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author vazhenin
 */
public class Worker {

    String cdrin_path;
    String cdrout_path;
    String logFileFullPath;
    String log4gFullPath;
    String cdroutext;
    int max_cdr_files;
    ASN asn = new ASN();
    byte[] b;

    public static boolean debug = false;
    public static String debug_msisdn;

    static Logger log = Logger.getLogger(Worker.class);

    public Worker() {
    }

    public Worker(String paramFileFullPath, String debug_msisdn) {

        ParseXMLUtilities xml = new ParseXMLUtilities(paramFileFullPath);
        xml.initiate();

        this.cdrin_path = xml.getNodeValue(xml.getChildNodes("parameters"), "cdrin");
        this.cdrout_path = xml.getNodeValue(xml.getChildNodes("parameters"), "cdrout");
        this.max_cdr_files = Integer.parseInt(xml.getNodeValue(xml.getChildNodes("parameters"), "maxCDRFilesToRead"));
        this.cdroutext = xml.getNodeValue(xml.getChildNodes("parameters"), "cdroutext");
        this.log4gFullPath = xml.getNodeValue(xml.getChildNodes("parameters"), "log4jPath");

        PropertyConfigurator.configure(this.log4gFullPath);

        this.debug_msisdn = debug_msisdn;
    }

    void run() {

        try {
            /* read files */
            File[] files = getFiles(cdrin_path);

            for (int i = 0; i < Math.min(files.length, max_cdr_files); i++) {/* start of reading files loop */

                File file = files[i];

                File cdr = new File(files[i].getAbsoluteFile().toString());
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(cdr));

                b = new byte[bis.available()];
                bis.read(b);

                ArrayList<CDRoutCalls> outCDRrecords = new ArrayList<CDRoutCalls>();
                PGW_CDR pgw = new PGW_CDR();
                int exits_count = 0;
                for (int j = 0; j < b.length; j++) {/* start of reading CDR bytes */

                    int debug_point = j;

                    String hex_tag = asn.get_tag(b, j);
                    int sequenceTagLength = asn.get_tag_length(b, j);
                    int skipTag0 = ((hex_tag.length() > 2) ? hex_tag.length() / 2 : 1);
                    int[] seq_bits = asn.get_tag_bits(b[j + skipTag0]);
                    int seq_bitsl = (seq_bits[0] == 0) ? 0 : (asn.get_bitpos_decimal(1, 7, seq_bits));
                    int sequence_start = j;
                    int sequence_data_start = j + seq_bitsl + skipTag0 + 1;
                    int sequence_stop = j + skipTag0 + seq_bitsl + sequenceTagLength;
                    j = sequence_stop; // set stop position for the main loop

                    if (hex_tag.equalsIgnoreCase("bf4f")) {
                        // PGW-CDR
                        ArrayList<CDRoutCalls> calls = pgw.parse(b, sequence_data_start, sequence_stop);
                        
                        for (int k = 0; k < calls.size(); k++) {
                            CDRoutCalls cdrCall = calls.get(k);
                            outCDRrecords.add(cdrCall);
                        }

                    } else {
                        log.info("Unrecognized TAG " + hex_tag);
                    }
                }/* end of read CDR bytes */

                new ExportCDR().export(cdrout_path, cdroutext, outCDRrecords, file.getName());

                b = null; // prepare bytes for new CDR
                log.info(file.getName() + " has been converted");
            }/* end of reading files loop */

        } catch (Exception e) {
            log.info("ERROR", e);
        }

    }

    public File[] getFiles(String path) {
        File f = new File(path);

        File[] cdrFilesList = f.listFiles();

        return cdrFilesList;
    }

}
