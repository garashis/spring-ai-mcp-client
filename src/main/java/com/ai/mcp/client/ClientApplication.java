package com.ai.mcp.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.PageTitle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@PageTitle("AI in Vaadin")
@SpringBootApplication
@Push
public class ClientApplication implements AppShellConfigurator {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class, args);
	}
}
