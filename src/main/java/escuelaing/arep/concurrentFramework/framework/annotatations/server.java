package escuelaing.arep.concurrentFramework.framework.annotatations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface server {
    String path() default "/";
}