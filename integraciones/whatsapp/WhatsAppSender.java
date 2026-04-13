package com.dentalflow.dentalflow.integraciones.whatsapp;

public interface WhatsAppSender {

	WhatsAppSendResult send(String destinationPhone, String message);
}


