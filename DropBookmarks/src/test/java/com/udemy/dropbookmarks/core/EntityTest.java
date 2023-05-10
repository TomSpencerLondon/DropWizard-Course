package com.udemy.dropbookmarks.core;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;

public abstract class EntityTest {

    protected static final String ERROR_NOT_NULL = "must not be null";

    protected static final String ERROR_LENGTH
            = "size must be between 1 and 255";

    protected static Validator validator;

    @BeforeAll
    public static void setUpClass() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
}
