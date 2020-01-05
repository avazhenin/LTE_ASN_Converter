/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lte_asn_converter;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.temporal.JulianFields;
import java.util.ArrayList;
import java.util.Date;
import static lte_asn_converter.PGW_CDR.log;
import org.apache.log4j.Logger;

/**
 *
 * @author vazhenin
 */
public class Utils {

    static Logger log = Logger.getLogger(Utils.class);
    ASN asn = new ASN();

    /**
     * start at position 2
     */
    String getMsisdn(String tbcdstring) {
        return tbcdstring.substring(2, tbcdstring.length() - 1);
    }

    /**
     * start at position 1
     */
    String getMsisdn1(String tbcdstring) {
        return tbcdstring.substring(1, tbcdstring.length() - 1);
    }

    String getDate(String dataHex, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String result = "";
        try {
            result = sdf.format((new SimpleDateFormat(format).parse(dataHex.substring(0, dataHex.length() - 6)))).toString();
        } catch (Exception e) {
            log.info(e);
        }
        return result;
    }

    String getImsi(String data) {
        return data.substring(0, data.length() - 1);
    }

    String getRoute(String data) {
        String res = "";

        try {
            int pos = 0; // initiate initial position
            byte b = asn.hex_to_byte(data.substring(pos, pos + 2));// get byte value
            int[] bits = asn.get_tag_bits(b); // get byte bits
            int lpos = asn.get_bitpos_decimal(1, 7, bits); // get number of byte we need to read for getting tag data length
            pos += 2; // step to reading tag length
            int length = Integer.parseInt(data.substring(pos, pos + lpos * 2), asn.HEX_RADIX); // get tag data length
            pos = pos + lpos * 2; // skip pos to tag data

            // concatenate chars tag data
            for (int i = 0; i < length; i++) {
                res += String.valueOf((char) Integer.parseInt(data.substring(pos, pos + 2), asn.HEX_RADIX));
                pos += 2;
            }
        } catch (Exception e) {
            log.info(data, e);
        }

        return res;
    }

    String getLAC(String tagData) {
        int pos = 0; // initiate initial position
        byte b;
        int[] bits;
        int lpos;
        int length;
        String res;

        pos = 0; // initiate initial position
        b = asn.hex_to_byte(tagData.substring(pos, pos + 2));// get byte value        
        bits = asn.get_tag_bits(b); // get byte bits
        lpos = asn.get_bitpos_decimal(1, 7, bits); // get number of byte we need to read for getting tag data length
        pos += 2; // step to reading tag length
        length = Integer.parseInt(tagData.substring(pos, pos + lpos * 2), asn.HEX_RADIX); // get tag data length
        pos = pos + lpos * 2; // skip pos to tag data
        res = String.valueOf(Integer.parseInt(tagData.substring(pos, pos + length * 2), asn.HEX_RADIX));

        return res;
    }

    // Cell Identification
    String getCI(String tagData) {
        int pos = 0; // initiate initial position
        byte b;
        int[] bits;
        int lpos;
        int length;
        String res;

        pos = 0; // initiate initial position
        b = asn.hex_to_byte(tagData.substring(pos, pos + 2));// get byte value        
        bits = asn.get_tag_bits(b); // get byte bits
        lpos = asn.get_bitpos_decimal(1, 7, bits); // get number of byte we need to read for getting tag data length
        pos += 2; // step to reading tag length
        length = Integer.parseInt(tagData.substring(pos, pos + lpos * 2), asn.HEX_RADIX); // get tag data length
        pos = pos + lpos * 2; // skip pos to tag data
        pos += length * 2;

        b = asn.hex_to_byte(tagData.substring(pos, pos + 2));// get byte value        
        bits = asn.get_tag_bits(b); // get byte bits
        lpos = asn.get_bitpos_decimal(1, 7, bits); // get number of byte we need to read for getting tag data length
        pos += 2; // step to reading tag length
        length = Integer.parseInt(tagData.substring(pos, pos + lpos * 2), asn.HEX_RADIX); // get tag data length
        pos = pos + lpos * 2; // skip pos to tag data
        res = String.valueOf(Integer.parseInt(tagData.substring(pos, pos + length * 2), asn.HEX_RADIX));
//        res = res.substring(1, res.length() - 1);
        return res;
    }

    String getCell(String tagData) {
        String cell = getCI(tagData);
        return cell.substring(cell.length() - 1, cell.length());
    }

    String getBSC(String tagData) {
        String bsc = getCI(tagData);
        return bsc.substring(0, bsc.length() - 1);
    }

    String hex2String(String hex) {
        char[] c = new char[hex.length() / 2];
        try {
            int p = 0;
            for (int i = 0; i < hex.length(); i++) {
                c[p] = (char) Integer.parseInt(hex.substring(0 + i, 0 + i + 2), asn.HEX_RADIX);
                p++;
                i++;
            }

        } catch (Exception e) {
            log.fatal(e);
        }
        return new String(c);
    }

    String getTAI(String hex) {
        /**
         * Example hex data 18 04 f1 70 27 39 04 f1 70 01 8a 8f 03
         */
        String result = new String();
        try {
            result = hex.substring(1 * 2, 4 * 2);
            result = asn.TBCDSTRING(result);
            log.debug(result);
        } catch (Exception e) {
            log.fatal(e);
        }
        return result;
    }

    /**
     * LTE LAC
     */
    String getTAC(String hex, int rattype) {
        /**
         * Example hex data 18 04 f1 70 27 39 04 f1 70 01 8a 8f 03
         */
        String result = new String();
        try {
            if (rattype < 6) {
                result = String.valueOf(Integer.parseInt(hex.substring(hex.length() - 8, hex.length() - 4), asn.HEX_RADIX));
            } else {
                result = String.valueOf(Integer.parseInt(hex.substring(4 * 2, 6 * 2), asn.HEX_RADIX));
            }
        } catch (Exception e) {
            log.fatal(e);
        }
        return result;
    }

    /**
     * BSC
     */
    String geteNodeB_ID(String hex, int rattype) {
        /**
         * Example hex data 18 04 f1 70 27 39 04 f1 70 01 8a 8f 03
         */
        String result = new String();
        int loc_type = Integer.parseInt(hex.substring(0, 2), asn.HEX_RADIX);
        if (loc_type > 2) {
            try {
                result = String.valueOf(Integer.parseInt(hex.substring(9 * 2, 12 * 2), asn.HEX_RADIX));
            } catch (Exception e) {
                log.fatal(e);
            }
        }

        return result;
    }

    String getCell(String hex, int rattype) {
        /**
         * Example hex data 18 04 f1 70 27 39 04 f1 70 01 8a 8f 03
         */
        String result = new String();
        int loc_type = Integer.parseInt(hex.substring(0, 2), asn.HEX_RADIX);

        try {
            if (rattype < 6) {
                result = String.valueOf(Integer.parseInt(hex.substring(hex.length() - 4, hex.length()), asn.HEX_RADIX));
            } else {
                result = String.valueOf(Integer.parseInt(hex.substring(hex.length() - 2, hex.length()), asn.HEX_RADIX));
            }
        } catch (Exception e) {
            log.fatal(e);
        }

        return result;
    }

    /**
     * debug_msisdn is used to trace coverted data only for certain msisdn
     */
    ArrayList<ListOfServiceData> listOfServiceData(String hex, String debug_msisdn) {

        ListOfServiceData losd = new ListOfServiceData();
        ArrayList<ListOfServiceData> losda = new ArrayList<ListOfServiceData>();
        String delimiter = " ";

        /**
         * Debug all or specified msisdn
         */
        if (Worker.debug_msisdn != null) {
            if (debug_msisdn.indexOf(Worker.debug_msisdn) != -1) {
                log.debug("listOfServiceData");
            }
        } else {
            log.debug("listOfServiceData");
        }
        for (int i = 0; i < hex.length(); i++) {
            String tag = hex.substring(i, i + 2);
            i = i + 2; // skip to get length
            int length = Integer.valueOf(hex.substring(i, i + 2), asn.HEX_RADIX); // get length
            i = i + 2;
            String data = hex.substring(i, i + length * 2);
            i = i + length * 2 - 1;

//            log.debug(tag + delimiter + length + delimiter + data);
            for (int j = 0; j < data.length(); j++) {
                String debugINFO;
                String tag0 = data.substring(j, j + 2);
                j = j + 2; // skip to get length
                int length0 = Integer.valueOf(data.substring(j, j + 2), asn.HEX_RADIX); // get length
                j = j + 2;
                String data0 = data.substring(j, j + length0 * 2);
                j = j + length0 * 2 - 1;

                debugINFO = "______" + tag0 + delimiter + length0 + delimiter + data0;

                switch (tag0.toLowerCase()) {
                    case "8c": //datavolumeFBCUplink
                        losd.datavolumeFBCUplink = String.valueOf(Integer.parseInt(data0, asn.HEX_RADIX));
                        debugINFO += delimiter + " converted " + (String.valueOf(Integer.parseInt(data0, asn.HEX_RADIX)));
                        break;
                    case "8d": // datavolumeFBCDownlink
                        losd.datavolumeFBCDownlink = String.valueOf(Integer.parseInt(data0, asn.HEX_RADIX));
                        debugINFO += delimiter + " converted " + (String.valueOf(Integer.parseInt(data0, asn.HEX_RADIX)));
                        break;
                    case "85": // timeOfFirstUsage
                        losd.timeOfFirstUsage = getDate(data0, "yyMMddHHmmss");
                        debugINFO += delimiter + " converted " + (getDate(data0, "yyMMddHHmmss"));
                        break;
//                    case "86": // timeOfLastUsage
//                        debugINFO += delimiter + " converted " + (getDate(data0, "yyMMddHHmmss"));
//                        break;
                    case "8e": // timeOfReport
                        debugINFO += delimiter + " converted " + (getDate(data0, "yyMMddHHmmss"));
                        break;
                    case "87": // timeUsage
                        debugINFO += delimiter + " converted " + Integer.parseInt(data0, asn.HEX_RADIX);
                        break;
                    case "aa": // sgsn-Address
                        String sgsn_Address = getIPAddress(data0);
                        debugINFO += delimiter + " converted " + sgsn_Address;
                        losd.sgsnAddress = sgsn_Address;
                        break;
                    case "8f": // rATType
                        debugINFO += delimiter + " converted " + Integer.parseInt(data0, asn.HEX_RADIX);
                        break;
                    case "81": // RatingGroup
                        debugINFO += delimiter + " converted " + Integer.parseInt(data0, asn.HEX_RADIX);
                        losd.RatingGroup = String.valueOf(Integer.parseInt(data0, asn.HEX_RADIX));
                        break;
                }
                /**
                 * Debug all or specified msisdn
                 */
                if (Worker.debug_msisdn != null) {
                    if (debug_msisdn.indexOf(Worker.debug_msisdn) != -1) {
                        log.debug(debugINFO);
                    }
                } else {
                    log.debug(debugINFO);
                }
            }
            losda.add(new ListOfServiceData(
                    losd.RatingGroup,
                    losd.timeOfFirstUsage,
                    losd.qoSInformationNeg,
                    losd.sgsnAddress,
                    losd.datavolumeFBCUplink,
                    losd.datavolumeFBCDownlink));
        }
        losd = new ListOfServiceData(); // clear

        return losda;
    }

    String getIPAddress(String ip_hex) {
        String result = "";
        ip_hex = ip_hex.substring(4, ip_hex.length());
        for (int o = 0; o < ip_hex.length(); o++) {
            result += Integer.parseInt(ip_hex.substring(o, o + 2), asn.HEX_RADIX) + ((o + 2 >= ip_hex.length()) ? "" : ".");
            o++;
        }

        return result;
    }
}
