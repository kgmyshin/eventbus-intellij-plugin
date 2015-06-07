package com.kgmyshin.ideaplugin.eventbus;

import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

/**
 * Created by kgmyshin on 2015/06/07.
 */
public class EventBusLineMarkerProvider implements LineMarkerProvider {

    public static final Icon ICON = IconLoader.getIcon("/icons/icon.png");

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {
        if (isEventBusPost(psiElement)) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                    Pass.UPDATE_ALL, null, null,
                    GutterIconRenderer.Alignment.LEFT);
        } else if (isEventBusReceiver(psiElement)) {
            return new LineMarkerInfo<PsiElement>(psiElement, psiElement.getTextRange(), ICON,
                    Pass.UPDATE_ALL, null, null,
                    GutterIconRenderer.Alignment.LEFT);
        }
        return null;
    }

    private boolean isEventBusPost(PsiElement psiElement) {
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

    private boolean isEventBusClass(PsiClass psiClass) {
        if (psiClass.getName().equals("EventBus")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isSuperClassEventBus(PsiClass psiClass) {
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

    private boolean isEventBusReceiver(PsiElement psiElement) {
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

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> list, @NotNull Collection<LineMarkerInfo> collection) {
    }
}
