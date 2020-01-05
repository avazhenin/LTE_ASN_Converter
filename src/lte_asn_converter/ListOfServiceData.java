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
public class ListOfServiceData {

    String RatingGroup;
    String timeOfFirstUsage;
    String qoSInformationNeg;
    String sgsnAddress;
    String datavolumeFBCUplink;
    String datavolumeFBCDownlink;

    public ListOfServiceData(String RatingGroup, String timeOfFirstUsage, String qoSInformationNeg, String sgsnAddress, String datavolumeFBCUplink, String datavolumeFBCDownlink) {
        this.RatingGroup = RatingGroup;
        this.timeOfFirstUsage = timeOfFirstUsage;
        this.qoSInformationNeg = qoSInformationNeg;
        this.sgsnAddress = sgsnAddress;
        this.datavolumeFBCUplink = datavolumeFBCUplink;
        this.datavolumeFBCDownlink = datavolumeFBCDownlink;
    }

    public ListOfServiceData() {
    }

}
