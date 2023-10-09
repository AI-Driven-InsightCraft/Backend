package com.ka.springbootinit.mq;

import com.rabbitmq.client.*;

public class DirectConsumer {
  private static final String EXCHANGE_NAME = "direct-exchange";

  public static void main(String[] argv) throws Exception {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("localhost");
      Connection connection = factory.newConnection();
      Channel channel = connection.createChannel();

      channel.exchangeDeclare(EXCHANGE_NAME, "direct");
      String queueName = "direct_kaka";
      channel.queueDeclare(queueName, true, false, false, null);
      channel.queueBind(queueName, EXCHANGE_NAME, "kaka");

      String queueName2 = "direct_feifei";
      channel.queueDeclare(queueName2, true, false, false, null);
      channel.queueBind(queueName2, EXCHANGE_NAME, "feifei");

      System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), "UTF-8");
          System.out.println(" [x] kaka Received '" + message + "'");
      };
      DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
          String message = new String(delivery.getBody(), "UTF-8");
          System.out.println(" [x] feifei Received '" + message + "'");
      };
      channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
      channel.basicConsume(queueName2, true, deliverCallback2, consumerTag -> { });
  }
}