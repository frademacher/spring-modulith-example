package de.codecentric.spring_modulith_example.shared;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

public abstract class Defaults {
    private Defaults() {
        // NOOP
    }

    public static final CurrencyUnit DEFAULT_CURRENCY = Monetary.getCurrency("EUR");
}
