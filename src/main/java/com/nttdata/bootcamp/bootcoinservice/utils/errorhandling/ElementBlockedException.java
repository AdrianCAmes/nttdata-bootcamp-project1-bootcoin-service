package com.nttdata.bootcamp.bootcoinservice.utils.errorhandling;

public class ElementBlockedException extends RuntimeException {
    private static final long serialVersionUID = -5713584292717311039L;

    public ElementBlockedException(String s) {
        super(s);
    }
}

