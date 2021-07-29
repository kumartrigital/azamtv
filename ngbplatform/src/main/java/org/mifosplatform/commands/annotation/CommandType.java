package org.mifosplatform.commands.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface CommandType {

    /**
     * Returns the name of the entity for this {@link CommandType}.
     */
    String entity();

    /**
     * Return the name of the action for this {@link CommandType}.
     */
    String action();
}