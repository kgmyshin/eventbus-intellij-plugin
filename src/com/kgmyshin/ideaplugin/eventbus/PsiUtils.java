package com.kgmyshin.ideaplugin.eventbus;

import com.intellij.psi.*;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class PsiUtils {

    public static PsiClass getClass(PsiType psiType) {
        if (psiType instanceof PsiClassType) {
            return ((PsiClassType) psiType).resolve();
        }
        return null;
    }

    public static boolean isEventBusReceiver(PsiElement psiElement) {
        if (psiElement instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) psiElement;
            if (method.getName() != null
                    && (method.getName().equals("onEvent") || method.getName().equals("onMainThreadEvent"))
                    && method.getParameterList().getParametersCount() == 1) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEventBusPost(PsiElement psiElement) {
        if (psiElement instanceof PsiCallExpression) {
            PsiCallExpression callExpression = (PsiCallExpression) psiElement;
            PsiMethod method = callExpression.resolveMethod();
            if (method != null) {
                String name = method.getName();
                PsiElement parent = method.getParent();
                if (name != null && name.equals("post") && parent instanceof PsiClass) {
                    PsiClass implClass = (PsiClass) parent;
                    if (isEventBusClass(implClass) || isSuperClassEventBus(implClass)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isEventBusClass(PsiClass psiClass) {
        if (psiClass.getName().equals("EventBus")) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isSuperClassEventBus(PsiClass psiClass) {
        PsiClass[] supers = psiClass.getSupers();
        if (supers.length == 0) {
            return false;
        }
        for (PsiClass superClass : supers) {
            if (superClass.getName().equals("EventBus")) {
                return true;
            }
        }
        return false;
    }

}
