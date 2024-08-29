package de.burnthelemon.ggnadditons.commands.commandhandler;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubCMD {
    String[] value();
}
