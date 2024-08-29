package de.burnthelemon.ggnadditons.commands.commandhandler;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CommandSource {
    boolean console() default true;
    boolean player() default true;
    boolean commandBlock() default true;
}
