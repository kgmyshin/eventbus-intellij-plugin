package com.kgmyshin.ideaplugin.eventbus;

import com.intellij.psi.*;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageInfo2UsageAdapter;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class ReceiverFilter implements Filter {

    public final PsiClass eventClass;

    public ReceiverFilter(PsiClass eventClass) {
        this.eventClass = eventClass;
    }

    @Override
    public boolean shouldShow(Usage usage) {
        PsiElement element = ((UsageInfo2UsageAdapter) usage).getElement();
        if (element instanceof PsiJavaCodeReferenceElement) {
            if ((element = element.getParent()) instanceof PsiTypeElement) {
                if ((element = element.getParent()) instanceof PsiParameter) {
                    if ((element = element.getParent()) instanceof PsiParameterList) {
                        if ((element = element.getParent()) instanceof PsiMethod) {
                            PsiMethod method = (PsiMethod) element;
                            if (PsiUtils.isEventBusReceiver(method)) {
                                if (isInstance(PsiUtils.getClass(method.getParameterList().getParameters()[0].getType(), element))) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isInstance(PsiClass psiClass) {
        String psiClassName = psiClass.getName();
        for (PsiClass cls : eventClass.getSupers()) {
            if (cls.getName().equals(psiClassName)) {
                return true;
            }
        }
        return false;
    }
}
