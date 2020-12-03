# PROYECTO FINAL CIBERSEGURIDAD

## CHAT CON COMUNICACIÓN CIFRADA

##### 1. ¿Cómo hicimos el programa?

Se creó una clase ChatServer, la cual se encarga de recibir las conexiones de los clientes y los mensajes que estos envían, funcionando como un puente de comunicación entre los clientes para así entablar las respectivas conversaciones del chat. Luego se implementó la clase ChatClient que tiene una relación con la interfaz gráfica donde se pueden escribir y ver los mensajes teniendo en cuenta siempre un usuario objetivo, esto con la modalidad de emplear el algoritmo Diffie Hellman cuando se requiera y así entablar una comunicación secreta en un canal público.

La encriptación de los mensajes se realizó con el algoritmo AES a 128 bits empleando el método de cifrado ECB.

##### 2. Dificultades al momento de realizar el programa

A la hora de realizar el proyecto se nos presentó bastantes dificultades tales como:

- Convertir los objetos de entrada y salida en bytes para ser procesados por los sockets.
- Realizar procesos redundantes como transformar el archivo recibido en texto plano para después transformarlo en bytes de nuevo.
- Conectar varios clientes y establecer su comunicación.
- Mostrar los mensajes en la interfaz gráfica.
- Usar las librerías de encriptación.
- Entender la aritmética modular del algoritmo Diffie-Hellman.
- Buscar información relevante de diferentes fuentes a la hora de emplear los algoritmos de encriptación.

##### 3. Conclusiones

Este proyecto logró afianzar nuestros conocimientos de encriptación vistos en la primera unidad, observando de primera mano cómo se realiza un proceso completo de comunicación secreta en un entorno público. De igual forma pudimos apreciar que preservar la seguridad de la información es un procedimiento largo pero indispensable para mantener nuestro software limpio y libre de cualquier amenaza que pueda perjudicarnos a nosotros y a nuestros clientes.
