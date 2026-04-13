package com.dentalflow.dentalflow.integraciones.whatsapp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.whatsapp")
public class WhatsAppProperties {

	private boolean enabled = false;
	private boolean useMetaApi = false;
	private String webhookToken = "dev-whatsapp-token";
	private String webhookSecret = "";
	private String apiUrl = "https://graph.facebook.com";
	private String accessToken = "";
	private String phoneNumberId = "";

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isUseMetaApi() {
		return useMetaApi;
	}

	public void setUseMetaApi(boolean useMetaApi) {
		this.useMetaApi = useMetaApi;
	}

	public String getWebhookToken() {
		return webhookToken;
	}

	public void setWebhookToken(String webhookToken) {
		this.webhookToken = webhookToken;
	}

	public String getWebhookSecret() {
		return webhookSecret;
	}

	public void setWebhookSecret(String webhookSecret) {
		this.webhookSecret = webhookSecret;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getPhoneNumberId() {
		return phoneNumberId;
	}

	public void setPhoneNumberId(String phoneNumberId) {
		this.phoneNumberId = phoneNumberId;
	}
}


