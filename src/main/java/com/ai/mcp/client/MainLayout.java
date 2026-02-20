package com.ai.mcp.client;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Locale;

@Layout
public class MainLayout extends AppLayout {

    public MainLayout() {
        UI.getCurrent().setLocale(Locale.ENGLISH);

        // Page Header/Nav Bar
        var head = new HorizontalLayout();
        head.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        head.add(new DrawerToggle());
        head.add(new H2("Spring AI in Java"){{addClassNames(LumoUtility.FontSize.LARGE);}});

        addToNavbar(head);

        // Sidebar to display menu items
        var sideBar = new VerticalLayout();
        var links = new VerticalLayout();
        links.setMargin(false);

        // Populate Side bar with menu items
        MenuConfiguration.getMenuEntries().forEach(menuEntry -> {
            links.add(new RouterLink(menuEntry.title(), menuEntry.menuClass()));
        });
        sideBar.addAndExpand(links);

        // Add Toggle button to switch between dark and light theme
        var themeToggle = new Checkbox("Dark theme");

        themeToggle.addValueChangeListener(e -> {
            var js = "document.documentElement.setAttribute('theme', $0)";
            getElement().executeJs(js, e.getValue() ? Lumo.DARK : Lumo.LIGHT);
        });
        sideBar.add(themeToggle);

        // Add Sidebar to page
        addToDrawer(sideBar);
    }
}
