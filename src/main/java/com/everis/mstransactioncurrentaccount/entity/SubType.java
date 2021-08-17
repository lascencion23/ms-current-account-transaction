package com.everis.mstransactioncurrentaccount.entity;

import lombok.Data;

@Data
public class SubType {
    String id;

    EnumSubType value;

    public enum EnumSubType{
        NORMAL, VIP, PYME
    }
}
