/**
 * Mobile Originating Call
 */
package lte_asn_converter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Vazhenin
 */
public class PGW_CDR {

    ASN asn = new ASN();
    int eventPos;
    Utils utils = new Utils();
    static Logger log = Logger.getLogger(PGW_CDR.class);

    public PGW_CDR() {
    }

    ArrayList<CDRoutCalls> parse(byte[] b, int spos, int epos) {

        CDRoutCalls outCols = new CDRoutCalls();
        ArrayList<CDRoutCalls> outColsa = new ArrayList<CDRoutCalls>();
        String debugINFO = new String();
        ArrayList<String> debugMessages = new ArrayList<>();
        ArrayList<ListOfServiceData> losd = new ArrayList<ListOfServiceData>();

        for (eventPos = spos; eventPos <= epos; eventPos++) {
            try {
                String tag = asn.get_tag(b, eventPos);

                int tagl = asn.get_tag_length(b, eventPos);

                int skipTag = ((tag.length() > 2) ? tag.length() / 2 : 1);
                int[] bits = asn.get_tag_bits(b[eventPos + skipTag]);
                int bitsl = (bits[0] == 0) ? 0 : (asn.get_bitpos_decimal(1, 7, bits));

                int tagEndPos = eventPos + skipTag + tagl + bitsl;
                eventPos = tagEndPos - tagl; // skip to call data                
                String dataHex = getTagData(b, tagl);

                debugINFO = "TAG " + tag + " ,data hex " + dataHex;

                switch (tag.toLowerCase().trim()) {
                    case "80":
                        outCols.recordType = dataHex;
                        debugINFO += (" ,data converted " + outCols.recordType);
                        break;
                    case "83":
                        outCols.servedIMSI = utils.getImsi(asn.TBCDSTRING(dataHex));
                        debugINFO += (" ,data converted " + outCols.servedIMSI);
                        break;
                    case "9d":
                        outCols.servedIMEI = asn.TBCDSTRING(dataHex);
                        debugINFO += (" ,data converted " + outCols.servedIMEI);
                        break;
                    case "96":
                        outCols.callingNumber = utils.getMsisdn1(asn.TBCDSTRING(dataHex));
                        debugINFO += (" ,data converted " + outCols.callingNumber);
                        break;
                    case "92":
                        outCols.mscIncomingROUTE = utils.hex2String(dataHex);
                        debugINFO += (" ,data converted " + outCols.mscIncomingROUTE);
                        break;
                    case "8e":
                        outCols.callDuration = String.valueOf(Integer.parseInt(dataHex, asn.HEX_RADIX));
                        debugINFO += (" ,data converted " + outCols.callDuration);
                        break;
                    case "87":
                        outCols.apn = utils.hex2String(dataHex);
                        debugINFO += (" ,data converted " + outCols.apn);
                        break;
                    case "9f20": // location
                        outCols.bsc_a = utils.geteNodeB_ID(dataHex, Integer.parseInt(outCols.rATType));
                        outCols.lacA = utils.getTAC(dataHex, Integer.parseInt(outCols.rATType));
                        outCols.cellA = utils.getCell(dataHex, Integer.parseInt(outCols.rATType));
                        debugINFO += (" ,data converted " + outCols.bsc_a);
                        debugINFO += (" ,data converted " + outCols.lacA);
                        debugINFO += (" ,data converted " + outCols.cellA);
                        break;
                    case "9f26":
                        outCols.seizureTime = utils.getDate(dataHex, "yyMMddHHmmss");
                        debugINFO += (" ,data converted " + outCols.seizureTime);
                        break;
                    case "9e":
                        outCols.rATType = String.valueOf(Integer.parseInt(dataHex, asn.HEX_RADIX));
                        debugINFO += (" ,data converted " + outCols.rATType);
                        break;
                    case "bf22":
                        losd = utils.listOfServiceData(dataHex, outCols.callingNumber);
                        break;
                    case "a9":
                        outCols.calledNumber = utils.getIPAddress(dataHex.substring(4, dataHex.length()));
                        debugINFO += (" ,vol_in converted " + outCols.calledNumber);
                        break;

                }

                /**
                 * if k != tag end postition , means tag value wasnt parsed
                 */
                if (eventPos != tagEndPos) {
                    eventPos = tagEndPos;
                    log.info("Tag " + tag + " wasnt parsed properly ; POS- " + spos);
                }
            } catch (Exception e) {
                e.printStackTrace();
//                log.info(dataHex,e);
            }
            debugMessages.add(debugINFO);
            debugINFO = ""; // clear            

        }

            for (int i = 0; i < losd.size(); i++) {
                if (outCols.callingNumber.indexOf("7080109849") != -1) {
                    log.info(i + " " + outCols.callingNumber + " " + losd.get(i).timeOfFirstUsage + " " + losd.get(i).datavolumeFBCDownlink + " ; " + losd.get(i).datavolumeFBCUplink);
                }

                outColsa.add(new CDRoutCalls(
                        outCols.recordType,
                        outCols.servedIMSI,
                        outCols.servedIMEI,
                        outCols.callingNumber,
                        outCols.calledNumber,
                        outCols.mscIncomingROUTE,
                        outCols.mscOutgoingROUTE,
                        outCols.bsc_a,
                        outCols.cellA,
                        outCols.lacA,
                        losd.get(i).datavolumeFBCDownlink,
                        losd.get(i).datavolumeFBCUplink,
                        losd.get(i).timeOfFirstUsage,
                        outCols.callDuration,
                        outCols.callType,
                        outCols.rATType,
                        outCols.apn,
                        losd.get(i).RatingGroup,
                        losd.get(i).qoSInformationNeg,
                        losd.get(i).sgsnAddress
                ));
            }        

        /**
         * Debug all or specified msisdn
         */
        if (Worker.debug_msisdn != null) {
            if (outCols.callingNumber != null && outCols.callingNumber.indexOf(Worker.debug_msisdn) != -1) {
                for (int i = 0; i < debugMessages.size(); i++) {
                    log.debug(debugMessages.get(i));
                }
            }
        } else {
            log.debug(debugINFO);
        }

        return outColsa;
    }

    String getTagData(byte[] b, int tagLength) {
        String result = new String();
        for (int l = 0; l < tagLength; l++) {
            eventPos++;
            result += asn.byte_to_hex(b[eventPos]);
        }
        return result;
    }

}
