package pl.sokolak.MyBooksLite.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import pl.sokolak.MyBooksLite.ui.AccessDeniedDialog;

import java.util.Collections;
import java.util.Set;

import static pl.sokolak.MyBooksLite.security.SecurityUtils.getUserRoles;

@Aspect
public class SecurityAspect {

    @Around("@annotation(secured) && call(* *(..))")
    public void inspectMethod(ProceedingJoinPoint jp, Secured secured) throws Throwable {
        Set<Role> userRoles = getUserRoles();
        OperationType operationType = secured.value();
        if (!Collections.disjoint(userRoles, operationType.getRoles())) {
            jp.proceed();
        } else {
            new AccessDeniedDialog();
        }
    }
}
