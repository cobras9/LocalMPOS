package com.mobilis.android.nfc.model;

/**
 * Created by lewischao on 29/09/15.
 * #CPEXT
 * 255
 * #CURRENCYCODE
 * 936
 * #DIALPREFIX
 * 233
 * #DISMSISDN
 * 1
 * #DSTCURRENCIES
 * 936
 * #EXCHANGEQUOTE
 * 0
 * #EXTCTLR
 * 0
 * #GENERALFLAGS
 * 236
 * #IDLECTLS
 * 0
 * #POLLTOUT
 * 5
 * #SSL
 * 1
 * #STORELOC
 * GHANA
 * #STORENAME
 * AIRTEL MONEY
 * #SVRTOUT
 * 30
 * #HOSTPORT
 * 2993
 * #HOSTIP
 * 172.26.98.184
 * #MENUTOUT
 * 90
 * #TRANSID
 * 0000000001
 * #UPDATEFLAGS
 * 17
 * SOCKET
 * 16
 * KEYBOARD
 * 0
 * CPAD
 * 1500
 * AKM
 * CP
 * DARK
 * 5
 * TZ
 * WAT
 * GP_APN
 * internet
 * GO
 * F:MMPAY.OUT
 * ZA
 * AIRTELGH
 * ZT
 * <p/>
 * ZSSL
 * 0
 * ZN
 * 172.26.98.184:18013
 * PW
 * 3609719
 * SMPW
 * 3609717
 */

public class TerminalConfigurationVariablesModel {
    private String serverSourceJSON;
    private String GENERALFLAGS = "236";
    private String CPEXT = "255";
    private String CURRENCYCODE = "936";
    private String DIALPREFIX = "233";
    private String DISMSISDN = "1";
    private String DSTCURRENCIES = "936";
    private String EXCHANGEQUOTE = "0";
    private String EXTCTLR = "0";
    private String IDLECTLS = "0";
    private String POLLTOUT = "5";
    private String SSL = "1";
    private String STORELOC = "GHANA";
    private String STORENAME = "AIRTEL MONEY";
    private String SVRTOUT = "30";
    private String HOSTPORT = "2993";
    private String HOSTIP = "172.26.98.184";
    private String MENUTOUT = "90";
    private String TRANSID = "0000000001";
    private String UPDATEFLAGS = "17";
    private String SOCKET = "16";
    private String KEYBOARD = "1500";
    private String CPAD = "1500";
    private String AKM = "CP";
    private String DARK = "5";
    private String TZ = "WAT";
    private String GP_APN = "internet";
    private String GO = "F:MMPAY.OUT";
    private String ZA = "AIRTELGH";
    private String ZT = "<p/>";
    private String ZSSL = "0";
    private String ZN = "172.26.98.184:18013";
    private String PW = "3609719";
    private String SMPW = "3609717";

    public String getServerSourceJSON() {
        return serverSourceJSON;
    }

    public void setServerSourceJSON(String serverSourceJSON) {
        this.serverSourceJSON = serverSourceJSON;
    }

    public String getGENERALFLAGS() {
        return GENERALFLAGS;
    }

    public void setGENERALFLAGS(String GENERALFLAGS) {
        this.GENERALFLAGS = GENERALFLAGS;
    }

    public String getCPEXT() {
        return CPEXT;
    }

    public void setCPEXT(String CPEXT) {
        this.CPEXT = CPEXT;
    }

    public String getCURRENCYCODE() {
        return CURRENCYCODE;
    }

    public void setCURRENCYCODE(String CURRENCYCODE) {
        this.CURRENCYCODE = CURRENCYCODE;
    }

    public String getDIALPREFIX() {
        return DIALPREFIX;
    }

    public void setDIALPREFIX(String DIALPREFIX) {
        this.DIALPREFIX = DIALPREFIX;
    }

    public String getDISMSISDN() {
        return DISMSISDN;
    }

    public void setDISMSISDN(String DISMSISDN) {
        this.DISMSISDN = DISMSISDN;
    }

    public String getDSTCURRENCIES() {
        return DSTCURRENCIES;
    }

    public void setDSTCURRENCIES(String DSTCURRENCIES) {
        this.DSTCURRENCIES = DSTCURRENCIES;
    }

    public String getEXCHANGEQUOTE() {
        return EXCHANGEQUOTE;
    }

    public void setEXCHANGEQUOTE(String EXCHANGEQUOTE) {
        this.EXCHANGEQUOTE = EXCHANGEQUOTE;
    }

    public String getEXTCTLR() {
        return EXTCTLR;
    }

    public void setEXTCTLR(String EXTCTLR) {
        this.EXTCTLR = EXTCTLR;
    }

    public String getIDLECTLS() {
        return IDLECTLS;
    }

    public void setIDLECTLS(String IDLECTLS) {
        this.IDLECTLS = IDLECTLS;
    }

    public String getPOLLTOUT() {
        return POLLTOUT;
    }

    public void setPOLLTOUT(String POLLTOUT) {
        this.POLLTOUT = POLLTOUT;
    }

    public String getSSL() {
        return SSL;
    }

    public void setSSL(String SSL) {
        this.SSL = SSL;
    }

    public String getSTORELOC() {
        return STORELOC;
    }

    public void setSTORELOC(String STORELOC) {
        this.STORELOC = STORELOC;
    }

    public String getSTORENAME() {
        return STORENAME;
    }

    public void setSTORENAME(String STORENAME) {
        this.STORENAME = STORENAME;
    }

    public String getSVRTOUT() {
        return SVRTOUT;
    }

    public void setSVRTOUT(String SVRTOUT) {
        this.SVRTOUT = SVRTOUT;
    }

    public String getHOSTPORT() {
        return HOSTPORT;
    }

    public void setHOSTPORT(String HOSTPORT) {
        this.HOSTPORT = HOSTPORT;
    }

    public String getHOSTIP() {
        return HOSTIP;
    }

    public void setHOSTIP(String HOSTIP) {
        this.HOSTIP = HOSTIP;
    }

    public String getMENUTOUT() {
        return MENUTOUT;
    }

    public void setMENUTOUT(String MENUTOUT) {
        this.MENUTOUT = MENUTOUT;
    }

    public String getTRANSID() {
        return TRANSID;
    }

    public void setTRANSID(String TRANSID) {
        this.TRANSID = TRANSID;
    }

    public String getUPDATEFLAGS() {
        return UPDATEFLAGS;
    }

    public void setUPDATEFLAGS(String UPDATEFLAGS) {
        this.UPDATEFLAGS = UPDATEFLAGS;
    }

    public String getSOCKET() {
        return SOCKET;
    }

    public void setSOCKET(String SOCKET) {
        this.SOCKET = SOCKET;
    }

    public String getKEYBOARD() {
        return KEYBOARD;
    }

    public void setKEYBOARD(String KEYBOARD) {
        this.KEYBOARD = KEYBOARD;
    }

    public String getCPAD() {
        return CPAD;
    }

    public void setCPAD(String CPAD) {
        this.CPAD = CPAD;
    }

    public String getAKM() {
        return AKM;
    }

    public void setAKM(String AKM) {
        this.AKM = AKM;
    }

    public String getDARK() {
        return DARK;
    }

    public void setDARK(String DARK) {
        this.DARK = DARK;
    }

    public String getTZ() {
        return TZ;
    }

    public void setTZ(String TZ) {
        this.TZ = TZ;
    }

    public String getGP_APN() {
        return GP_APN;
    }

    public void setGP_APN(String GP_APN) {
        this.GP_APN = GP_APN;
    }

    public String getGO() {
        return GO;
    }

    public void setGO(String GO) {
        this.GO = GO;
    }

    public String getZA() {
        return ZA;
    }

    public void setZA(String ZA) {
        this.ZA = ZA;
    }

    public String getZT() {
        return ZT;
    }

    public void setZT(String ZT) {
        this.ZT = ZT;
    }

    public String getZSSL() {
        return ZSSL;
    }

    public void setZSSL(String ZSSL) {
        this.ZSSL = ZSSL;
    }

    public String getZN() {
        return ZN;
    }

    public void setZN(String ZN) {
        this.ZN = ZN;
    }

    public String getPW() {
        return PW;
    }

    public void setPW(String PW) {
        this.PW = PW;
    }

    public String getSMPW() {
        return SMPW;
    }

    public void setSMPW(String SMPW) {
        this.SMPW = SMPW;
    }
}
