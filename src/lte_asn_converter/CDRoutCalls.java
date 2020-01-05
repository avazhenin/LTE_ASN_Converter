/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lte_asn_converter;

/**
 *
 * @author home
 */
public class CDRoutCalls {

    String recordType;
    String servedIMSI;
    String servedIMEI;
    String callingNumber;
    String calledNumber;
    String mscIncomingROUTE;
    String mscOutgoingROUTE;
    String bsc_a;
    String cellA;
    String lacA;
    String vol_in;
    String vol_out;
    String seizureTime;
    String callDuration;
    String callType;
    String serviceCentre;
    String rATType;
    String apn;
    String ratingGroup;
    String qos;
    String sgsnAddress;

    public CDRoutCalls(
            String recordType,
            String servedIMSI,
            String servedIMEI,
            String callingNumber,
            String calledNumber,
            String mscIncomingROUTE,
            String mscOutgoingROUTE,
            String bsc_a,
            String cellA,
            String lacA,
            String vol_in,
            String vol_out,
            String seizureTime,
            String callDuration,
            String callType,
            String rATType,
            String apn,
            String ratingGroup,
            String qos,
            String sgsnAddress) {
        this.recordType = recordType;
        this.servedIMSI = servedIMSI;
        this.servedIMEI = servedIMEI;
        this.callingNumber = callingNumber;
        this.calledNumber = calledNumber;
        this.mscIncomingROUTE = mscIncomingROUTE;
        this.mscOutgoingROUTE = mscOutgoingROUTE;
        this.bsc_a = bsc_a;
        this.cellA = cellA;
        this.lacA = lacA;
        this.vol_in = vol_in;
        this.vol_out = vol_out;
        this.seizureTime = seizureTime;
        this.callDuration = callDuration;
        this.callType = callType;
        this.rATType = rATType;
        this.apn = apn;
        this.ratingGroup = ratingGroup;
        this.qos = qos;
        this.sgsnAddress = sgsnAddress;

    }

    public CDRoutCalls() {
    }
}
