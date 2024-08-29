package de.burnthelemon.ggnadditons.commands.commandhandler;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CMDPermission {
    String value();
}
