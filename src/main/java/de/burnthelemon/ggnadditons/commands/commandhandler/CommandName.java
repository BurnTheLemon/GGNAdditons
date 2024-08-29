package de.burnthelemon.ggnadditons.commands.commandhandler;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandName {
    String value();
}
