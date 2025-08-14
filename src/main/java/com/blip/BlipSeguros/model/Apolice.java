package com.blip.BlipSeguros.model;

public enum Apolice {
    BASICA_001("📋 Apolice Basica 001"),
    PADRAO_002("📋 Apolice Padrao 002"),
    PREMIUM_003("📋 Apolice Premium 003");

    private final String label;

    Apolice(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
